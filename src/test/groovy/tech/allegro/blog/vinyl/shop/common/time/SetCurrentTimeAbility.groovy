package tech.allegro.blog.vinyl.shop.common.time

import tech.allegro.blog.vinyl.shop.TestData

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

trait SetCurrentTimeAbility {

  void setDefaultCurrentTime() {
    ClockProvider.setSystemClock(Clock.fixed(TestData.DEFAULT_CURRENT_DATE, ZoneId.systemDefault()))
  }

  def setCurrentTime(String dateTimeText) {
    Instant currentTime = Instant.parse(dateTimeText)
    Clock.fixed(currentTime, ZoneId.systemDefault())
  }
}
