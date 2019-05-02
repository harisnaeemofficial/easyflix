package net.creasource.webflix.actors

import java.nio.file.Path

import akka.actor.{Actor, Props, Status}
import akka.event.Logging
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.{KillSwitches, SharedKillSwitch, UniqueKillSwitch}
import net.creasource.Application
import net.creasource.exceptions.NotFoundException
import net.creasource.webflix.events.{FileAdded, LibraryUpdate}
import net.creasource.webflix.{Library, LibraryFile}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object LibraryActor {

  case object GetFiles
  case class GetFile(path: Path)

  case object Scan

  def props(library: Library)(implicit app: Application): Props = Props(new LibraryActor(library))

}

class LibraryActor(library: Library)(implicit app: Application) extends Actor {

  import LibraryActor._
  import app.materializer
  import context.dispatcher

  val logger = Logging(context.system, this)

  var files: Map[Path, LibraryFile] = Map.empty

  val killSwitch: SharedKillSwitch = KillSwitches.shared(library.name)

  var foldersKillSwitches: Map[Path, UniqueKillSwitch] = Map.empty

  case object LibraryScanComplete
  case class ScanComplete(path: Path)

  case class WatchComplete(path: Path)

  case class UpdateLibrary(library: Library.Local)

//  app.bus.subscribe(self, classOf[ResolverUpdate])

  library match {
    case lib: Library.Local => app.system.scheduler.schedule(1.minutes, 1.minutes, self, UpdateLibrary(lib))
    case _ =>
  }

  def common(library: Library): Receive = {

//    case ResolverUpdate(ctr) =>
//      logger.info("Received a content type resolver update. Rescanning.")
//      contentTypeResolver = ctr
//      files.values.foreach {
//        case LibraryFile(name, path, false, _, _, _) if !ctr(name).mediaType.isVideo => files -= path // TODO submit to event stream
//      }
//      self ! Scan

    case GetFiles => sender() ! files.values.toSeq

    case GetFile(path) =>
      files.get(path) match {
        case Some(file) => sender() ! file
        case _ => sender() ! Status.Failure(NotFoundException("No file with that path"))
      }

    case file: LibraryFile =>
      app.bus.publish(FileAdded(file))
      files += (file.path -> file)

    case (file: LibraryFile, DirectoryChange.Creation) =>
      logger.info(s"File created: ${file.path}")
      files += (file.path -> file)
      if (file.isDirectory) {
        // Watch it
        library match {
          case lib: Library.Watchable =>
            val ks = lib
              .watch(file.path)
              .via(killSwitch.flow)
              .viaMat(KillSwitches.single)(Keep.right)
              .to(Sink.actorRef(self, WatchComplete(file.path)))
              .run()
            foldersKillSwitches += (file.path -> ks)
          case _ =>
        }
        // Scan it
        library
          .scan(file.path)
          .via(killSwitch.flow)
          .to(Sink.actorRef(self, ScanComplete(file.path)))
          .run()
      }

    // TODO wrap in an object and only pass the path here: don't create a LibraryFile for a file that has been deleted
    case (file: LibraryFile, DirectoryChange.Deletion) =>
      if (files.isDefinedAt(file.path)) {
        if (files(file.path).isDirectory) { // check in our map if it was a directory, see TODO above
          // Stop watching it
          foldersKillSwitches.get(file.path).foreach(_.shutdown())
          foldersKillSwitches -= file.path
          // Delete children
          val children = files.values.filter(_.path.startsWith(file.path)).map(_.path)
          files --= children
          logger.info(s"Folder and ${children.toSeq.length} children deleted: ${file.path}")
        } else {
          logger.info(s"File deleted: ${file.path}")
        }
        files -= file.path
      }

    case (file: LibraryFile, DirectoryChange.Modification) => files += (file.path -> file)

    case WatchComplete(folder) => logger.info(s"Stopped watching: $folder")

    case ScanComplete(folder) => logger.info(s"Scan complete: $folder")

  }

  def ready(library: Library): Receive = common(library) orElse {

    case UpdateLibrary(lib) =>
      val updated = lib.copy(totalSpace = lib.path.toFile.getTotalSpace, freeSpace = lib.path.toFile.getFreeSpace)
      if (updated != library) {
        app.bus.publish(LibraryUpdate(updated))
        context become ready(updated)
      }

    case Scan =>
      val client = sender()
      library.scan()
        .via(killSwitch.flow)
        .alsoTo(Sink.actorRef(self, LibraryScanComplete))
        .alsoTo(Sink.foreach {
          case file if file.isDirectory & library.isInstanceOf[Library.Watchable] & foldersKillSwitches.get(file.path).isEmpty =>
            logger.info(s"Watching: ${file.path}")
            val ks = library.asInstanceOf[Library.Watchable]
              .watch(file.path)
              .via(killSwitch.flow)
              .viaMat(KillSwitches.single)(Keep.right)
              .to(Sink.actorRef(self, WatchComplete(file.path)))
              .run()
            foldersKillSwitches += (file.path -> ks)
          case _ =>
        })
        .runWith(Sink.seq)
        .onComplete {
          case Success(scannedFiles) => if (client != self) { client ! scannedFiles }
          case Failure(exception) => if (client != self) { client ! Status.Failure(exception) }
        }
      context become scanning(library)

  }

  def scanning(library: Library): Receive = common(library) orElse {

    case UpdateLibrary(_) => // ignore

    case Scan =>
      if (sender() != self)
        sender() ! Status.Failure(new RuntimeException("A scan is already in progress")) // TODO custom exception

    case LibraryScanComplete =>
      logger.info(s"Library scan complete: ${library.name}")
      context become ready(library)

  }

  override def receive: Receive = ready(library)

  override def postStop(): Unit = {
    killSwitch.shutdown()
    foldersKillSwitches.values.foreach(_.shutdown())
  }

}
