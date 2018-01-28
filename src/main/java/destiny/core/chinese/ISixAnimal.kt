/**
 * Created by smallufo on 2018-01-28.
 */
package destiny.core.chinese

object ISixAnimal {

  fun getSixAnimals(dayStem: Stem): List<SixAnimal> {
    var count = 0
    return generateSequence { SixAnimal.get(dayStem).next(count++) }
      .take(6)
      .toList()
  }
}