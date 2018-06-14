package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives.{complete, entity, _}
import com.neo.sk.svss.shared.ptcl.UserProtocol._
import com.neo.sk.svss.shared.ptcl._
import io.circe.generic.auto._
import io.circe.Error
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.svss.models.dao._

trait FollowService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)
  val followRoutes:Route= {
   (path("addFollow") & post) {
      parseUserSession {
        session =>
          entity(as[Either[Error, addFollowReq]]) {
            case Right(q) =>
              dealFutureResult{
                followDao.addFollow(session.userId.toInt,session.name,session.signature,q.id,q.name,q.sign,System.currentTimeMillis()).map {
                  rst =>
                    complete(SuccessRsp())
                }.recover{
                  case x: Exception =>
                    log.warn(s"addFollow error,${x.getMessage}")
                    complete(ErrorRsp(5, s"addFollow error:${x.getMessage}"))
                }
              }
            case Left(error) =>
              complete(ErrorRsp(2,s"$error"))
          }
      }
    }~ (path("deleteFollow") & post) {
      parseUserSession {
        session=>
          entity(as[Either[Error, deleteFollowReq]]) {
            case Right(q) =>
              dealFutureResult {
                followDao.deleteFollow(session.userId.toInt,q.id).map {
                  rst =>
                    complete(SuccessRsp())
                }.recover{
                  case x: Exception =>
                    log.warn(s"deleteFollow error,${x.getMessage}")
                    complete(ErrorRsp(3, s"deleteFollow error:${x.getMessage}"))
                }
              }
            case Left(error) =>
              complete(ErrorRsp(2,s"$error"))
          }
      }
    }~(path("addFollow2") & post) {
     parseUserSession {
       session =>
         entity(as[Either[Error, addFollowReq2]]) {
           case Right(q) =>
             dealFutureResult{
               followDao.addFollow2(session.userId.toInt,session.name,session.signature,q.id,q.name,System.currentTimeMillis()).map {
                 rst =>
                   complete(SuccessRsp())
               }.recover{
                 case x: Exception =>
                   log.warn(s"addFollow error,${x.getMessage}")
                   complete(ErrorRsp(5, s"addFollow error:${x.getMessage}"))
               }
             }
           case Left(error) =>
             complete(ErrorRsp(2,s"$error"))
         }
     }
   }~ (path("getFollowing") & get) {
     parseUserSession{
       session =>
         dealFutureResult {
           followDao.getFollowing(session.userId.toInt).map {
             rst =>
               val list = rst.map(i=>FollowUserInfo(i._1,i._2,i._3,1)).toList
               complete(FollowUserInfoRsp(list))
           }.recover {
             case x: Exception =>
               log.warn(s"getFollowing error,${x.getMessage}")
               complete(ErrorRsp(2, s"getFollowing error:${x.getMessage}"))
           }
         }
     }
   }~ (path("getFollower") & get) {
     parseUserSession {
       session =>
         dealFutureResult {
           followDao.getFollower(session.userId.toInt).map {
             rst =>
               val list= rst._2.map{i=>FollowUserInfo(i._1,i._2,i._3,if (rst._1.contains(i._1)) 1 else 0)}.toList
               complete(FollowUserInfoRsp(list))
           }.recover {
             case x: Exception =>
               log.warn(s"getFollower error,${x.getMessage}")
               complete(ErrorRsp(2, s"getFollower error:${x.getMessage}"))
           }
         }
     }
   }~ (path("getFollowing") & post){
     parseUserSession{
       session=>
         entity(as[Either[Error, getVisitInfoReq]]){
           case Right(q) =>
             dealFutureResult {
               followDao.getVisitFollowing(session.userId.toInt,q.id).map {
                 rst =>
                   val list = rst._2.map{i=>FollowUserInfo(i._1,i._2,i._3,if(rst._1.contains(i._1)) 1 else if(i._1 != session.userId.toInt) 0 else 2)}.toList
                   complete(FollowUserInfoRsp(list))
               }.recover {
                 case x: Exception =>
                   log.warn(s"getFollowing&post error,${x.getMessage}")
                   complete(ErrorRsp(3, s"getFollowing error:${x.getMessage}"))
               }
             }
           case Left(error) =>
             complete(ErrorRsp(2,s"$error"))
         }
     }
   }~ (path("getFollower") & post){
     parseUserSession{
       session=>
         entity(as[Either[Error, getVisitInfoReq]]){
           case Right(q) =>
             dealFutureResult {
               followDao.getVisitFollower(session.userId.toInt,q.id).map {
                 rst =>
                   val list = rst._2.map{i=>FollowUserInfo(i._1,i._2,i._3,if(rst._1.contains(i._1)) 1 else if(i._1 != session.userId.toInt) 0 else 2)}.toList
                   complete(FollowUserInfoRsp(list))
               }.recover {
                 case x: Exception =>
                   log.warn(s"getFollower&post error,${x.getMessage}")
                   complete(ErrorRsp(3, s"getFollower error:${x.getMessage}"))
               }
             }
           case Left(error) =>
             complete(ErrorRsp(2,s"$error"))
         }
     }
   }
  }
}



