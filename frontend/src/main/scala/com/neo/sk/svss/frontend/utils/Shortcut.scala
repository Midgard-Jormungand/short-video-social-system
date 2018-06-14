package com.neo.sk.svss.frontend.utils

import java.util.Date

import org.scalajs.dom


/**
  * User: Taoz
  * Date: 12/2/2016
  * Time: 11:12 AM
  */
object Shortcut {
  def redirect(url: String): Unit = {
    dom.window.location.href = url
  }


  def setTitle(title: String): Unit = {
    dom.document.title = title
  }

  def getUrlParams: Map[String, String] = {
    val paramStr =
      Option(dom.document.getElementById("fakeUrlSearch"))
        .map(_.textContent).getOrElse(dom.window.location.search)

    val str1 = paramStr.substring(1)
    val pairs = str1.split("&").filter(s => s.length > 0)
    val tmpMap = pairs.map(_.split("=", 2)).filter(_.length == 2)
    tmpMap.map(d => (d(0), d(1))).toMap
  }


  def errorDetailMsg(t: Throwable, line: Int = 5): String = {
    val stack = t.getStackTrace.take(line).map(t => t.toString).mkString("\n")
    val msg = t.getMessage
    val localMsg = t.getLocalizedMessage
    s"msg: $msg \nlocalMsg: $localMsg \n stack: $stack"
  }
}
