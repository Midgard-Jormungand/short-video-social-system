package com.neo.sk.svss.models.dao
import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.svss.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object likeCommentDao {
  def addCommentLike(uid:Int,commentid:Int)={
    val a = for{
      b <- tLikecommentinfo+=rLikecommentinfo(1,uid,commentid)
      c <- tVideocomments.filter(_.commentid===commentid).map(_.likecount).result.head
      d <- tVideocomments.filter(_.commentid===commentid).map(_.likecount).update(c+1)
    }yield (b,d)
    db.run(a.transactionally)
  }
  def deleteCommentLike(uid:Int,commentid:Int)={
    val a = for{
      b <- tLikecommentinfo.filter(i=>i.uid===uid &&  i.commentid===commentid).delete
      c <- tVideocomments.filter(_.commentid===commentid).map(_.likecount).result.head
      d <- tVideocomments.filter(_.commentid===commentid).map(_.likecount).update(c-1)
    }yield (b,d)
    db.run(a.transactionally)
  }
}
