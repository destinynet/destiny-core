/**
 * Created by smallufo at 2008/11/6 下午 2:41:54
 */
package destiny.astrology

/** 一個星盤當中，兩個星體，是否形成交角。以及即將形成 (APPLYING , 入相位)，還是離開該交角 (SEPARATING , 出相位)  */
interface IAspectApplySeparate {

  enum class AspectType {
    APPLYING, SEPARATING
  }

  /** 如果不是形成 aspect 交角，會傳回 null  */
  fun getAspectType(h: IHoroscopeModel, p1: Point, p2: Point, aspect: Aspect): AspectType?

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun getAspectType(h: IHoroscopeModel, p1: Point, p2: Point, aspects: Collection<Aspect>): AspectType?
}
