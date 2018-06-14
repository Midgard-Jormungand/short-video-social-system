package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives.{complete, entity, _}
import com.neo.sk.svss.common.Constants.UserRolesType
import com.neo.sk.svss.service.SessionBase.UserSession
import com.neo.sk.svss.shared.ptcl.UserProtocol._
import com.neo.sk.svss.shared.ptcl._
import io.circe.generic.auto._
import io.circe.Error
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import com.neo.sk.svss.models.dao._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
trait AuthService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)
  val authRoutes:Route={
    (path("loginCheck") & post) {
      entity(as[Either[Error, LoginReq]]) {
        case Right(q) =>
          dealFutureResult{
            uiDao.queryPw(q.CheckName,q.CheckPw).map {
              rst =>
                if (rst.nonEmpty) {
                  setSession(
                    UserSession(SessionUserInfo(UserRolesType.comMember, rst.head._1.toString,q.CheckName,rst.head._2.toString,rst.head._3), System.currentTimeMillis()).toSessionMap
                  ) {
                    cxt =>
                      cxt.complete(SuccessRsp())
                  }
                } else {
                  complete(ErrorRsp(4,"用户名或密码错误"))
                }
            }.recover{
              case x: Exception =>
                log.warn(s"queryPw error,${x.getMessage}")
                complete(ErrorRsp(3, s"queryPw error:${x.getMessage}"))
            }
          }
        case Left(error) =>
          complete(ErrorRsp(2,s"$error"))
      }
    }~ (path("signUpCheck") & post) {
      entity(as[Either[Error, SignUpReq]]) {
        case Right(p) =>
          dealFutureResult{
            uiDao.queryName(p.CheckName).map {
              rst =>
                if (rst.nonEmpty) {
                  complete(ErrorRsp(4,"该用户名已被注册"))
                }else{
                  dealFutureResult{
                    uiDao.insertDb(p.CheckName, p.CheckPw,System.currentTimeMillis()).map {
                      rst =>
                        complete(SuccessRsp())
                    }.recover{
                      case x: Exception =>
                        log.warn(s"insertDb error,${x.getMessage}")
                        complete(ErrorRsp(6, s"insertDb error:${x.getMessage}"))
                    }
                  }
                }
            }.recover{
              case x: Exception =>
                log.warn(s"queryName error,${x.getMessage}")
                complete(ErrorRsp(3, s"queryName error:${x.getMessage}"))
            }
          }
        case Left(error) =>
          complete(ErrorRsp(2,s"$error"))
      }
    }~ (path("quit") & get) {
      invalidateSession {
        complete(SuccessRsp())
      }
    }~ (path("authJudge") & get) {
      optionalUserSession{
        case Some(_)=>complete(SuccessRsp())
        case None=>complete(ErrorRsp(1,"未登录"))
      }
    }
  }
}
