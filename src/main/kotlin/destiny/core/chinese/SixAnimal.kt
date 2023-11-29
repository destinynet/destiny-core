/**
 * Created by smallufo on 2018-01-28.
 */
package destiny.core.chinese

import destiny.core.ILoop
import destiny.tools.ArrayTools

enum class SixAnimal(val shortName:String) : ILoop<SixAnimal> {
  青龍("龍"),
  朱雀("雀"),
  勾陳("勾"),
  螣蛇("蛇"),
  白虎("虎"),
  玄武("玄");

  override fun next(n: Int): SixAnimal {
    return get(getIndex(this) + n)
  }

  companion object {

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
      return ArrayTools[entries.toTypedArray(), index]
    }

    fun getSixAnimals(dayStem: Stem): List<SixAnimal> {
      var count = 0
      return generateSequence { SixAnimal.get(dayStem).next(count++) }
        .take(6)
        .toList()
    }

    private fun getIndex(animal: SixAnimal): Int {
      return entries.indexOf(animal)
    }
  }
}
