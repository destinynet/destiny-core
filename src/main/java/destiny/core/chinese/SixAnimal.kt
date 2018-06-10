/**
 * Created by smallufo on 2018-01-28.
 */
package destiny.core.chinese

import destiny.core.ILoop
import destiny.tools.ArrayTools

enum class SixAnimal : ILoop<SixAnimal> {
  青龍,
  朱雀,
  勾陳,
  螣蛇,
  白虎,
  玄武;

  override fun next(n: Int): SixAnimal {
    return get(getIndex(this) + n)
  }

  companion object {
    //private val ARRAY = arrayOf(青龍, 朱雀, 勾陳, 螣蛇, 白虎, 玄武)

    fun get(stem : Stem) : SixAnimal {
      return when(stem.fiveElement) {
        FiveElement.木 -> 青龍
        FiveElement.火 -> 朱雀
        FiveElement.土 -> if (stem.booleanValue) 勾陳 else 螣蛇
        FiveElement.金 -> 白虎
        FiveElement.水 -> 玄武
      }
    }

    operator fun get(index: Int): SixAnimal {
      return ArrayTools[values(), index]
    }

    private fun getIndex(animal: SixAnimal): Int {
      return values().indexOf(animal)
    }
  }
}