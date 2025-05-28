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


interface IDayHourSelectionModel {
  val bdnp: IBirthDataNamePlace
  val fromDate: LocalDate
  val toDate: LocalDate
  val loc: ILocation
  val filterOutHour: Boolean
  val workingHour: Pair<LocalTime, LocalTime>?
}

@Serializable
data class DayHourSelectionModel(
  override val bdnp: IBirthDataNamePlace,
  @Serializable(with = LocalDateSerializer::class)
  override val fromDate: LocalDate,
  @Serializable(with = LocalDateSerializer::class)
  override val toDate: LocalDate,
  override val loc: ILocation,
  override val filterOutHour: Boolean,
  override val workingHour: Pair<@Serializable(with = LocalTimeSerializer::class) LocalTime, @Serializable(with = LocalTimeSerializer::class) LocalTime>? = null,
) : IDayHourSelectionModel

interface IDayHourSelectionRequest : IDayHourSelectionModel {
  val purpose: ElectionalPurpose
  val notes : String?
}

@Serializable
data class DayHourSelectionRequest(
  private val dayHourSelectionModel: DayHourSelectionModel,
  override val purpose: ElectionalPurpose,
  override val notes : String?,
) : IDayHourSelectionRequest, IDayHourSelectionModel by dayHourSelectionModel
