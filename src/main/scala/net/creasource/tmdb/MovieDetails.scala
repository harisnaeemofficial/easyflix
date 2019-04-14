package net.creasource.tmdb

import net.creasource.json.JsonSupport
import spray.json.RootJsonFormat

// https://developers.themoviedb.org/3/movies/get-movie-details

// Dropped three fields: homepage, belongs_to_collection and video
case class MovieDetails(
    adult: Boolean,
    backdrop_path: Option[String],
    budget: Int,
    genres: List[MovieDetails.Genre],
    id: Int,
    imdb_id: Option[String],
    original_language: String,
    original_title: String,
    overview: Option[String],
    popularity: Float,
    poster_path: Option[String],
    production_companies: List[MovieDetails.Company],
    production_countries: List[MovieDetails.Country],
    release_date: String,
    revenue: Int,
    runtime: Option[Int],
    spoken_languages: List[MovieDetails.Language],
    status: String,
    tagline: Option[String],
    title: String,
    vote_average: Float,
    vote_count: Int)

object MovieDetails extends JsonSupport {

  def get(
      movie_id: Int,
      api_key: String,
      language: Option[String] = None,
      append_to_response: Option[String] = None): String = {

    s"/3/movie/$movie_id?api_key=$api_key" +
      language.toParam("language") +
      append_to_response.toParam("append_to_response")
  }

  implicit val format: RootJsonFormat[MovieDetails] = jsonFormat22(MovieDetails.apply)

  case class Genre(id: Int, name: String)
  object Genre {
    implicit val format: RootJsonFormat[Genre] = jsonFormat2(Genre.apply)
  }

  case class Company(name: String, id: Int, logo_path: Option[String], origin_country: String)
  object Company {
    implicit val format: RootJsonFormat[Company] = jsonFormat4(Company.apply)
  }

  case class Country(iso_3166_1: String, name: String)
  object Country {
    implicit val format: RootJsonFormat[Country] = jsonFormat2(Country.apply)
  }

  case class Language(iso_639_1: String, name: String)
  object Language {
    implicit val format: RootJsonFormat[Language] = jsonFormat2(Language.apply)
  }

}