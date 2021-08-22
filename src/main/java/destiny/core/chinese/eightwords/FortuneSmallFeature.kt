/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.chinese.StemBranch
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable


@Serializable
data class FortuneSmallConfig(val impl: Impl = Impl.Hour,
                              /** 取得幾條小運 */
                              val count: Int = 120,
                              val intAgeImpl : IntAgeImpl = IntAgeImpl.EightWords,
                              val intAgeNotes: List<IntAgeNoteImpl> = listOf(IntAgeNoteImpl.WestYear, IntAgeNoteImpl.Minguo),
                              val eightWordsConfig: EightWordsConfig = EightWordsConfig()) {
  enum class Impl {
    Hour,   // 以時柱推算小運
    Star,   // 《星學大成》
  }
}

@DestinyMarker
class FortuneSmallConfigBuilder : Builder<FortuneSmallConfig> {
  var impl: FortuneSmallConfig.Impl = FortuneSmallConfig.Impl.Hour
  var count: Int = 120
  var intAgeImpl: IntAgeImpl = IntAgeImpl.EightWords

  var intAgeNotes: List<IntAgeNoteImpl> = listOf(IntAgeNoteImpl.WestYear, IntAgeNoteImpl.Minguo)
  fun intAgeNotes(impls: List<IntAgeNoteImpl>) {
    intAgeNotes = impls
  }

  override fun build(): FortuneSmallConfig {
    return FortuneSmallConfig(impl, count, intAgeImpl, intAgeNotes)
  }

  companion object {
    fun fortuneSmall(block: FortuneSmallConfigBuilder.() -> Unit = {}) : FortuneSmallConfig {
      return FortuneSmallConfigBuilder().apply(block).build()
    }
  }
}

class FortuneSmallFeature(private val eightWordsFeature: EightWordsFeature,
                          private val fortuneDirectionImpl: IFortuneDirection,
                          private val intAgeImpl: IIntAge,
                          private val julDayResolver: JulDayResolver,
                          private val ageNoteImplMap: Map<IntAgeNoteImpl , IntAgeNote>) : PersonFeature<FortuneSmallConfig, List<FortuneData>> {
  override val key: String = "fortuneSmall"

  override val defaultConfig: FortuneSmallConfig = FortuneSmallConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneSmallConfig): List<FortuneData> {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)


    val ageNoteImpls: List<IntAgeNote> = config.intAgeNotes.map { impl: IntAgeNoteImpl ->
      ageNoteImplMap[impl]!!
    }.toList()

    return when (config.impl) {
      FortuneSmallConfig.Impl.Hour -> {
        /**
         * 以時柱推算小運 , 由 醉醒子 提出
         * 陽男陰女順推 , 反之逆推
         *
         * 如：一九九八（戊寅）年戊午月戊寅日壬子時生男，
         * 陽年男命，八字小運的推排以時辰干支 壬子為起點 順行推排，
         * 一歲小運：癸丑；
         * 二歲小運：甲寅；
         * 三歲小運：乙卯；
         * 四歲小運：丙辰...
         */
        val forward = fortuneDirectionImpl.isForward(lmt, loc, gender)
        val ew = eightWordsFeature.getModel(gmtJulDay, loc, config.eightWordsConfig)

        var sb: StemBranch = ew.hour

        intAgeImpl.getRangesMap(gender, gmtJulDay, loc, 1, config.count).map { (age, pair) ->
          sb = if (forward) sb.next as StemBranch else sb.prev as StemBranch
          val (from, to) = pair
          val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
          val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
          FortuneData(sb, from, to, age, age + 1, startFortuneAgeNotes, endFortuneAgeNotes)
        }.toList()
      }
      FortuneSmallConfig.Impl.Star -> {
        /**
         * 《星學大成》記載的推排方法：
         * 八字小運推算不分陰命陽命，一律以男子一歲時從丙寅起順數，女子一歲時從壬申起逆數。
         */
        var sb = if (gender == Gender.男) StemBranch.丙寅.prev else StemBranch.壬申.next
        intAgeImpl.getRangesMap(gender , gmtJulDay , loc , 1 , config.count).map { (age , pair) ->
          val (from , to) = pair
          val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
          val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
          sb = if (gender == Gender.男) sb.next else sb.prev
          FortuneData(sb , from , to , age , age+1 , startFortuneAgeNotes , endFortuneAgeNotes)
        }.toList()
      }
    }
  }

}
