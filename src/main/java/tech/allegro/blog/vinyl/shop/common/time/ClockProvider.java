package tech.allegro.blog.vinyl.shop.common.time;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.Clock;

@AllArgsConstructor
public class ClockProvider {
  @Setter
  private Clock clock;

  private static final ClockProvider INSTANCE = new ClockProvider(Clock.systemDefaultZone());

  public static Clock systemClock() {
    return INSTANCE.clock;
  }

  public static void setSystemClock(Clock clock) {
    INSTANCE.setClock(clock);
  }
}
