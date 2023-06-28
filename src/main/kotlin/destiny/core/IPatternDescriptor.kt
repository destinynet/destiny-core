/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.core

interface IPatternDescriptor<in T : IPattern> {
  fun getDescriptor(pattern: T): Descriptive
}