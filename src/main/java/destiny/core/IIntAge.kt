/**
 * Created by smallufo on 2017-10-15.
 */
package destiny.core

import destiny.core.calendar.ILocation
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 取得一張命盤（不論是八字、斗數、或是占星）
 * 的「整數、歲數」範圍
 * <pre>
 * 例如：
 * 八字的「一歲」指的是「出生之時」到「下一個立春」為止 , 日後均以「立春」為界
 * 斗數的「一歲」指的是「出生之時」到「下一個大年初一」為止 , 日後均以「大年初一」為界
 * 日本、韓國的「一歲」指的是「出生之時」到「西元年底」為止，日後均以西元年份為界
 * 韓國另有另一種「一歲」指的是「出生之時」到「下一個三月一日」為止，日後均以「三月初一」為界
</pre> *
 */
interface IIntAge {

  /**
   * 此時刻出生的某人，在第幾歲時，範圍為何
   * @return 前者為 prior GMT 時刻，後者為 later GMT 時刻
   * */
  fun getRange(gender: Gender, gmtJulDay: Double, loc: ILocation, age: Int): Pair<Double, Double>

  /** 承上 , 傳回 [ChronoLocalDateTime] 版本 */
  fun getRangeTime(gender: Gender, gmtJulDay: Double, loc: ILocation, age: Int, revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): Pair<ChronoLocalDateTime<*>, ChronoLocalDateTime<*>> {
    return getRange(gender, gmtJulDay, loc, age).let { pair ->
      Pair(revJulDayFunc.invoke(pair.first), revJulDayFunc.invoke(pair.second))
    }
  }

  /** 列出 fromAge 到 toAge 的結果 */
  fun getRanges(gender: Gender, gmtJulDay: Double, loc: ILocation, fromAge: Int, toAge: Int): List<Pair<Double, Double>>

  /** 承上 , 列出 fromAge 到 toAge 的結果 (GMT) , 傳回 Map[Age , Pair[from , to]] */
  fun getRangesMap(gender: Gender, gmtJulDay: Double, loc: ILocation, fromAge: Int, toAge: Int): Map<Int, Pair<Double, Double>> {
    val list = getRanges(gender, gmtJulDay, loc, fromAge, toAge)

    return (fromAge .. toAge).zip(list).toMap()
  }

  /** 承上 , 列出 fromAge 到 toAge 的結果 , 傳回 Map[Age , Pair[from , to]] , 傳回的是 {@link ChronoLocalDateTime} , GMT 時刻 */
  fun getRangesGmtMap(gender: Gender, gmtJulDay: Double, loc: Location, fromAge: Int, toAge: Int, revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): Map<Int, Pair<ChronoLocalDateTime<*>, ChronoLocalDateTime<*>>> {
    return getRangesMap(gender, gmtJulDay, loc, fromAge, toAge)
      .mapValues { entry ->
        entry.value.let { pair ->
          Pair(revJulDayFunc.invoke(pair.first), revJulDayFunc.invoke(pair.second))
        }
      }
  }

  /** 承上 , 列出 fromAge 到 toAge 的結果 , 傳回 Map[Age , Pair[from , to]] , 傳回的是 {@link ChronoLocalDateTime} , LMT 時刻 */
  fun getRangesLmtMap(gender: Gender, gmtJulDay: Double, loc: Location, fromAge: Int, toAge: Int, revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): Map<Int, Pair<ChronoLocalDateTime<*>, ChronoLocalDateTime<*>>> {
    return getRangesMap(gender, gmtJulDay, loc, fromAge, toAge)
      .mapValues { entry ->
        entry.value.let { pair ->
          Pair(
            TimeTools.getLmtFromGmt(pair.first, loc, revJulDayFunc),
            TimeTools.getLmtFromGmt(pair.second, loc, revJulDayFunc)
          )
        }
      }
  }
}
