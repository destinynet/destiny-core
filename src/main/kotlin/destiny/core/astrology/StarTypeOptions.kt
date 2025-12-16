/**
 * Created by smallufo on 2025-12-16.
 *
 * Calculation options for star position calculations.
 * These options determine how certain celestial bodies are calculated.
 *
 * Architecture Design:
 * - Star objects (LunarNode, LunarApsis) represent directional meaning only (NORTH/SOUTH, APOGEE/PERIGEE)
 * - Calculation type (TRUE/MEAN for nodes, OSCU/MEAN for apsis) is specified via StarTypeOptions
 * - This separation of concerns keeps Star objects simple and calculation types configurable
 *
 * Usage Examples:
 * ```kotlin
 * // Calculate North Node using MEAN method (numerically stable)
 * starPosition.calculate(LunarNode.NORTH, gmtJulDay, centric, coordinate,
 *                        StarTypeOptions(nodeType = NodeType.MEAN))
 *
 * // Calculate North Node using TRUE method (more accurate but can produce NaN)
 * starPosition.calculate(LunarNode.NORTH, gmtJulDay, centric, coordinate,
 *                        StarTypeOptions(nodeType = NodeType.TRUE))
 *
 * // Calculate Apogee using OSCU method (osculating/instantaneous)
 * starPosition.calculate(LunarApsis.APOGEE, gmtJulDay, centric, coordinate,
 *                        StarTypeOptions(apsisType = MeanOscu.OSCU))
 *
 * // Use predefined options for convenience
 * starPosition.calculate(LunarNode.NORTH, gmtJulDay, centric, coordinate,
 *                        StarTypeOptions.MEAN)     // Default: stable calculations
 * starPosition.calculate(LunarNode.NORTH, gmtJulDay, centric, coordinate,
 *                        StarTypeOptions.PRECISE)  // Precise but potentially unstable
 * ```
 *
 * Effects on Different Star Types:
 * - **LunarNode**: nodeType determines whether to use TRUE or MEAN node calculation
 * - **LunarApsis**: apsisType determines whether to use OSCU or MEAN apsis calculation
 * - **Planet, Asteroid, etc.**: StarTypeOptions has no effect
 */
package destiny.core.astrology

import kotlinx.serialization.Serializable

@Serializable
data class StarTypeOptions(
  /**
   * Calculation type for LunarNode (TRUE or MEAN).
   *
   * - **TRUE**: Osculating (instantaneous) node position
   *   - More astronomically accurate for current moment
   *   - Can produce NaN values in extreme edge cases
   *   - Shows slight oscillation around mean position
   *
   * - **MEAN**: Averaged node position
   *   - Numerically stable (never produces NaN)
   *   - Smoothed orbital average
   *   - Recommended for most astrological applications
   *
   * Only affects [LunarNode] calculations. Has no effect on other star types.
   */
  val nodeType: NodeType = NodeType.MEAN,

  /**
   * Calculation type for LunarApsis (OSCU or MEAN).
   *
   * - **OSCU**: Osculating (instantaneous) apsis position
   *   - True instantaneous apogee/perigee position
   *   - Can show retrograde motion
   *   - More astronomically precise
   *
   * - **MEAN**: Averaged apsis position
   *   - Smoothed elliptical average
   *   - Always direct motion (no retrograde)
   *   - More stable and predictable
   *
   * Only affects [LunarApsis] calculations. Has no effect on other star types.
   */
  val apsisType: MeanOscu = MeanOscu.MEAN
) : java.io.Serializable {
  companion object {
    /**
     * Default options using MEAN for both node and apsis calculations.
     *
     * This is the **recommended default** for most use cases:
     * - Provides numerical stability (no NaN errors)
     * - Suitable for natal charts, transits, and general astrological work
     * - Matches traditional astrological ephemeris conventions
     *
     * Example:
     * ```kotlin
     * starPosition.calculate(LunarNode.NORTH, time, geo, ecliptic, StarTypeOptions.MEAN)
     * ```
     */
    val MEAN = StarTypeOptions()

    /**
     * Options using TRUE/OSCU for precise astronomical calculations.
     *
     * Use when you need:
     * - Maximum astronomical accuracy for current moment
     * - Retrograde/stationary calculations (only TRUE nodes and OSCU apsis can be stationary)
     * - Scientific/research applications
     *
     * **Warning**: Can produce NaN values in edge cases. Handle with appropriate error checking.
     *
     * Example:
     * ```kotlin
     * starPosition.calculate(LunarNode.NORTH, time, geo, ecliptic, StarTypeOptions.PRECISE)
     * ```
     */
    val PRECISE = StarTypeOptions(nodeType = NodeType.TRUE, apsisType = MeanOscu.OSCU)
  }
}
