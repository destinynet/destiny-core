package destiny.core.astrology.classical.rules

import destiny.core.DayNight
import destiny.core.Descriptive
import destiny.core.IPatternDescriptor
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.FixedStar
import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet.*
import destiny.core.astrology.StationaryType
import destiny.tools.AbstractPropertyBasedPatternDescriptor
import destiny.tools.ILocaleString
import destiny.tools.Lang
import destiny.tools.StringTools.toEnglishOrdinal
import destiny.tools.getTitle
import java.util.*


/**
 * 將 [IPlanetPattern] 轉譯成 [Descriptive] , 以利終端顯示
 */
abstract class AbstractPlanetPatternDescriptor<out T : IPlanetPattern>(pattern: IPlanetPattern,
                                                                       commentKey: String,
                                                                       parameters: List<Any>)
  : AbstractPropertyBasedPatternDescriptor(pattern, commentKey, parameters)


class EssentialDignityDescriptor(rule: EssentialDignity, key: String, parameters: List<Any>) :
  AbstractPlanetPatternDescriptor<EssentialDignity>(rule, key, parameters) {
  override val resource: String = "destiny.core.astrology.classical.rules.EssentialDignities"
}

class AccidentalDignityDescriptor(rule: AccidentalDignity, key: String, parameters: List<Any>) :
  AbstractPlanetPatternDescriptor<AccidentalDignity>(rule, key, parameters) {
  override val resource: String = "destiny.core.astrology.classical.rules.AccidentalDignities"
}

class DebilityDescriptor(rule: Debility, key: String, parameters: List<Any>) : AbstractPlanetPatternDescriptor<Debility>(
  rule, key, parameters) {
  override val resource: String = "destiny.core.astrology.classical.rules.Debilities"
}

class MiscDescriptor(rule: Misc, key: String, parameters: List<Any>) : AbstractPlanetPatternDescriptor<Misc>(
  rule, key, parameters) {
  override val resource: String = "destiny.core.astrology.classical.rules.Misc"
}

object PatternTranslator : IPatternDescriptor<IPlanetPattern>  {

