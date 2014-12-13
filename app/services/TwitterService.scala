package services

import java.io.ByteArrayInputStream
import play.api.Play.current
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.ws._
import scala.concurrent.Future
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory, StatusUpdate}

trait TwitterService {
  def updateWithMediaFromUrl(url: String): Future[String]
}

trait TwitterServiceComponent {
  def twitterService: TwitterService
}

class TwitterServiceImpl(val twitter: Twitter) {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def updateWithMediaFromUrl(urlString: String): Future[String] = {

    val stream: Future[(WSResponseHeaders, Enumerator[Array[Byte]])] =
      WS.url(urlString).getStream()

    val f: Future[ByteArrayInputStream] = stream.flatMap { case (headers, body) =>
      val it = Iteratee.consume[Array[Byte]]().map { (bytes: Array[Byte]) =>
        new ByteArrayInputStream(bytes)
      }
      body.run(it)
    }

    f.map { is =>
      val status: StatusUpdate = new StatusUpdate("").media(urlString, is)

      val updatedStatus = twitter.updateStatus(status)
      updatedStatus.getText
    }

    /*
    val url = new java.net.URL(urlString)
    val img = javax.imageio.ImageIO.read(url)
    val os = new java.io.ByteArrayOutputStream()
    javax.imageio.ImageIO.write(img, "image/png", os)
    val is = new java.io.ByteArrayInputStream(os.toByteArray)

    val status: StatusUpdate = new StatusUpdate("").media("hello", is)
    status.setMedia(new java.io.File(urlString))

    val updatedStatus = twitter.updateStatus(status)
    updatedStatus.getMediaEntities.headOption.map(_.getMediaURL).getOrElse("")
    */
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

