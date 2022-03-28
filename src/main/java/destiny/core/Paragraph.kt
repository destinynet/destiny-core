package destiny.core

import java.io.Serializable

sealed class Paragraph(open val content: String) : Serializable {
  data class Normal(override val content: String) : Paragraph(content)
  data class Scripture(override val content: String) : Paragraph(content)
}
