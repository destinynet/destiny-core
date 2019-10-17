/**
 * Created by smallufo at 2008/11/6 下午 2:41:54
 */
package destiny.astrology

import destiny.astrology.AspectData.Type.APPLYING
import destiny.astrology.AspectData.Type.SEPARATING

/** 一個星盤當中，兩個星體，是否形成交角。以及即將形成 ([APPLYING] , 入相位)，還是離開該交角 ([SEPARATING] , 出相位)  */
interface IAspectApplySeparate {

  /** 如果不是形成 aspect 交角，會傳回 null  */
  fun getAspectType(h: IHoroscopeModel, p1: Point, p2: Point, aspect: Aspect): AspectData.Type?

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun getAspectAndType(h: IHoroscopeModel, p1: Point, p2: Point, aspects: Collection<Aspect>): Pair<Aspect , AspectData.Type>?

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun getAspectType(h: IHoroscopeModel, p1: Point, p2: Point, aspects: Collection<Aspect>): AspectData.Type? {
    return getAspectAndType(h, p1, p2, aspects)?.second
  }
}
