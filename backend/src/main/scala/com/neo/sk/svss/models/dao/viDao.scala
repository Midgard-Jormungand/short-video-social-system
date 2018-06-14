package com.neo.sk.svss.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.svss.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object viDao {
  def getFollowVideo(id:Int)={
      val a = for{
        i <- tFollowinfo.filter(_.followid===id).map(_.befollowedid).result
        j <- tVideoinfo.filter(_.vuperid.inSet(i)).sortBy(_.vuptime.desc).map(k=>(k.vid,k.vname,k.vupername,k.vuptime)).result
      }yield j
    db.run(a.transactionally)
  }
  def getNewest = {
    db.run(tVideoinfo.sortBy(_.vuptime.desc).map(i=>(i.vid,i.vname,i.vclickcount,i.vlikecount)).result)
  }
  def getLikeMost ={
    db.run(tVideoinfo.sortBy(_.vlikecount.desc).map(i=>(i.vid,i.vname,i.vclickcount,i.vlikecount)).result)
  }
  def getSearchResult(content:String)={
    db.run(tVideoinfo.filter(_.vname.like(s"%$content%")).sortBy(_.vuptime.desc).map(i=>(i.vid,i.vname,i.vclickcount,i.vlikecount)).result)
  }
  def addVideo(vName: String, vUperId:Int, vUperName:String,vType:String,vUptime:Long,vdir:String) =
  {
    db.run(
      tVideoinfo.returning(tVideoinfo.map(_.vid)) += rVideoinfo(1, vName, vUperId,vUperName,vType,vUptime,0,0,vdir)
    )
  }
  def deleteVideo(id:Int)={
    val a = for{
      d <- tVideoinfo.filter(_.vid===id).map(_.vdir).result.head
      i <- tVideoinfo.filter(_.vid===id).delete
      j <- tLikevideoinfo.filter(_.videoid===id).delete
      c <- tVideocomments.filter(_.vid===id).map(_.commentid).result
      b <- tLikecommentinfo.filter(_.commentid.inSet(c)).delete
      k <-  tVideocomments.filter(_.vid===id).delete
      }yield (d,i,j,b,k)
    db.run(a.transactionally)
  }
  def addClick(id:Int)={
    val a = for{
      i <- tVideoinfo.filter(_.vid===id).map(_.vclickcount).result.head
      j <- tVideoinfo.filter(_.vid===id).map(_.vclickcount).update(i+1)
    }yield j
    db.run(a.transactionally)
  }

  def getVideoInfo(vId:Int)={
    db.run(tVideoinfo.filter(_.vid===vId).result.headOption)
  }
  def getVideoOther(uperId:Int,vId:Int,uId:Int)={
    val a = for{
      j <- tFollowinfo.filter(m=>m.followid===uId && m.befollowedid===uperId).result.headOption
      k <- tLikevideoinfo.filter(n=>n.userid===uId && n.videoid===vId).result.headOption
      l <- tVideocomments.filter(_.vid===vId).sortBy(_.commenttime.desc).map(p=>(p.commentid,p.commentuserid,p.commentusername,p.commentcontent,p.likecount,p.commenttime)).result
      q <- tLikecommentinfo.filter(_.uid===uId).map(_.commentid).result
    }yield (j,k,l,q)
    db.run(a.transactionally)
  }
}
