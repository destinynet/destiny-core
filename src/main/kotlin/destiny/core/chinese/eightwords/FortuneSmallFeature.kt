/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IIntAgeNote
import destiny.core.IntAgeNote
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.StemBranchCycle
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import jakarta.inject.Named
import kotlinx.serialization.Serializable


@Serializable
data class FortuneSmallConfig(val impl: Impl = Impl.Hour,
                              /** 取得幾條小運 */
                              val count: Int = 120,
                              val intAgeNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo),
                              val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable {
  enum class Impl {
    Hour,   // 以時柱推算小運
    Star,   // 《星學大成》
    SixGia, // 六甲起行年法
  }
}

@DestinyMarker
class FortuneSmallConfigBuilder : Builder<FortuneSmallConfig> {
  var impl: FortuneSmallConfig.Impl = FortuneSmallConfig.Impl.Hour
  var count: Int = 120

  var intAgeNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo)
  fun intAgeNotes(impls: List<IntAgeNote>) {
    intAgeNotes = impls
  }

  var eightWordsConfig : EightWordsConfig = EightWordsConfig()

  override fun build(): FortuneSmallConfig {
    return FortuneSmallConfig(impl, count, intAgeNotes, eightWordsConfig)
  }

  companion object {
    fun fortuneSmall(block: FortuneSmallConfigBuilder.() -> Unit = {}) : FortuneSmallConfig {
      return FortuneSmallConfigBuilder().apply(block).build()
    }
  }
}

interface IFortuneSmall {

  val impl : FortuneSmallConfig.Impl

  fun getFortuneDataList(gmtJulDay: GmtJulDay, loc:ILocation, gender: Gender, count: Int, ageNoteImpls: List<IIntAgeNote>, config: EightWordsConfig): List<FortuneData>
}


@Named
class FortuneSmallFeature(private val eightWordsFeature: EightWordsFeature,
                          private val ageNoteImplMap: Map<IntAgeNote , IIntAgeNote>,
                          private val fortuneDirectionFeature: IFortuneDirectionFeature,
                          @Named("intAge8wImpl")
                          private val intAgeImpl: IIntAge) : AbstractCachedPersonFeature<FortuneSmallConfig, List<FortuneData>>() {
  override val key: String = "fortuneSmall"

  override val defaultConfig: FortuneSmallConfig = FortuneSmallConfig()

  private val implMap : Map<FortuneSmallConfig.Impl, IFortuneSmall>
    get() {
      return mapOf(
        FortuneSmallConfig.Impl.Hour to FortuneSmallHour(),
        FortuneSmallConfig.Impl.Star to FortuneSmallStar(),
        FortuneSmallConfig.Impl.SixGia to FortuneSmallSixGia()
      )
    }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneSmallConfig): List<FortuneData> {

    val ageNoteImpls: List<IIntAgeNote> = config.intAgeNotes.map { impl: IntAgeNote ->
      ageNoteImplMap[impl]!!
    }.toList()

    return implMap[config.impl]!!.getFortuneDataList(gmtJulDay, loc, gender, config.count, ageNoteImpls, config.eightWordsConfig)

  }

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
   *
   */
  inner class FortuneSmallHour : IFortuneSmall {
    override val impl: FortuneSmallConfig.Impl = FortuneSmallConfig.Impl.Hour

    override fun getFortuneDataList(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, count: Int, ageNoteImpls: List<IIntAgeNote>, config: EightWordsConfig): List<FortuneData> {
      val forward = fortuneDirectionFeature.getPersonModel(gmtJulDay, loc, gender, null, null, config)
      val eightWords = eightWordsFeature.getModel(gmtJulDay, loc, config)
      return implByRangesMap(gmtJulDay, eightWords, gender, loc, count, forward, ageNoteImpls)
    }

    /** 內定實作法 : 透過 [IIntAge.getRangesMap] 取得歲數 map , 套上干支 */
    private fun implByRangesMap(gmtJulDay: GmtJulDay,
                                eightWords: IEightWords,
                                gender: Gender,
                                location: ILocation,
                                count: Int,
                                forward: Boolean,
                                ageNoteImpls: List<IIntAgeNote>): List<FortuneData> {
      var sb = eightWords.hour
      return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, count).map { (age, pair) ->
        sb = if (forward) sb.next as StemBranch else sb.prev as StemBranch
        val (from, to) = pair
        val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
        val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
        FortuneData(sb, from, to, age, age + 1, startFortuneAgeNotes, endFortuneAgeNotes)
      }.toList()
    }

  }

  /**
   * 《星學大成》記載的推排方法：
   * 八字小運推算不分陰命陽命，一律以男子一歲時從丙寅起順數，女子一歲時從壬申起逆數。
   */
  inner class FortuneSmallStar : IFortuneSmall {

    override val impl: FortuneSmallConfig.Impl = FortuneSmallConfig.Impl.Star

    override fun getFortuneDataList(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, count: Int, ageNoteImpls: List<IIntAgeNote>, config: EightWordsConfig): List<FortuneData> {
      var sb = if (gender == Gender.男) 丙寅.prev else 壬申.next

      return intAgeImpl.getRangesMap(gender , gmtJulDay , loc , 1 , count).map { (age , pair) ->
        val (from , to) = pair
        val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
        val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
        sb = if (gender == Gender.男) sb.next else sb.prev
        FortuneData(sb , from , to , age , age+1 , startFortuneAgeNotes , endFortuneAgeNotes)
      }.toList()
    }
  }

  /**
   * 六甲起行年法
   *
   * 甲子旬 男起丙寅 順行 女起壬申 逆行
   * 甲戌旬 男起丙子 女起壬午
   * 甲申旬 男起丙戌 女起壬辰
   * 甲午旬 男起丙申 女起壬寅
   * 甲辰旬 男起丙午 女起壬子
   * 甲寅旬 男起丙辰 女起壬戌
   */
  inner class FortuneSmallSixGia : IFortuneSmall {

    override val impl: FortuneSmallConfig.Impl = FortuneSmallConfig.Impl.SixGia

    override fun getFortuneDataList(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, count: Int, ageNoteImpls: List<IIntAgeNote>, config: EightWordsConfig): List<FortuneData> {
      val eightWords = eightWordsFeature.getModel(gmtJulDay, loc, config)
      var sb = when (eightWords.day.cycle) {
        StemBranchCycle.甲子 -> if (gender == Gender.男) 丙寅.prev else 壬申.next
        StemBranchCycle.甲戌 -> if (gender == Gender.男) 丙子.prev else 壬午.next
        StemBranchCycle.甲申 -> if (gender == Gender.男) 丙戌.prev else 壬辰.next
        StemBranchCycle.甲午 -> if (gender == Gender.男) 丙申.prev else 壬寅.next
        StemBranchCycle.甲辰 -> if (gender == Gender.男) 丙午.prev else 壬子.next
        StemBranchCycle.甲寅 -> if (gender == Gender.男) 丙辰.prev else 壬戌.next
      }

      return intAgeImpl.getRangesMap(gender , gmtJulDay , loc , 1 , count).map { (age , pair) ->
        val (from , to) = pair
        val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
        val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
        sb = if (gender == Gender.男) sb.next else sb.prev
        FortuneData(sb , from , to , age , age+1 , startFortuneAgeNotes , endFortuneAgeNotes)
      }.toList()
    }
  }
}
