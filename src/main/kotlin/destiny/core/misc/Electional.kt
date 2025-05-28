/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.misc

import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.ILocation
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime



class Electional {

  interface ITraversalModel {
    val bdnp: IBirthDataNamePlace
    val fromDate: LocalDate
    val toDate: LocalDate
    val loc: ILocation
  }

  @Serializable
  data class TraversalModel(
    override val bdnp: IBirthDataNamePlace,
    @Serializable(with = LocalDateSerializer::class)
    override val fromDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    override val toDate: LocalDate,
    override val loc: ILocation = bdnp.location,
  ) : ITraversalModel

  interface IRequestModel : ITraversalModel {
    val topN: Int
    val purpose: ElectionalPurpose
    val filterOutHour: Boolean
    val workingHour: Pair<LocalTime, LocalTime>?
    val notes: String?
  }

  @Serializable
  data class RequestModel(
    private val traversal: ITraversalModel,
    override val topN: Int,
    override val purpose: ElectionalPurpose,
    override val filterOutHour: Boolean,
    override val notes: String?,
    override val workingHour: Pair<@Serializable(with = LocalTimeSerializer::class) LocalTime, @Serializable(with = LocalTimeSerializer::class) LocalTime>? = null,
  ) : IRequestModel, ITraversalModel by traversal
}
