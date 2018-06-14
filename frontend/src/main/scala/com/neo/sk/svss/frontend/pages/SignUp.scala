package com.neo.sk.svss.frontend.pages

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Page
import com.neo.sk.svss.frontend.utils.Http
import com.neo.sk.svss.shared.ptcl.{SuccessRsp, UserProtocol}
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Rx,Var,emptyHTML}
import scala.scalajs.js
import scala.xml.Node
import org.scalajs.dom

import concurrent.ExecutionContext.Implicits.global
object SignUp extends Page {
  override val locationHashString: String = "#/SignUp"
  private val rxName:Var[String] = Var("")
  private val rxPassword:Var[String] = Var("")
  private val rxRepeatPw:Var[String] = Var("")
  private val rxNameWarnPattern:Var[Int] = Var(0)
  private val rxPasswordWarnPattern:Var[Int] =Var(0)
  private val rxRepeatPwWarnPattern:Var[Int] =Var(0)
  private val rxFormData=for { a <- rxName; b <- rxPassword;c <- rxRepeatPw } yield (a, b, c)
  private val rxNameWarnControl=rxName.zip(rxNameWarnPattern)
  private val rxPasswordWarnControl=rxPassword.zip(rxPasswordWarnPattern)
  private val rxRepeatPwWarnControl=rxRepeatPw.zip(rxRepeatPwWarnPattern)
  private val rxNameWarn:Rx[Node] = rxNameWarnControl.map{
    case ("",1) =>
      <p class="sign-warn">请输入用户名</p>
    case (_,2) =>
      <p class="sign-warn">该用户名已被注册</p>
    case _ =>
      emptyHTML
  }
  private val rxPasswordWarn:Rx[Node] = rxPasswordWarnControl.map{
    case ("",1)=>
      <p class="sign-warn">请输入密码</p>
    case _ =>
      emptyHTML
  }
  private val rxRepeatPwWarn:Rx[Node] = rxRepeatPwWarnControl.map{
    case ("",1)=>
      <p class="sign-warn">请再次输入密码</p>
    case (_,2) =>
      <p class="sign-warn">服务器boom</p>
    case (_,3) =>
      <p class="sign-warn">数据库boom</p>
    case (_,4) =>
      <p class="sign-warn">网络连接失败</p>
    case (_,5)=>
      <p class="sign-warn">密码不一致</p>
    case _ =>
      emptyHTML
  }
  private val rxSignUpButton:Rx[Node]=rxFormData.map(i=>
    <button class="btn btn-primary btn-block" id="sign-button-first" onclick={()=>signUpCheck(i._1,i._2,i._3)}>注册</button>
  )
  private val signUpForm=
    <div class="col-xs-10 col-xs-offset-1">
      <form class="form">
        <h2 class="text-center" style="margin-bottom: 20px">注册</h2>
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
        <div class="form-group">
          <label for="repeatPw" class="sr-only">重复密码</label>
          <input type="password" class="form-control" id="repeatPw" placeholder="再次输入密码" oninput={
        (e: js.Dynamic) =>
          rxRepeatPw:=e.target.value.asInstanceOf[String]
          rxRepeatPwWarnPattern:=1
        }/>
          {rxRepeatPwWarn}
        </div>
        {rxSignUpButton}
        <button class="btn btn-default btn-block" id="sign-button-second" onclick={()=>dom.window.location.hash="#/Login"}>返回</button>
      </form>
    </div>
  def signUpCheck(name:String,password:String,repeatPw:String):Unit= {
    if (name == "" | password=="" | repeatPw=="") {
      ()
    }else if(password != repeatPw){
      rxRepeatPwWarnPattern:=5
    }else{
      val url = Routes.SvssRoute.signUpCheck
      val data = UserProtocol.SignUpReq(name,password).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](url,data).map{
        case Right(rsp) =>
          rsp.errCode match {
            case 0 =>
              dom.window.location.hash = "#/Login"
            case 2 =>
              rxRepeatPwWarnPattern:=2
            case 3 =>
              rxRepeatPwWarnPattern:=3
            case 4 =>
              rxNameWarnPattern:=2
          }
        case Left(error) =>
          rxRepeatPwWarnPattern:=4
          println(s"$error")
      }
    }
  }
  override def render: Node = {
    <div id="sign-content">
      <div class="container-fluid">
        <div class="row">
          {signUpForm}
        </div>
      </div>
    </div>
  }
}
