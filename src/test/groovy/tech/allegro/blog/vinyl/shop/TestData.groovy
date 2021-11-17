package tech.allegro.blog.vinyl.shop

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.common.money.Money

import java.time.Instant

class TestData {
  public static final Instant DEFAULT_CURRENT_DATE = Instant.parse("2021-11-05T00:00:00.00Z")
  public static final String ORDER_ID = "ORDER_ID_001"
  public static final String CLIENT_ID = "CLIENT_ID_001"
  public static final String EURO_CURRENCY_CODE = "EUR"
  public static final Currency EURO_CURRENCY = Currency.getInstance(EURO_CURRENCY_CODE)
  public static final Money EUR_40 = Money.of(40.00, EURO_CURRENCY_CODE)
  public static final String CZESLAW_NIEMEN_ALBUM_ID = "PRODUCT_ID_001"
  public static final String BOHEMIAN_RHAPSODY_ALBUM_ID = "PRODUCT_ID_002"
  public static final VinylId VINYL_CZESLAW_NIEMEN_ID = new VinylId(CZESLAW_NIEMEN_ALBUM_ID)
  public static final Vinyl VINYL_CZESLAW_NIEMEN = new Vinyl(VINYL_CZESLAW_NIEMEN_ID, EUR_40)

}
