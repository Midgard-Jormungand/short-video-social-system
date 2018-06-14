package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.common.CommonFunction._
import com.neo.sk.svss.frontend.components._
import com.neo.sk.svss.frontend.utils.{Http, JsFunc}
import com.neo.sk.svss.shared.ptcl.ErrorRsp
import com.neo.sk.svss.shared.ptcl.VideoProtocol.{BaseVideoInfo, BaseVideoInfoRsp}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import mhtml.{Rx, Var, emptyHTML}

import scala.xml.Node
import org.scalajs.dom

import concurrent.ExecutionContext.Implicits.global


object IndexPage extends Page {
  override val locationHashString: String = "#/IndexPage"
  val indexNum:Var[Int]=Var(0)
  private var likeGetState=0
  private var newGetState=0
  private val hotButton=indexNum.dropRepeats.map{
    case 0=>
      <div class="index-bar-item index-bar-active">
        精选
      </div>
    case 1=>
      <div class="index-bar-item" onclick={()=>indexNum:=0}>
        精选
      </div>
  }
  private val newButton=indexNum.dropRepeats.map{
    case 0=>
      <div class="index-bar-item" onclick={()=>newButtonOnclick()}>
        发现
      </div>
    case 1=>
      <div class="index-bar-item index-bar-active">
        发现
      </div>
  }
  private val indexBar:Node=
    <div id="index-bar">
      {hotButton}
      {newButton}
    </div>
  private val hotVideoList=Var(List.empty[BaseVideoInfo])
  private val newVideoList=Var(List.empty[BaseVideoInfo])
  private val hotContent:Rx[Node] = hotVideoList.map{list=>
    buildContent(list)
  }
  private val newContent:Rx[Node] = newVideoList.map{list=>
    buildContent(list)
  }
  private val indexContent=indexNum.dropRepeats.map{
    case 0=>
      <div id="hot-content">
        {hotContent}
      </div>
    case 1=>
      <div id="new-content">
        {newContent}
      </div>
  }
  def buildContent(list:List[BaseVideoInfo]):Node ={
    if (list != Nil){
      <div id="index-content">
        {
        list.map{item=>
          getItem(item)
        }
        }
      </div>
    }else
      emptyHTML
  }
  private def getItem(item:BaseVideoInfo):Node={
    <div class="index-item">
      <img class="index-item-image" src={s"/svss/static/vImg/${item.vId}.jpg"} alt="视频" onclick={()=>clickVideo(item.vId)}/>
      <div class="index-item-name">{s"${item.vName}"}</div>
      <div class="index-item-count">
        <div class="index-item-click"><span class="glyphicon glyphicon-expand" aria-hidden="true"></span> {s"${item.vClickCount}"}</div>
        <div><span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span> {s"${item.vLikeCount}"}</div>
      </div>
    </div>
  }
  def getNewContent():Unit={
    val url = Routes.SvssRoute.getIndexNew
    Http.getAndParse[BaseVideoInfoRsp](url).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          newVideoList := rsp.BaseVideos
        }else if(rsp.errCode==1){
          dom.window.location.hash = "#/Login"
        }else{
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"getNew internal error: $error")
    }
  }
  def getLikeContent():Unit={
    val url = Routes.SvssRoute.getIndexLike
    Http.getAndParse[BaseVideoInfoRsp](url).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          hotVideoList := rsp.BaseVideos
        }else if(rsp.errCode==1){
          dom.window.location.hash = "#/Login"
        }else{
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"getLike internal error: $error")
    }
  }
  private def newButtonOnclick():Unit={
    if(newGetState==0){
      getNewContent()
      newGetState=1
    }else{
      ()
    }
    indexNum:=1
  }
  private def init={
    if(likeGetState==0){
      getLikeContent()
      likeGetState=1
    }else{
      authJudge()
    }
  }
  override def render: Node = {
    init
    <div>
      {indexBar}
      {indexContent}
      {KBar.render}
    </div>
  }
}