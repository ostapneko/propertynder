package propertynder.extractors.seloger

import org.scalatest.{FlatSpec, Matchers}
import propertynder.model.Property

import scala.io.Source
import scala.xml.{XML => ScalaXML}
import XML._
import akka.http.scaladsl.model.Uri

class XMLSpec extends FlatSpec with Matchers {

  val xml = ScalaXML.load(Source.fromFile("src/test/resources/seloger.xml").reader())

  val lastPageXml = ScalaXML.load(Source.fromFile("src/test/resources/selogerLastPage.xml").reader())

  "parseProperties" should "parse a XML payload from seloger.com" in {
    parseProperties(xml).head should be(
      Property(
        "Appartement 3 pièces",
        "http://www.seloger.com/annonces/achat/appartement/paris-9eme-75/117856511.htm?p=CCBPqSgIBo-wKSdA",
        "",
        Some(3),
        Some(2),
        Some(860000),
        Some(76.0),
        Some(75009),
        None,
        None
      )
    )
  }

  "parseNextRequest" should "extract the next request uri from the payload" in {
    parseNextRequest(xml) should be(
      Some(Uri("http://ws.seloger.com/search.xml?cp=75009&idtt=2&nb_chambres=2&SEARCHpg=2"))
    )
  }

  it should "return None if it is the last page" in {
    parseNextRequest(lastPageXml) should be(
      None
    )
  }

}
