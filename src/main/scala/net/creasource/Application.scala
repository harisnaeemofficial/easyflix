package net.creasource

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model.{ContentType, MediaType}
import akka.http.scaladsl.server.directives.ContentTypeResolver
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.creasource.webflix.actors.LibrarySupervisor
import net.creasource.webflix.events.ApplicationBus

import scala.concurrent.Await

object Application {

  def apply() = new Application

}

/**
  * Represents an application. This is where you'll instantiate your top actors, connect to a database, etc...
  */
class Application {

  val config: Config = ConfigFactory.load().getConfig("webflix")

  implicit val system: ActorSystem = ActorSystem("MySystem", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  system.log.info("Application starting.")

  val bus: ApplicationBus = new ApplicationBus
  val libraries: ActorRef = system.actorOf(LibrarySupervisor.props()(this), "libraries")

  private val `video/x-mastroka`: MediaType.Binary = MediaType.video("x-mastroka", NotCompressible, "mkv")

  private def getContentTypeResolver(customMediaTypes: Seq[MediaType.Binary]): ContentTypeResolver = (fileName: String) =>  {
    val lastDotIx = fileName.lastIndexOf('.')
    if (lastDotIx >= 0) {
      val extension = fileName.substring(lastDotIx + 1)
      customMediaTypes.find(mediaType => mediaType.fileExtensions.contains(extension)) match {
        case Some(mediaType) => ContentType(mediaType)
        case None => ContentTypeResolver.Default(fileName)
      }
    } else ContentTypeResolver.Default(fileName)
  }

  implicit val contentTypeResolver: ContentTypeResolver = getContentTypeResolver(Seq(`video/x-mastroka`))

  def shutdown() {
    system.log.info("Shutting down Akka materializer and system.")
    import scala.concurrent.duration._
    materializer.shutdown()
    Await.result(system.terminate(), 30.seconds)
  }

}
