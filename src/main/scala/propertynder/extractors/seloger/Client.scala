package propertynder.extractors.seloger

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, MediaRange, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import propertynder.model.Property
import propertynder.util.HTTPCache

import scala.collection.immutable.Range.Inclusive
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Client extends LazyLogging {
  private val parallelism = 1
  private val baseUri: Uri = Uri("http://ws.seloger.com/search.xml?")
  // idtt represents the type of transaction, number 2 being sales (as opposed to rental)
  private val params = Map("idtt" -> "2", "cp" -> "75")

  private def uri: Uri = baseUri.withQuery(Query(params))

  private def getRequest(uri: Uri): HttpRequest =
    Get(uri).addHeader(Accept(MediaRange(`application/xml`)))

  private def getWithPagination(request: Option[HttpRequest])(implicit mat: ActorMaterializer, as: ActorSystem):
    Future[Option[(Option[HttpRequest], HttpResponse)]] = {
    request match {
      case Some(req) =>
        logger.info(s"Requesting Seloger.com end point: ${req.uri}")
        val response = HTTPCache.get(req)
        response.flatMap(_.toStrict(10 minutes)).flatMap(r => {
          val nextRequest = XML.extractNextRequest(r)
          nextRequest.map(_.map(nr =>
            (Some(getRequest(nr)), r)
          ))
        }.recover { case err =>
          logger.error(s"ERROR: $err")
          None
        }
      )
      case None => Future.successful(None)
    }
  }

  def graph(implicit mat: ActorMaterializer, as: ActorSystem): RunnableGraph[Future[Done]] = {
    implicit val ec = mat.system.dispatcher

    val sink = Sink.foreach[Seq[Property]]{el =>
      logger.info(s"received element $el")
    }

    Source.unfoldAsync[Option[HttpRequest], HttpResponse](Some(getRequest(uri)))(getWithPagination)
      .mapAsync(parallelism)(XML.extractProperties)
      .toMat(sink)(Keep.right)
  }
}
