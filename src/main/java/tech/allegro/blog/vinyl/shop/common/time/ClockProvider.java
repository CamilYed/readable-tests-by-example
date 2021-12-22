package tech.allegro.blog.vinyl.shop.common.time;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.Clock;

@AllArgsConstructor
public class ClockProvider {
  @Setter(AccessLevel.PRIVATE)
  private Clock clock;

  private static final ClockProvider INSTANCE = new ClockProvider(Clock.systemDefaultZone());

  public static Clock systemClock() {
    return INSTANCE.clock;
  }

  public static void setSystemClock(Clock clock) {
    INSTANCE.setClock(clock);
  }
}
