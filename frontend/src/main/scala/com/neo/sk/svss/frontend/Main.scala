package com.neo.sk.svss.frontend

import com.neo.sk.svss.frontend.pages._
import com.neo.sk.svss.frontend.common.PageSwitcher
import mhtml.{Cancelable, Rx, mount}
import org.scalajs.dom

import scala.xml.Node

/**
  * User: Taoz
  * Date: 4/16/2018
  * Time: 10:51 PM
  */


object Main extends PageSwitcher {

  val currentPage: Rx[Node] = currentHashVar.map {
      case Nil => Login.render
      case "Login" :: Nil => Login.render
      case "SignUp" :: Nil => SignUp.render
      case "Index" :: Nil => IndexPage.render
      case "Follow" :: Nil => Follow.render
      case "Search"::Nil=>Search.render
      case "Personal"::Nil=>Personal.render
      case "MyFollowing"::Nil=>MyFollowing.render
      case "MyFollower"::Nil=>MyFollower.render
      case "Visit"::pid::Nil=>new Visit(pid.toInt).render
      case "Following"::id::Nil=>new Following(id.toInt).render
      case "Follower"::id::Nil=>new Follower(id.toInt).render
      case "Video"::vid::Nil=>new Video(vid).render
      case _ => <div>Error Page</div>
  }
  def show(): Cancelable = {
    switchPageByHash()
    val page =
      <div>
        {currentPage}
      </div>
    mount(dom.document.body, page)
  }

  def main(args: Array[String]): Unit = {
    show()
  }
}

