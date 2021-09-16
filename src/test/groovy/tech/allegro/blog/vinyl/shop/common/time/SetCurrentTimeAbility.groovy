package tech.allegro.blog.vinyl.shop.common.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

trait SetCurrentTimeAbility {

    static final Instant DEFAULT_CURRENT_DATE = Instant.parse("2021-11-05T00:00:00.00Z")

    void setDefaultCurrentTime() {
        ClockProvider.setSystemClock(Clock.fixed(DEFAULT_CURRENT_DATE, ZoneId.systemDefault()))
    }

    def setCurrentTime(String dateTimeText) {
        Instant currentTime = Instant.parse(dateTimeText)
        Clock.fixed(currentTime, ZoneId.systemDefault())
    }
}