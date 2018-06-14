package com.neo.sk.utils

import java.io._
import java.net.URLEncoder
import com.neo.sk.svss.common.AppSettings
import akka.util.ByteString
import org.slf4j.LoggerFactory

/**
  * Created by dry on 2018/3/12.
  **/
object FileUtil {
  private val log = LoggerFactory.getLogger(this.getClass)

  def getFilePath(id:Long,name:String) = {
    AppSettings.storeFilePath + "/" + getFileName(id,name)
  }

  def getFileName(id:Long,name:String) = {
    val index = name.lastIndexOf(".")
    val p = if(index < 0) "" else name.substring(index)
    id + p
  }


  def getConvertJobId(id:Long,name:String) = {
    id+"_"+URLEncoder.encode(name,"UTF-8")
  }

  def storeFile(fileInfo: String, file:File) = {
    val fileDir = AppSettings.storeFilePath
    val filePath = fileDir  + "/" + fileInfo
    val dir = new File(fileDir)
    if(!dir.exists()) dir.mkdirs()
    val dest = new File(filePath)
    if(dest.exists()) dest.delete()
    copyFile(dest,file)
    if(file.exists()) file.delete()
  }

  def storeFile(dest: String,input:String) = {
    val dir = new File(AppSettings.storeFilePath)
    if(!dir.exists()) dir.mkdirs()
    val destF = new File(dest)
    if(destF.exists()) destF.delete()
    val inputF = new File(input)
    if(inputF.exists()) copyFile(destF,inputF)
  }

  def storeFile(dest:String,input:List[ByteString]) = {
    val dir = new File(AppSettings.storeFilePath)
    if(!dir.exists()) dir.mkdirs()
    val destF = new File(dest)
    if(destF.exists()) destF.delete()
    copyFile(destF,input)
  }

  def copyFile(dest:File,source:List[ByteString]) = {
    var out:FileOutputStream = null
    try{
      out = new FileOutputStream(dest)
      source.foreach(s => out.write(s.toArray))
    }catch{
      case e:Exception =>
        log.error(s"copy to file ${dest.getName} error",e)
    }finally {
      if(out!=null) out.close()
    }
  }

  def copyFile(dest:File,source:File) = {
    var in:InputStream = null
    var out:OutputStream = null
    try{
      in = new FileInputStream(source)
      out = new FileOutputStream(dest)
      val buffer = new Array[Byte](1024)
      var byte = in.read(buffer)
      while(byte >= 0){
        out.write(buffer,0,byte)
        byte = in.read(buffer)
      }
    }catch{
      case e:Exception =>
        log.error(s"copy file ${source.getName} error",e)
    }finally {
      if(in!=null) in.close()
      if(out!=null) out.close()
    }
  }

  def deleteFile(fileName:String,imgName:String) = {
    val file = new File(AppSettings.storeFilePath + "/" + fileName)
    if(file.exists()) file.delete()
    val imgFile = new File(AppSettings.storeImgPath + "/" + imgName)
    if(imgFile.exists()) imgFile.delete()
  }
  def getFile(name:String, targetDir: String) = {
    val file = new File(AppSettings.storeFilePath + targetDir + "/" + name)
    file
  }
}
