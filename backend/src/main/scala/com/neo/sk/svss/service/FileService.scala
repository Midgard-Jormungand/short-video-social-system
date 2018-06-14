package com.neo.sk.svss.service

import akka.http.scaladsl.server.Directives.{complete, entity, _}
import com.neo.sk.svss.shared.ptcl._
import com.neo.sk.utils.FileUtil
import io.circe.generic.auto._
import io.circe.Error
import akka.http.scaladsl.server.{Directive1, Route}
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import com.neo.sk.svss.shared.ptcl.VideoProtocol._
import org.slf4j.LoggerFactory
import scala.sys.process._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Left, Success}
import com.neo.sk.svss.models.dao._

trait FileService extends BaseService{
  private val log = LoggerFactory.getLogger(this.getClass)
  private def storeFile(source: Source[ByteString, Any]): Directive1[java.io.File] = {
    val dest = java.io.File.createTempFile("svss", ".tmp")
    val file = source.runWith(FileIO.toPath(dest.toPath)).map(_ => dest)
    onComplete[java.io.File](file).flatMap {
      case Success(f) =>
        provide(f)
      case Failure(e) =>
        dest.deleteOnExit()
        failWith(e)
    }
  }
  val fileRoutes:Route= {
    (path("uploadFile") & post) {
      parameter(
        'vname.as[String],
        'vtype.as[String]) {
        (vname,vtype) =>
          parseUserSession {
            session =>
              fileUpload("fileUpload") {
                case (fileInfo, file) =>
                  storeFile(file) { f =>
                    dealFutureResult {
                      val fileExtension=fileInfo.getContentType.mediaType.fileExtensions.head
                      viDao.addVideo(vname,session.userId.toInt,session.name,vtype,System.currentTimeMillis(),fileExtension).map {
                        id =>
                          val fileName=id + "." + fileExtension
                          val videoFilePath="video/"+fileName
                          val  imgFilePath="vImg/"+id+".jpg"
                          FileUtil.storeFile(fileName, f)
                          f.deleteOnExit()
                          Seq("ffmpeg","-i",videoFilePath,"-y","-f","mjpeg","-y","-f","mjpeg","-ss","1","-t","0.001",imgFilePath).!
                          complete(UploadRsp(id))
                      }.recover {
                        case x: Exception =>
                          log.warn(s"uploadFile error,${x.getMessage}")
                          complete(UploadRsp(0,2, s"uploadFile error:${x.getMessage}"))
                      }
                    }
                  }
              }
          }
      }
    }~(path("deleteVideo") & post & pathEndOrSingleSlash) {
      parseUserSession { session =>
        entity(as[Either[Error, deleteVideoReq]]) {
          case Right(p) =>
            dealFutureResult {
              viDao.deleteVideo(p.id).map {
                rst=>
                  FileUtil.deleteFile(p.id+"_"+rst._1,p.id+".jpg")
                  complete(SuccessRsp())
              }.recover {
                case x: Exception =>
                  log.warn(s"deleteVideo error,${x.getMessage}")
                  complete(ErrorRsp(3, s"deleteVideo error:${x.getMessage}"))
              }
            }
          case Left(e) =>
            complete(ErrorRsp(2, s"$e"))
        }
      }
    }
  }
}



