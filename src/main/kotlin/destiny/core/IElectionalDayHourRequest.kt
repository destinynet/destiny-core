/**
 * Created by smallufo on 2025-05-30.
 */
package destiny.core

import destiny.core.electional.Electional
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


interface IElectionalDayHourRequest : Electional.IDayHourModel {
  val inner: IBirthDataNamePlace
}

@Serializable
data class ElectionalDayHourRequest(
  @Contextual
  override val inner: IBirthDataNamePlace,
  @Contextual
  private val dayHourModel: Electional.IDayHourModel,
) : IElectionalDayHourRequest, Electional.IDayHourModel by dayHourModel
