/**
 * Created by smallufo on 2025-06-17.
 */
package destiny.core

import destiny.core.calendar.ILocation
import java.time.LocalDate


@Deprecated("")
interface IYearMonthReportRequest {

  val bdnp: IBirthDataNamePlace
  val nowLocation: ILocation
  val nowPlace: String?
  val localDate: LocalDate
  val span: Span

  enum class Span {
    YEAR,
    MONTH
  }
}

data class YearMonthReportRequest(
  override val bdnp: IBirthDataNamePlace,
  override val nowLocation: ILocation,
  override val nowPlace: String?,
  override val localDate: LocalDate,
  override val span: IYearMonthReportRequest.Span,
) : IYearMonthReportRequest
