package com.neo.sk.svss.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Directive1, RequestContext}
import akka.http.scaladsl.server.directives.BasicDirectives
import com.neo.sk.svss.common.AppSettings
import com.neo.sk.svss.common.Constants.UserRolesType
import com.neo.sk.svss.shared.ptcl.ErrorRsp
import com.neo.sk.svss.shared.ptcl.UserProtocol.SessionUserInfo
import com.neo.sk.utils.SessionSupport
import org.slf4j.LoggerFactory
import io.circe.generic.auto._
import com.neo.sk.utils.CirceSupport

/**
  * User: Taoz
  * Date: 12/4/2016
  * Time: 7:57 PM
  */

object SessionBase extends CirceSupport{
  val log = LoggerFactory.getLogger(this.getClass)

  val SessionTypeKey = "STKey"

  object SessionKeys {
    val sessionType = "svss_session"
    val userType = "svss_userType"
    val userId = "svss_userId"
    val name = "svss_name"
    val sex = "svss_sex"
    val signature ="svss_signature"
    val timestamp = "svss_timestamp"
  }

  case class UserSession(
                           userInfo: SessionUserInfo,
                           time: Long
                         ) {
    def toSessionMap: Map[String, String] = {
      Map(
        SessionTypeKey -> SessionKeys.sessionType,
        SessionKeys.userType -> userInfo.userType,
        SessionKeys.userId -> userInfo.userId,
        SessionKeys.name -> userInfo.name,
        SessionKeys.sex -> userInfo.sex,
        SessionKeys.signature -> userInfo.signature,
        SessionKeys.timestamp -> time.toString
      )
    }
  }

}

trait SessionBase extends SessionSupport {

  import SessionBase._

  override val sessionEncoder = SessionSupport.PlaySessionEncoder
  override val sessionConfig = AppSettings.sessionConfig
  private val timeout = 24 * 60 * 60 * 1000
  private val log = LoggerFactory.getLogger(this.getClass)

  implicit class SessionTransformer(sessionMap: Map[String, String]) {
    def toUserSession:Option[UserSession] = {
      //      log.debug(s"toAdminSession: change map to session, ${sessionMap.mkString(",")}")
      try {
        if (sessionMap.get(SessionTypeKey).exists(_.equals(SessionKeys.sessionType))) {
          if(sessionMap(SessionKeys.timestamp).toLong - System.currentTimeMillis() > timeout){
            None
          }else {
            Some(UserSession(
              SessionUserInfo(
                sessionMap(SessionKeys.userType),
                sessionMap(SessionKeys.userId),
                sessionMap(SessionKeys.name),
                sessionMap(SessionKeys.sex),
                sessionMap(SessionKeys.signature)
              ),
              sessionMap(SessionKeys.timestamp).toLong
            ))
          }
        } else {
          log.debug("no session type in the session")
          None
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          log.warn(s"toAdminSession: ${e.getMessage}")
          None
      }
    }
  }
  protected val optionalUserSession: Directive1[Option[UserSession]] = optionalSession.flatMap {
    case Right(sessionMap) => BasicDirectives.provide(sessionMap.toUserSession)
    case Left(error) =>
      log.debug(error)
      BasicDirectives.provide(None)
  }
  private def loggingAction: Directive[Tuple1[RequestContext]] = extractRequestContext.map { ctx =>
    log.info(s"Access uri: ${ctx.request.uri} from ip ${ctx.request.uri.authority.host.address}.")
    ctx
  }

  def noSessionError(message:String = "no session") = ErrorRsp(1,s"$message")

  //管理员
  def adminAuth(f: SessionUserInfo => server.Route) = loggingAction { ctx =>
    optionalUserSession {
      case Some(session) =>
        if(session.userInfo.userType == UserRolesType.devManager){
          f(session.userInfo)
        } else{
          complete(noSessionError("you don't have right."))
        }

      case None =>
        complete(noSessionError())
    }
  }

  //会员
  def memberAuth(f: SessionUserInfo => server.Route) = loggingAction { ctx =>
    optionalUserSession {
      case Some(session) =>
        if(session.userInfo.userType == UserRolesType.comMember){
          f(session.userInfo)
        } else{
          complete(noSessionError("you don't have right."))
        }

      case None =>
        complete(noSessionError())
    }
  }


  def parseUserSession(f: SessionUserInfo => server.Route) = loggingAction { ctx =>
    optionalUserSession {
      case Some(session) =>
        f(session.userInfo)

      case None =>
        redirect("/svss",StatusCodes.SeeOther)
    }
  }

}
