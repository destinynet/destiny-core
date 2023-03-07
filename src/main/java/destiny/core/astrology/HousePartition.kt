/**
 * Created by smallufo on 2023-03-07.
 */
package destiny.core.astrology

import java.io.Serializable


data class HousePartition(val house: Int, val toHead: Double, val toTail: Double) : Serializable
