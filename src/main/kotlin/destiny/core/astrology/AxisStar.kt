package destiny.core.astrology

import destiny.tools.Score
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ScoreTwoDecimalSerializer
import kotlinx.serialization.Serializable

@Serializable
data class AxisStar(val astroPoint: AstroPoint,
                    @Serializable(with = DoubleTwoDecimalSerializer::class)
                    val orb: Double,
                    @Serializable(with = ScoreTwoDecimalSerializer::class)
                    val score: Score?)