  override fun getDescriptor(pattern: IPlanetPattern): Descriptive {
    return when (pattern) {
      is EssentialDignity -> when (pattern) {
        is EssentialDignity.Ruler -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is EssentialDignity.Exaltation -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is EssentialDignity.Triplicity -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign, pattern.dayNight))
        is EssentialDignity.Term -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.lngDeg.value))
        is EssentialDignity.Face -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.lngDeg.value))
        is EssentialDignity.MutualReception -> EssentialDignityDescriptor(pattern, "comment",
                                                                          listOf(pattern.planet, pattern.sign1, pattern.dig2, pattern.dig2.getTitle(Locale.ENGLISH), pattern.p2, pattern.sign2, pattern.dig1, pattern.dig2))

      }
      is AccidentalDignity -> when(pattern) {
        is AccidentalDignity.House_1_10 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.House_4_7_11 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.House_2_5 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.House_9 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.House_3 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Direct -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Swift -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Oriental -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Occidental -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        AccidentalDignity.Moon_Increase_Light -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Free_Combustion -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Cazimi -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Partile_Conj_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, CONJUNCTION))
        is AccidentalDignity.Partile_Conj_North_Node -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.node, CONJUNCTION))
        is AccidentalDignity.Partile_Trine_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, TRINE))
        is AccidentalDignity.Partile_Sextile_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, SEXTILE))
        is AccidentalDignity.Partile_Conj_Regulus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, FixedStar.REGULUS, CONJUNCTION))
        is AccidentalDignity.Partile_Conj_Spica -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, FixedStar.SPICA, CONJUNCTION))
        is AccidentalDignity.JoyHouse -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.Hayz -> AccidentalDignityDescriptor(pattern, "comment", sectParams(pattern.fact))
        is AccidentalDignity.Besieged_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, VENUS, JUPITER))
        is AccidentalDignity.Translation_of_Light -> {
          if (pattern.aspectType != null)
            AccidentalDignityDescriptor(pattern, "commentAspect", listOf(pattern.planet, pattern.from, pattern.to, pattern.aspectType, pattern.angle))
          else
            AccidentalDignityDescriptor(pattern, "commentUnaspect", listOf(pattern.planet, pattern.from, pattern.to, pattern.angle))
        }
        is AccidentalDignity.Collection_of_Light -> {
          val p1 = pattern.twoPlanets[0]
          val p2 = pattern.twoPlanets[1]
          AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, p1, p2, pattern.angle))
        }

        is AccidentalDignity.Refrain_from_Mars_Saturn -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, pattern.aspect))
      }
      is Debility -> when(pattern) {
        is Debility.Detriment -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is Debility.Fall -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is Debility.Peregrine -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.House_12 ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.House_6_8 ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is Debility.Retrograde ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Slower ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Occidental ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Oriental ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        Debility.Moon_Decrease_Light ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Combustion ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Sunbeam ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Partile_Conj_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, CONJUNCTION))
        is Debility.Partile_Conj_South_Node -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, LunarNode.SOUTH, CONJUNCTION))
        is Debility.Besieged_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, MARS, SATURN))
        is Debility.Partile_Oppo_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, OPPOSITION))
        is Debility.Partile_Square_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, SQUARE))
        is Debility.Conj_Algol ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, FixedStar.ALGOL, CONJUNCTION))
        is Debility.Out_of_Sect -> DebilityDescriptor(pattern, "comment", sectParams(pattern.fact))
        is Debility.Refrain_from_Venus_Jupiter -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, pattern.aspect))
        is Debility.MutualDeception -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign1, pattern.dig2, pattern.p2, pattern.sign2, pattern.dig1, pattern.dig2))
      }
      // Misc 是 sealed：這裡窮舉，新增一種 Misc 而沒給翻譯會是編譯錯誤，不是執行期才炸
      is Misc -> when (pattern) {
        is Misc.Prohibited ->
          MiscDescriptor(pattern, (if (pattern.byBody) "commentBody" else "commentRays") + (if (pattern.ingress != null) "Ingress" else ""),
            listOfNotNull(pattern.planet, pattern.applied, pattern.aspect, pattern.prohibitor, pattern.with, pattern.interruption, pattern.ingress))
        is Misc.Frustrated ->
          MiscDescriptor(pattern, if (pattern.ingress != null) "commentIngress" else "comment",
            listOfNotNull(pattern.planet, pattern.applied, pattern.aspect, pattern.frustrator, pattern.interruption, pattern.ingress))
        is Misc.Refrained ->
          MiscDescriptor(pattern, (if (pattern.stationary == StationaryType.DIRECT_TO_RETROGRADE) "commentRetrograde" else "commentDirect") + (if (pattern.ingress != null) "Ingress" else ""),
            listOfNotNull(pattern.planet, pattern.applied, pattern.aspect, pattern.refranator, pattern.closestOrb, pattern.ingress))
        is Misc.Besieged ->
          // 星體那一側沒有交角；佔位符位置固定，所以放空字串而不是拿掉
          MiscDescriptor(pattern, "comment" + (if (pattern.beforeAspect == null) "Body" else "Rays") + (if (pattern.afterAspect == null) "Body" else "Rays"),
            listOf(pattern.planet, pattern.before, pattern.beforeAspect ?: "", pattern.beforeDistance, pattern.after, pattern.afterAspect ?: "", pattern.afterDistance))
        // 居上：句子以被居上的那顆為主語；凶星只會是四分（本命篩選），吉星分四分（督促）與六分／三分（順勢支持）
        is Misc.Overcoming -> {
          val o = pattern.overcoming
          val key = when (o.superior) {
            MARS, SATURN   -> if (o.aspect == SQUARE) "commentMalefic" else "comment"
            VENUS, JUPITER -> if (o.aspect == SQUARE) "commentBeneficSquare" else "commentBenefic"
            else           -> "comment"
          }.let { if (pattern.ascRuler && it != "comment") it + "AscRuler" else it }
          MiscDescriptor(pattern, key, listOf(o.inferior, o.superior, o.aspect,
            OvercomingClause.superior(pattern), OvercomingClause.inferior(pattern)))
        }
        is Misc.InAntiscia -> MiscDescriptor(pattern, if (pattern.antiscion.contra) "commentContra" else "comment",
          listOf(pattern.antiscion.planet, pattern.antiscion.other, "%.2f°".format(Locale.ROOT, pattern.antiscion.orb)))
        is Misc.SpearBearing -> MiscDescriptor(pattern, "comment", listOf(pattern.planet, SpearBearers(pattern.planet, pattern.bearers, pattern.sect)))
        is Misc.Maltreated -> {
          val m = pattern.maltreatment
          val key = if (m.polarity == destiny.core.astrology.classical.Maltreatment.Polarity.MALTREATMENT) "commentMaltreatment" else "commentBonification"
          MiscDescriptor(pattern, key, listOf(m.planet, MaltreatmentConditions(m.conditions)))
        }
        is Misc.Aversion -> {
          val a = pattern.aversion
          val key = when (a.basis) {
            destiny.core.astrology.classical.Aversion.Basis.RULER      -> "commentRuler"
            destiny.core.astrology.classical.Aversion.Basis.EXALTATION -> "commentExaltation"
            destiny.core.astrology.classical.Aversion.Basis.ASCENDANT  -> "commentAscendant"
          }
          MiscDescriptor(pattern, key, listOf(a.planet, a.from, SignPlace(a.place)))
        }
        is Misc.Phasis -> {
          val f = pattern.phasis
          val key = when (f.type) {
            destiny.core.astrology.classical.Phasis.Type.STATION_RETROGRADE -> "commentStationRetrograde"
            destiny.core.astrology.classical.Phasis.Type.STATION_DIRECT     -> "commentStationDirect"
            destiny.core.astrology.classical.Phasis.Type.EMERGING           -> "commentEmerging"
            destiny.core.astrology.classical.Phasis.Type.SINKING            -> "commentSinking"
          }
          MiscDescriptor(pattern, key, listOf(f.planet, FromBirth(f.days), MorningEvening(f.oriental)))
        }
        is Misc.Feral -> MiscDescriptor(pattern,
          if (pattern.feral.basis == destiny.core.astrology.classical.FeralOptions.Basis.BY_SIGN) "commentBySign" else "commentByOrb",
          listOf(pattern.planet))
        is Misc.LunarConnection -> {
          val sep = pattern.connection.separating
          val app = pattern.connection.applying
          val key = when {
            sep != null && app != null -> "comment"
            sep != null                -> "commentSeparating"
            else                       -> "commentApplying"
          }
          // 句子依 key 只用到其中一端的三個參數；另一端缺時以空字串佔位，位置不變
          MiscDescriptor(pattern, key, listOf(
            sep?.planet ?: "", sep?.aspect?.let(::AspectWord) ?: "", sep?.let { LinkWhen(it.days, past = true) } ?: "",
            app?.planet ?: "", app?.aspect?.let(::AspectWord) ?: "", app?.let { LinkWhen(it.days, past = false) } ?: "",
          ))
        }
        is Misc.ViaCombusta -> MiscDescriptor(pattern, "comment", listOf(pattern.planet))
        // 空亡是時間區段，沒有 Misc.properties 的句子，刻意不翻譯
        is Misc.VoidCourseSpan ->
          throw IllegalArgumentException("No description for : $pattern")
      }
      else -> {
        throw IllegalArgumentException("Not Supported : $pattern")
      }
    }
  }
}

