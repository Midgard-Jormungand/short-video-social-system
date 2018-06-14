package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.utils.{Http, JsFunc, TimeTool}
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol, VideoProtocol}
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx, Var, emptyHTML}

import scala.xml.Node
import org.scalajs.dom

import org.scalajs.dom.html.Input

import concurrent.ExecutionContext.Implicits.global
class Video(id:String) extends Page{
  override val locationHashString: String = "#/Video"
  private var currentCommentId=0
  private var myId= 0
  private var myName=""
  private val src =Var("")
  private val vName = Var("")
  private var vName1 =""
  private val uperId=Var(0)
  private val uperName=Var("")
  private val uperControl=uperId.zip(uperName)
  private val followingState:Var[Int]=Var(0)
  private val likeState:Var[Int]=Var(0)
  private val upTime=Var("")
  private val vType=Var("")
  private val vLikeCount:Var[Int]=Var(0)
  private var vLikeCount1 = 0
  private val vClickCount=Var(0)
  private var vClickCount1 = 0
  private val commentCount:Var[Int]=Var(0)
  private val commentList:Var[List[Comment]]=Var(Nil)
  private val likeButton:Rx[Node]=likeState.map(i=>
    if(i==0)
      <div class="text-center"  style="width:10%;height:45px;line-height: 45px;" onclick={()=>addLike()}>
        <img src="/svss/static/img/empty_heart.png" style="width:24px;height: 24px"/>
      </div>
    else
      <div class="text-center"  style="width:10%;height:45px;line-height: 45px;" onclick={()=>deleteLike()}>
        <img src="/svss/static/img/heart.png" style="width:24px;height: 24px"/>
      </div>
  )
  private val uperContent:Rx[Node]=followingState.map(i=>
    if (i==0)
      <div class="video-uper" style="width: 80%;">
        {uperControl.map(j=> <div class="uper" onclick={()=>dom.window.location.hash={s"#/Visit/${j._1}"}}>{j._2}</div>)}
        {uperControl.map(k=> <button type="button" class="btn btn-primary btn-xs" onclick={()=>addFollow(k._1,k._2)}>+关注</button>)}
      </div>
    else if(i==1)
      <div class="video-uper" style="width: 80%;">
        {uperControl.map(l=> <div class="uper" onclick={()=>dom.window.location.hash={s"#/Visit/${l._1}"}}>{l._2}</div>)}
        {uperId.map(m=> <button type="button" class="btn btn-default btn-xs" onclick={()=>deleteFollow(m)}>已关注</button>)}
      </div>
    else
      <div class="video-uper" style="width: 80%;">
        <div class="uper" onclick={()=>dom.window.location.hash="#/Personal"}>{uperName}</div>
      </div>
  )
  private val videoTopBar =
    <div id="video-top-bar">
      <div class="text-center" style="width:10%;height:45px;line-height: 45px;" onclick={()=>dom.window.history.go(-1)}>
        <span class="glyphicon glyphicon-menu-left" aria-hidden="true"></span>
      </div>
      {likeButton}
      {uperContent}
    </div>
  private val videoComment=commentList.map(list=>
    buildCommentContent(list)
  )
  private val videoContent=
    <div id="video-content">
      <div class="video-player">
        <video src={src} controls="controls" poster={s"/svss/static/vImg/$id.jpg"} preload="auto" style="width:100%"></video>
      </div>
      <div class="video-info">
        <div class="video-intro">
          {vName}
        </div>
        <div>
          <small style="display:inline-block;width:10%;">{vType}</small>
          <small style="display:inline-block;">{upTime}</small>
        </div>
        <div>
          <span class="glyphicon glyphicon-expand" aria-hidden="true"></span>
          <span style="display:inline-block;width:20%;"> {vClickCount}</span>
          <span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span>
          <span> {vLikeCount}</span>
        </div>
        <div class="comment-count">
          {commentCount}评论
        </div>
      </div>
      {videoComment}
    </div>
  private val videoBottomBar=
    <div id="video-bottom-bar">
      <div style="width:90%;">
        <input class="comment-input" id="sendContent" type="text" placeholder="说点什么..."/>
      </div>
      <div class="text-center" style="width:10%;height: 40px;line-height: 40px" onclick={()=>sendComment()}>
        <span class="glyphicon glyphicon-send" aria-hidden="true"></span>
      </div>
    </div>
  private def getVideoAll():Unit={
    val url=Routes.SvssRoute.getVideoAll
    val data= getVideoAllReq(id.toInt).asJson.noSpaces
    Http.postJsonAndParse[VideoAllRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          val data = rsp.data
          myId  = data.myId
          myName = data.myName
          src:="/svss/static/video/"+id+"."+data.dir
          vName:=data.vname
          vName1=data.vname
          uperId:=data.uperId
          uperName:=data.uperName
          vType:=data.vtype
          upTime:=TimeTool.getTimeToNow2(data.uptime)
          vLikeCount:=data.likeCount
          vLikeCount1=data.likeCount
          vClickCount:=data.clickCount
          vClickCount1=data.clickCount
          commentCount:=data.commentCount
          commentList:=data.comment
          followingState:=data.followingState
          likeState:=data.likeState
        }else if(rsp.errCode ==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2) {
          JsFunc.alert(s"some server error happen: ${rsp.msg}")
        }else{
            JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"some internal error happen: $error")
    }
  }
  private def buildCommentContent(list:List[Comment]):Node ={
    if (list != Nil){
      <div class="video-comment">
        {
        list.map{item=>
          val imgSrc=if(item.likeState==0){
            <div class="comment-img"><img src="/svss/static/img/empty_heart.png" style="width:40%" onclick={()=>addCommentLike(item.commentId)}/></div>
          }else{
            <div class="comment-img"><img src="/svss/static/img/heart.png" style="width:45%"  onclick={()=>deleteCommentLike(item.commentId)}/></div>
          }
          val commentContent= if(item.id != myId){
            <div class="comment-left">
              <div class="comment-user" onclick={()=>dom.window.location.hash={s"#/Visit/${item.id}"}}>{item.name}</div>
              <div>
                <div class="comment-content">{item.content}</div>
                <div class="comment-time">{TimeTool.getTimeToNow2(item.time)}</div>
              </div>
            </div>
          }else{
            <div class="comment-left">
              <div class="comment-user" onclick={()=>dom.window.location.hash="#/Personal"}>{item.name}</div>
              <div data-toggle="modal" data-target="#myModal" onclick={()=>currentCommentId=item.commentId}>
                <div class="comment-content">{item.content}</div>
                <div class="comment-time">{TimeTool.getTimeToNow2(item.time)}</div>
              </div>
            </div>
          }
          <div class="comment-item">
            {commentContent}
            <div class="comment-right">
              {imgSrc}
              <div class="comment-like">{item.likeCount}</div>
            </div>
          </div>
        }
        }
      </div>
    }else
      emptyHTML
  }
  private def addLike():Unit={
    val url=Routes.SvssRoute.addLikeVideo
    val data=addLikeVideoReq(id.toInt).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          likeState:=1
          vLikeCount.update(i=>i+1)
          vLikeCount1 += 1
          Personal.addLike(BaseVideoInfo(id.toInt,vName1,vClickCount1,vLikeCount1))
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"addvideo server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"addvideo internal error happen: $error")
    }
  }
  private def deleteLike():Unit={
    val url=Routes.SvssRoute.deleteLikeVideo
    val data=deleteLikeVideoReq(id.toInt).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          likeState:=0
          vLikeCount.update(i=>i-1)
          vLikeCount1 -= 1
          Personal.deleteLike(id.toInt)
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"deletevideo server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"deletevideo internal error happen: $error")
    }
  }
  private def addFollow(uperId:Int,uperName:String):Unit={
    val url = Routes.SvssRoute.addFollow2
    val data = UserProtocol.addFollowReq2(uperId,uperName).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followingState:=1
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"addFollow server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"addFollow internal error happen: $error")
    }
  }
  private def deleteFollow(uperId:Int):Unit={
    val url = Routes.SvssRoute.deleteFollow
    val data = UserProtocol.deleteFollowReq(uperId).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followingState:=0
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"some server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"some internal error happen: $error")
    }
  }
  private def addCommentLike(commentId:Int):Unit={
    val url = Routes.SvssRoute.addCommentLike
    val data = VideoProtocol.addCommentLikeReq(commentId).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          commentList.update{list=>
            list.map(i=>if(i.commentId==commentId) Comment(i.commentId,i.id,i.name,i.content,i.likeCount+1,i.time,1) else i)
          }
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"addCommentLike server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"addCommentLike internal error happen: $error")
    }
  }
  private def deleteCommentLike(commentId:Int):Unit={
    val url = Routes.SvssRoute.deleteCommentLike
    val data = VideoProtocol.deleteCommentLikeReq(commentId).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          commentList.update{list=>
            list.map(i=>if(i.commentId==commentId) Comment(i.commentId,i.id,i.name,i.content,i.likeCount-1,i.time,0) else i)
          }
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"deleteCommentLike server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"deleteCommentLike internal error happen: $error")
    }
  }
  private def sendComment():Unit={
    val sendContent=dom.document.getElementById("sendContent").asInstanceOf[Input].value
    if (sendContent==""){
      ()
    }else{
      val url=Routes.SvssRoute.addComment
      val data=VideoProtocol.addCommentReq(id.toInt,sendContent).asJson.noSpaces
      Http.postJsonAndParse[CommentRsp](url,data).map{
        case Right(rsp) =>
          if(rsp.errCode == 0) {
            commentList.update(i=>Comment(rsp.commentId,myId,myName,sendContent,0,System.currentTimeMillis(),0)::i)
            commentCount.update(_ + 1)
          }else if(rsp.errCode==1) {
            dom.window.location.hash = "#/Login"
          }else if(rsp.errCode==2){
            JsFunc.alert(s"addComment server error happen: ${rsp.msg}")
          }else {
            JsFunc.alert(s"${rsp.msg}")
          }
        case Left(error) =>
          JsFunc.alert(s"addComment internal error happen: $error")
      }
    }
  }
  private def deleteComment(commentId:Int):Unit={
    val url=Routes.SvssRoute.deleteComment
    val data=VideoProtocol.deleteCommentReq(commentId).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          commentList.update(i=>i.filter(_.commentId != commentId))
          commentCount.update(_ - 1)
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"deleteComment server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"deleteComment internal error happen: $error")
    }
  }


  override def render: Node = {
    getVideoAll()
    <div>
      {videoTopBar}
      {videoContent}
      {videoBottomBar}
      <div class="modal" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div style="height: 100%;width: 100%;display: flex;align-items: center">
          <div class="modal-content" style="width:100%;margin:0 10px">
            <div class="model-button" data-dismiss="modal" onclick={()=>deleteComment(currentCommentId)}>删除</div>
            <div class="model-button" data-dismiss="modal">取消</div>
          </div><!-- /.modal-content -->
        </div><!-- /.modal -->
      </div>
    </div>
  }
}