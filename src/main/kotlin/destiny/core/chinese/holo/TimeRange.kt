/**
 * Created by smallufo on 2019-03-08.
 */
package destiny.core.chinese.holo

/**
 * used for Gmt Julian Day comparison
 */
interface TimeRange<T: Comparable<T>> {

  val start : T

  /**
   * 這是與 [ClosedRange] 最不同之處
   */
  val endExclusive : T

  operator fun contains(value: T): Boolean = value >= start && value < endExclusive

}