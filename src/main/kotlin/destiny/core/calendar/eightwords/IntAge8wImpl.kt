/**
 * Created by smallufo on 2017-10-20.
 */
package destiny.core.calendar.eightwords

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms.立春
import java.io.Serializable
import java.util.concurrent.TimeUnit

/**
 * 八字的「虛歲」大運
 * 出生當下，即為一歲。（故， age 不可以 <= 0）
 * 「一歲」終止於「順推」的立春之時
 */
class IntAge8wImpl(private val solarTermsImpl: ISolarTerms) : IIntAge, Serializable {

  private data class CacheKey(val gender: Gender,
                              val gmtJulDay: GmtJulDay,
                              val loc: ILocation,
                              val fromAge: Int,
                              val toAge: Int)


  private val cache : Cache<CacheKey, List<Pair<GmtJulDay, GmtJulDay>>> = Caffeine.newBuilder()
    .maximumSize(100)
    //.expireAfterWrite(5 , TimeUnit.SECONDS)
    .expireAfterAccess(10 , TimeUnit.SECONDS)
    .build()

  override fun getRange(gender: Gender, gmtJulDay: GmtJulDay, loc: ILocation, age: Int): Pair<GmtJulDay, GmtJulDay> {
    val age1: Pair<GmtJulDay, GmtJulDay> = Pair(gmtJulDay, solarTermsImpl.getSolarTermsTime(立春, gmtJulDay, true))

    return getRangeInner(age1, age)
  }

  private fun getRangeInner(prevResult: Pair<GmtJulDay, GmtJulDay>, count: Int): Pair<GmtJulDay, GmtJulDay> {
    return if (count == 1) {
      prevResult
    } else {
      val stepDay = prevResult.second + 1 // 取「立春日+1」作為 臨時的日子，以此日子，分別往 prior , after 推算立春日期
      val start = solarTermsImpl.getSolarTermsTime(立春, stepDay, false)
      val end = solarTermsImpl.getSolarTermsTime(立春, stepDay, true)
      getRangeInner(Pair(start, end), count - 1)
    }
  }

  override fun getRanges(gender: Gender,
                         gmtJulDay: GmtJulDay,
                         loc: ILocation,
                         fromAge: Int,
                         toAge: Int): List<Pair<GmtJulDay, GmtJulDay>> {
    require(fromAge <= toAge) { "fromAge($fromAge) must be <= toAge($toAge)" }

    val key = CacheKey(gender, gmtJulDay, loc, fromAge, toAge)


    fun innerGetList() : List<Pair<GmtJulDay, GmtJulDay>> {
      val from = getRange(gender, gmtJulDay, loc, fromAge)
      val result = mutableListOf<Pair<GmtJulDay, GmtJulDay>>().apply { add(from) }
      return getRangesInner(result, toAge - fromAge)
    }

    return cache.get(key) {innerGetList()}

  }

  private fun getRangesInner(prevResults: MutableList<Pair<GmtJulDay, GmtJulDay>>, count: Int): List<Pair<GmtJulDay, GmtJulDay>> {
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
