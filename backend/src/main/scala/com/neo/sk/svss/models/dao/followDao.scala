package com.neo.sk.svss.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.svss.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object followDao {
  def getFollowing(id:Int)={
    db.run(tFollowinfo.filter(_.followid===id).sortBy(_.followtime.desc).map(i=>(i.befollowedid,i.befollowedname,i.befollowedsign)).result)
  }
  def getFollower(id:Int)={
    val a = for{
      i <- tFollowinfo.filter(_.followid===id).map(_.befollowedid).result
      j <- tFollowinfo.filter(_.befollowedid===id).sortBy(_.followtime.desc).map(k=>(k.followid,k.followname,k.followsign)).result
    }yield (i,j)
    db.run(a)
  }
  def getFollowingId(id:Int)={
    db.run(tFollowinfo.filter(_.followid===id).map(_.befollowedid).result)
  }
  def getVisitFollowing(uid:Int,visitId:Int)={
    val a = for{
      i <- tFollowinfo.filter(_.followid===uid).map(_.befollowedid).result
      j <- tFollowinfo.filter(_.followid===visitId).sortBy(_.followtime.desc).map(k=>(k.befollowedid,k.befollowedname,k.befollowedsign)).result
    }yield (i,j)
    db.run(a)
  }
  def getVisitFollower(uid:Int,visitId:Int)={
    val a = for{
      i <- tFollowinfo.filter(_.followid===uid).map(_.befollowedid).result
      j <- tFollowinfo.filter(_.befollowedid===visitId).sortBy(_.followtime.desc).map(k=>(k.followid,k.followname,k.followsign)).result
    }yield (i,j)
    db.run(a)
  }
  def addFollow(followId:Int,followName:String,followSign:String,beFollowedId:Int,beFollowedName:String,beFollowedSign:String,time:Long)={
    db.run(tFollowinfo += rFollowinfo(1,followId,followName,followSign,beFollowedId,beFollowedName,beFollowedSign,time))
  }
  def deleteFollow(followId:Int,beFollowedId:Int)={
    db.run(tFollowinfo.filter(i=>i.followid===followId && i.befollowedid===beFollowedId).delete)
  }
  def addFollow2(followId:Int,followName:String,followSign:String,beFollowedId:Int,beFollowedName:String,time:Long)={
    val a = for{
      i <- tUserinfo.filter(_.uid===beFollowedId).map(_.signature).result.headOption
      j <- tFollowinfo += rFollowinfo(1,followId,followName,followSign,beFollowedId,beFollowedName,i.getOrElse(""),time)
    }yield j
    db.run(a.transactionally)
  }
}
