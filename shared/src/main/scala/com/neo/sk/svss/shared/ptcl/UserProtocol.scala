package com.neo.sk.svss.shared.ptcl

import java.sql.Date

import com.neo.sk.svss.shared.ptcl.VideoProtocol.BaseVideoInfo

/**
  * Created by dry on 2018/3/8.
  */
object UserProtocol {
  case class LoginReq(CheckName:String,CheckPw:String)
  case class SignUpReq(CheckName:String,CheckPw:String)

  case class deleteFollowReq(id:Int)
  case class addFollowReq(id:Int,name:String,sign:String)
  case class addFollowReq2(id:Int,name:String)

  case class getVisitInfoReq(id:Int)

  case class updateUserInfoReq(name:String,sex:Short,signature:String)

  case class SessionUserInfo(
                           userType:String,
                           userId: String,
                           name: String,
                           sex:String,
                           signature:String
                         )
  case class FollowUserInfo(
                                userId:Int,
                                name:String,
                                signature:String,
                                followState:Int
                              )
  case class FollowUserInfoRsp(
                              data: List[FollowUserInfo],
                              errCode: Int = 0,
                              msg: String = "ok"
                            )
  case class UserInfo(
                        name:String,
                        sex:Short,
                        signature:String,
                        followingCount:Int,
                        followerCount:Int,
                        upList:List[BaseVideoInfo],
                        likeList:List[BaseVideoInfo],
                      )
  case class UserInfoRsp(
                      userInfo:UserInfo,
                      errCode: Int = 0,
                      msg: String = "ok"
                      )
  case class VisitUserInfoRsp(
                             userInfo:UserInfo,
                             followingState:Int,
                             errCode: Int = 0,
                             msg: String = "ok"
                             )
}
