package destiny.core.astrology


data class AstrologyTraversalConfig(
  /** 占星盤的設定 */
  val horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
  /** 計算全球星體(outer ring)交角 */
  val globalAspect: Boolean = true,
  /** 計算外圈對內圈星體(outer to inner)交角 */
  val personalAspect: Boolean = true,
  /** 月亮空亡 */
  val voc: Boolean = true,
  /** 星體滯留 */
  val stationary: Boolean = true,
  /** 星體逆行 */
  val retrograde: Boolean = true,
  /** 日食、月食 */
  val eclipse: Boolean = true,
  /** 月相 */
  val lunarPhase: Boolean = true,
  /**
   * 是否在 DTO 中包含「過運星體 to 本命星體」的詳細相位資料。預設為 false
   */
  val includeTransitToNatalAspects: Boolean = false,
  /** 星體換星座 */
  val signIngress: Boolean = true,
  /** 星體換宮位 */
  val houseIngress: Boolean = true,
//  val outerStars: Set<AstroPoint> = horoscopeConfig.points,
//  val innerStars: Set<AstroPoint> = horoscopeConfig.points
)
