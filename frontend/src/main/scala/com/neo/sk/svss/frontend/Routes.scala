package com.neo.sk.svss.frontend

/**
  * User: Taoz
  * Date: 1/16/2017
  * Time: 6:49 PM
  */
object Routes {
  object SvssRoute{
    val svssRoot = "/svss"

    val loginCheck:String = svssRoot + "/loginCheck"
    val signUpCheck:String = svssRoot + "/signUpCheck"
    val quit:String = svssRoot + "/quit"
    val authJudge:String = svssRoot + "/authJudge"

    val getIndexNew:String=svssRoot+"/getIndexNew"
    val getIndexLike:String=svssRoot+"/getIndexLike"

    val getFollowVideo:String= svssRoot + "/getFollowVideo"

    val getSearchResult:String=svssRoot+"/getSearchResult"
    val addFollow:String=svssRoot+"/addFollow"
    val deleteFollow:String=svssRoot+"/deleteFollow"

    val getUserInfo:String=svssRoot+"/getUserInfo"
    val updateUserInfo:String=svssRoot+"/updateUserInfo"

    val getFollowing:String=svssRoot+"/getFollowing"
    val getFollower:String=svssRoot+"/getFollower"

    val deleteVideo:String=svssRoot+"/deleteVideo"

    val addClick:String=svssRoot+"/addClick"

    val getVideoAll:String=svssRoot+"/getVideoAll"

    val addLikeVideo:String=svssRoot+"/addLikeVideo"
    val deleteLikeVideo:String=svssRoot+"/deleteLikeVideo"

    val addFollow2:String=svssRoot+"/addFollow2"

    val addComment:String=svssRoot+"/addComment"
    val deleteComment:String=svssRoot+"/deleteComment"

    val addCommentLike:String=svssRoot+"/addCommentLike"
    val deleteCommentLike:String=svssRoot+"/deleteCommentLike"
    def upload(vname:String,vtype:String):String = svssRoot + s"/uploadFile?vname=$vname&vtype=$vtype"
  }
}
