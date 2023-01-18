package com.resy

import akka.actor.ActorSystem
import org.apache.logging.log4j.scala.Logging
import org.joda.time.DateTime
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object ResyBookingBot extends Logging {

  def main(args: Array[String]): Unit = {
    logger.info("Starting Resy Booking Bot")

    val resyConfig = ConfigSource.resources("resyConfig.conf")
    val resyKeys   = resyConfig.at("resyKeys").loadOrThrow[ResyKeys]
    val resDetails = resyConfig.at("resDetails").loadOrThrow[ReservationDetails]
    val snipeTime  = resyConfig.at("snipeTime").loadOrThrow[SnipeTime]

    val resyApi             = new ResyApi(resyKeys)
    val resyClient          = new ResyClient(resyApi)
    val resyBookingWorkflow = new ResyBookingWorkflow(resyClient, resDetails)

    val system      = ActorSystem("System")
    val dateTimeNow = DateTime.now
    val nextSnipeTime = dateTimeNow
      .withDayOfMonth(snipeTime.day)
      .withMonthOfYear(snipeTime.month)
      .withYear(snipeTime.year)
      .withHourOfDay(snipeTime.hours)
      .withMinuteOfHour(snipeTime.minutes)
      .withSecondOfMinute(0)
      .withMillisOfSecond(0)

    val millisUntilSnipeTime = nextSnipeTime.getMillis - DateTime.now.getMillis - 2000
    val hoursRemaining      = millisUntilSnipeTime / 1000 / 60 / 60
    val minutesRemaining    = millisUntilSnipeTime / 1000 / 60 - hoursRemaining * 60
    val secondsRemaining =
      millisUntilSnipeTime / 1000 - hoursRemaining * 60 * 60 - minutesRemaining * 60

    logger.info(s"Next snipe time: $nextSnipeTime")
    logger.info(
      s"Sleeping for $hoursRemaining hours, $minutesRemaining minutes, and $secondsRemaining seconds"
    )

    system.scheduler.scheduleOnce(millisUntilSnipeTime millis) {
      resyBookingWorkflow.run()

      logger.info("Shutting down Resy Booking Bot")
      System.exit(0)
    }
  }
}
