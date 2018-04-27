/**
 * Created by smallufo on 2017-10-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms.立春
import java.io.Serializable

/**
 * 八字的「虛歲」大運
 * 出生當下，即為一歲。（故， age 不可以 <= 0）
 * 「一歲」終止於「順推」的立春之時
 */
class IntAge8wImpl(private val solarTermsImpl: ISolarTerms) : IIntAge, Serializable {

  private data class CacheKey(val gender: Gender,
                              val gmtJulDay: Double,
                              val loc: ILocation,
                              val fromAge: Int,
                              val toAge: Int)

  private val cacheThreadLocal = ThreadLocal<Pair<CacheKey, List<Pair<Double, Double>>>>()

  override fun getRange(gender: Gender, gmtJulDay: Double, loc: ILocation, age: Int): Pair<Double, Double> {
    val age1 = Pair(gmtJulDay, solarTermsImpl.getSolarTermsTime(立春, gmtJulDay, true))

    return getRangeInner(age1, age)
  }

  private fun getRangeInner(prevResult: Pair<Double, Double>, count: Int): Pair<Double, Double> {
    return if (count == 1) {
      prevResult
    } else {
      val stepDay = prevResult.first + 1 // 取「立春日+1」作為 臨時的日子，以此日子，分別往 prior , after 推算立春日期
      val start = solarTermsImpl.getSolarTermsTime(立春, stepDay, false)
      val end = solarTermsImpl.getSolarTermsTime(立春, stepDay, true)
      getRangeInner(Pair(start, end), count - 1)
    }
  }

  override fun getRanges(gender: Gender,
                         gmtJulDay: Double,
                         loc: ILocation,
                         fromAge: Int,
                         toAge: Int): List<Pair<Double, Double>> {
    require(fromAge <= toAge) { "fromAge($fromAge) must be <= toAge($toAge)" }

    val key = CacheKey(gender, gmtJulDay, loc, fromAge, toAge)
    val pair = cacheThreadLocal.get()

    fun innerGetList() : List<Pair<Double, Double>> {
      val from = getRange(gender, gmtJulDay, loc, fromAge)
      val result = mutableListOf<Pair<Double, Double>>().apply { add(from) }
      return getRangesInner(result, toAge - fromAge)
    }

    if (pair == null) {
      // pair is null
      val list = innerGetList()
      cacheThreadLocal.set(key to list)
      return list
    } else {
      // pair is not null
      return if (key == pair.first) {
        // cache matches
        pair.second
      } else {
        // cache not matches
        val list = innerGetList()
        cacheThreadLocal.set(key to list)
        list
      }
    }

  }

  private fun getRangesInner(prevResults: MutableList<Pair<Double, Double>>, count: Int): List<Pair<Double, Double>> {
    return if (count == 0) {
      prevResults
    } else {
      val (_, second) = prevResults[prevResults.size - 1]
      val stepDay = second + 1
      val start = solarTermsImpl.getSolarTermsTime(立春, stepDay, false)
      val end = solarTermsImpl.getSolarTermsTime(立春, stepDay, true)
      val newResult = Pair(start, end)
      prevResults.add(newResult)
      getRangesInner(prevResults, count - 1)
    }
  }


}
