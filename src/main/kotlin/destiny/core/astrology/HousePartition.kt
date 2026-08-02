/**
 * Created by smallufo on 2023-03-07.
 */
package destiny.core.astrology

import destiny.tools.JSerializable


data class HousePartition(val house: Int, val toHead: Double, val toTail: Double) : JSerializable
