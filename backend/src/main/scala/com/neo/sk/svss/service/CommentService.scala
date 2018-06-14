package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives.{complete, entity, _}
import com.neo.sk.svss.shared.ptcl._
import io.circe.generic.auto._
import io.circe.Error
import akka.http.scaladsl.server.{Directive1, Route}
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Left, Success}
import com.neo.sk.svss.models.dao._

trait CommentService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)

  val commentRoutes:Route= {
   (path("addComment") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, addCommentReq]]) {
          case Right(p) =>
            dealFutureResult {
              commentDao.addComment(p.vid,session.userId.toInt,session.name,p.content,System.currentTimeMillis()).map {
                rst=>
                  complete(CommentRsp(rst))
              }.recover {
                case x: Exception =>
                  log.warn(s"addComment error,${x.getMessage}")
                  complete(ErrorRsp(3, s"addComment error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }~(path("deleteComment") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, deleteCommentReq]]) {
          case Right(p) =>
            dealFutureResult {
              commentDao.deleteComment(p.commentId).map {
                rst=>
                  complete(SuccessRsp())
              }.recover {
                case x: Exception =>
                  log.warn(s"deleteComment error,${x.getMessage}")
                  complete(ErrorRsp(3, s"deleteComment error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }~(path("addCommentLike") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, addCommentLikeReq]]) {
          case Right(p) =>
            dealFutureResult {
              likeCommentDao.addCommentLike(session.userId.toInt,p.commentId).map {
                rst=>
                  complete(SuccessRsp())
              }.recover {
                case x: Exception =>
                  log.warn(s"addCommentLike error,${x.getMessage}")
                  complete(ErrorRsp(3, s"addCommentLike error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }~(path("deleteCommentLike") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, addCommentLikeReq]]) {
          case Right(p) =>
            dealFutureResult {
              likeCommentDao.deleteCommentLike(session.userId.toInt,p.commentId).map {
                rst=>
                  complete(SuccessRsp())
              }.recover {
                case x: Exception =>
                  log.warn(s"deleteCommentLike error,${x.getMessage}")
                  complete(ErrorRsp(3, s"deleteCommentLike error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }
  }
}



