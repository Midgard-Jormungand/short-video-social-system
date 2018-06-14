package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.utils.Http
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol}
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx, Var, emptyHTML}

import scala.xml.Node
import org.scalajs.dom

import scala.scalajs.js
import concurrent.ExecutionContext.Implicits.global
object Login extends Page {
  override val locationHashString: String = "#/Login"
  private val rxName:Var[String] = Var("")
  private val rxPassword:Var[String] = Var("")
  private val rxNameWarnPattern:Var[Int] = Var(0)
  private val rxPasswordWarnPattern:Var[Int] =Var(0)
  private val rxFormData=rxName.zip(rxPassword)
  private val rxNameWarnControl=rxName.zip(rxNameWarnPattern)
  private val rxPasswordWarnControl=rxPassword.zip(rxPasswordWarnPattern)
  private val rxNameWarn:Rx[Node] = rxNameWarnControl.map{
    case ("",1) =>
      <p class="sign-warn">请输入用户名</p>
    case _ =>
      emptyHTML
  }
 private val rxPasswordWarn:Rx[Node] = rxPasswordWarnControl.map{
   case ("",1)=>
     <p class="sign-warn">请输入密码</p>
   case (_,2) =>
     <p class="sign-warn">服务器异常</p>
   case (_,3) =>
     <p class="sign-warn">数据库异常</p>
   case (_,4) =>
     <p class="sign-warn">用户名或密码错误</p>
   case (_,5) =>
     <p class="sign-warn">网络连接失败</p>
   case _ =>
     emptyHTML
 }
  private val rxLoginButton:Rx[Node]=rxFormData.map(i=>
    <button type="button" class="btn btn-primary btn-block login-button"  id="sign-button-first" onclick={()=>loginCheck(i._1,i._2)}>立即登录</button>
  )
  private val loginForm=
      <div class="col-xs-10 col-xs-offset-1">
        <form class="form">
          <h2 class="text-center" style="margin-bottom: 20px">登录</h2>
          <div class="form-group">
            <label for="inputName" class="sr-only">用户名</label>
            <input type="text" class="form-control" id="inputName" placeholder="用户名" oninput={
          (e: js.Dynamic) =>
            rxName:=e.target.value.asInstanceOf[String]
            rxNameWarnPattern:=1
          }/>
            {rxNameWarn}
          </div>
          <div class="form-group">
            <label for="inputPassword" class="sr-only">密码</label>
            <input type="password" class="form-control" id="inputPassword" placeholder="密码" oninput={
          (e: js.Dynamic) =>
            rxPassword:=e.target.value.asInstanceOf[String]
            rxPasswordWarnPattern:=1
          }/>
            {rxPasswordWarn}
          </div>
          {rxLoginButton}
          <button type="button" class="btn btn-default btn-block"  id="sign-button-second" onclick={()=>dom.window.location.hash="#/SignUp"}>注册</button>
        </form>
      </div>
  private def loginCheck(username:String,password:String):Unit= {
    if (username == "" | password == "") {
      ()
    } else {
      val url = Routes.SvssRoute.loginCheck
      val data = UserProtocol.LoginReq(username,password).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](url, data).map {
        case Right(rsp) =>
          rsp.errCode match {
            case 0 =>
              dom.window.location.hash = "#/Index"
            case 2 =>
              rxPasswordWarnPattern:=2
            case 3 =>
              rxPasswordWarnPattern:=3
            case 4 =>
              rxPasswordWarnPattern:=4
          }
        case Left(error) =>
          rxPasswordWarnPattern:=5
          println(s"$error")
      }
    }
  }
  override def render: Node = {
    <div id="sign-content">
      <div class="container-fluid">
        <div class="row">
          {loginForm}
        </div>
      </div>
    </div>
  }
}
