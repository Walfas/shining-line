package services

import java.io.ByteArrayInputStream
import play.api.Play.current
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.ws._
import scala.concurrent.Future
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory, Status, StatusUpdate}

trait TwitterService {
  def updateWithMediaFromUrl(url: String): Future[String]
}

trait TwitterServiceComponent {
  def twitterService: TwitterService
}

class TwitterServiceImpl(val twitter: Twitter) {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def updateWithMediaFromUrl(urlString: String): Future[String] = {
    val f = getBytesFromUrl(urlString)

    f.map { (byteStream: ByteArrayInputStream) =>
      updateStatusWithMedia(byteStream).getText
    }
  }

  private def getBytesFromUrl(urlString: String): Future[ByteArrayInputStream] = {
    val stream: Future[(WSResponseHeaders, Enumerator[Array[Byte]])] =
      WS.url(urlString).getStream

    val f: Future[ByteArrayInputStream] = stream.flatMap { case (headers, body) =>
      val it = Iteratee.consume[Array[Byte]]().map { (bytes: Array[Byte]) =>
        new ByteArrayInputStream(bytes)
      }
      body.run(it)
    }

    f
  }

  private def updateStatusWithMedia(byteStream: ByteArrayInputStream): Status = {
    val newStatus: StatusUpdate = new StatusUpdate("").media("", byteStream)
    val updatedStatus: Status = twitter.updateStatus(newStatus)
    updatedStatus
  }
}

class TwitterServiceComponentImpl {
  val cb: ConfigurationBuilder = new ConfigurationBuilder()
    .setDebugEnabled(true)
    .setOAuthConsumerKey("")
    .setOAuthConsumerSecret("")
    .setOAuthAccessToken("")
    .setOAuthAccessTokenSecret("");

  val twitter: Twitter = new TwitterFactory(cb.build()).getInstance()

  val twitterService = new TwitterServiceImpl(twitter)
}

