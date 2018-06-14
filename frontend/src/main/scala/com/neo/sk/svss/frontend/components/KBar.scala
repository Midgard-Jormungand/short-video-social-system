package com.neo.sk.svss.frontend.components

import com.neo.sk.svss.frontend.Routes
import com.neo.sk.svss.frontend.common.Component
import com.neo.sk.svss.frontend.pages.{IndexPage, Personal, Search}
import com.neo.sk.svss.frontend.utils.{Http, JsFunc}
import com.neo.sk.svss.shared.ptcl.SuccessRsp
import com.neo.sk.svss.shared.ptcl.VideoProtocol.UploadRsp
import org.scalajs.dom
import io.circe.generic.auto._
import io.circe.syntax._
import mhtml.{Var, emptyHTML}
import org.scalajs.dom.html.{Form, Input, Select, TextArea}
import org.scalajs.dom.raw.FormData

import scala.xml.Node
import concurrent.ExecutionContext.Implicits.global

object KBar extends Component{
  val state = Var(0)//0代表首页 1代表关注 2代表搜索 3代表我
  val fileWarn=Var(emptyHTML)
  val introWarn=Var(emptyHTML)
  val resultWarn=Var(emptyHTML)

  def indexOnclick():Unit = {
    dom.window.location.hash = "#/Index"
  }
  def followOnclick():Unit = {
    dom.window.location.hash = "#/Follow"
  }
  def searchOnclick():Unit = {
    Search.resetBarNum()
    dom.window.location.hash = "#/Search"
  }
  def userOnclick():Unit = {
    dom.window.location.hash = "#/Personal"
  }

  def judgeActive():Unit = {
    if(dom.window.location.hash.contains("Index"))
      state := 0
    else if(dom.window.location.hash.contains("Follow"))
      state := 1
    else if(dom.window.location.hash.contains("Search"))
      state := 2
    else
      state := 3
  }
  private def upload():Unit={
    initWarn()
    val vFile = dom.document.getElementById("inputFile").asInstanceOf[Input].files
    val vFileName=dom.document.getElementById("inputFile").asInstanceOf[Input].value
    val vName=dom.document.getElementById("inputVName").asInstanceOf[TextArea].value
    val vType=dom.document.getElementById("inputVType").asInstanceOf[Select].value
    if (vFileName == "") {
      fileWarn:= <div class="upload-warn">请选择一个文件</div>
      if(vName=="")
        introWarn:= <div class="upload-warn">请输入视频简介</div>
      else
        ()
    }else if(vName==""){
      introWarn:= <div class="upload-warn">请输入视频简介</div>
    }else{
      val file = vFile(0)
      val fileSize = file.size / 1024
      if (fileSize > 200 * 1024) {
        fileWarn:= <div class="upload-warn">文件大小不得大于200M</div>
      }
      else {
        resultWarn:= <div style="padding-top: 10px;text-align: center;font-size: 16px;">上传中...</div>
        val form = new FormData()
        form.append("fileUpload", file)
        Http.postFormAndParse[UploadRsp](Routes.SvssRoute.upload(vName,vType),form).map {
          case Right(rsp) =>
            if (rsp.errCode == 0) {
              resultWarn:= <div style="padding-top: 10px;text-align: center;font-size: 16px;color: #4cae4c">视频已上传</div>
              Personal.addUp(rsp.vId,vName)
            }else if(rsp.errCode==1){
              dom.window.location.hash = "#/Login"
            }else{
              JsFunc.alert(s"${rsp.msg}")
            }
          case Left(e) =>
            JsFunc.alert(s"upload internal error happen: $e")
        }
      }
    }
  }
  private def initWarn():Unit={
    fileWarn:=emptyHTML
    introWarn:=emptyHTML
    resultWarn:=emptyHTML
  }
  private def initUploadForm():Unit={
    initWarn()
    dom.document.getElementById("uploadForm").asInstanceOf[Form].reset()
  }
  override def render: Node = {
    judgeActive()
    <div>
      <div id="k-bar">

        {state.map { i =>
        if (i == 0)
          <div class="k-index-active">
            {IndexPage.indexNum.map { j =>
              if (j == 0)
                <div onclick={() => IndexPage.getLikeContent()}>
                  <img class="upload-image" src="/svss/static/img/refresh.png" alt="刷新"/>
                </div>
              else
                <div onclick={() => IndexPage.getNewContent()}>
                  <img class="upload-image" src="/svss/static/img/refresh.png" alt="刷新"/>
                </div>
            }}
          </div>

        else
          <div class="k-bar-item" onclick={() => indexOnclick()}>
            首页
          </div>
      }}
        {state.map { i =>
        if (i == 1)
          <div class="k-bar-item k-bar-active">
            关注
          </div>
        else
          <div class="k-bar-item" onclick={() => followOnclick()}>
            关注
          </div>
      }}
        <div class="k-bar-upload">
          <div class="upload-button" data-toggle="modal" data-target="#uploadModal">
            <img class="upload-image" src="/svss/static/img/up.png" alt="上传"/>
          </div>
        </div>
        {state.map { i =>
        if (i == 2)
          <div class="k-bar-item k-bar-active">
            搜索
          </div>
        else
          <div class="k-bar-item" onclick={() => searchOnclick()}>
            搜索
          </div>
      }}
        {state.map { i =>
        if (i == 3)
          <div class="k-bar-item k-bar-active">
            我
          </div>
        else
          <div class="k-bar-item" onclick={() => userOnclick()}>
            我
          </div>
      }}
      </div>
      <div class="modal fade" id="uploadModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header text-center">
              <h3 class="modal-title" id="myModalLabel">上传视频</h3>
            </div>
            <div class="modal-body">
              <form id="uploadForm">
                <div class="form-group">
                  <label for="inputFile">上传视频</label>
                  <input type="file" id="inputFile" accept=".mp4,.flv,.avi,.wmv,.mov,.webm,.mpeg4,.ts,.mpg,.rm,.rmvb,.mkv"/>
                  {fileWarn}
                </div>
                <div class="form-group">
                  <label for="inputVName">视频简介</label>
                  <textarea class="form-control" rows="2" id="inputVName" placeholder="请输入视频简介"></textarea>
                  {introWarn}
                </div>
                <div class="form-group upload-type">
                  <label for="inputVType">视频类型</label>
                  <div class="row">
                    <div class="col-xs-4">
                      <select class="form-control"  id="inputVType">
                        <option>歌舞</option>
                        <option>娱乐</option>
                        <option>游戏</option>
                        <option>科技</option>
                        <option>生活</option>
                      </select>
                    </div>
                  </div>
                </div>
                {resultWarn}
              </form>
            </div>
            <div class="modal-footer">
              <div class="upload-footer">
                <button type="button" class="btn btn-primary btn-block"  onclick={()=>upload()}>上传</button>
              </div>
              <div class="upload-footer">
                <button type="button" class="btn btn-default btn-block" data-dismiss="modal" onclick={()=>initUploadForm()}>关闭</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  }
}
