/**
 * Created by smallufo on 2025-06-17.
 */
package destiny.core.ai.reply

import destiny.core.BirthDataDomain
import destiny.tools.ai.model.FormatSpec
import kotlinx.serialization.Serializable


@Serializable
data class YearMonthReply(
  val domains: Map<BirthDataDomain, String>
) {
  companion object {
    val monthlyFormatSpec = FormatSpec.of<YearMonthReply>("lunar_return_reply", "Response format for this month")
    val formatSpec = FormatSpec.of<YearMonthReply>("year_month_reply", "Response format for this period")
  }
}
