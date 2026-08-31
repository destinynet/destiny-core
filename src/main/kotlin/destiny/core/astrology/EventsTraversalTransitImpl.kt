package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.IVoidCourseFeature
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.electional.Impact
import destiny.core.electional.Span
import destiny.core.toString
import destiny.tools.getTitle
import destiny.tools.reverse
import destiny.tools.round
import destiny.tools.truncateToString
import jakarta.inject.Named
import java.util.*

/**
 * Progression-based
 */
@Named
class EventsTraversalTransitImpl(
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val relativeTransitImpl: IRelativeTransit,
  private val eclipseImpl: IEclipseFactory,
  private val horoscopeFeature: IHoroscopeFeature,
  private val modernAspectCalculator: IAspectCalculator,
  private val voidCourseFeature: IVoidCourseFeature,
  private val retrogradeImpl: IRetrograde,
) : IEventsTraversal {

  private companion object {
    /**
     * 互相位事件所附本命接點的 orb 上限。
     *
     * ⚠️ **這個上限是必要的，不是風格選擇。** 不設限時實測（A011 三個月預測窗）：
     * 14 條互相位事件附出 **103 筆**接點（平均 7.4 筆／條），素材 **+16.4%**，
     * 而 orb 中位數 3.55°、**62% 超過 3°** —— 絕大多數是噪音，
     * 而它們與真正的訊號（如 `Mars ☌ Uranus` 同時四分本命交點軸 1.72°）混在同一行，
     * 讀者要自己再篩一次。素材的每一行都在跟其他行競爭注意力。
     *
     * 取 3.0° 與 corpus 的 `RETURN_MAX_ORB` 一致（事件群的逐日行運相位更嚴，用 2°）。
     *
     * ⚠️ 滯留（`StationaryMoment.contacts`）刻意**不**套這個上限：它是**工具**的回覆、
     * 依 orb 排序、且只在被問到時才出現，不佔素材版面。兩者的消費端不同。
     */
    const val MUTUAL_NATAL_MAX_ORB: Double = 3.0
  }

  private val Planet.isStationaryPossible: Boolean
    get() = this != Planet.SUN && this != Planet.MOON

  override fun traverse(
    model: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    grain: BirthDataGrain,
    config: AstrologyTraversalConfig,
    transitingPoints: Set<AstroPoint>,
    natalTargetPoints: Set<AstroPoint>,
  ): Sequence<AstroEventDto> {

    // 外圈要考慮的星體 — filterIsInstance<Star> 包含 Planet + LunarNode，排除 Axis（Axis 不是 Star，無法計算行運）
    val transitingStars: Set<Star> = transitingPoints.filterIsInstance<Star>().toSet()

    // 內圈本命星體 (Natal Target Points) — 包含 Planet, LunarNode, Axis
    val natalPoints: Set<AstroPoint> = model.points
      .asSequence()
      .filter { it in natalTargetPoints }
      .filter { it is Planet || it is LunarNode || it is Axis }
      // ⚠️ 用正典 [allowsNatalTarget]，不要手寫 `grain == MINUTE` 的推導：
      //    兩個閘門的判準不同（軸點要分鐘、月亮到時辰即可），寫成單一二分就會把
      //    HOUR2 的月亮一起擋掉、或把 DAY 的月亮一起放行。行為對照：
      //      MINUTE 全留（不變）／HOUR2 擋軸點、留月亮（不變）／DAY 擋軸點**與月亮**（本次修正）。
      .filter { grain.allowsNatalTarget(it) }.toSet()

    /** 互相位事件的本命接點用哪些相位 —— 與滯留的處置同款（HIGH importance）。 */
    val mutualNatalAspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet()

    // 從 config.aspectTypes 推導所有等價角度（用於 global aspects）
    val defaultAngles: Set<Double> = config.aspectTypes.flatMap { it.mirrorAngles }.toSet()
    // Use model.getPosition() to support all AstroPoint types (Planet, LunarNode, Axis)
    val natalPointsPosMap: Map<AstroPoint, ZodiacDegree> = natalPoints.mapNotNull { point ->
      model.getPosition(point)?.let { point to it.lngDeg }
    }.toMap()


    val houseRelatedPoints = listOf(Axis.values.toList(), Arabic.values.toList()).flatten()

    /**
     * [this] : Outer
     * [chosenPoints] 外圈的某星 針對內圈 的星體，形成哪些交角
     */
    fun IHoroscopeModel.outerToInner(vararg chosenPoints: AstroPoint): List<SynastryAspect> {
      // 外盤時刻是行運遍歷的天文精確解，恆為 MINUTE
      return horoscopeFeature.synastry(this, model, modernAspectCalculator,
                                       threshold = null,
                                       innerGrain = grain,
                                       outerGrain = BirthDataGrain.MINUTE).aspects.filter { aspect: SynastryAspect ->
        aspect.outerPoint in chosenPoints && (
          if (grain.includeAxis)
            true
          else {
            aspect.innerPoint !in houseRelatedPoints
          }
          )
          // ⚠️ **疊加**，不是取代上面那段。本檔的消費端（事件 description）會被 LLM 逐條引用，
          //    正是 [allowsNatalTarget] KDoc 指名的那種「讀者無從分辨哪一腳可信」的場合 ——
          //    而 [HoroscopeFeature.synastry] 本身刻意只濾軸點（該 KDoc 明文禁止接入本函式），
          //    所以閘門要在這裡補，不在那裡改。
          //    寫成取代會連 MINUTE 都變樣（該分支現行完全不濾，阿拉伯點與恆星都留著）。
          && grain.allowsNatalTarget(aspect.innerPoint)
      }
    }

    /**
     * 搜尋 personal aspects（外圈 transit to 本命星體）。
     * 透過 [AstrologyTraversalConfig.effectiveAngles] 實現 per-planet 的相位規則過濾，
     * 在計算前就跳過不需要的角度組合。
     */
    fun searchPersonalEvents(transitingStars: Set<Star>, natalPoints: Set<AstroPoint>): Sequence<AspectData> {
      return transitingStars.asSequence().flatMap { outer ->
        val outerPlanet = outer as? Planet
        natalPoints.asSequence().flatMap { inner ->
          natalPointsPosMap[inner]?.let { innerDeg ->
            // 透過 aspectFilterRules 決定此 outer-inner 組合允許的角度
            val effectiveAngles = if (outerPlanet != null) config.effectiveAngles(outerPlanet, inner) else defaultAngles
            if (effectiveAngles.isEmpty()) return@flatMap emptySequence()
            val degrees = effectiveAngles.map { it.toZodiacDegree() }.map { it + innerDeg }.toSet()
            starTransitImpl.getRangeTransitGmt(outer, degrees, fromGmtJulDay, toGmtJulDay, options = config.horoscopeConfig.starTypeOptions).map { (zDeg, gmt) ->
              val angle: Double = zDeg.getAngle(innerDeg).round()
              val pattern = PointAspectPattern(listOf(outer, inner), angle, null, 0.0)
              AspectData(pattern, null, 0.0, null, gmt)
            }
          } ?: emptySequence()
        }
      }
    }


    // Global (sky-to-sky) aspects: only between Planets; LunarNodes are excluded because
    // North/South Node map to the same SwissEph body (True Node), causing TCPlanetPlanet to throw,
    // and their mutual 180° opposition is a constant, not a meaningful event.
    val globalAspectEvents = relativeTransitImpl.mutualAspectingEvents(
      transitingStars.filterIsInstance<Planet>().toSet(), defaultAngles,
      fromGmtJulDay, toGmtJulDay, config.horoscopeConfig.starTypeOptions
    ).map { aspectData: AspectData ->
      val (outerStar1, outerStar2) = aspectData.points.let { it[0] to it[1] }

      /**
       * ⭐ 兩顆行運星**各自**對本命的相位。
       *
       * 沒有這一段，素材只印得出「兩顆行運星成相」而不含任何本命側資訊，
       * 讀者要判斷它壓不壓在本命點上就只能憑記憶回想那時該星在哪 ——
       * 而憑記憶回答天象正是本專案要消滅的行為（2026-08-31 裁定）。
       *
       * ⚠️ **不建整張盤**：取兩顆星的黃經對 [natalPointsPosMap]（迴圈外算好、已過閘門）
       * 求相位。相反做法的代價實測過：為每個事件建盤讓一段掃描從 290ms 變成 1m10s。
       */
      val mutualToNatal: List<SynastryAspect> = listOfNotNull(
        outerStar1 as? Star, outerStar2 as? Star   // mutualAspectingEvents 只吐 Planet，這裡只是型別收窄
      ).flatMap { outer ->
        fun lngAt(gmt: GmtJulDay) = starPositionImpl.calculate(
          outer, gmt, config.horoscopeConfig.centric,
          config.horoscopeConfig.coordinate, config.horoscopeConfig.starTypeOptions
        ).lngDeg
        val outerPosMap = mapOf<AstroPoint, IZodiacDegree>(outer to lngAt(aspectData.gmtJulDay))
        // ⭐ 入相／出相需要「稍後位置」對照 —— 與 `ReportFactory` 的 transitSynastry 同法
        //    （+0.01 天 ≈ 15 分鐘）。少了它，[SynastryAspect.aspectType] 恆為 null，
        //    而「逼近 vs 分離」正是判讀主被動時實際被用到的區分。
        //    ⚠️ 仍然不建盤：這只是同一顆星多一次 starPosition。本命側是靜態的，later 即自身。
        val laterOuter = mapOf<AstroPoint, IZodiacDegree>(outer to lngAt(aspectData.gmtJulDay + 0.01))
        natalPointsPosMap.keys.mapNotNull { inner ->
          modernAspectCalculator.getAspectPattern(
            outer, inner, outerPosMap, natalPointsPosMap,
            { p -> laterOuter[p] }, { p -> natalPointsPosMap[p] }, mutualNatalAspects
          )?.let { p ->
            // ⚠️ aspect / orb 型別上可空 —— `!!` 會把「這筆沒有相位」的正常結果變成 NPE
            p.aspect?.let { asp -> SynastryAspect(outer, inner, null, null, asp, p.orb ?: 0.0, p.aspectType, p.score) }
          }?.takeIf { it.orb <= MUTUAL_NATAL_MAX_ORB }
        }
      }

      val description = buildString {
        append("[transiting ${outerStar1.toString(Locale.ENGLISH)}] ${aspectData.aspect} [transiting ${outerStar2.toString(Locale.ENGLISH)}]")
        if (mutualToNatal.isNotEmpty()) {
          appendLine()
          appendLine(mutualToNatal.describeAspects(grain))
        }
      }
      // ⚠️ global = true —— 兩端都是行運端。消費端若照「[0]=行運、[1]=本命」投影會產生假資料，
      //    見 [AstroEvent.AspectEvent.global]。
      AstroEventDto(
        AstroEvent.AspectEvent(description, aspectData, global = true, transitToNatalAspects = mutualToNatal),
        aspectData.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL
      )
    }

    val vocConfig = VoidCourseConfig(Planet.MOON, vocImpl = VoidCourseImpl.Medieval)
    val moonVocSeq = if (Planet.MOON in transitingStars) {
      voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, vocConfig)
        .map { it: Misc.VoidCourseSpan ->
          val description = buildString {
            append("${it.planet.toString(Locale.ENGLISH)} Void of Course (空亡). ")
            append("From ${it.fromPos.sign.getTitle(Locale.ENGLISH)}/${it.fromPos.signDegree.second.truncateToString(2)}° ")
            append("to ${it.toPos.sign.getTitle(Locale.ENGLISH)}/${it.toPos.signDegree.second.truncateToString(2)}°. ")
          }
          AstroEventDto(AstroEvent.MoonVoc(description, it), it.begin, it.end, Span.HOURS, Impact.GLOBAL)
        }
    } else emptySequence()


    // 滯留（由 config.stationaryPlanets 獨立控制，不依賴 transitingStars）
    val planetStationaries = config.stationaryPlanets.asSequence().filter { it.isStationaryPossible }.flatMap { planet ->
      retrogradeImpl.getRangeStationaries(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl).map { s: Stationary ->
        val outer = horoscopeFeature.getModel(s.gmtJulDay, loc, config.horoscopeConfig)
        val zodiacDegree = outer.getZodiacDegree(planet)!!
        val transitToNatalAspects = outer.outerToInner(planet)

        val description = buildString {
          append("${s.star.toString(Locale.ENGLISH)} Stationary (滯留). ${s.type.getTitle(Locale.ENGLISH)}")
          append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
          if (config.includeTransitToNatalAspects) {
            if (transitToNatalAspects.isNotEmpty()) {
              appendLine()
              appendLine(transitToNatalAspects.describeAspects(grain))
            }
          }
        }
        AstroEventDto(
          AstroEvent.PlanetStationary(
            description, s, zodiacDegree,
            if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
          ), s.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 當日星體逆行
    val planetRetrogrades = transitingStars.filterIsInstance<Planet>().asSequence().filter { it.isStationaryPossible }.flatMap { planet ->
      retrogradeImpl.getDailyRetrogrades(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl, starTransitImpl).map { (gmtJulDay, progress) ->
        val description = buildString {
          append("${planet.toString(Locale.ENGLISH)} Retrograding (逆行). ")
          append("Progress = ${(progress * 100.0).truncateToString(2)}%")
        }
        AstroEventDto(AstroEvent.PlanetRetrograde(description, planet, progress), gmtJulDay, null, Span.DAY, Impact.GLOBAL)
      }
    }

    // 日食
    val solarEclipses = eclipseImpl.getRangeSolarEclipses(fromGmtJulDay, toGmtJulDay).map { eclipse ->
      val outer = horoscopeFeature.getModel(eclipse.max, loc, config.horoscopeConfig)
      val zodiacDegree = outer.getZodiacDegree(Planet.SUN)!!
      val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.SUN)

      val description = buildString {
        append("Solar Eclipse (日食). ")
        append("Type = ${eclipse.solarType.getTitle(Locale.ENGLISH)}")
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
        if (config.includeTransitToNatalAspects) {
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(grain))
          }
        }
      }
      AstroEventDto(
        AstroEvent.Eclipse(
          // ⚠️ 相位**無條件**存進模型。`includeTransitToNatalAspects` 只該管
          //    「要不要印進 description」（呈現），不該連帶把資料從模型裡刪掉。
          //    兩者綁在一起的後果：想用這份資料做計算的消費端，只剩下
          //    正則解析我們自己渲染的字串這一條路。
          //    這個改動**不動素材一個 byte** —— description 仍然照舊由旗標決定。
          description, eclipse, transitToNatalAspects, zodiacDegree,
        ), eclipse.max, null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月食
    val lunarEclipses = eclipseImpl.getRangeLunarEclipses(fromGmtJulDay, toGmtJulDay).map { eclipse ->
      val outer = horoscopeFeature.getModel(eclipse.max, loc, config.horoscopeConfig)
      val zodiacDegree = outer.getZodiacDegree(Planet.MOON)!!
      val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.MOON)


      val description = buildString {
        append("Lunar Eclipse (月食). ")
        append("Type = ${eclipse.lunarType.getTitle(Locale.ENGLISH)}")
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
        if (config.includeTransitToNatalAspects) {
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(grain))
          }
        }
      }
      AstroEventDto(
        AstroEvent.Eclipse(
          // ⚠️ 相位**無條件**存進模型。`includeTransitToNatalAspects` 只該管
          //    「要不要印進 description」（呈現），不該連帶把資料從模型裡刪掉。
          //    兩者綁在一起的後果：想用這份資料做計算的消費端，只剩下
          //    正則解析我們自己渲染的字串這一條路。
          //    這個改動**不動素材一個 byte** —— description 仍然照舊由旗標決定。
          description, eclipse, transitToNatalAspects, zodiacDegree,
        ), eclipse.max, null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月相 (只在 SUN 和 MOON 都被選中時計算)
    val lunarPhases = if (Planet.SUN in transitingStars && Planet.MOON in transitingStars) {
      sequenceOf(
        0.0 to LunarPhase.NEW,
        90.0 to LunarPhase.FIRST_QUARTER,
        180.0 to LunarPhase.FULL,
        270.0 to LunarPhase.LAST_QUARTER
      ).flatMap { (angle, phase) ->
        relativeTransitImpl.getPeriodRelativeTransitGmtJulDays(Planet.MOON, Planet.SUN, fromGmtJulDay, toGmtJulDay, angle, config.horoscopeConfig.starTypeOptions).map { gmtJulDay ->
          val outer = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
          val zodiacDegree = outer.getZodiacDegree(Planet.MOON)!!
          val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.MOON, Planet.SUN)
          val description = buildString {
            append("${Planet.MOON.toString(Locale.ENGLISH)} ")
            append(
              when (phase) {
                LunarPhase.NEW           -> "🌑"
                LunarPhase.FIRST_QUARTER -> "🌓"
                LunarPhase.FULL          -> "🌕"
                LunarPhase.LAST_QUARTER  -> "🌗"
              }
            )
            append(phase.getTitle(Locale.ENGLISH))
            append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
            if (config.includeTransitToNatalAspects) {
              if (transitToNatalAspects.isNotEmpty()) {
                appendLine()
                appendLine(transitToNatalAspects.describeAspects(grain))
              }
            }
          }
          AstroEventDto(
            AstroEvent.LunarPhaseEvent(
              description, phase, zodiacDegree,
              if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
            ),
            gmtJulDay, null,
            Span.INSTANT, Impact.GLOBAL
          )
        }
      }
    } else emptySequence()

    /**
     * ingress 三型別的「對本命相位」—— **只在 [AstrologyTraversalConfig.includeTransitToNatalAspects]
     * 為真時才計算**。每個事件要多建一張該時刻的星盤（`getModel`），
     * 換座／換宮的事件數遠多於滯留，不設閘門會讓既有呼叫端平白付這筆成本。
     */
    fun aspectsAt(gmt: GmtJulDay, planet: AstroPoint): List<SynastryAspect> =
      if (config.includeTransitToNatalAspects)
        horoscopeFeature.getModel(gmt, loc, config.horoscopeConfig).outerToInner(planet)
      else emptyList()

    // 星體換星座
    val signDegrees = (0..<360 step 30).map { it.toDouble().toZodiacDegree() }.toSet()
    val signIngresses = transitingStars.asSequence().flatMap { planet ->
      starTransitImpl.getRangeTransitGmt(planet, signDegrees, fromGmtJulDay, toGmtJulDay, options = config.horoscopeConfig.starTypeOptions).map { (zDeg, gmt) ->

        val speed = starPositionImpl.calculate(planet, gmt, config.horoscopeConfig.centric, config.horoscopeConfig.coordinate , config.horoscopeConfig.starTypeOptions).speedLng
        val (oldSign, newSign, eventType) = if (speed >= 0) {
          // 順行：進入 zDeg.sign，來自前一個星座
          Triple(zDeg.sign.prev, zDeg.sign, "Ingresses (enters)")
        } else {
          // 逆行：離開 zDeg.sign，進入前一個星座
          Triple(zDeg.sign, zDeg.sign.prev, "Regresses (retrogrades into)")
        }

        val transitToNatalAspects = aspectsAt(gmt, planet)
        val description = buildString {
          append("${planet.toString(Locale.ENGLISH)} $eventType Sign. ")
          append("From ${oldSign.getTitle(Locale.ENGLISH)} to ${newSign.getTitle(Locale.ENGLISH)}")
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(grain))
          }
        }
        AstroEventDto(
          AstroEvent.SignIngress(description, planet, oldSign, newSign, transitToNatalAspects),
          gmt, null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 本命星體赤緯表（用於 OOB 事件的 parallel/contra-parallel 計算）
    val natalDeclinations: Map<AstroPoint, Double> = natalPoints.mapNotNull { point ->
      (point as? Star)?.let { star ->
        runCatching {
          star to starPositionImpl.calculate(star, model.gmtJulDay, Centric.GEO, Coordinate.EQUATORIAL, config.horoscopeConfig.starTypeOptions).lat
        }.getOrNull()
      }
    }.toMap()

    fun findNatalParallels(transitPlanet: Star, transitDecl: Double): List<DeclinationAspect> {
      return natalDeclinations.mapNotNull { (natalPoint, natalDecl) ->
        DeclinationAspect.calculate(transitPlanet, natalPoint, transitDecl, natalDecl)
      }.sortedBy { it.orb }
    }

    fun List<DeclinationAspect>.describeParallels(grain: BirthDataGrain): String {
      return joinToString("\n") { da ->
        val typeLabel = when (da.type) {
          DeclinationAspectType.PARALLEL -> "Parallel"
          DeclinationAspectType.CONTRA_PARALLEL -> "Contra-parallel"
        }
        buildString {
          append("\t(p) [transiting ${da.transitPoint.toString(Locale.ENGLISH)}]")
          append(" $typeLabel")
          append(" [natal ${da.natalPoint.toString(Locale.ENGLISH)}")
          // 同 describeAspects：宮位需要精確出生時刻，用 [includeAxis] 不用推導。
          if (grain.includeAxis) {
            model.getHouse(da.natalPoint)?.let { append(" (H$it)") }
          }
          append("] decl ${da.natalDeclination.truncateToString(2)}° orb = ${da.orb.truncateToString(2)}")
        }
      }
    }

    // 星體進入/離開 OOB（使用獨立的 oobPlanets 集合，不受 transitingStars 限制）
    val oobIngresses = config.oobPlanets.asSequence().flatMap { planet ->
      val stepDays = if (planet == Planet.MOON) 0.25 else 1.0

      // 檢查 range 開始時是否已在 OOB，如果是則插入初始狀態事件
      val initialDecl = starPositionImpl.calculate(planet, fromGmtJulDay, Centric.GEO, Coordinate.EQUATORIAL, config.horoscopeConfig.starTypeOptions).lat
      val initialOobEvent = if (kotlin.math.abs(initialDecl) > OobCrossingFinder.OBLIQUITY) {
        val parallels = findNatalParallels(planet, initialDecl)
        val transitToNatalAspects = aspectsAt(fromGmtJulDay, planet)
        val description = buildString {

          append("${planet.toString(Locale.ENGLISH)} is OOB at range start. ")
          append("Declination = ${initialDecl.truncateToString(2)}°")
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine(); appendLine(transitToNatalAspects.describeAspects(grain))
          }
          if (config.includeTransitToNatalAspects && parallels.isNotEmpty()) {
            appendLine()
            appendLine(parallels.describeParallels(grain))
          }
        }
        sequenceOf(AstroEventDto(
          AstroEvent.OobIngress(description, planet, true, initialDecl,
            if (config.includeTransitToNatalAspects) parallels else emptyList(),
            transitToNatalAspects),
          fromGmtJulDay, null, Span.INSTANT, Impact.GLOBAL
        ))
      } else emptySequence()

      val crossings = OobCrossingFinder.findCrossings(
        starPositionImpl, planet, fromGmtJulDay, toGmtJulDay,
        options = config.horoscopeConfig.starTypeOptions,
        stepDays = stepDays
      ).map { crossing ->
        val parallels = findNatalParallels(planet, crossing.declination)
        val transitToNatalAspects = aspectsAt(crossing.gmtJulDay, planet)
        val direction = if (crossing.entering) "enters OOB" else "returns from OOB"
        val description = buildString {
          append("${planet.toString(Locale.ENGLISH)} $direction. ")
          append("Declination = ${crossing.declination.truncateToString(2)}°")
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine(); appendLine(transitToNatalAspects.describeAspects(grain))
          }
          if (config.includeTransitToNatalAspects && parallels.isNotEmpty()) {
            appendLine()
            appendLine(parallels.describeParallels(grain))
          }
        }
        AstroEventDto(
          AstroEvent.OobIngress(description, planet, crossing.entering, crossing.declination,
            if (config.includeTransitToNatalAspects) parallels else emptyList(),
            transitToNatalAspects),
          crossing.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL
        )
      }

      initialOobEvent + crossings
    }

    // 星體換宮位
    val houseIngresses = if (grain == BirthDataGrain.MINUTE) {
      // grain 到「時/分」, 宮位可信
      val cuspDegreeMap: Map<ZodiacDegree, Int> = model.cuspDegreeMap.reverse()
      val cuspDegrees = cuspDegreeMap.keys.toSet()
      transitingStars.asSequence().flatMap { planet ->
        starTransitImpl.getRangeTransitGmt(planet, cuspDegrees, fromGmtJulDay, toGmtJulDay, options = config.horoscopeConfig.starTypeOptions).map { (zDeg, gmt) ->
          // maybe retrograde
          val speed = starPositionImpl.calculate(planet, gmt, config.horoscopeConfig.centric, config.horoscopeConfig.coordinate , config.horoscopeConfig.starTypeOptions).speedLng
          val cuspHouseNumber = cuspDegreeMap.getValue(zDeg)

          // 根據順行或逆行，決定 old/new house 以及文字描述
          val (oldHouse, newHouse, eventType) = if (speed >= 0) {
            // 順行：進入 cuspHouseNumber，來自前一個宮位
            val fromHouse = if (cuspHouseNumber == 1) 12 else cuspHouseNumber - 1
            Triple(fromHouse, cuspHouseNumber, "Ingresses (enters)")
          } else {
            // 逆行：離開 cuspHouseNumber，退入前一個宮位
            val toHouse = if (cuspHouseNumber == 1) 12 else cuspHouseNumber - 1
            Triple(cuspHouseNumber, toHouse, "Regresses (retrogrades into)")
          }

          // 產生更精確的文字描述
          val transitToNatalAspects = aspectsAt(gmt, planet)
          val description = buildString {
            append("${planet.toString(Locale.ENGLISH)} $eventType House. ")
            append("From House $oldHouse to House $newHouse")
            if (transitToNatalAspects.isNotEmpty()) {
              appendLine()
              appendLine(transitToNatalAspects.describeAspects(grain))
            }
          }
          AstroEventDto(
            AstroEvent.HouseIngress(description, planet, oldHouse, newHouse, transitToNatalAspects),
            gmt, null, Span.INSTANT, Impact.PERSONAL
          )
        }
      }
    } else {
      emptySequence()
    }

    return sequence {

      if (config.globalAspect) {
        // 全球星體交角
        yieldAll(globalAspectEvents)
      }

      if (config.personalAspect) {
        // 全球 to 個人 , 交角
        yieldAll(searchPersonalEvents(transitingStars, natalPoints).map { aspectData ->
          val (outerStar, innerStar) = aspectData.points.let { it[0] to it[1] }
          val description = buildString {
            append("[transiting ${outerStar.toString(Locale.ENGLISH)}] ${aspectData.aspect} [natal ${innerStar.toString(Locale.ENGLISH)}]")
          }
          AstroEventDto(AstroEvent.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.PERSONAL)
        })
      }

      if (config.voc) {
        // 月亮空亡
        yieldAll(moonVocSeq)
      }
      if (config.stationaryPlanets.isNotEmpty()) {
        // 行星滯留（由 config.stationaryPlanets 控制）
        yieldAll(planetStationaries)
      }
      if (config.retrograde) {
        // 星體當日逆行
        yieldAll(planetRetrogrades)
      }
      if (config.eclipse) {
        // 日食
        yieldAll(solarEclipses)
        // 月食
        yieldAll(lunarEclipses)
      }
      if (config.lunarPhase) {
        // 月相
        yieldAll(lunarPhases)
      }
      if (config.signIngress) {
        // 星體換星座
        yieldAll(signIngresses)
      }
      if (config.houseIngress && grain == BirthDataGrain.MINUTE) {
        // 星體換宮位
        yieldAll(houseIngresses)
      }
      if (config.oobIngress) {
        // 星體進入/離開 OOB
        yieldAll(oobIngresses)
      }
    }
  }

  private fun List<SynastryAspect>.describeAspects(grain: BirthDataGrain): String {
    return this.sortedBy { it.orb }.joinToString("\n") { aspect: SynastryAspect ->
      buildString {
        append("\t")
        append("(p) [transiting ${aspect.outerPoint.toString(Locale.ENGLISH)}")
        // 宮位只在有精確出生時刻時成立 —— 用 [includeAxis] 而非 `grain == MINUTE` 的推導。
        if (grain.includeAxis) {
          append(" (H${aspect.outerPointHouse})")
        }
        append("] ")
        append(aspect.aspect)
        append(" [natal ${aspect.innerPoint.toString(Locale.ENGLISH)}")
        if (grain.includeAxis) {
          append(" (H${aspect.innerPointHouse})")
        }
        append("] orb = ${aspect.orb.truncateToString(2)}")
        // ⭐ 入相／出相 —— 受測者實際用它分主被動（「四分、逼近」vs「對分、分離」）。
        //    null ＝ 該路徑沒有提供「稍後位置」，此時**不印**（不得猜）。
        aspect.aspectType?.also { append(" (${it.name.lowercase(Locale.ENGLISH)})") }
      }
    }
  }
}
