package propertynder.util

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.concurrent.Future

object HTTP {
  implicit class ResponseWithBody(response: HttpResponse) {
    def body(implicit mat: ActorMaterializer): Future[String] = {
      implicit val ec = mat.system.dispatcher

      response.entity.dataBytes
        .runFold(ByteString(""))(_ ++ _)
        .map(_.utf8String)
    }
  }


}
