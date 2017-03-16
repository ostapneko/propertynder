package propertynder

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import propertynder.extractors.seloger.Client
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object Main extends LazyLogging {

  private def runJob(implicit mat: ActorMaterializer): Future[Unit] = {

    implicit val as = mat.system

    Client.graph.run().map(_ => logger.info("Job completed"))
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    Await.result(runJob, Duration.Inf)

    system.terminate()
  }

}
