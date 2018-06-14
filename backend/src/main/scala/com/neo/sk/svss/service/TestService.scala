package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives._
import com.neo.sk.svss.common.Constants.UserRolesType
import com.neo.sk.svss.service.SessionBase.UserSession
import com.neo.sk.svss.shared.ptcl.UserProtocol.SessionUserInfo


/**
  * User: Taoz
  * Date: 8/10/2017
  * Time: 2:55 PM
  */
trait TestService extends BaseService{


  def getAdminSession = (path("getAdminSession") & get & pathEndOrSingleSlash){
    addSession(
      UserSession(SessionUserInfo(UserRolesType.devManager, "admin", "管理员","0","签名"),
        System.currentTimeMillis()).toSessionMap
    ) { ctx =>
      ctx.complete("ok")
    }
  }


  def getMemberSession = (path("getMemberSession") & get & pathEndOrSingleSlash){
    addSession(
      UserSession(SessionUserInfo(UserRolesType.comMember, "100", "测试","1","签名"),
        System.currentTimeMillis()).toSessionMap
    ) { ctx =>
      ctx.complete("ok")
    }
  }

  def cleanSession = (path("cleanSession") & get & pathEndOrSingleSlash){
    invalidateSession{
      complete("ok")
    }
  }

  val testRoutes = pathPrefix("tests") {
    getAdminSession  ~ getMemberSession ~ cleanSession
  }


}
