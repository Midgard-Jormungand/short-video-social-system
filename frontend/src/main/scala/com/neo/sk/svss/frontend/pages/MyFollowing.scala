package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.utils.{Http, JsFunc}
import com.neo.sk.svss.shared.ptcl.UserProtocol._
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol}
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx, Var}

import scala.xml.Node
import org.scalajs.dom

import concurrent.ExecutionContext.Implicits.global

object MyFollowing extends Page {
  override val locationHashString: String = "#/MyFollowing"
  private val followingList=Var(List.empty[FollowUserInfo])
  private val followingContent = followingList.map { list =>
    if (list != Nil)
      <div class="following-content">
        {list.map { item =>
        <div class="user-item">
          <div class="user-item-left">
            <div class="user-item-name" onclick={() =>
              dom.window.location.hash = {
                s"#/Visit/${item.userId}"
              }}>
              {item.name}
            </div>
            <div class="user-item-signature">
              {if (item.signature != "") item.signature else "暂无个性签名"}
            </div>
          </div>
          <div class="user-item-right">
            {if (item.followState == 1)
            <button type="button" class="btn btn-default" onclick={() => deleteFollow(item.userId)}>已关注</button>
          else
            <button type="button" class="btn btn-primary" onclick={() => addFollow(item.userId, item.name, item.signature)}>+关注</button>}
          </div>
        </div>
      }}
      </div>
    else
      <div class="text-center follow-nothing">
        您还没有关注任何人
      </div>
  }
  private def getFollowing():Unit={
    val url = Routes.SvssRoute.getFollowing
    Http.getAndParse[FollowUserInfoRsp](url).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followingList := rsp.data
        } else if(rsp.errCode==1) {
          dom.window.location.hash = "#/Login"
        }else{
          JsFunc.alert(s"${rsp.msg}")
        }
      case Left(error) =>
        JsFunc.alert(s"some internal error happen: $error")
    }
  }
  private def addFollow(id:Int,name:String,signature:String):Unit={
    val url = Routes.SvssRoute.addFollow
    val data = UserProtocol.addFollowReq(id,name,signature).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followingList.update { list1 =>
              list1.map { j =>
                if (j.userId == id)
                  FollowUserInfo(j.userId, j.name, j.signature, 1)
                else
                  j
              }
          }
          Personal.addFollowingCount()
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
  private def deleteFollow(id:Int):Unit={
    val url = Routes.SvssRoute.deleteFollow
    val data = UserProtocol.deleteFollowReq(id).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0) {
          followingList.update { list2 =>
            list2.map { j =>
              if (j.userId == id)
                FollowUserInfo(j.userId, j.name, j.signature, 0)
              else
                j
            }
          }
          Personal.deleteFollowingCount()
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
  override def render: Node = {
    getFollowing()
    <div>
      <div class="following-bar">
        <div class="text-center following-bar-back" onclick={()=>dom.window.location.hash="#/Personal"}>
          <span class="glyphicon glyphicon-menu-left" aria-hidden="true"></span>
        </div>
        <div class="text-center following-bar-word">
          我关注的
        </div>
      </div>
      {followingContent}
    </div>
  }
}