package com.neo.sk.utils

import java.nio.charset.Charset

import org.asynchttpclient.{BoundRequestBuilder, DefaultAsyncHttpClient, Param, Response}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * User: Taoz
  * Date: 11/28/2016
  * Time: 17:28 PM
  *
  * update by zhangtao 2017-01-01 23:54
  * 1, remove unused import "akka.stream.Materializer"
  * 2, change "implicit val executor" to implicit in the function.
  * 3, you can import HttpUtil.Imports to use the api.
  *
  *
  *
  */

object HttpUtil {
  private val ahClientImp: DefaultAsyncHttpClient = new DefaultAsyncHttpClient()
  private val log = LoggerFactory.getLogger(this.getClass)

  object Imports extends HttpUtil

  implicit class AhcToScala[T](reqBuilder: BoundRequestBuilder) {

    def scalaExecute(): Future[Response] = {
      import org.asynchttpclient.AsyncCompletionHandler
      val result = Promise[Response]()
      reqBuilder.execute(new AsyncCompletionHandler[Response]() {
        override def onCompleted(response: Response): Response = {
          result.success(response)
          response
        }
        override def onThrowable(t: Throwable): Unit = {
          result.failure(t)
        }
      })
      result.future
    }
  }
}

trait HttpUtil {


  import HttpUtil._

  //implicit val executor: ExecutionContext

  private val ahClient: DefaultAsyncHttpClient = ahClientImp

  import collection.JavaConverters._

  private def parseResp(response: Response, charset: Charset) = {
    val body = new String(response.getResponseBodyAsBytes, charset)
    log.debug("getRequestSend response headers:" + response.getHeaders)
    log.debug("getRequestSend response body:" + body)
    if (response.getStatusCode != 200) {
      val uri = response.getUri
      val bodyLength = body.length
      val msg = s"getRequestSend http failed url = $uri, status = ${response.getStatusCode}, text = ${response.getStatusText}, body = ${body.substring(0, Math.min(bodyLength, 1024))}"
      log.warn(msg)
    }
    body
  }

  private def executeRequest(
    methodName: String,
    request: BoundRequestBuilder,
    charset: Charset
  )(implicit executor: ExecutionContext) = {
    request.scalaExecute().map { response =>
      Right(parseResp(response, charset))
    }.recover { case e: Throwable => Left(e) }
  }

  def postJsonRequestSend(
    methodName: String,
    url: String,
    parameters: List[(String, String)],
    jsonStr: String,
    headers:List[(String,String)] = List(("Content-Type","application/json")),
    charsetName: String = "UTF-8"
  )(implicit executor: ExecutionContext): Future[Either[Throwable, String]] = {
    log.info("Post Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    log.debug(methodName + " postData=" + jsonStr)
    val cs = Charset.forName(charsetName)
    val request = ahClient.
      preparePost(url).
      setFollowRedirect(true).
      setRequestTimeout(20 * 1000).
      setCharset(cs).
      addQueryParams(parameters.map { kv => new Param(kv._1, kv._2) }.asJava).
      setBody(jsonStr)
    headers.foreach(h => request.addHeader(h._1,h._2))
    executeRequest(methodName, request, cs)
  }

  def getRequestSend(
    methodName: String,
    url: String,
    parameters: List[(String, String)],
    headers:List[(String,String)] = List(("Content-Type","application/json")),
    responseCharsetName: String = "UTF-8"
  )(implicit executor: ExecutionContext): Future[Either[Throwable, String]] = {
    log.info("Get Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    val request = ahClient.
      prepareGet(url).
      setFollowRedirect(true).
      setRequestTimeout(20 * 1000).
      addQueryParams(parameters.map { kv => new Param(kv._1, kv._2) }.asJava)
    val cs = Charset.forName(responseCharsetName)
    executeRequest(methodName, request, cs)
  }

}
