package destiny.core

import destiny.tools.JSerializable

enum class Hint {
  POSITIVE,
  NEGATIVE,
  NOTICE
}

sealed class Paragraph(open val content: String, open val hint: Hint? = null) : JSerializable {
  data class Normal(override val content: String, override val hint: Hint? = null) : Paragraph(content, hint)
  data class Scripture(override val content: String, override val hint: Hint? = null) : Paragraph(content, hint)
}
