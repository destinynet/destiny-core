package destiny.core.astrology

import destiny.core.Graph
import destiny.core.ITimeLoc
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.rules.Misc
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.abs

/**
 * 與「人」無關的星盤資料
 * 沒有性別 [destiny.core.Gender]
 */
interface IHoroscopeModel : ITimeLoc {

  /** 地名 */
  val place: String?

  /** 分宮法  */
  val houseSystem: HouseSystem

  /** 座標系統  */
  val coordinate: Coordinate

  /** 中心系統  */
  val centric: Centric

  /** 溫度  */
  val temperature: Double?

  /** 壓力  */
  val pressure: Double?

  /** 星體位置表 */
  val positionMap: Map<AstroPoint, IPosWithAzimuth>

  /** 地盤 12宮 (1~12) , 每宮宮首在黃道幾度*/
  val cuspDegreeMap: Map<Int, ZodiacDegree>

  /** 行星空亡表 */
  val vocMap: Map<Planet, Misc.VoidCourseSpan>

  /** 行星時 , Planetary Hour */
  val planetaryHour : PlanetaryHour?

  /** 星體逆行狀態 */
  val retrogradePhaseMap: Map<Star, RetrogradePhase>

  /** Ruler */
  val rulingHouseMap: Map<Planet, Set<RulingHouse>>

  // ==================================== 以下為 推導值 ====================================

  /**
   * 星體於黃經的度數
   */
  val pointDegreeMap: Map<AstroPoint, ZodiacDegree>
    get() = positionMap.mapValues { (_, posWithAzimuth) -> posWithAzimuth.lngDeg }

  /**
   * 星體於黃道上的星座
   */
  val pointSignMap: Map<AstroPoint, ZodiacSign>
    get() = pointDegreeMap.mapValues { (_, lngDeg) -> lngDeg.sign }

  /**
   * 星座星體列表，按照黃道經度排列
   */
  val signPointsMap : Map<ZodiacSign, List<AstroPoint>>
    get() = ZodiacSign.entries.associateWith { sign ->
      pointSignMap.filter { (k, v) -> sign == v }
        .map { (k, v) -> k }
        .sortedBy { p -> positionMap[p]!!.lng }
        .toList()
    }


  /**
   * @return 取得 GMT 時刻
   */
  val gmt: ChronoLocalDateTime<*>
    get() = julDayResolver.getLocalDateTime(gmtJulDay)

  /**
   * 承上 , 取得 GMT , 但帶入自訂的 reverse Julian Day converter
   */
  fun getGmt(revJulDayFunc: Function1<GmtJulDay, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    return revJulDayFunc.invoke(gmtJulDay)
  }


  /**
   * @return 取得 LMT 時刻
   */
  val lmt: ChronoLocalDateTime<*>
    get() = TimeTools.getLmtFromGmt(gmt, location)

  /**
   * 承上 , 取得 LMT , 但帶入自訂的 reverse Julian Day converter
   */
  fun getLmt(revJulDayFunc: Function1<GmtJulDay, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    return TimeTools.getGmtFromLmt(getGmt(revJulDayFunc), location)
  }

  val points: Set<AstroPoint>
    get() = positionMap.keys

  /**
   * 取得所有行星 [Planet] 的位置
   */
  val planetPositionWithAzimuth: Map<Planet, IPosWithAzimuth>
    get() = positionMap
      .filter { it.key is Planet }
      .mapKeys { it.key as Planet }
      .toMap()

  /**
   * 取得所有小行星 [Asteroid] 的位置
   */
  val asteroidPositionWithAzimuth: Map<Asteroid, IPosWithAzimuth>
    get() = positionMap
      .filter { it.key is Asteroid }
      .mapKeys { it.key as Asteroid }
      .toMap()

  /**
   * 取得八個漢堡學派虛星 [Hamburger] 的位置
   */
  val hamburgerPositionWithAzimuth: Map<Hamburger, IPosWithAzimuth>
    get() = positionMap
      .filter { it.key is Hamburger }
      .mapKeys { it.key as Hamburger }
      .toMap()

  fun getGraph(ruler : IRuler) : Graph<Planet> {
    val rulerMap: Map<ZodiacSign, Planet> = ZodiacSign.entries.associateWith { sign ->
      with(ruler) {
        sign.getRulerPoint(null) as Planet
      }
    }

    val planetSignMap: Map<Planet, ZodiacSign> by lazy {
      points.filterIsInstance<Planet>().filter { it in Planet.classicalList }.associateWith { p -> getZodiacSign(p)!! }
    }

    return Analyzer.analyzeHoroscope(planetSignMap, rulerMap)
  }

  val houses : List<House>
    get() {
      return (1..12).map { houseIndex ->
        getHouse(houseIndex)
      }.toList()
    }