/**
 * 得時／不得時句子的參數，兩種句子共用：`{0}` 晝星／夜星、`{1}` 星、`{2}` 白天／夜晚、`{3}` 地平面上／下、`{4}` 陽性／陰性星座、`{5}` 星座。
 * 句子由事實的欄位組出來，所以 ḥalb 的「異隊的星在地平下」也講得出來（舊句子寫死「在地平面上」）
 */
private fun sectParams(c: destiny.core.astrology.classical.ISectCondition): List<Any> = listOf(
  SectWord("planet." + c.planetSect.name),
  c.planet,
  SectWord("chart." + c.chartSect.name),
  SectWord(if (c.aboveHorizon) "above" else "below"),
  SectWord(if (c.masculineSign) "masculine" else "feminine"),
  c.sign,
)

/** 得時／不得時句子裡的詞：字在 AccidentalDignities 語系檔的 `Hayz.word.*`，得時與不得時共用一份 */
internal class SectWord(private val key: String) : ILocaleString {
  override fun getTitle(lang: Lang): String =
    destiny.tools.I18nBundles.string("destiny.core.astrology.classical.rules.AccidentalDignities", lang, "Hayz.word.$key") ?: key
}

/** 持矛者清單：「水星（先於太陽升起，相距 25.53°，與日盤同區分）」 */
internal class SpearBearers(
  private val light: destiny.core.astrology.Planet,
  private val bearers: List<destiny.core.astrology.classical.Doryphoria>,
  private val sect: DayNight?,
) : ILocaleString {
  override fun getTitle(lang: Lang): String {
    val lightName = light.getTitle(lang)
    return bearers.joinToString(if (lang.language == "en") "; " else "、") { d ->
      val name = d.bearer.getTitle(lang)
      val dist = "%.2f°".format(Locale.ROOT, d.distance)
      val sectWord = if (sect == null || d.inSect == null) null else when (lang.language) {
        "en" -> (if (d.inSect) "of the sect" else "contrary to the sect") + " of this ${if (sect == DayNight.DAY) "day" else "night"} chart"
        "ja" -> (if (sect == DayNight.DAY) "昼" else "夜") + "の生まれで" + (if (d.inSect) "同じセクト" else "異なるセクト")
        else -> "與" + (if (sect == DayNight.DAY) "日" else "夜") + "盤" + (if (d.inSect) "同區分" else "異區分")
      }
      when (lang.language) {
        "en" -> "$name (" + listOfNotNull((if (d.preceding) "rising before the $lightName" else "rising after the $lightName") + ", $dist away", sectWord).joinToString("; ") + ")"
        "ja" -> "$name（" + listOfNotNull("${lightName}より" + (if (d.preceding) "先に" else "後に") + "昇る、$dist", sectWord).joinToString("、") + "）"
        else -> "$name（" + listOfNotNull((if (d.preceding) "先於$lightName" else "在${lightName}之後") + "升起，相距 $dist", sectWord).joinToString("，") + "）"
      }
    }
  }
}

