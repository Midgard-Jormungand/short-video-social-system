package com.neo.sk.svss.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.svss.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object likeVideoDao {
  def addLikeVideo(uid:Int,vid:Int,time:Long)={
    val a = for{
      b <- tLikevideoinfo+=rLikevideoinfo(1,uid,vid,time)
      c <- tVideoinfo.filter(_.vid===vid).map(_.vlikecount).result.head
      d <- tVideoinfo.filter(_.vid===vid).map(_.vlikecount).update(c+1)
    }yield (b,c,d)
    db.run(a.transactionally)
  }
  def deleteLikeVideo(uid:Int,vid:Int)={
    val a = for{
      b <- tLikevideoinfo.filter(i=>i.userid===uid &&  i.videoid===vid).delete
      c <- tVideoinfo.filter(_.vid===vid).map(_.vlikecount).result.head
      d <- tVideoinfo.filter(_.vid===vid).map(_.vlikecount).update(c-1)
    }yield (b,c,d)
    db.run(a.transactionally)
  }
}