  /**
   * 取得第幾宮的宮首落於黃道幾度。
   *
   * @param cusp 1 <= cusp <= 12
   */
  fun getCuspDegree(cusp: Int): ZodiacDegree {
    return cuspDegreeMap.getCuspDegree(cusp)
  }


  /**
   * 取得單一 Horoscope 中 , 任兩顆星的交角
   */
  fun getAngle(fromPoint: AstroPoint, toPoint: AstroPoint): Double {
    return positionMap.getValue(fromPoint).lngDeg.getAngle(positionMap.getValue(toPoint).lngDeg)
  }


  /** 取得一顆星體 Point / Star 在星盤上的角度  */
  fun getPositionWithAzimuth(point: AstroPoint): IPosWithAzimuth {
    return positionMap.getValue(point)
  }


  /**
   * 取得一連串星體的位置（含地平方位角）
   */
  fun getPositionWithAzimuth(stars: List<Star>): Map<Star, IPosWithAzimuth> {
    return positionMap
      .filter { (k, _) -> stars.contains(k) }
      .mapKeys { it as Star }
      .toMap()
  }


  fun getPositionWithAzimuth(clazz: Class<out AstroPoint>): Map<AstroPoint, IPosWithAzimuth> {
    return positionMap
      .filter { (k, _) -> k.javaClass == clazz }
      .toMap()
  }


  /** 取得星體的位置以及地平方位角  */
  fun getPosition(point: AstroPoint): IPosWithAzimuth? {
    return positionMap[point]
  }

  fun getStarPosition(star: Star): IStarPositionWithAzimuth? {
    return positionMap[star]?.takeIf { it is IStarPositionWithAzimuth }?.let { it as IStarPositionWithAzimuth }
  }


  /** 取得某星 位於什麼星座以及度數  */
  fun getZodiacDegree(point: AstroPoint): ZodiacDegree? {
    return getPosition(point)?.lngDeg
  }

  /** 取得某星 位於什麼星座  */
  fun getZodiacSign(point: AstroPoint): ZodiacSign? {
    return getZodiacDegree(point)?.sign
  }

  /**
   * 黃道幾度，落於第幾宮 ( 1 <= house <= 12 )
   */
  fun getHouse(zodiacDegree: ZodiacDegree): Int {
    return getHouse(zodiacDegree, cuspDegreeMap)
  }

  /**
   * @param point 取得此星體在第幾宮
   */
  fun getHouse(point: AstroPoint): Int? {
    return positionMap[point]?.let { pos -> getHouse(pos.lngDeg) }
  }


  fun getHouse(index: Int): House {
    require(index in 1..12)
    return House(
      index, getCuspDegree(index), positionMap.filter { (p, _) ->
        getHouse(p) == index
      }.toList()
        .sortedWith(compareBy(ZodiacDegree.Companion.OrientalComparator) { p -> p.second.lngDeg })
    )
  }

  /**
   * 取得第幾宮內的星星列表 , 1 <= index <=12 , 並且按照黃道度數「由小到大」排序
   */
  fun getHousePoints(index: Int): List<AstroPoint> {
    if (index < 1)
      return getHousePoints(index + 12)
    return if (index > 12) getHousePoints(index - 12)
    else positionMap.filter { (_, posWithAzimuth) -> getHouse(posWithAzimuth.lngDeg) == index }
      .map { it.key to it.value}
      .sortedBy { it.second.lng }
      .map { it.first }
  }


  /**
   * 在不同的 [HouseType] 當中有哪些星體
   */
  fun getHousePoints(houseType: HouseType): List<AstroPoint> {
    val houses = when (houseType) {
      HouseType.ANGULAR   -> listOf(1, 4, 7, 10)
      HouseType.SUCCEDENT -> listOf(2, 5, 8, 11)
      HouseType.CADENT    -> listOf(3, 6, 9, 12)
    }

    return positionMap.filter { (_, posWithAzimuth) -> getHouse(posWithAzimuth.lngDeg) in houses }.map { it.key }
  }

  /**
   * 黃道幾度，落於第幾宮 ( 1<= house <= 12) , 以及，距離宮首、宮尾各幾度
   */
  fun getHousePartition(zodiacDegree: ZodiacDegree): HousePartition {
    val house = getHouse(zodiacDegree, cuspDegreeMap)
    val toHead = zodiacDegree.getAngle(getCuspDegree(house))
    val toTail = zodiacDegree.getAngle(getCuspDegree(house + 1))
    return HousePartition(house, toHead, toTail)
  }

  fun getHousePartition(p: AstroPoint): HousePartition? {
    return getPosition(p)?.lngDeg?.let {
      getHousePartition(it)
    }
  }

