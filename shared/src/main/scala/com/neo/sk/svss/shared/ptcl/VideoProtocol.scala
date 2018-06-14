package com.neo.sk.svss.shared.ptcl

import com.neo.sk.svss.shared.ptcl.UserProtocol.FollowUserInfo

object VideoProtocol {
  case class addClickReq(id:Int)
  case class getVideoAllReq(id:Int)
  case class getSearchReq(content:String)
  case class deleteVideoReq(id:Int)

  case class addLikeVideoReq(id:Int)
  case class deleteLikeVideoReq(id:Int)

  case class addCommentReq(vid:Int,content:String)
  case class deleteCommentReq(commentId:Int)

  case class addCommentLikeReq(commentId:Int)
  case class deleteCommentLikeReq(commentId:Int)

  case class BaseVideoInfo(
                          vId:Int,
                          vName:String,
                          vClickCount:Int,
                          vLikeCount:Int,
                          )
  case class BaseVideoInfoRsp(
                              BaseVideos: List[BaseVideoInfo],
                              errCode: Int = 0,
                              msg: String = "ok"
                            )
  case class UploadRsp(
                      vId:Int,
                      errCode: Int = 0,
                      msg: String = "ok"
                      )
  case class FollowVideoInfo(
                            vId:Int,
                            vName:String,
                            uperName:String,
                            upTime:Long,
                            )
  case class FollowVideoInfoRsp(
                               FolllowVideos: List[FollowVideoInfo],
                               errCode: Int = 0,
                               msg: String = "ok"
                             )
  case class Comment(
                    commentId:Int,
                    id:Int,
                    name: String,
                    content:String,
                    likeCount:Int,
                    time:Long,
                    likeState:Int
                    )
  case class CommentRsp(
                       commentId:Int,
                       errCode:Int=0,
                       msg: String = "ok"
                       )
  case class VideoAll(
                      myId:Int,
                      myName:String,
                      vname:String,
                      uperId:Int,
                      uperName:String,
                      vtype:String,
                      uptime:Long,
                      commentCount:Int,
                      likeCount:Int,
                      clickCount:Int,
                      dir:String,
                      followingState:Int,
                      likeState:Int,
                      comment:List[Comment]
                      )
  case class VideoAllRsp(
                          data:VideoAll,
                          errCode: Int = 0,
                          msg: String = "ok"
                        )
  case class SearchRsp(
                      userList:List[FollowUserInfo],
                      videoList:List[BaseVideoInfo],
                      errCode: Int = 0,
                      msg: String = "ok"
                      )
}
