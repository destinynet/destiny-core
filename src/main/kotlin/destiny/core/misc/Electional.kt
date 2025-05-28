/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.misc

import destiny.core.calendar.ILocation
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


class Electional {

  interface ITraversalModel {
    val fromDate: LocalDate
    val toDate: LocalDate
    val loc: ILocation
  }

  @Serializable
  data class TraversalModel(
    @Serializable(with = LocalDateSerializer::class)
    override val fromDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    override val toDate: LocalDate,
    override val loc: ILocation
  ) : ITraversalModel


  interface IRequestModel : ITraversalModel {
    val topN: Int
    val purpose: ElectionalPurpose
    val filterOutHour: Boolean
    val timeRange: TimeRange?
    val notes: String?
  }

  @Serializable
  data class RequestModel(
    private val traversal: ITraversalModel,
    override val topN: Int,
    override val purpose: ElectionalPurpose,
    override val filterOutHour: Boolean,
    override val timeRange: TimeRange? = null,
    override val notes: String? = null,
  ) : IRequestModel, ITraversalModel by traversal
}
