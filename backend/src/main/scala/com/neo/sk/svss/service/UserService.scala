package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives.{complete, entity, _}
import com.neo.sk.svss.common.Constants.UserRolesType
import com.neo.sk.svss.service.SessionBase.{SessionKeys, UserSession}
import com.neo.sk.svss.shared.ptcl.UserProtocol._
import com.neo.sk.svss.shared.ptcl._
import io.circe.generic.auto._
import io.circe.Error
import akka.http.scaladsl.server.{Directive1, Route}
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Left, Success}
import com.neo.sk.svss.models.dao._

trait UserService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)
  private def storeFile(source: Source[ByteString, Any]): Directive1[java.io.File] = {
    val dest = java.io.File.createTempFile("svss", ".tmp")
    val file = source.runWith(FileIO.toPath(dest.toPath)).map(_ => dest)
    onComplete[java.io.File](file).flatMap {
      case Success(f) =>
        provide(f)
      case Failure(e) =>
        dest.deleteOnExit()
        failWith(e)
    }
  }
  val userRoutes:Route= {
    (path("getUserInfo") & get){
      parseUserSession {
        session =>
          dealFutureResult {
            uiDao.getUserInfo(session.userId.toInt).map {
              rst =>
                val upList=rst._3.map{i=>BaseVideoInfo(i._1,i._2,i._3,i._4)}.toList
                val likeList=rst._4.map{i=>BaseVideoInfo(i._1,i._2,i._3,i._4)}.toList
                complete(UserInfoRsp(UserInfo(session.name,session.sex.toShort,session.signature,rst._1,rst._2,upList,likeList)))
            }.recover {
              case x: Exception =>
                log.warn(s"getUserInfo error,${x.getMessage}")
                complete(ErrorRsp(2, s"getUserInfo error:${x.getMessage}"))
            }
          }
      }
    }~ (path("updateUserInfo") & post){
      parseUserSession{
        session=>
          entity(as[Either[Error, updateUserInfoReq]]){
            case Right(q) =>
              if(q.name != session.name){
                dealFutureResult {
                  uiDao.selectUserName(q.name).map {
                    case Some(i) if i != session.userId.toInt =>
                      complete(ErrorRsp(2,"该昵称已被使用"))
                    case None =>
                      dealFutureResult{
                        uiDao.updateUserInfoWithName(session.userId.toInt,q.name,q.sex,q.signature).map{
                          rst=>
                            addSession(
                              UserSession(SessionUserInfo(UserRolesType.comMember, session.userId,q.name,q.sex.toString,q.signature), System.currentTimeMillis()).toSessionMap
                            ) {
                              cxt =>
                                cxt.complete(SuccessRsp())
                            }
                        }.recover {
                          case x: Exception =>
                            log.warn(s"updateUserInfoWithName error,${x.getMessage}")
                            complete(ErrorRsp(5, s"updateUserInfoWithName error:${x.getMessage}"))
                        }
                      }
                  }.recover {
                    case x: Exception =>
                      log.warn(s"selectUserName error,${x.getMessage}")
                      complete(ErrorRsp(4, s"selectUserName error:${x.getMessage}"))
                  }
                }
              }else{
                dealFutureResult{
                  uiDao.updateUserInfoWithoutName(session.userId.toInt,q.sex,q.signature).map{
                    rst=>
                      addSession(
                        UserSession(SessionUserInfo(UserRolesType.comMember, session.userId,q.name,q.sex.toString,q.signature), System.currentTimeMillis()).toSessionMap
                      ) {
                        cxt =>
                          cxt.complete(SuccessRsp())
                      }
                  }.recover {
                    case x: Exception =>
                      log.warn(s"updateUserInfoWithoutName error,${x.getMessage}")
                      complete(ErrorRsp(5, s"updateUserInfoWithoutName error:${x.getMessage}"))
                  }
                }
              }
            case Left(error) =>
              complete(ErrorRsp(3,s"$error"))
          }
      }
    }~ (path("getUserInfo") & post){
      parseUserSession{
        session=>
          entity(as[Either[Error, getVisitInfoReq]]){
            case Right(q) =>
              dealFutureResult {
                uiDao.getVisitUserInfo(q.id,session.userId.toInt).map {
                  rst =>
                    val baseInfo=rst._1
                    val upList=rst._4.map{i=>BaseVideoInfo(i._1,i._2,i._3,i._4)}.toList
                    val likeList=rst._5.map{i=>BaseVideoInfo(i._1,i._2,i._3,i._4)}.toList
                    complete(VisitUserInfoRsp(UserInfo(baseInfo._1,baseInfo._2,baseInfo._3,rst._2,rst._3,upList,likeList),rst._6.getOrElse(0)))
                }.recover {
                  case x: Exception =>
                    log.warn(s"getUserInfo&post error,${x.getMessage}")
                    complete(ErrorRsp(3, s"getUserInfo&post error:${x.getMessage}"))
                }
              }
            case Left(error) =>
              complete(ErrorRsp(2,s"$error"))
          }
      }
    }
  }
}



