package propertynder.util

import java.io.{File, FileWriter}
import java.nio.file.{Files, Path}
import java.security.MessageDigest

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import HTTP.ResponseWithBody
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

object HTTPCache extends LazyLogging {

  lazy val conf: Config = ConfigFactory.load()
  lazy val folder: String = conf.getString("http-cache.directory")
  lazy val cacheDirectory: Path = {
    val home = new File(System.getProperty("user.home")).toPath
    val dirPath = home.resolve(conf.getString("http-cache.directory"))

    if (!Files.isDirectory(dirPath)) {
      Files.createDirectories(dirPath)
    }
    dirPath
  }

  def get(request: HttpRequest)(implicit as: ActorSystem, mat: ActorMaterializer): Future[HttpResponse] = {
    val cacheId = getId(request)
    if (fileExists(cacheId)) {
      logger.info(s"Getting $cacheId from cache")
      val data = Source.fromFile(s"$cacheDirectory/$cacheId").getLines().mkString("")
      val response = HttpResponse(entity = HttpEntity(ContentTypes.`text/xml(UTF-8)`, data))
      Future.successful(response)
    }
    else {
      logger.info(s"$cacheId not found in cache, initiating request")
      val response = Http().singleRequest(request).flatMap(_.toStrict(10 minutes))
      writeToCache(request, response)
      response
    }
  }

  def getId(request: HttpRequest): String = {
    val uri = request.uri
    val path = uri.path.dropChars(1).toString()
    val query = uri.rawQueryString.getOrElse("noQueryString")
    val hash = md5(query)

    s"$path-$query-$hash.xml"
  }

  def writeToCache(request: HttpRequest, response: Future[HttpResponse])(implicit mat: ActorMaterializer): Future[Unit] = {
    val cacheId = getId(request)
    val fw = new FileWriter(new File(s"$cacheDirectory/$cacheId"))
    response.flatMap { r =>
      val f = r.body.map(fw.write)
      f.onComplete { _ => fw.close() }
      f
    }
  }

  private[util] def fileExists(cacheId: String): Boolean = {
    cacheDirectory.resolve(s"$cacheId").toFile.exists()
  }

  private def md5(str: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    val b = str.getBytes("UTF-8")
    md5.digest(b).map(0xFF & _).map("%02x".format(_)).mkString
  }

}
