package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.common.CommonFunction._
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
import org.scalajs.dom.html.{Input, Select, TextArea}

import concurrent.ExecutionContext.Implicits.global

object Personal extends Page {
  override val locationHashString: String = "#/Personal"
  private var state = 0
  private val homeState:Var[Int]=Var(0)

  private val currentVideoId:Var[Int]=Var(0)
  private val nameWarn:Var[Int]=Var(0)

  private val name:Var[String]=Var("")
  private val sex:Var[Short]=Var(0)
  private val signature:Var[String]=Var("")

  private val followingCount:Var[Int]=Var(0)
  private val followerCount:Var[Int]=Var(0)

  private val upCount:Var[Int]=Var(0)
  private val likeCount:Var[Int]=Var(0)

  private val upVideoList=Var(List.empty[BaseVideoInfo])
  private val likeVideoList=Var(List.empty[BaseVideoInfo])

  private val upVideoContent = upVideoList.map{list=>
    buildUpVideoContent(list)
  }
  private val likeVideoContent = likeVideoList.map{list=>
    buildLikeVideoContent(list)
  }


  private val userHome=
    <div id="user-content">
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
          <div data-toggle="modal" data-target="#myModal1">
            <img src="svss/static/img/logout.png" alt="man" style="width: 24px;"/>
          </div>
        </div>
        <div class="user-sign">
          {signature.map(i=>if(i != "") i else "暂无个性签名")}
        </div>
        <div class="user-edit">
          <div class="user-edit-button" data-toggle="modal" data-target="#myModal">编辑个人资料</div>
        </div>
        <div class="user-follow-count">
          <div style="margin-right: 16px" onclick={()=>dom.window.location.hash="#/MyFollowing"}><strong>{followingCount} 关注</strong></div>
          <div onclick={()=>dom.window.location.hash="#/MyFollower"}><strong>{followerCount} 粉丝</strong></div>
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
      <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header text-center">
              <h4 class="modal-title" id="myModalLabel">编辑个人资料</h4>
            </div>
            <div class="modal-body">
              <div class="edit">
                <label for="userName" class="control-label" style="width: 15%">昵称</label>
                <input type="text" class="form-control" id="userName" value={name} style="width: 85%"/>
                {nameWarn.map(i=>if(i==0) emptyHTML else if(i==1) <div class="change-warn">昵称不能为空</div> else <div class="change-warn">该昵称已有人使用</div>)}
              </div>
              <div class="edit">
                <label for="userSex" class="control-label" style="width: 15%">性别</label>
                {sex.map(i=>
                if(i==0)
                  <select class="form-control" id="userSex" style="width: 25%">
                    <option>男</option>
                    <option>女</option>
                    <option selected="selected">保密</option>
                  </select>
                else if(i==1)
                  <select class="form-control" id="userSex" style="width: 25%">
                    <option selected="selected">男</option>
                    <option>女</option>
                    <option>保密</option>
                  </select>
                else
                  <select class="form-control" id="userSex" style="width: 25%">
                    <option>男</option>
                    <option selected="selected">女</option>
                    <option>保密</option>
                  </select>
              )}
              </div>
              <div class="edit">
                <label for="userSign" class="control-label" style="width: 15%">签名</label>
                <textarea class="form-control" rows="2" id="userSign" style="width: 85%">{signature}</textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
              {for{a<-name;b<-sex;c<-signature} yield
              <button type="button" class="btn btn-primary" data-dismiss="modal" onclick={()=>saveChange(a,b,c)}>保存</button>
              }
              </div>
          </div>
        </div>
      </div>
      <div class="modal" id="myModal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div style="height: 100%;width: 100%;display: flex;align-items: center">
          <div class="modal-content" style="width:100%;margin:0 10px">
            <div class="model-button" data-dismiss="modal" onclick={()=>logOut()}>登出</div>
            <div class="model-button" data-dismiss="modal">取消</div>
          </div><!-- /.modal-content -->
        </div><!-- /.modal -->
      </div>
      <div class="modal" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div style="height: 100%;width: 100%;display: flex;align-items: center">
          <div class="modal-content" style="width:100%;margin:0 10px">
            {currentVideoId.map(i=> <div class="model-button" data-dismiss="modal" onclick={()=>deleteVideo(i)}>确认删除</div>)}
            <div class="model-button" data-dismiss="modal">取消</div>
          </div><!-- /.modal-content -->
        </div><!-- /.modal -->
      </div>
      <div class="modal" id="myModal3" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div style="height: 100%;width: 100%;display: flex;align-items: center">
          <div class="modal-content" style="width:100%;margin:0 10px">
            {currentVideoId.map(i=> <div class="model-button" data-dismiss="modal" onclick={()=>deleteLikeVideo(i)}>确认取消点赞</div>)}
            <div class="model-button" data-dismiss="modal">取消</div>
          </div><!-- /.modal-content -->
        </div><!-- /.modal -->
      </div>
    </div>