/** 受剋／受扶的條件清單，依語言組成一串（zh「被 土星 四分居上、與 火星 對沖」） */
internal class MaltreatmentConditions(private val conditions: List<destiny.core.astrology.classical.Maltreatment.Condition>) : ILocaleString {

  override fun getTitle(lang: Lang): String {
    val name = { p: destiny.core.astrology.AstroPoint -> p.getTitle(lang) }
    val deg = { d: Double -> "%.2f°".format(Locale.ROOT, d) }
    val zhAspect = mapOf(SEXTILE to "六分", SQUARE to "四分", TRINE to "三分", OPPOSITION to "對沖", CONJUNCTION to "合")
    return conditions.joinToString(when (lang.language) { "en" -> "; "; else -> "、" }) { c ->
      when (c) {
        is destiny.core.astrology.classical.Maltreatment.Condition.Overcome -> when (lang.language) {
          "en" -> "overcome by ${name(c.overcoming.superior)} by ${c.overcoming.aspect.getTitle(lang).lowercase()}"
          "ja" -> "${name(c.overcoming.superior)}に${c.overcoming.aspect.getTitle(lang)}でオーバーカミングされる"
          else -> "被 ${name(c.overcoming.superior)} ${zhAspect[c.overcoming.aspect]}居上"
        }
        is destiny.core.astrology.classical.Maltreatment.Condition.Opposed -> when (lang.language) {
          "en" -> "opposed by ${name(c.source)}"
          "ja" -> "${name(c.source)}とオポジション"
          else -> "與 ${name(c.source)} 對沖"
        }
        is destiny.core.astrology.classical.Maltreatment.Condition.Adherence -> when (lang.language) {
          "en" -> "in adherence with ${name(c.source)} (conjunction, ${deg(c.orb)} apart)"
          "ja" -> "${name(c.source)}と密着（コンジャンクション、${deg(c.orb)}）"
          else -> "與 ${name(c.source)} 貼合（合相，相距 ${deg(c.orb)}）"
        }
        is destiny.core.astrology.classical.Maltreatment.Condition.RayHurled -> when (lang.language) {
          "en" -> "struck by the ${c.aspect.getTitle(lang).lowercase()} ray of ${name(c.source)} (${deg(c.orb)} off)"
          "ja" -> "${name(c.source)}の${c.aspect.getTitle(lang)}の光線を受ける（${deg(c.orb)}）"
          else -> "被 ${name(c.source)} 以${zhAspect[c.aspect]}光線射中（差 ${deg(c.orb)}）"
        }
        is destiny.core.astrology.classical.Maltreatment.Condition.Besieged -> when (lang.language) {
          "en" -> "besieged between ${name(c.besiegement.before.by)} and ${name(c.besiegement.after.by)}"
          "ja" -> "${name(c.besiegement.before.by)}と${name(c.besiegement.after.by)}に挟まれる"
          else -> "被 ${name(c.besiegement.before.by)} 與 ${name(c.besiegement.after.by)} 包夾"
        }
        is destiny.core.astrology.classical.Maltreatment.Condition.Sequence -> when (lang.language) {
          "en" -> "separating from the ${c.fromAspect.getTitle(lang).lowercase()} of ${name(c.from)} (${deg(c.fromOrb)} past) and applying to the ${c.toAspect.getTitle(lang).lowercase()} of ${name(c.to)} (${deg(c.toOrb)} to go)"
          "ja" -> "${name(c.from)}との${c.fromAspect.getTitle(lang)}から離れ（${deg(c.fromOrb)}）、${name(c.to)}との${c.toAspect.getTitle(lang)}へ接近する（${deg(c.toOrb)}）"
          else -> "離開 ${name(c.from)} 的${zhAspect[c.fromAspect]}（已過 ${deg(c.fromOrb)}）、迎上 ${name(c.to)} 的${zhAspect[c.toAspect]}（還差 ${deg(c.toOrb)}）"
        }
      }
    }
  }
}

