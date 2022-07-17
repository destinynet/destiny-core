/**
 * Created by smallufo on 2022-07-17.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords


class HazardService : java.io.Serializable {

  fun getChildHazards(eightWords: IEightWords, gender: Gender?): List<Pair<ChildHazard, Book>> {

    return childHazardFactories.flatMap { factory ->
      factory.impls.filter { iHazard ->
        iHazard.test(eightWords, gender)
      }.flatMap { iHazard: IHazard ->
        iHazard.getBooks().map { b -> factory.hazard to b  }
      }
    }
  }

  companion object {
    val childHazardFactories: Set<IHazardFactory> = setOf(
      h百日關,
      h千日關,
      h閻王關,
      h鬼門關,
      h雞飛關,
      h鐵蛇關,
      h斷橋關,
      h落井關,
      h四柱關,
      h短命關,
      h浴盆關,
      h湯火關,
      h水火關,
      h深水關,
      h夜啼關,
      h白虎關,
      h天狗關,
      h四季關,
      h急腳關,
      h急腳煞,
      h五鬼關,
      h金鎖關,
      h直難關,
      h取命關,
      h斷腸關,
      h埋兒關,
      h天吊關,
      h和尚關,
      h撞命關,
      h下情關,
      h劫煞關,
      h血刃關,
      h基敗關,
      h將軍箭,
      h雷公關,
    )

  }
}
