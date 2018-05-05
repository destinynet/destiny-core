/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology.eclipse

/** 日環食 */
interface IEclipseAnnular {

  val annularBegin: Double

  val annularEnd: Double
}


/** 中線 ， 開始與結束  */
interface IEclipseCenter {

  val centerBegin: Double

  val centerEnd: Double
}
