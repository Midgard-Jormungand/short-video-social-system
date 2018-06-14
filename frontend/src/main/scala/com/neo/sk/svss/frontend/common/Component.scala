package com.neo.sk.svss.frontend.common

import scala.language.implicitConversions
import scala.xml.Node

/**
  * User: Taoz
  * Date: 3/29/2018
  * Time: 1:59 PM
  */
trait Component {

  def render: Node

}

object Component {
  implicit def component2Node(comp: Component): Node = comp.render
}

