/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.fengshui

import kotlin.test.Test

class MountainTest {

  @Test
  fun opposite() {
    Mountain.values().forEach { mnt ->
      println("mnt = $mnt , index = ${mnt.index} , oppo = ${mnt.opposite}" )
    }
  }
}