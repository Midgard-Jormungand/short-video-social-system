package com.neo.sk.svss.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.svss.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object commentDao {
  def addComment(vid:Int,uId:Int,uName:String,content:String,time:Long)={
    db.run(tVideocomments.returning(tVideocomments.map(_.commentid))+= rVideocomments(1,vid,uId,uName,content,0,time))
  }
  def deleteComment(commentId:Int)={
    db.run(tVideocomments.filter(_.commentid===commentId).delete)
  }
}

