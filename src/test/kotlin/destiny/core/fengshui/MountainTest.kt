/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.core.fengshui

import destiny.tools.KotlinLogging
import kotlin.test.Test

class MountainTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun opposite() {
    Mountain.entries.forEach { mnt ->
      logger.trace {
        """mnt = $mnt , index = ${mnt.index} , oppo = ${mnt.opposite}"""
      }
    }
  }
}
