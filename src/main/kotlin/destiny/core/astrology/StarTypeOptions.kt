/**
 * Created by smallufo on 2025-12-16.
 *
 * Calculation options for star position calculations.
 * These options override the default calculation type embedded in certain Star objects.
 *
 * For example:
 * - If a LunarNode.NORTH_TRUE is passed but options.nodeType is MEAN, the calculation will use MEAN node.
 * - If a LunarApsis.APOGEE_OSCU is passed but options.apsisType is MEAN, the calculation will use MEAN apsis.
 *
 * This design facilitates gradual migration from the old API (where calculation type is embedded in Star)
 * to the new API (where calculation type is specified in options).
 */
package destiny.core.astrology

import kotlinx.serialization.Serializable

@Serializable
data class StarTypeOptions(
  /**
   * Calculation type for LunarNode (TRUE or MEAN).
   * TRUE node has more accurate instantaneous position but can produce NaN in edge cases.
   * MEAN node uses averaged motion and is numerically stable.
   */
  val nodeType: NodeType = NodeType.MEAN,

  /**
   * Calculation type for LunarApsis (OSCU or MEAN).
   * OSCU (osculating) gives the instantaneous apsis position.
   * MEAN gives the averaged apsis position.
   */
  val apsisType: MeanOscu = MeanOscu.MEAN
) {
  companion object {
    /**
     * Default options using MEAN for both node and apsis calculations.
     * This is the recommended default for numerical stability.
     */
    val DEFAULT = StarTypeOptions()

    /**
     * Options using TRUE/OSCU for more accurate but potentially unstable calculations.
     */
    val PRECISE = StarTypeOptions(nodeType = NodeType.TRUE, apsisType = MeanOscu.OSCU)
  }
}
