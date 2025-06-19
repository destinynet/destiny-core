/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.ISynastryAspect
import destiny.core.calendar.GmtJulDay
import java.io.Serializable


enum class ProgressionType {
  TRANSIT, SECONDARY, TERTIARY, MINOR
}

interface ITransitModel : Serializable {
  val natalTime: GmtJulDay
  val viewTime: GmtJulDay
}

interface IProgressionModel : ITransitModel {
  val type : ProgressionType
  val convergentTime: GmtJulDay
  val progressedAspects: List<ISynastryAspect>
}

data class ProgressionModel(override val type: ProgressionType,
                            override val natalTime: GmtJulDay,
                            override val viewTime: GmtJulDay,
                            override val convergentTime: GmtJulDay,
                            override val progressedAspects: List<ISynastryAspect>) : IProgressionModel