/** 「從某星座起算的第 n 個星座」：zh／ja 用中文數詞的寫法，en 用序數 */
internal class SignPlace(private val n: Int) : ILocaleString {
  override fun getTitle(lang: Lang): String = when (lang.language) {
    "en" -> n.toEnglishOrdinal()
    "ja" -> "${n}番目"
    else -> "第 $n 個"
  }
}

/**
 * 居上句子裡跟在星名後面的括號：落哪一宮、守護哪幾宮、（居上者）與盤的日夜同不同區分。沒有細節（精度不夠、盤沒有宮位）時為空字串。
 * 「同區分」只比對星的晝夜歸屬與盤的日夜，不是 [AccidentalDignity.Hayz] 那種還要看地平線與星座陰陽的得時
 */
internal class OvercomingClause private constructor(
  private val house: Int?,
  private val rules: List<Int>,
  private val sect: DayNight?,
  private val inSect: Boolean?,
) : ILocaleString {

  override fun getTitle(lang: Lang): String {
    val ja = lang.language == "ja"
    val en = lang.language == "en"
    val parts = buildList {
      house?.let { add(when { en -> "in the ${it.toEnglishOrdinal()} house"; ja -> "第${it}ハウス"; else -> "第 $it 宮" }) }
      if (house != null && rules.isNotEmpty()) add(when {
        en -> "ruling the " + rules.joinToString(" and ") { it.toEnglishOrdinal() }
        ja -> "第" + rules.joinToString("・") + "ハウスの支配星"
        else -> "守護第 " + rules.joinToString("、") + " 宮"
      })
      if (sect != null && inSect != null) add(when {
        en -> (if (inSect) "of the sect" else "contrary to the sect") + " of this ${if (sect == DayNight.DAY) "day" else "night"} chart"
        ja -> (if (sect == DayNight.DAY) "昼" else "夜") + "の生まれで" + (if (inSect) "同じセクト" else "異なるセクト")
        else -> "與" + (if (sect == DayNight.DAY) "日" else "夜") + "盤" + (if (inSect) "同區分" else "異區分")
      })
    }
    if (parts.isEmpty()) return ""
    return when {
      en -> " (" + parts.joinToString("; ") + ")"
      ja -> "（" + parts.joinToString("、") + "）"
      else -> "（" + parts.joinToString("，") + "）"
    }
  }

  companion object {
    fun superior(p: Misc.Overcoming) = OvercomingClause(p.houses?.superiorHouse, p.houses?.superiorRules.orEmpty(), p.sect, p.superiorInSect)
    fun inferior(p: Misc.Overcoming) = OvercomingClause(p.houses?.inferiorHouse, p.houses?.inferiorRules.orEmpty(), null, null)
  }
}

