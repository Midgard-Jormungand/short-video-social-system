package com.neo.sk.utils

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

/**
  * User: Taoz
  * Date: 2/9/2015
  * Time: 4:33 PM
  */
object DBUtil {
  val log = LoggerFactory.getLogger(this.getClass)
  private val dataSource = createDataSource()

  import com.neo.sk.svss.common.AppSettings._

  private def createDataSource() = {

    val dataSource = new org.postgresql.ds.PGSimpleDataSource()

    //val dataSource = new MysqlDataSource()

    log.info(s"connect to db: $slickUrl")
    dataSource.setUrl(slickUrl)
    dataSource.setUser(slickUser)
    dataSource.setPassword(slickPassword)

    val hikariDS = new HikariDataSource()
    hikariDS.setDataSource(dataSource)
    hikariDS.setMaximumPoolSize(slickMaximumPoolSize)
    hikariDS.setConnectionTimeout(slickConnectTimeout)
    hikariDS.setIdleTimeout(slickIdleTimeout)
    hikariDS.setMaxLifetime(slickMaxLifetime)
    hikariDS.setAutoCommit(true)
    hikariDS
  }


  val db = Database.forDataSource(dataSource, Some(slickMaximumPoolSize))




}