  def buildUpVideoContent(list:List[BaseVideoInfo]):Node ={
    if (list != Nil){
      <div id="user-video">
        {
        list.map{item=>getUpVideoItem(item)}
        }
      </div>
    }else{
      emptyHTML
    }
  }
  def buildLikeVideoContent(list:List[BaseVideoInfo]):Node ={
    if (list != Nil){
      <div id="user-video">
        {
        list.map{item=>getLikeVideoItem(item)}
        }
      </div>
    }else{
      emptyHTML
    }
  }
  def getUpVideoItem(item:BaseVideoInfo):Node={
    <div class="half-video-item">
      <img class="half-video-image" src={s"/svss/static/vImg/${item.vId}.jpg"} alt="Error" onclick={()=>clickVideo(item.vId)}/>
      <div class="half-video-info">
        <div class="half-video-name">{s"${item.vName}"}</div>
        <div class="half-video-other">
          <div><span class="glyphicon glyphicon-expand" aria-hidden="true"></span> <span> {s"${item.vClickCount}"}</span></div>
          <div><span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span> <span> {s"${item.vLikeCount}"}</span></div>
          <span data-toggle="modal" data-target="#myModal2" onclick={()=>currentVideoId:=item.vId}><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></span>
        </div>
      </div>
    </div>
  }
  def getLikeVideoItem(item:BaseVideoInfo):Node={
    <div class="half-video-item">
      <img class="half-video-image" src={s"/svss/static/vImg/${item.vId}.jpg"} alt="Error" onclick={()=>clickVideo(item.vId)}/>
      <div class="half-video-info">
        <div class="half-video-name">{s"${item.vName}"}</div>
        <div class="half-video-other">
          <div><span class="glyphicon glyphicon-expand" aria-hidden="true"></span> <span> {s"${item.vClickCount}"}</span></div>
          <div><span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span> <span> {s"${item.vLikeCount}"}</span></div>
          <span data-toggle="modal" data-target="#myModal3" onclick={()=>currentVideoId:=item.vId}><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></span>
        </div>
      </div>
    </div>
  }
  private def getMyInfo():Unit={
    val url = Routes.SvssRoute.getUserInfo
    Http.getAndParse[UserInfoRsp](url).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          val userInfo=rsp.userInfo
          name:=userInfo.name
          sex:=userInfo.sex
          signature:=userInfo.signature

          followingCount:=userInfo.followingCount
          followerCount:=userInfo.followerCount

          upVideoList:=userInfo.upList
          upCount:=userInfo.upList.length

          likeVideoList:=userInfo.likeList
          likeCount:=userInfo.likeList.length
        }else if(rsp.errCode ==1) {
          dom.window.location.hash = "#/Login"
        }else{
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"some internal error happen: $error")
    }
  }
  private def saveChange(nameNow:String,sexNow:Short,signNow:String):Unit={
    val inputName:String=dom.document.getElementById("userName").asInstanceOf[Input].value
    val inputSex:Short=dom.document.getElementById("userSex").asInstanceOf[Select].value match{
      case "男"=>
        1
      case "女"=>
        2
      case _=>
        0
    }
    val inputSign:String=dom.document.getElementById("userSign").asInstanceOf[TextArea].value
    if(inputName==nameNow && inputSex==sexNow && inputSign== signNow){
      ()
    }else if (inputName == ""){
      nameWarn:=1
    }else{
      val url = Routes.SvssRoute.updateUserInfo
      val data = UserProtocol.updateUserInfoReq(inputName,inputSex,inputSign).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](url,data).map{
        case Right(rsp) =>
          if (rsp.errCode == 0) {
            name:=inputName
            sex:=inputSex
            signature:=inputSign
          }else if(rsp.errCode==1){
            dom.window.location.hash = "#/Login"
          }else if(rsp.errCode==2){
            nameWarn:=2
          }else{
            JsFunc.alert(s"${rsp.msg}")
          }
        case Left(error) =>
          JsFunc.alert(s"update internal error happen: $error")
      }
    }
  }
  private def logOut():Unit={
    Http.getAndParse[SuccessRsp](Routes.SvssRoute.quit).map {
      case Right(_) =>
        dom.window.location.hash = "#/Login"
      case Left(error) =>
        JsFunc.alert(s"quit internal error: $error")
    }
  }
  private def deleteVideo(vid: Int):Unit={
    val url=Routes.SvssRoute.deleteVideo
    val data=deleteVideoReq(vid).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          upVideoList.update{i=>i.filter(_.vId != vid)}
          upCount.update(i=>i-1)
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
  private def deleteLikeVideo(vid: Int):Unit={
    val url=Routes.SvssRoute.deleteLikeVideo
    val data=deleteLikeVideoReq(vid).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          deleteLike(vid)
        }else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"deleteLikeVideo server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"deleteLikeVideo internal error happen: $error")
    }
  }
  def addFollowingCount():Unit={
    followingCount.update(_ + 1)
  }
  def deleteFollowingCount():Unit={
    followingCount.update(_ - 1)
  }
  def addUp(id:Int,name:String):Unit={
    upCount.update(_ + 1)
    upVideoList.update(list=>BaseVideoInfo(id,name,0,0)::list)
  }
  def deleteLike(id:Int):Unit={
    likeVideoList.update{i=>i.filter(_.vId != id)}
    likeCount.update(_ - 1)
  }
  def addLike(baseVideoInfo: BaseVideoInfo):Unit={
    likeCount.update(_ + 1)
    likeVideoList.update(i=>baseVideoInfo::i)
  }

  private def init():Unit={
    if(state==1){
      authJudge()
    }
    else{
      getMyInfo()
      state=1
    }
  }
  override def render: Node = {
    init()
    <div>
      {userHome}
      {KBar.render}
    </div>
  }
}