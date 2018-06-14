package com.neo.sk.svss.shared

/**
  * User: Taoz
  * Date: 1/16/2017
  * Time: 3:50 PM
  */
package object ptcl {


  trait Request

  trait CommonRsp {
    val errCode: Int
    val msg: String
  }

  final case class ErrorRsp(
                             errCode: Int,
                             msg: String
                           ) extends CommonRsp

  final case class SuccessRsp(
                               errCode: Int = 0,
                               msg: String = "ok"
                             ) extends CommonRsp
  final case class ComRsp(
                          errCode: Int,
                          msg: String
                          ) extends CommonRsp
}