  private fun getUnsortedMidPoints(points: Set<AstroPoint> = this.points): List<MidPoint> {
    return points.filter { this.points.contains(it) }
      .let { pointList ->
        pointList.flatMapIndexed { index, p1 ->
          pointList.drop(index + 1).map { p2 ->
            p1 to p2
          }
        }
      }
      .filterNot { (p1, p2) -> p1 is LunarNode && p2 is LunarNode }
      .map { (p1, p2) ->
        val pts = setOf(p1, p2)
        val p1Deg = positionMap.getValue(p1).lngDeg
        val p2Deg = positionMap.getValue(p2).lngDeg
        val midPointDeg = p1Deg.midPoint(p2Deg)

        MidPoint(pts, midPointDeg, getHouse(midPointDeg))
      }
  }

  fun getMidPoints(points: Set<AstroPoint> = this.points, comparator: Comparator<IMidPoint> = MidPointAstroPointComparator): List<IMidPoint> {
    return getUnsortedMidPoints(points)
      .sortedWith(comparator)
  }

  fun getMidPointsWithFocal(
    points: Set<AstroPoint> = this.points.filterNot { it is FixedStar }.toSet(),
    orb: Double = 2.0,
    comparator: Comparator<in IMidPointWithFocal> = MidPointOrbIntenseComparator
  ): List<IMidPointWithFocal> {
    return getUnsortedMidPoints(points).flatMap { midpoint ->
      points.filterNot { midpoint.points.contains(it) }  // Exclude points that are already part of the midpoint
        .mapNotNull { focal ->
          val focalDegree = positionMap.getValue(focal).lngDeg
          val angle = midpoint.degree.getAngle(focalDegree)
          if (angle <= orb) {
            MidPointWithFocal(midpoint, focal, angle)
          } else {
            null
          }
        }
    }.sortedWith(comparator)
  }

  fun getRetrogradePhase(star: Star) : RetrogradePhase? {
    return retrogradePhaseMap[star]
  }

  fun getMotion(star: Star): Motion? {
    return getStarPosition(star)?.speedLng?.let { value ->
      if (value >= 0)
        Motion.DIRECT
      else
        Motion.RETROGRADE
    }
  }

  fun getRulingHouses(planet : Planet) : Set<RulingHouse> {
    return rulingHouseMap[planet]?: emptySet()
  }

  /**
   * 傳回四軸附近（左右 [thresholdOrb]) 的星體，以及他們與 Axis 的交角
   */
  fun getAxisStars(thresholdOrb : Double) : Map<Axis, List<Pair<AstroPoint , Double>>> {
    val angularConsideringPoints = points.filter { it is Planet || it is FixedStar || it is LunarPoint }

    return mapOf(
      Axis.RISING to 1,
      Axis.MERIDIAN to 10,
      Axis.SETTING to 7,
      Axis.NADIR to 4
    ).map { (axis, houseNum) ->
      axis to this.getCuspDegree(houseNum)
    }.associate { (axis, zDeg) ->
      axis to angularConsideringPoints
        .asSequence()
        .map { p -> p to getZodiacDegree(p) }
        .filter { (_, pDeg) -> pDeg != null }
        .map { (p, zDeg) -> p to zDeg!! }
        .map { (p, pDeg) -> p to pDeg.getAngle(zDeg) }
        .filter { (_: AstroPoint, orb) ->
          orb < thresholdOrb
        }
        .sortedBy { it.second }
        .toList()
    }.toMap()
  }

  companion object {

    fun getHouse(degree: ZodiacDegree, cuspDegreeMap: Map<Int, ZodiacDegree>): Int {
      return (1..11).firstOrNull { house ->
        if ((abs(cuspDegreeMap.getValue(house + 1).value - cuspDegreeMap.getValue(house).value) < 180)) {
          //沒有切換360度的問題
          cuspDegreeMap.getValue(house).value <= degree.value && degree.value < cuspDegreeMap.getValue(house + 1).value
        } else {
          //切換360度
          cuspDegreeMap.getValue(house).value <= degree.value && degree.value < cuspDegreeMap.getValue(house + 1).value + 360 ||
            cuspDegreeMap.getValue(house).value <= degree.value + 360 && degree.value < cuspDegreeMap.getValue(house + 1).value
        }
      } ?: 12
    }

    /**
     * 取得此兩顆星，對於此交角 Aspect 的誤差是幾度
     * 例如兩星交角 175 度 , Aspect = 沖 (180) , 則 誤差 5 度
     */
    fun getAspectError(positionMap: Map<AstroPoint, IPos>, p1: AstroPoint, p2: AstroPoint, aspect: Aspect): Double? {
      return positionMap[p1]?.let { p1Pos ->
        positionMap[p2]?.let { p2Pos ->
          val angle = p1Pos.lngDeg.getAngle(p2Pos.lngDeg)
          abs(aspect.degree - angle)
        }
      }
    }

    private val julDayResolver = JulDayResolver1582CutoverImpl()
  } // companion

} // interface




