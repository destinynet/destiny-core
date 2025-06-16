/**
 * Created by smallufo on 2022-08-06.
 */
package destiny.core.astrology.prediction


class Transit(override val forward: Boolean = true) : AbstractProgression() {

  override val type: ProgressionType = ProgressionType.TRANSIT

  override val numerator: Double = 1.0

  override val denominator: Double = 1.0
}
