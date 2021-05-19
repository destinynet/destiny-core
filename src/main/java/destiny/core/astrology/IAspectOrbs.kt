/**
 * @author smallufo
 * Created on 2007/11/24 at 下午 11:47:20
 */
package destiny.core.astrology

/**
 * 「現代占星術」使用，取得相位交角「容許度」的介面
 * 內定實作為 [AspectOrbsDefaultImpl]
 */
interface IAspectOrbs {

  fun getAspectOrbAndThreshold(aspect: Aspect) : Pair<Double , Double>

  fun getAspectOrb(aspect: Aspect): Double
}
