create table userinfo
(
  uid serial not null
    constraint userinfo_pkey
    primary key,
  uname varchar(255) default ''::character varying not null,
  upassword varchar(255) default ''::character varying not null,
  sex smallint default 0 not null,
  signature varchar(255) default ''::character varying not null,
  signuptime bigint default 0 not null
)
;

comment on table userinfo is '用户信息表'
;

create table videoinfo
(
  vid serial not null
    constraint videoinfo_pkey
    primary key,
  vname varchar(255) default ''::character varying not null,
  vuperid integer default 0 not null,
  vupername varchar(255) default ''::character varying not null,
  vtype varchar(255) default ''::character varying not null,
  vuptime bigint default 0 not null,
  vlikecount integer default 0 not null,
  vclickcount integer default 0 not null,
  vdir varchar(255) not null
)
;

create index videoinfo_vuperid_index
  on videoinfo (vuperid)
;

comment on table videoinfo is '视频信息表'
;

create table videocomments
(
  commentid serial not null
    constraint videocomments_commentid_pk
    primary key,
  vid integer default 0 not null,
  commentuserid integer default 0 not null,
  commentusername varchar(255) default ''::character varying not null,
  commentcontent varchar(255) default ''::character varying not null,
  likecount integer default 0 not null,
  commenttime bigint default 0 not null
)
;

create index videocomments_vid_index
  on videocomments (vid)
;

create index videocomments_commentuserid_index
  on videocomments (commentuserid)
;

comment on table videocomments is '视频评论信息表'
;

create table followinfo
(
  id serial not null
    constraint followinfo_pkey
    primary key,
  followid integer default 0 not null,
  followname varchar(255) default ''::character varying not null,
  followsign varchar(255) default ''::character varying not null,
  befollowedid integer default 0 not null,
  befollowedname varchar(255) default ''::character varying not null,
  befollowedsign varchar(255) default ''::character varying not null,
  followtime bigint default 0 not null
)
;

create index followinfo_followid_index
  on followinfo (followid)
;

create index followinfo_befollowedid_index
  on followinfo (befollowedid)
;

comment on table followinfo is '关注信息表'
;

create table likecommentinfo
(
  id serial not null
    constraint likecommentinfo_id_pk
    primary key,
  uid integer default 0 not null,
  commentid integer default 0 not null
)
;

create index likecommentinfo_uid_index
  on likecommentinfo (uid)
;

create index likecommentinfo_commentid_index
  on likecommentinfo (commentid)
;

comment on table likecommentinfo is '视频评论点赞信息表'
;

create table likevideoinfo
(
  id serial not null
    constraint likevideoinfo_id_pk
    primary key,
  userid integer default 0 not null,
  videoid integer default 0 not null,
  liketime bigint default 0 not null
)
;

create index likevideoinfo_userid_index
  on likevideoinfo (userid)
;

create index likevideoinfo_videoid_index
  on likevideoinfo (videoid)
;

comment on table likevideoinfo is '视频点赞信息表'
;

