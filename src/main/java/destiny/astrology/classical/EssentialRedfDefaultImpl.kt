/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 3:50:48
 */
package destiny.astrology.classical

import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity.*
import java.io.Serializable

/**
 * 取得星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail
 * 內定實作為 托勒密 Ptolomy 表格
 */
class EssentialRedfDefaultImpl(private val rulerImpl: IRuler,
                               private val detrimentImpl: IDetriment ,
                               private val exaltImpl : IExaltation ,
                               private val fallImpl : IFall) : IEssentialRedf, Serializable {


  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 null ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 null
   */
  override fun getPointOld(sign: ZodiacSign, dignity: Dignity): Point? {
    return when (dignity) {
    /** 旺 , +5 */
      RULER -> rulerImpl.getPoint(sign)
    /** 廟 , +4  */
      EXALTATION -> exaltImpl.getPoint(sign)// findPoint(sign, starExaltationMap) // nullable
    /** 落 , -4  */
      FALL ->  fallImpl.getPoint(sign) // findPoint(sign, starFallMap) // nullable
    /** 陷 , -5 */
      DETRIMENT -> detrimentImpl.getPoint(sign)
    }
  }

}
