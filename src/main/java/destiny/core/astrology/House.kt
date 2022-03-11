/**
 * Created by smallufo on 2022-03-11.
 */
package destiny.core.astrology


data class House(val index: Int,
                 val cusp: ZodiacDegree,
                 val pointPositions: List<Pair<Point, IPosWithAzimuth>>) : java.io.Serializable
