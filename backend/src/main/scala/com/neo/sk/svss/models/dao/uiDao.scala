package com.neo.sk.svss.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import slick.driver.JdbcProfile
import com.neo.sk.svss.models.SlickTables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object uiDao {
  def getUserInfo(userId:Int) = {
    val a = for{
      c <- tFollowinfo.filter(_.followid===userId).length.result
      d <- tFollowinfo.filter(_.befollowedid===userId).length.result

      e <- tVideoinfo.filter(_.vuperid===userId).sortBy(_.vuptime.desc).map(i=>(i.vid,i.vname,i.vclickcount,i.vlikecount)).result

      f <- {tLikevideoinfo join tVideoinfo on { (like, video) =>
        like.videoid === video.vid && like.userid === userId
      }}.sortBy(_._1.liketime.desc).map(i=>(i._2.vid,i._2.vname,i._2.vclickcount,i._2.vlikecount)).result
    }yield (c,d,e,f)
    db.run(a.transactionally)
  }
  def getUserSignature(userId:Int)={
    db.run(tUserinfo.filter(_.uid===userId).map(_.signature).result.headOption)
  }
  def selectUserName(name:String)={
    db.run(tUserinfo.filter(_.uname===name).map(_.uid).result.headOption)
  }
  def updateUserInfoWithName(userId:Int,name:String,sex:Short,signature:String)={
    val a = for{
      b <- tUserinfo.filter(_.uid===userId).map(i=>(i.uname,i.sex,i.signature)).update(name,sex,signature)
      c <- tFollowinfo.filter(_.followid===userId).map(m=>(m.followname,m.followsign)).update(name,signature)
      d <- tFollowinfo.filter(_.befollowedid===userId).map(n=>(n.befollowedname,n.befollowedsign)).update(name,signature)
      e <- tVideocomments.filter(_.commentuserid===userId).map(_.commentusername).update(name)
      f <- tVideoinfo.filter(_.vuperid===userId).map(_.vupername).update(name)
    }yield (b,c,d)
    db.run(a.transactionally)
  }
  def updateUserInfoWithoutName(userId:Int,sex:Short,signature:String)={
    val a = for{
      b <- tUserinfo.filter(_.uid===userId).map(i=>(i.sex,i.signature)).update(sex,signature)
      c <- tFollowinfo.filter(_.followid===userId).map(_.followsign).update(signature)
      d <- tFollowinfo.filter(_.befollowedid===userId).map(_.befollowedsign).update(signature)
    }yield (b,c,d)
    db.run(a.transactionally)
  }
  def getVisitUserInfo(userId:Int,myId:Int)={
    val a = for{
      b <- tUserinfo.filter(_.uid===userId).map(i=>(i.uname,i.sex,i.signature)).result.head
      c <- tFollowinfo.filter(_.followid===userId).length.result
      d <- tFollowinfo.filter(_.befollowedid===userId).length.result

      e <- tVideoinfo.filter(_.vuperid===userId).sortBy(_.vuptime.desc).map(i=>(i.vid,i.vname,i.vclickcount,i.vlikecount)).result

      f <- {tLikevideoinfo join tVideoinfo on { (like, video) =>
        like.videoid === video.vid && like.userid === userId
      }}.sortBy(_._1.liketime.desc).map(i=>(i._2.vid,i._2.vname,i._2.vclickcount,i._2.vlikecount)).result

      g<- tFollowinfo.filter(i=>i.followid===myId && i.befollowedid===userId).map(_.id).result.headOption
    }yield (b,c,d,e,f,g)
    db.run(a.transactionally)
  }
  def queryPw(username: String,password:String) = {
    db.run(tUserinfo.filter(i=>i.uname===username && i.upassword===password).map(i=>(i.uid,i.sex,i.signature)).result)
  }
  def queryName(username:String)={
    db.run(tUserinfo.filter(_.uname===username).map(_.uid).result)
  }
  //增一项e
  def insertDb(username: String,password: String,time:Long) =
    db.run(tUserinfo += rUserinfo(1,username,password,0,"",time))
  //删一项
  def deleteDb(id: Int) =
    db.run(tUserinfo.filter(_.uid === id).delete)
  def getUserSearchResult(content:String)={
    db.run(tUserinfo.filter(_.uname.like(s"%$content%")).map(i=>(i.uid,i.uname,i.signature)).result)
  }
}
