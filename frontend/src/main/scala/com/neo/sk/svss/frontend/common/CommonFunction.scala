package com.neo.sk.svss.frontend.common

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.utils.{Http, JsFunc}
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol}
import com.neo.sk.svss.shared.ptcl.UserProtocol.FollowUserInfo
import com.neo.sk.svss.shared.ptcl.VideoProtocol.addClickReq
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object CommonFunction {
  def authJudge()={
    val url = Routes.SvssRoute.authJudge
    Http.getAndParse[SuccessRsp](url).map{
      case Right(rsp)=>
        if(rsp.errCode == 0) {
          ()
        }else{
          dom.window.location.hash = "#/Login"
        }
      case Left(error)=>
        JsFunc.alert(s"authJudge internal error: $error")
    }
  }
  def clickVideo(vid: Int):Unit={
    val url=Routes.SvssRoute.addClick
    val data=addClickReq(vid).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](url,data).map{
      case Right(rsp) =>
        if(rsp.errCode == 0){
          dom.window.location.hash=s"#/Video/$vid"
        }else if(rsp.errCode==1){
          dom.window.location.hash = "#/Login"
        }else if(rsp.errCode==2){
          JsFunc.alert(s"clickVideo server error happen: ${rsp.msg}")
        }else {
          JsFunc.alert("该视频已不存在")
        }
      case Left(error) =>
        JsFunc.alert(s"clickVideo internal error happen: $error")
    }
  }
}
