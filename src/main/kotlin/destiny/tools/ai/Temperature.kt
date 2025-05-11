/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai


@JvmInline
value class Temperature(val value : Double) {
  init {
    require(value in 0.0..1.0)
  }
}
