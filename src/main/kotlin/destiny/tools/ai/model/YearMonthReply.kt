/**
 * Created by smallufo on 2025-06-17.
 */
package destiny.tools.ai.model

import kotlinx.serialization.Serializable


@Serializable
data class YearMonthReply(
  val summary: String,
  val domains: Map<BirthDataDomain, String>
) {
  companion object {
    val formatSpec = FormatSpec.of<YearMonthReply>("year_month_reply", "Response format for this period")
  }
}
