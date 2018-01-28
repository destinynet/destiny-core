/**
 * Created by smallufo on 2018-01-28.
 */
package destiny.core.chinese

import destiny.tools.ArrayTools

enum class SixAnimal {
  青龍,
  朱雀,
  勾陳,
  螣蛇,
  白虎,
  玄武;

  fun next(n: Int): SixAnimal {
    return get(getIndex(this) + n)
  }

  fun next(): SixAnimal {
    return get(getIndex(this) + 1)
  }


  fun prev(n: Int): SixAnimal {
    return get(getIndex(this) - n)
  }

  fun prev(): SixAnimal {
    return get(getIndex(this) - 1)
  }


  companion object {
    private val ARRAY = arrayOf(青龍, 朱雀, 勾陳, 螣蛇, 白虎, 玄武)

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
      return ArrayTools[ARRAY, index]
    }

    private fun getIndex(animal: SixAnimal): Int {
      return ARRAY.binarySearch(animal)
    }
  }
}