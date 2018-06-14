package com.neo.sk.svss.service


import akka.http.scaladsl.server.Directives.{complete, entity, _}
import com.neo.sk.svss.shared.ptcl.UserProtocol._
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

trait VideoService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)
  val videoRoutes:Route= {
    (path("getFollowVideo") & get) {
      parseUserSession {
        session =>
          dealFutureResult {
            viDao.getFollowVideo(session.userId.toInt).map {
              rst =>
                val list = rst.map(i=>FollowVideoInfo(i._1,i._2,i._3,i._4)).toList
                complete(FollowVideoInfoRsp(list))
            }.recover{
              case x: Exception =>
                log.warn(s"getFollowVideo error,${x.getMessage}")
                complete(FollowVideoInfoRsp(Nil,2,s"getFollowVideo error:${x.getMessage}"))
            }
          }
      }
    }~(path("getSearchResult") & post){
      parseUserSession{
        session=>
          entity(as[Either[Error, getSearchReq]]){
            case Right(q) =>
              dealFutureResult{
                viDao.getSearchResult(q.content).map{
                  rst=>
                    val videoList = rst.map(i=>BaseVideoInfo(i._1,i._2,i._3,i._4)).toList
                    dealFutureResult{
                      uiDao.getUserSearchResult(q.content).map{
                        rst2=>
                          dealFutureResult{
                            followDao.getFollowingId(session.userId.toInt).map{
                              rst3=>
                                val userList=rst2.map{i=>
                                  val followingState=if(i._1==session.userId.toInt) 2 else if(rst3.contains(i._1)) 1 else 0
                                  FollowUserInfo(i._1,i._2,i._3,followingState)
                                }.toList
                                complete(SearchRsp(userList,videoList))
                            }.recover{
                              case x: Exception =>
                                log.warn(s"getFollowingId error,${x.getMessage}")
                                complete(SearchRsp(Nil,Nil,5, s"getFollowingId error:${x.getMessage}"))
                            }
                          }
                      }.recover{
                        case x: Exception =>
                          log.warn(s"getUserSearchResult error,${x.getMessage}")
                          complete(SearchRsp(Nil,Nil,4, s"getUserSearchResult error:${x.getMessage}"))
                      }
                    }
                }.recover{
                  case x: Exception =>
                    log.warn(s"getSearchResult error,${x.getMessage}")
                    complete(SearchRsp(Nil,Nil,3, s"getSearchResult error:${x.getMessage}"))
                }
              }
            case Left(error) =>
              complete(SearchRsp(Nil,Nil,2,s"$error"))
          }
      }
    }~(path("addClick") & post){
      parseUserSession {
        session=>
          entity(as[Either[Error, addClickReq]]){
            case Right(q) =>
              dealFutureResult{
                viDao.addClick(q.id).map{
                  rst=>
                    complete(SuccessRsp())
                }.recover{
                  case x: Exception =>
                    log.warn(s"addClick error,${x.getMessage}")
                    complete(ErrorRsp(3, s"addClick error:${x.getMessage}"))
                }
              }
            case Left(error) =>
              complete(ErrorRsp(2,s"$error"))
          }
      }
    }~(path("getVideoAll") & post){
      parseUserSession{
        session=>
          entity(as[Either[Error, getVideoAllReq]]){
            case Right(q) =>
              dealFutureResult{
                viDao.getVideoInfo(q.id).map{
                  case Some(rst)=>
                    dealFutureResult{
                      viDao.getVideoOther(rst.vuperid,q.id,session.userId.toInt).map{
                        rst2=>
                          val followingState=if(rst.vuperid==session.userId.toInt){
                            2
                          }else if(rst2._1.isEmpty){
                            0
                          }else{
                            1
                          }
                          val likeState=if(rst2._2.isEmpty) 0 else 1
                          val commentList=rst2._3.map(i=>Comment(i._1,i._2,i._3,i._4,i._5,i._6,if(rst2._4.contains(i._1)) 1 else 0)).toList
                          complete(VideoAllRsp(VideoAll(session.userId.toInt,session.name,rst.vname,rst.vuperid,rst.vupername,rst.vtype,rst.vuptime,commentList.length,rst.vlikecount,rst.vclickcount,rst.vdir,followingState,likeState,commentList)))
                      }.recover{
                        case x: Exception =>
                          log.warn(s"getVideoOther error,${x.getMessage}")
                          complete(ErrorRsp(5, s"getVideoOther error:${x.getMessage}"))
                      }
                    }
                  case None=>
                    complete(ErrorRsp(4,"can't find this video"))
                }.recover{
                  case x: Exception =>
                    log.warn(s"getVideoInfo error,${x.getMessage}")
                    complete(ErrorRsp(3, s"getVideoInfo error:${x.getMessage}"))
                }
              }
            case Left(error) =>
              complete(ErrorRsp(2,s"$error"))
          }
      }
    }~(path("addLikeVideo") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, addLikeVideoReq]]) {
          case Right(p) =>
            dealFutureResult {
              likeVideoDao.addLikeVideo(session.userId.toInt,p.id,System.currentTimeMillis()).map {
                rst=>
                  complete(SuccessRsp())
              }.recover {
                case x: Exception =>
                  log.warn(s"addLikeVideo error,${x.getMessage}")
                  complete(ErrorRsp(3, s"addLikeVideo error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }~(path("deleteLikeVideo") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, deleteLikeVideoReq]]) {
          case Right(p) =>
            dealFutureResult {
              likeVideoDao.deleteLikeVideo(session.userId.toInt,p.id).map {
                rst=>
                  complete(SuccessRsp())
              }.recover {
                case x: Exception =>
                  log.warn(s"deleteLikeVideo error,${x.getMessage}")
                  complete(ErrorRsp(3, s"deleteLikeVideo error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }
  }
}



