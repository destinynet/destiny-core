/**
 * @author smallufo
 * @date 2002/9/23
 * @time 上午 11:19:48
 */
package destiny.fengshui.sanyuan


/**
 * 正針24山(地盤) , 子山從 352.5 度開始
 */
class EarthlyCompass : AbstractMountainCompass() {

  override val initDegree: Double
    get() = 352.5
}
