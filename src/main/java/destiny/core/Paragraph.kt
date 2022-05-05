package destiny.core

import java.io.Serializable

enum class Hint {
  POSITIVE,
  NEGATIVE,
  NOTICE
}

sealed class Paragraph(open val content: String, open val hint: Hint? = null) : Serializable {
  data class Normal(override val content: String, override val hint: Hint? = null) : Paragraph(content, hint)
  data class Scripture(override val content: String, override val hint: Hint? = null) : Paragraph(content, hint)
}
