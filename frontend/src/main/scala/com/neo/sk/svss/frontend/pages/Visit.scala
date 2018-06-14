package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.common.CommonFunction.clickVideo
import com.neo.sk.svss.frontend.components.KBar
import com.neo.sk.svss.frontend.utils.{Http, JsFunc}
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import com.neo.sk.svss.shared.ptcl.UserProtocol._
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol}
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx, Var, emptyHTML}

import scala.xml.Node
import org.scalajs.dom

import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Visit(id:Int) extends Page {
  override val locationHashString: String = "#/Visit"
  private val homeState:Var[Int]=Var(0)

  private val name:Var[String]= Var("")
  private val sex:Var[Short]=Var(0)
  private val signature:Var[String]=Var("")

  private var name1=""
  private var signature1=""

  private val followingState=Var(0)
  private val followingCount=Var(0)
  private val followerCount=Var(0)

  private val upCount=Var(0)
  private val likeCount=Var(0)

  private val upVideoList=Var(List.empty[BaseVideoInfo])
  private val likeVideoList=Var(List.empty[BaseVideoInfo])

  private val upVideoContent = upVideoList.map{list=>
    buildVideoContent(list)
  }
  private val likeVideoContent = likeVideoList.map{list=>
    buildVideoContent(list)
  }
  private val userHome=
    <div id="visit-content">
      <div id="user-info">
        <div class="user-name">
          <div class="user-name-left">
            <div style="font-size: 24px">{name}</div>
            {
            sex.map{
              case 0=>
                emptyHTML
              case 1=>
                <div class="user-sex-man">
                  <img class="sex-image" src="/svss/static/img/man.png" alt="男"/>
                </div>
              case 2=>
                <div class="user-sex-woman">
                  <img class="sex-image" src="/svss/static/img/woman.png" alt="女"/>
                </div>
            }
            }
          </div>
        </div>
        <div class="user-sign">
          {signature.map(i=>if(i != "") i else "暂无个性签名")}
        </div>
        <div class="user-follow-count">
          <div style="margin-right: 16px" onclick={()=>dom.window.location.hash={s"#/Following/$id"}}><strong>{followingCount} 关注</strong></div>
          <div onclick={()=>dom.window.location.hash={s"#/Follower/$id"}}><strong>{followerCount} 粉丝</strong></div>
        </div>
      </div>
      <div id="user-bar">
        <div class={homeState.map(m=>if(m==0) "user-bar-item user-bar-active" else "user-bar-item")} onclick={()=>homeState:=0}>
          作品{upCount}
        </div>
        <div class={homeState.map(n=>if(n==1) "user-bar-item user-bar-active" else "user-bar-item")} onclick={()=>homeState:=1}>
          喜欢{likeCount}
        </div>
      </div>
      {homeState.dropRepeats.map(p=>if(p==0) <div>{upVideoContent}</div> else <div>{likeVideoContent}</div>)}
    </div>
  private def buildVideoContent(list:List[BaseVideoInfo]):Node ={
    if (list != Nil){
      <div id="user-video">
        {
        list.map{item=>
          getVideoItem(item)
        }
        }
      </div>
    }else
      emptyHTML
  }
  private def getVideoItem(item:BaseVideoInfo):Node={
    <div class="half-video-item">
      <img class="half-video-image" src={s"/svss/static/vImg/${item.vId}.jpg"} alt="视频缩略图" onclick={()=>clickVideo(item.vId)}/>
      <div class="half-video-info">
        <div class="half-video-name">{s"${item.vName}"}</div>
        <div class="half-video-other">
          <div><span class="glyphicon glyphicon-expand" aria-hidden="true"></span><span> {s"${item.vClickCount}"}</span></div>
          <div><span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span><span> {s"${item.vLikeCount}"}</span></div>
        </div>
      </div>
    </div>
  }
  private def getVisitUserInfo()={
    val url = Routes.SvssRoute.getUserInfo
    val data = getVisitInfoReq(id).asJson.noSpaces
    Http.postJsonAndParse[VisitUserInfoRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          val userInfo=rsp.userInfo
          name:=userInfo.name
          sex:=userInfo.sex
          signature:=userInfo.signature

          name1=userInfo.name
          signature1=userInfo.signature

          followingCount:=userInfo.followingCount
          followerCount:=userInfo.followerCount

          upVideoList:=userInfo.upList
          upCount:=userInfo.upList.length

          likeVideoList:=userInfo.likeList
          likeCount:=userInfo.likeList.length

          followingState:=rsp.followingState
        }else if(rsp.errCode ==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"getVisitUserInfo server error happen: ${rsp.msg}")
        }else{
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"getVisitUserInfo internal error happen: $error")
    }.onComplete{
      case Success(_)=>
        ()
      case Failure(error)=>
        println("An error has occured: " + error.getMessage)
    }
  }
  private def addFollow(name:String,signature:String):Unit={
    val url = Routes.SvssRoute.addFollow
    val data = UserProtocol.addFollowReq(id,name,signature).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followerCount.update(_ + 1)
          followingState:=1
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
  private def deleteFollow():Unit={
    val url = Routes.SvssRoute.deleteFollow
    val data = UserProtocol.deleteFollowReq(id).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followerCount.update(_ - 1)
          followingState:=0
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"deleteFollow server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"deleteFollow internal error happen: $error")
    }
  }
  private val followButton:Rx[Node]= followingState.map{i=>
    if (i==0)
      <div style="margin-right: 10px">
        <button class="btn btn-warning btn-sm" onclick={()=>addFollow(name1,signature1)}>+关注</button>
      </div>
       else
      <div style="margin-right: 10px">
        <button class="btn btn-default btn-sm" onclick={()=>deleteFollow()}>已关注</button>
      </div>
  }
  private val visitBar=
    <div id="visit-bar">
      <div class="text-center" style="width:10%;height:45px;line-height: 45px;" onclick={()=>dom.window.history.back()}>
        <span class="glyphicon glyphicon-menu-left" aria-hidden="true"></span>
      </div>
      {followButton}
    </div>
  override def render: Node = {
    getVisitUserInfo()
    <div>
      {visitBar}
      {userHome}
    </div>
  }
}