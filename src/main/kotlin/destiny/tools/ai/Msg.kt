package destiny.tools.ai

sealed class Content {
  data class StringContent(val string: String) : Content()
  data class MimeContent(val mimeType: String, val data: String) : Content()
}

data class Msg(val role: Role, val contents: List<Content>) {
  constructor(role: Role, content: String) : this(role, listOf(Content.StringContent(content)))

  val stringContents: String = contents.filterIsInstance<Content.StringContent>().joinToString("\n") { it.string }
}
