package com.neo.sk.svss.models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.jdbc.PostgresProfile
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(tFollowinfo.schema, tLikecommentinfo.schema, tLikevideoinfo.schema, tUserinfo.schema, tVideocomments.schema, tVideoinfo.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tFollowinfo
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param followid Database column followid SqlType(int4), Default(0)
   *  @param followname Database column followname SqlType(varchar), Length(255,true), Default()
   *  @param followsign Database column followsign SqlType(varchar), Length(255,true), Default()
   *  @param befollowedid Database column befollowedid SqlType(int4), Default(0)
   *  @param befollowedname Database column befollowedname SqlType(varchar), Length(255,true), Default()
   *  @param befollowedsign Database column befollowedsign SqlType(varchar), Length(255,true), Default()
   *  @param followtime Database column followtime SqlType(int8), Default(0) */
  case class rFollowinfo(id: Int, followid: Int = 0, followname: String = "", followsign: String = "", befollowedid: Int = 0, befollowedname: String = "", befollowedsign: String = "", followtime: Long = 0L)
  /** GetResult implicit for fetching rFollowinfo objects using plain SQL queries */
  implicit def GetResultrFollowinfo(implicit e0: GR[Int], e1: GR[String], e2: GR[Long]): GR[rFollowinfo] = GR{
    prs => import prs._
    rFollowinfo.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[Long]))
  }
  /** Table description of table followinfo. Objects of this class serve as prototypes for rows in queries. */
  class tFollowinfo(_tableTag: Tag) extends profile.api.Table[rFollowinfo](_tableTag, "followinfo") {
    def * = (id, followid, followname, followsign, befollowedid, befollowedname, befollowedsign, followtime) <> (rFollowinfo.tupled, rFollowinfo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(followid), Rep.Some(followname), Rep.Some(followsign), Rep.Some(befollowedid), Rep.Some(befollowedname), Rep.Some(befollowedsign), Rep.Some(followtime)).shaped.<>({r=>import r._; _1.map(_=> rFollowinfo.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column followid SqlType(int4), Default(0) */
    val followid: Rep[Int] = column[Int]("followid", O.Default(0))
    /** Database column followname SqlType(varchar), Length(255,true), Default() */
    val followname: Rep[String] = column[String]("followname", O.Length(255,varying=true), O.Default(""))
    /** Database column followsign SqlType(varchar), Length(255,true), Default() */
    val followsign: Rep[String] = column[String]("followsign", O.Length(255,varying=true), O.Default(""))
    /** Database column befollowedid SqlType(int4), Default(0) */
    val befollowedid: Rep[Int] = column[Int]("befollowedid", O.Default(0))
    /** Database column befollowedname SqlType(varchar), Length(255,true), Default() */
    val befollowedname: Rep[String] = column[String]("befollowedname", O.Length(255,varying=true), O.Default(""))
    /** Database column befollowedsign SqlType(varchar), Length(255,true), Default() */
    val befollowedsign: Rep[String] = column[String]("befollowedsign", O.Length(255,varying=true), O.Default(""))
    /** Database column followtime SqlType(int8), Default(0) */
    val followtime: Rep[Long] = column[Long]("followtime", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tFollowinfo */
  lazy val tFollowinfo = new TableQuery(tag => new tFollowinfo(tag))

  /** Entity class storing rows of table tLikecommentinfo
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param uid Database column uid SqlType(int4), Default(0)
   *  @param commentid Database column commentid SqlType(int4), Default(0) */
  case class rLikecommentinfo(id: Int, uid: Int = 0, commentid: Int = 0)
  /** GetResult implicit for fetching rLikecommentinfo objects using plain SQL queries */
  implicit def GetResultrLikecommentinfo(implicit e0: GR[Int]): GR[rLikecommentinfo] = GR{
    prs => import prs._
    rLikecommentinfo.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table likecommentinfo. Objects of this class serve as prototypes for rows in queries. */
  class tLikecommentinfo(_tableTag: Tag) extends profile.api.Table[rLikecommentinfo](_tableTag, "likecommentinfo") {
    def * = (id, uid, commentid) <> (rLikecommentinfo.tupled, rLikecommentinfo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(uid), Rep.Some(commentid)).shaped.<>({r=>import r._; _1.map(_=> rLikecommentinfo.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column uid SqlType(int4), Default(0) */
    val uid: Rep[Int] = column[Int]("uid", O.Default(0))
    /** Database column commentid SqlType(int4), Default(0) */
    val commentid: Rep[Int] = column[Int]("commentid", O.Default(0))
  }
  /** Collection-like TableQuery object for table tLikecommentinfo */
  lazy val tLikecommentinfo = new TableQuery(tag => new tLikecommentinfo(tag))

  /** Entity class storing rows of table tLikevideoinfo
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param userid Database column userid SqlType(int4), Default(0)
   *  @param videoid Database column videoid SqlType(int4), Default(0)
   *  @param liketime Database column liketime SqlType(int8), Default(0) */
  case class rLikevideoinfo(id: Int, userid: Int = 0, videoid: Int = 0, liketime: Long = 0L)
  /** GetResult implicit for fetching rLikevideoinfo objects using plain SQL queries */
  implicit def GetResultrLikevideoinfo(implicit e0: GR[Int], e1: GR[Long]): GR[rLikevideoinfo] = GR{
    prs => import prs._
    rLikevideoinfo.tupled((<<[Int], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table likevideoinfo. Objects of this class serve as prototypes for rows in queries. */
  class tLikevideoinfo(_tableTag: Tag) extends profile.api.Table[rLikevideoinfo](_tableTag, "likevideoinfo") {
    def * = (id, userid, videoid, liketime) <> (rLikevideoinfo.tupled, rLikevideoinfo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userid), Rep.Some(videoid), Rep.Some(liketime)).shaped.<>({r=>import r._; _1.map(_=> rLikevideoinfo.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column userid SqlType(int4), Default(0) */
    val userid: Rep[Int] = column[Int]("userid", O.Default(0))
    /** Database column videoid SqlType(int4), Default(0) */
    val videoid: Rep[Int] = column[Int]("videoid", O.Default(0))
    /** Database column liketime SqlType(int8), Default(0) */
    val liketime: Rep[Long] = column[Long]("liketime", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tLikevideoinfo */
  lazy val tLikevideoinfo = new TableQuery(tag => new tLikevideoinfo(tag))

  /** Entity class storing rows of table tUserinfo
   *  @param uid Database column uid SqlType(serial), AutoInc, PrimaryKey
   *  @param uname Database column uname SqlType(varchar), Length(255,true), Default()
   *  @param upassword Database column upassword SqlType(varchar), Length(255,true), Default()
   *  @param sex Database column sex SqlType(int2), Default(0)
   *  @param signature Database column signature SqlType(varchar), Length(255,true), Default()
   *  @param signuptime Database column signuptime SqlType(int8), Default(0) */
  case class rUserinfo(uid: Int, uname: String = "", upassword: String = "", sex: Short = 0, signature: String = "", signuptime: Long = 0L)
  /** GetResult implicit for fetching rUserinfo objects using plain SQL queries */
  implicit def GetResultrUserinfo(implicit e0: GR[Int], e1: GR[String], e2: GR[Short], e3: GR[Long]): GR[rUserinfo] = GR{
    prs => import prs._
    rUserinfo.tupled((<<[Int], <<[String], <<[String], <<[Short], <<[String], <<[Long]))
  }
  /** Table description of table userinfo. Objects of this class serve as prototypes for rows in queries. */
  class tUserinfo(_tableTag: Tag) extends profile.api.Table[rUserinfo](_tableTag, "userinfo") {
    def * = (uid, uname, upassword, sex, signature, signuptime) <> (rUserinfo.tupled, rUserinfo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uid), Rep.Some(uname), Rep.Some(upassword), Rep.Some(sex), Rep.Some(signature), Rep.Some(signuptime)).shaped.<>({r=>import r._; _1.map(_=> rUserinfo.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uid SqlType(serial), AutoInc, PrimaryKey */
    val uid: Rep[Int] = column[Int]("uid", O.AutoInc, O.PrimaryKey)
    /** Database column uname SqlType(varchar), Length(255,true), Default() */
    val uname: Rep[String] = column[String]("uname", O.Length(255,varying=true), O.Default(""))
    /** Database column upassword SqlType(varchar), Length(255,true), Default() */
    val upassword: Rep[String] = column[String]("upassword", O.Length(255,varying=true), O.Default(""))
    /** Database column sex SqlType(int2), Default(0) */
    val sex: Rep[Short] = column[Short]("sex", O.Default(0))
    /** Database column signature SqlType(varchar), Length(255,true), Default() */
    val signature: Rep[String] = column[String]("signature", O.Length(255,varying=true), O.Default(""))
    /** Database column signuptime SqlType(int8), Default(0) */
    val signuptime: Rep[Long] = column[Long]("signuptime", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tUserinfo */
  lazy val tUserinfo = new TableQuery(tag => new tUserinfo(tag))

  /** Entity class storing rows of table tVideocomments
   *  @param commentid Database column commentid SqlType(serial), AutoInc, PrimaryKey
   *  @param vid Database column vid SqlType(int4), Default(0)
   *  @param commentuserid Database column commentuserid SqlType(int4), Default(0)
   *  @param commentusername Database column commentusername SqlType(varchar), Length(255,true), Default()
   *  @param commentcontent Database column commentcontent SqlType(varchar), Length(255,true), Default()
   *  @param likecount Database column likecount SqlType(int4), Default(0)
   *  @param commenttime Database column commenttime SqlType(int8), Default(0) */
  case class rVideocomments(commentid: Int, vid: Int = 0, commentuserid: Int = 0, commentusername: String = "", commentcontent: String = "", likecount: Int = 0, commenttime: Long = 0L)
  /** GetResult implicit for fetching rVideocomments objects using plain SQL queries */
  implicit def GetResultrVideocomments(implicit e0: GR[Int], e1: GR[String], e2: GR[Long]): GR[rVideocomments] = GR{
    prs => import prs._
    rVideocomments.tupled((<<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[Int], <<[Long]))
  }
  /** Table description of table videocomments. Objects of this class serve as prototypes for rows in queries. */
  class tVideocomments(_tableTag: Tag) extends profile.api.Table[rVideocomments](_tableTag, "videocomments") {
    def * = (commentid, vid, commentuserid, commentusername, commentcontent, likecount, commenttime) <> (rVideocomments.tupled, rVideocomments.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(commentid), Rep.Some(vid), Rep.Some(commentuserid), Rep.Some(commentusername), Rep.Some(commentcontent), Rep.Some(likecount), Rep.Some(commenttime)).shaped.<>({r=>import r._; _1.map(_=> rVideocomments.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column commentid SqlType(serial), AutoInc, PrimaryKey */
    val commentid: Rep[Int] = column[Int]("commentid", O.AutoInc, O.PrimaryKey)
    /** Database column vid SqlType(int4), Default(0) */
    val vid: Rep[Int] = column[Int]("vid", O.Default(0))
    /** Database column commentuserid SqlType(int4), Default(0) */
    val commentuserid: Rep[Int] = column[Int]("commentuserid", O.Default(0))
    /** Database column commentusername SqlType(varchar), Length(255,true), Default() */
    val commentusername: Rep[String] = column[String]("commentusername", O.Length(255,varying=true), O.Default(""))
    /** Database column commentcontent SqlType(varchar), Length(255,true), Default() */
    val commentcontent: Rep[String] = column[String]("commentcontent", O.Length(255,varying=true), O.Default(""))
    /** Database column likecount SqlType(int4), Default(0) */
    val likecount: Rep[Int] = column[Int]("likecount", O.Default(0))
    /** Database column commenttime SqlType(int8), Default(0) */
    val commenttime: Rep[Long] = column[Long]("commenttime", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tVideocomments */
  lazy val tVideocomments = new TableQuery(tag => new tVideocomments(tag))

  /** Entity class storing rows of table tVideoinfo
   *  @param vid Database column vid SqlType(serial), AutoInc, PrimaryKey
   *  @param vname Database column vname SqlType(varchar), Length(255,true), Default()
   *  @param vuperid Database column vuperid SqlType(int4), Default(0)
   *  @param vupername Database column vupername SqlType(varchar), Length(255,true), Default()
   *  @param vtype Database column vtype SqlType(varchar), Length(255,true), Default()
   *  @param vuptime Database column vuptime SqlType(int8), Default(0)
   *  @param vlikecount Database column vlikecount SqlType(int4), Default(0)
   *  @param vclickcount Database column vclickcount SqlType(int4), Default(0)
   *  @param vdir Database column vdir SqlType(varchar), Length(255,true) */
  case class rVideoinfo(vid: Int, vname: String = "", vuperid: Int = 0, vupername: String = "", vtype: String = "", vuptime: Long = 0L, vlikecount: Int = 0, vclickcount: Int = 0, vdir: String)
  /** GetResult implicit for fetching rVideoinfo objects using plain SQL queries */
  implicit def GetResultrVideoinfo(implicit e0: GR[Int], e1: GR[String], e2: GR[Long]): GR[rVideoinfo] = GR{
    prs => import prs._
    rVideoinfo.tupled((<<[Int], <<[String], <<[Int], <<[String], <<[String], <<[Long], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table videoinfo. Objects of this class serve as prototypes for rows in queries. */
  class tVideoinfo(_tableTag: Tag) extends profile.api.Table[rVideoinfo](_tableTag, "videoinfo") {
    def * = (vid, vname, vuperid, vupername, vtype, vuptime, vlikecount, vclickcount, vdir) <> (rVideoinfo.tupled, rVideoinfo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(vid), Rep.Some(vname), Rep.Some(vuperid), Rep.Some(vupername), Rep.Some(vtype), Rep.Some(vuptime), Rep.Some(vlikecount), Rep.Some(vclickcount), Rep.Some(vdir)).shaped.<>({r=>import r._; _1.map(_=> rVideoinfo.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column vid SqlType(serial), AutoInc, PrimaryKey */
    val vid: Rep[Int] = column[Int]("vid", O.AutoInc, O.PrimaryKey)
    /** Database column vname SqlType(varchar), Length(255,true), Default() */
    val vname: Rep[String] = column[String]("vname", O.Length(255,varying=true), O.Default(""))
    /** Database column vuperid SqlType(int4), Default(0) */
    val vuperid: Rep[Int] = column[Int]("vuperid", O.Default(0))
    /** Database column vupername SqlType(varchar), Length(255,true), Default() */
    val vupername: Rep[String] = column[String]("vupername", O.Length(255,varying=true), O.Default(""))
    /** Database column vtype SqlType(varchar), Length(255,true), Default() */
    val vtype: Rep[String] = column[String]("vtype", O.Length(255,varying=true), O.Default(""))
    /** Database column vuptime SqlType(int8), Default(0) */
    val vuptime: Rep[Long] = column[Long]("vuptime", O.Default(0L))
    /** Database column vlikecount SqlType(int4), Default(0) */
    val vlikecount: Rep[Int] = column[Int]("vlikecount", O.Default(0))
    /** Database column vclickcount SqlType(int4), Default(0) */
    val vclickcount: Rep[Int] = column[Int]("vclickcount", O.Default(0))
    /** Database column vdir SqlType(varchar), Length(255,true) */
    val vdir: Rep[String] = column[String]("vdir", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tVideoinfo */
  lazy val tVideoinfo = new TableQuery(tag => new tVideoinfo(tag))
}
