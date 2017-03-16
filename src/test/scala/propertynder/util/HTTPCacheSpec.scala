package propertynder.util

import akka.http.scaladsl.model.{HttpRequest, Uri}
import org.scalatest.{FlatSpec, Matchers}
import propertynder.util.HTTPCache._

class HTTPCacheSpec extends FlatSpec with Matchers {
  "getId" should "create a cache id from a request" in {
    val req = HttpRequest(uri = Uri("http://www.example.com/search?param1=one&param2=two"))
    getId(req) should be("search-param1=one&param2=two-6599837003a0f1ca75414aaac8587340.xml")
  }

}
