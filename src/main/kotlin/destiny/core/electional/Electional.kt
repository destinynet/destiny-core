/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.electional

import destiny.core.TimeRange
import destiny.core.astrology.BirthDataGrain
import destiny.core.calendar.ILocation
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


class Electional {


  @Serializable
  sealed interface ITraversalModel {
    val fromDate: LocalDate
    val toDate: LocalDate
    val loc: ILocation?
  }

  @Serializable
  data class TraversalModel(
    @Serializable(with = LocalDateSerializer::class)
    override val fromDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    override val toDate: LocalDate,
    @Serializable(with = ILocationSerializer::class)
    override val loc: ILocation?
  ) : ITraversalModel


  /**
   * focus on 日、時 的擇日
   */
  interface IDayHourModel : ITraversalModel {
    val topN: Int
    val purpose: ElectionalPurpose
    val grain: BirthDataGrain
    val timeRange: TimeRange?
    val notes: String?
  }

  /**
   * focus on 日、時 的擇日
   */
  @Serializable
  data class DayHourModel(
    private val traversal: TraversalModel,
    override val topN: Int,
    override val purpose: ElectionalPurpose,
    override val grain: BirthDataGrain,
    override val timeRange: TimeRange? = null,
    override val notes: String? = null,
  ) : IDayHourModel, ITraversalModel by traversal
}
