package com.neo.sk.svss.frontend.utils

import scala.scalajs.js.Date

/**
  * Created by hongruying on 2017/5/15.
  */
object TimeTool {


  def timeFormat(timestamp: Long) = {
    new Date(timestamp).toLocaleString
  }

  /**
    * dateFormat default yyyy-MM-dd HH:mm:ss
    **/
  def dateFormatDefault(timestamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String = {
    DateFormatter(new Date(timestamp), format)
  }

  def DateFormatter(date: Date, `type`: String): String = {
    val y = date.getFullYear()
    val m = date.getMonth() + 1
    val d = date.getDate()
    val h = date.getHours()
    val mi = date.getMinutes()
    val s = date.getSeconds()
    date.getDate()
    val mS = if (m < 10)
      "0" + m
    else
      m
    val dS = if (d < 10)
      "0" + d
    else
      d
    val hS = if (h < 10)
      "0" + h
    else
      h
    val miS = if (mi < 10)
      "0" + mi
    else
      mi
    val sS = if (s < 10)
      "0" + s
    else
      s
    `type` match {
      case "yyyy-MM-dd hh:mm:ss" =>
        y + "-" + mS + "-" + dS + " " + hS + ":" + miS + ":" + sS
      case "yyyy-MM-dd hh:mm" =>
        y + "-" + mS + "-" + dS + " " + hS + ":" + miS
      case "yyyy-MM-dd" =>
        y + "-" + mS + "-" + dS
      case "yyyyMMdd" =>
        y + "" + mS + "" + dS
      case "yyyy/MM/dd" =>
        y + "/" + mS + "/" + dS
      case "yyyy-MM" =>
        y + "-" + mS
      case "MM-dd" =>
        mS + "-" + dS
      case "hh:mm" =>
        hS + ":" + miS
      case x =>
        y + "-" + mS + "-" + dS + " " + hS + ":" + miS + ":" + sS
    }
  }

  def WeekFormatter(date: Date): List[Date] = {
    var first = date.getTime()
    var last = date.getTime()
    date.getDay() match {
      case 0 =>
        first = date.getTime() - 86400000 * 6
        last = date.getTime()
        List(new Date(first), new Date(last))
      case 1 =>
        first = date.getTime()
        last = date.getTime() + 86400000 * 6
        List(new Date(first), new Date(last))
      case 2 =>
        first = date.getTime() - 86400000
        last = date.getTime() + 86400000 * 5
        List(new Date(first), new Date(last))
      case 3 =>
        first = date.getTime() - 86400000 * 2
        last = date.getTime() + 86400000 * 4
        List(new Date(first), new Date(last))
      case 4 =>
        first = date.getTime() - 86400000 * 3
        last = date.getTime() + 86400000 * 3
        List(new Date(first), new Date(last))
      case 5 =>
        first = date.getTime() - 86400000 * 4
        last = date.getTime() + 86400000 * 2
        List(new Date(first), new Date(last))
      case 6 =>
        first = date.getTime() - 86400000 * 5
        last = date.getTime() + 86400000
        List(new Date(first), new Date(last))
      case x =>
        List(date)
    }
  }

  //
  def parseDate(date: String) = {
    val year = date.take(4).toInt
    val month = date.substring(4, 6).toInt - 1
    val d = date.takeRight(2).toInt
    val x = new Date(year, month, d)
    x
  }

  def parseDate_yyyyMM(date: String) = {
    val year = date.take(4).toInt
    val month = date.substring(4, 6).toInt
    (year, month)
  }

  def parseDate_yyyyWW(date: String) = {
    val year = date.take(4).toInt
    val week = date.substring(4, 6).toInt
    (year, week)
  }

  def getWeekOfYear(date: Date) = {
    val year = date.getFullYear()
    val firstDay = new Date(year, 0, 1)
    val firstWeekDays = 7 - firstDay.getDay()
    val dayOfYear = ((new Date(year, date.getMonth(), date.getDate()).getTime() - firstDay.getTime()) / (24 * 3600 * 1000)) + 1
    Math.ceil((dayOfYear - firstWeekDays) / 7) + 1
  }
  def parse_yyyyMMdd_2_yyyyWW(date: String) = {
    val d = parseDate(date)
    val week = getWeekOfYear(d)
    val year = d.getFullYear()
    if (week < 10) year + "0" + week
    else year.toString + week.toString
  }

  def getTimeToNow(timeStamp:Long):String = {
    val count = (new Date().getTime() - timeStamp)/1000
    val str = if (count <= 30){
      "刚刚"
    }else if (count < 60){
      s"${count.round} 秒前"
    }else if (count < 3600){
      s"${(count/60).round}分钟前"
    }else if (count < 86400){
      s"${(count/3600).round}小时前"
    }else if (count < 2592000){
      s"${(count/86400).round}天前"
    }else if(count < 31104000) {
      s"${(count/2592000).round}月前"
    } else{
      s"${(count/31104000).round}年前"
    }
    str
  }

  def getTimeToNow2(timeStamp:Long):String = {
    val count = (new Date().getTime() - timeStamp)/1000
    val str = if (count <= 30){
      "刚刚"
    }else if (count < 60){
      s"${count.round} 秒前"
    }else if (count < 3600){
      s"${(count/60).round}分钟前"
    }else if (count < 86400){
      s"${(count/3600).round}小时前"
    }else if (count < 604800){
      s"${(count/86400).round}天前"
    }else{
      dateFormatDefault(timeStamp, "yyyy-MM-dd")
    }
    str
  }

}
