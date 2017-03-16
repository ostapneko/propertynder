package propertynder.extractors.seloger

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity, Uri}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import propertynder.model.Property

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scala.xml.NodeSeq

object XML extends ScalaXmlSupport {

  type NextRequestUri = Uri

  private def parseOptInt(str: String): Option[Int] =
    Try(Integer.parseInt(str)).toOption

  private[extractors] def parseProperties(nodeSeq: NodeSeq): Seq[Property] =
    nodeSeq.flatMap(node =>
      node.\\("recherche")
        .flatMap(
          _.\\("annonces").flatMap(
            _.\\("annonce").map(l =>
              Property(
                l.\\("titre").text,
                l.\\("permaLien").text,
                l.\\("description").text,
                parseOptInt(l.\\("nbPiece").text),
                parseOptInt(l.\\("nbChambre").text),
                parseOptInt(l.\\("prix").text),
                parseOptInt(l.\\("surface").text).map(_.toDouble),
                parseOptInt(l.\\("cp").text)
              )
            )
          )
        )
    )

  private[extractors] def parseNextRequest(nodeSeq: NodeSeq): Option[NextRequestUri] = {

    nodeSeq.flatMap(node =>
      node.\\("recherche").map(
        _.\\("pageSuivante").text
      )
    ).headOption
      .filterNot(_.isEmpty)
      .map(_.replace("http://ws.seloger.com/http://ws.seloger.com/", "http://ws.seloger.com/")).map(Uri(_))
  }

  implicit def propertyUnmarshaller: Unmarshaller[ResponseEntity, Seq[Property]] =
    defaultNodeSeqUnmarshaller.map(nodeSeq =>
      parseProperties(nodeSeq)
    )

  implicit def nextRequestUnmarshaller: Unmarshaller[ResponseEntity, Option[NextRequestUri]] =
    defaultNodeSeqUnmarshaller.map(nodeSeq =>
      parseNextRequest(nodeSeq)
    )


  def extractProperties(response: HttpResponse)(implicit mat: ActorMaterializer): Future[Seq[Property]] =
    Unmarshal(response.entity).to[Seq[Property]]

  def extractNextRequest(response: HttpResponse)(implicit mat: ActorMaterializer): Future[Option[NextRequestUri]] =
    Unmarshal(response.entity).to[Option[NextRequestUri]]

}
