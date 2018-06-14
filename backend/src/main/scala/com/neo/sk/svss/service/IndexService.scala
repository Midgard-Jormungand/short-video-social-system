package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives.{complete, _}
import com.neo.sk.svss.shared.ptcl._
import io.circe.generic.auto._
import io.circe.Error
import akka.http.scaladsl.server.Route
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.svss.models.dao._

trait IndexService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)

  val indexRoutes:Route= {
    (path("getIndexNew") & get){
      parseUserSession {
        session =>
          dealFutureResult {
            viDao.getNewest.map {
              rst =>
                val list=rst.map(i=>BaseVideoInfo(i._1,i._2,i._3,i._4)).toList
                complete(BaseVideoInfoRsp(list))
            }.recover{
              case x: Exception =>
                log.warn(s"getClickMost error,${x.getMessage}")
                complete(BaseVideoInfoRsp(Nil,2, s"getClickMost error:${x.getMessage}"))
            }
          }
      }
    }~ (path("getIndexLike") & get){
      parseUserSession {
        session=>
          dealFutureResult {
            viDao.getLikeMost.map {
              rst =>
                val list=rst.map(i=>BaseVideoInfo(i._1,i._2,i._3,i._4)).toList
                complete(BaseVideoInfoRsp(list))
            }.recover{
              case x: Exception =>
                log.warn(s"getLikeMost error,${x.getMessage}")
                complete(BaseVideoInfoRsp(Nil,2, s"getLikeMost error:${x.getMessage}"))
            }
          }
      }
    }
  }
}



