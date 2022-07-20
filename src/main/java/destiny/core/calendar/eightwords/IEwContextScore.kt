/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.calendar.eightwords


interface IEwContextScore : java.io.Serializable {

  fun getScore(ewContext: IEightWordsContextModel): Double

}
