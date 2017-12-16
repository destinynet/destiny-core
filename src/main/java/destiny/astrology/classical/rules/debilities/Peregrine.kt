/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:53:40
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*
import destiny.astrology.classical.Dignity
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * Peregrine : 漂泊、茫游、外出狀態
 *
 * http://www.skyscript.co.uk/gl/peregrine.html
 *
 * A planet is defined as peregrine when it has no level of rulership over its position.
 * That is, it is not placed in the sign(s) that it rules, nor those where it is exalted,
 * nor does it rule the triplicity, or the terms or face where it is located.
 *
 * 並非處於 ruler / exalt / triplicity / terms / faces 之類的狀態
 *
 * The word 'Peregrine' comes from a Latin term meaning 'alien' or 'foreigner'
 * (pereger = beyond the borders, ager = land, i.e., 'beyond one's own land').
 * In old English, to 'peregrinate' means to wander far from home.
 *
 * 意味：不在家
 *
 * Such a planet is therefore seen as having little influence or control over its environment.
 * In symbolic terms, it describes a drifter (流浪漢) - someone with no title or stake in his or
 * her environment.
 *
 * In matters of theft, for example, peregrine planets fall under suspicion in the same way that
 * strangers are often viewed with suspicion
 *
 * 如同我們看流浪漢ㄧ樣，抱持著懷疑的態度
 *
 * In other matters it might portray someone who lacks a clear sense of focus, a sense of feeling
 * 'lost' or on the outside of community thinking; or an inability to identify clear goals or
 * offer resolute commitments to others.
 *
 * 生活缺乏目標
 *
 */
class Peregrine(
  /** 計算白天黑夜的實作  */
  private val dayNightImpl: DayNightDifferentiator) : EssentialRule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg: Double? = h.getPosition(planet)?.lng
    val sign: ZodiacSign? = h.getZodiacSign(planet)

    if (planetDeg != null && sign != null) {
      val dayNight = dayNightImpl.getDayNight(h.lmt , h.location)
      if (planet !== essentialImpl.getPoint(sign, Dignity.RULER).orElse(null) &&
          planet !== essentialImpl.getPoint(sign, Dignity.EXALTATION).orElse(null) &&
          planet !== essentialImpl.getPoint(sign, Dignity.DETRIMENT).orElse(null) &&
          planet !== essentialImpl.getPoint(sign, Dignity.FALL).orElse(null) &&
          planet !== essentialImpl.getTermsPoint(sign, planetDeg) &&
          planet !== essentialImpl.getFacePoint(planetDeg)) {
        // 判定日夜 Triplicity
        if ( !(dayNight == DayNight.DAY   && planet === essentialImpl.getTriplicityPoint(sign, DayNight.DAY))
          && !(dayNight == DayNight.NIGHT && planet === essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT)))
          return "comment" to arrayOf<Any>(planet)
      }
    }
    return null
  }
}
