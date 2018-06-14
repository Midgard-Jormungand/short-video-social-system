package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.common.CommonFunction._
import com.neo.sk.svss.frontend.components._
import com.neo.sk.svss.frontend.utils.{Http, JsFunc, TimeTool}
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx, Var, emptyHTML}

import scala.xml.Node
import org.scalajs.dom

import concurrent.ExecutionContext.Implicits.global

object Follow extends Page {
  override val locationHashString: String = "#/Follow"
  private val videoList=Var(List.empty[FollowVideoInfo])
  private var getState=0
  private val videoContent:Rx[Node] = videoList.map{list=>
    buildContent(list)
  }
  private def buildContent(list:List[FollowVideoInfo]):Node ={
    if (list != Nil){
      <div id="follow-video-content">
        {
        list.map{item=>
          getItem(item)
        }
        }
      </div>
    }else
      emptyHTML
  }
  private def getItem(item:FollowVideoInfo):Node={
    <div class="half-video-item">
      <img class="half-video-image" src={s"/svss/static/vImg/${item.vId}.jpg"} alt="Error" onclick={()=>clickVideo(item.vId)}/>
      <div class="half-video-info">
        <div class="half-video-name">{s"${item.vName}"}</div>
        <div class="follow-video-other">
          <div class="follow-video-user">{s"${item.uperName}"}</div>
          <div>{TimeTool.getTimeToNow2(item.upTime)}</div>
        </div>
      </div>
    </div>
  }
  private def getFollowVideo():Unit={
    val url = Routes.SvssRoute.getFollowVideo
    Http.getAndParse[FollowVideoInfoRsp](url).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          videoList := rsp.FolllowVideos
        }else if(rsp.errCode==1){
          dom.window.location.hash = "#/Login"
        }else{
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"getFollowVideo internal error: $error")
    }
  }
  private def init()={
    if(getState==0){
      getFollowVideo()
      getState=1
    }
    else
      authJudge()
  }
  override def render: Node = {
    init()
    <div>
      <div id="follow-video-bar">
        <div class="follow-video-title">
          关注播单
        </div>
        <div class="follow-video-refresh" onclick={()=>getFollowVideo()}>
          <img class="upload-image" src="/svss/static/img/refresh_white.png" alt="刷新"/>
        </div>
      </div>
      {videoContent}
      {KBar.render}
    </div>
  }
}