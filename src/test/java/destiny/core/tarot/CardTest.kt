/**
 * Created by smallufo on 2023-04-01.
 */
package destiny.core.tarot

import destiny.core.EnumTest
import mu.KotlinLogging
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertNotNull

class CardTest : EnumTest() {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testString() {
    testEnums(Card::class, locales = listOf(Locale.TAIWAN, Locale.ENGLISH, Locale.JAPAN))
  }

  @Test
  fun testImages() {
    Card.values().forEach { c ->
      val res = "/destiny/core/tarot/waite/${c.suit}/${c.name}.jpeg"
      val img: BufferedImage = ImageIO.read(javaClass.getResource(res))
      assertNotNull(img)
      logger.info { "$res : ${img.width} x ${img.height}" }
    }
  }
}
