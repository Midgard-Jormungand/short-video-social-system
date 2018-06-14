package com.neo.sk.svss.service

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.util.Timeout

import scala.concurrent.{ExecutionContextExecutor, Future}
/**
  * User: Taoz
  * Date: 8/26/2016
  * Time: 10:27 PM
  */
trait HttpService extends ResourceService
  with ServiceUtils
  with TestService
  with AuthService
  with CommentService
  with FileService
  with FollowService
  with IndexService
  with UserService
  with VideoService {


  implicit val system: ActorSystem

  implicit val executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler


  lazy val routes: Route =
    ignoreTrailingSlash {
      pathPrefix("svss") {
        pathEndOrSingleSlash {
          getFromResource("html/index.html")
          } ~
          resourceRoutes ~
          testRoutes ~
          authRoutes ~
          commentRoutes ~
          fileRoutes ~
          followRoutes ~
          indexRoutes ~
          userRoutes ~
          videoRoutes
      }
    }
}