/** 顯現的時間：出生前／後幾天（一位小數）；不到 0.05 天算出生當下 */
internal class FromBirth(private val days: Double) : ILocaleString {
  override fun getTitle(lang: Lang): String {
    val d = "%.1f".format(Locale.ROOT, kotlin.math.abs(days))
    val now = kotlin.math.abs(days) < 0.05
    return when (lang.language) {
      "en" -> if (now) "at birth" else if (days < 0) "$d days before birth" else "$d days after birth"
      "ja" -> if (now) "出生時" else if (days < 0) "出生の${d}日前" else "出生の${d}日後"
      else -> if (now) "出生當下" else if (days < 0) "出生前 $d 天" else "出生後 $d 天"
    }
  }
}

/** 出入光束那一刻在太陽哪一側：東方是晨星（先於太陽升起），西方是昏星 */
internal class MorningEvening(private val oriental: Boolean) : ILocaleString {
  override fun getTitle(lang: Lang): String = when (lang.language) {
    "en" -> if (oriental) "as a morning star" else "as an evening star"
    "ja" -> if (oriental) "明けの星" else "宵の星"
    else -> if (oriental) "晨星，在太陽東方" else "昏星，在太陽西方"
  }
}

/** 月亮離合的時間：幾天前離開、幾天後成形（一位小數） */
internal class LinkWhen(private val days: Double, private val past: Boolean) : ILocaleString {
  override fun getTitle(lang: Lang): String {
    val d = "%.1f".format(Locale.ROOT, days)
    return when (lang.language) {
      "en" -> if (past) "$d days ago" else "in $d days"
      "ja" -> if (past) "${d}日前" else "${d}日後"
      else -> if (past) "$d 天前" else "$d 天後"
    }
  }
}

/** 句中的相位名稱：英文連冠詞一起、小寫（a trine、an opposition），其餘語系照相位的名稱 */
internal class AspectWord(private val aspect: destiny.core.astrology.Aspect) : ILocaleString {
  override fun getTitle(lang: Lang): String = aspect.getTitle(lang).let { t ->
    if (lang.language != "en") t
    else t.lowercase().let { w -> (if (w.first() in "aeiou") "an " else "a ") + w }
  }
}
