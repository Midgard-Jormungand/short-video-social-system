package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.components.KBar
import com.neo.sk.svss.frontend.utils.{Http, JsFunc}
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import com.neo.sk.svss.frontend.common.CommonFunction._
import com.neo.sk.svss.shared.ptcl.UserProtocol._
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol}
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx, Var, emptyHTML}

import scala.xml.Node
import org.scalajs.dom
import org.scalajs.dom.html.Input

import concurrent.ExecutionContext.Implicits.global

object Search extends Page {
  override val locationHashString: String = "#/Search"
  private val barNum=Var(0)
  def resetBarNum():Unit={
    barNum:=0
  }
  private val videoList=Var(List.empty[BaseVideoInfo])
  private val userList=Var(List.empty[FollowUserInfo])

  private val videoContent = videoList.map{list=>
    buildVideoContent(list)
  }
  private val userContent = userList.map{list=>
    buildUserContent(list)
  }

  private val searchResContent=barNum.dropRepeats.map{
    case 0=>
      emptyHTML
    case 1=>
      <div>
        <div id="search-result-bar">
          <div class="result-bar-item result-bar-active">
            用户
          </div>
          <div class="result-bar-item" onclick={()=>barNum:=2}>
            视频
          </div>
        </div>
        {userContent}
      </div>
    case 2=>
      <div>
        <div id="search-result-bar">
          <div class="result-bar-item" onclick={()=>barNum:=1}>
            用户
          </div>
          <div class="result-bar-item result-bar-active">
            视频
          </div>
        </div>
        {videoContent}
      </div>
  }
  private def buildVideoContent(list:List[BaseVideoInfo]):Node ={
    if (list != Nil){
      <div id="search-result-video">
        {
        list.map{item=>
          getVideoItem(item)
        }
        }
      </div>
    }else
      <div class="text-center search-nothing">
        没有搜到相关视频
      </div>
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
  private def buildUserContent(list: List[FollowUserInfo]):Node={
    if (list != Nil){
      <div id="search-result-user">
        {
        list.map{item=>
          getUserItem(item)
        }
        }
      </div>
    }else
      <div class="text-center search-nothing">
        没有搜到相关用户
      </div>
  }
  private def getUserItem(item:FollowUserInfo):Node={
    val userName=item.followState match {
      case 2=>
        <div class="user-item-name" onclick={()=>dom.window.location.hash="#/Personal"}>
          {item.name}
        </div>
      case _=>
        <div class="user-item-name" onclick={()=>dom.window.location.hash={s"#/Visit/${item.userId}"}}>
          {item.name}
        </div>
    }
    val followButton=item.followState match{
      case 0=>
        <div class="user-item-right">
          <button class="btn btn-primary" onclick={()=>addFollow(item.userId,item.name,item.signature)}>+关注</button>
        </div>
      case 1=>
        <div class="user-item-right">
          <button class="btn btn-default" onclick={()=>deleteFollow(item.userId)}>已关注</button>
        </div>
      case _=>
        emptyHTML
    }
    <div class="user-item">
      <div class="user-item-left">
        {userName}
        <div class="user-item-signature">
          {if (item.signature != "") item.signature else "暂无个性签名"}
        </div>
      </div>
      {followButton}
    </div>
  }
  private def addFollow(id: Int, name: String, sign:String):Unit={
    val url = Routes.SvssRoute.addFollow
    val data = UserProtocol.addFollowReq(id,name,sign).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          userList.update{list=>list.map{i=>if(i.userId==id) FollowUserInfo(i.userId,i.name,i.signature,1) else i}}
          Personal.addFollowingCount()
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
  private def deleteFollow(id:Int):Unit={
    val url = Routes.SvssRoute.deleteFollow
    val data = UserProtocol.deleteFollowReq(id).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          userList.update{list=>list.map{i=>if(i.userId==id) FollowUserInfo(i.userId,i.name,i.signature,0) else i}}
          Personal.deleteFollowingCount()
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
  private def getSearchResult():Unit={
    val searchContent=dom.document.getElementById("search-content").asInstanceOf[Input].value
    if(searchContent==""){
      ()
    }else{
      val url = Routes.SvssRoute.getSearchResult
      val data= getSearchReq(searchContent).asJson.noSpaces
      Http.postJsonAndParse[SearchRsp](url,data).map{
        case Right(rsp) =>
          if(rsp.errCode == 0) {
            barNum:=1
            videoList := rsp.videoList
            userList :=rsp.userList
          }else if(rsp.errCode==1) {
            dom.window.location.hash = "#/Login"
          }else if(rsp.errCode==2){
            JsFunc.alert(s"getSearchResult server error happen: ${rsp.msg}")
          }else {
            JsFunc.alert(s"${rsp.msg}")
          }
        case Left(error) =>
          JsFunc.alert(s"getSearchResult internal error: $error")
      }
    }
  }
  override def render: Node = {
    authJudge()
    <div>
      <div id="search-bar">
        <div style="width:85%;">
          <input id="search-content" class="form-control search-input" type="text" placeholder="输入搜索内容"/>
        </div>
        <div class="search-button" onclick={()=>getSearchResult()}>
          搜索
        </div>
      </div>
      {searchResContent}
      {KBar.render}
    </div>
  }
}