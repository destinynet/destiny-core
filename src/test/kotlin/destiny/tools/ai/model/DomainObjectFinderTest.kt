/**
 * Created by smallufo on 2024-07-01.
 */
package destiny.tools.ai.model

import kotlin.test.Test
import kotlin.test.assertSame

class DomainObjectFinderTest {

  @Test
  fun testFindDomainObjects() {
    assertSame(Domain.Bdnp.EW, DomainObjectFinder.findDomainObject("EW"))
    assertSame(Domain.Bdnp.ZIWEI, DomainObjectFinder.findDomainObject("ZIWEI"))
    assertSame(Domain.Bdnp.HOROSCOPE, DomainObjectFinder.findDomainObject("HOROSCOPE"))
    assertSame(Domain.ICHING_RAND, DomainObjectFinder.findDomainObject("ICHING_RAND"))
    assertSame(Domain.TAROT, DomainObjectFinder.findDomainObject("TAROT"))
    assertSame(Domain.CHANCE, DomainObjectFinder.findDomainObject("CHANCE"))
    assertSame(Domain.FENGSHUI_AERIAL, DomainObjectFinder.findDomainObject("FENGSHUI_AERIAL"))
    assertSame(Domain.Period.DAILY_HOROSCOPE, DomainObjectFinder.findDomainObject("DAILY_HOROSCOPE"))
  }
}
