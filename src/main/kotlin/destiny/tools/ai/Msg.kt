package destiny.tools.ai

sealed class Content {
  data class StringContent(val string: String) : Content()
  data class MimeContent(val mimeType: String, val data: String) : Content()
}

/**
 * 通用對話訊息 — provider-agnostic。
 *
 * @property role [Role.USER] / [Role.ASSISTANT] / [Role.SYSTEM]
 * @property contents 訊息內容（文字 / 圖片等）
 * @property cacheable Prompt caching hint —— 支援的 provider（Claude `cache_control: ephemeral`）
 *           會標此訊息為可快取。同一 user 跨 turn 重複的大塊靜態內容（如 natal data summary）
 *           標 `cacheable = true` 後，第 2 輪以後 input tokens 打 1 折。
 *           不支援的 provider 忽略此 flag。
 * @property cacheTtl Cache TTL 長度偏好（只在 [cacheable]=true 時有意義）。null = provider 預設
 *           （Anthropic = 5m）；[CachePromptTtl.SHORT] = 短；[CachePromptTtl.LONG] = 長。
 *           Provider impl 負責 mapping 到具體 API value（e.g. Claude `"5m"` / `"1h"`）。
 */
data class Msg(
  val role: Role,
  val contents: List<Content>,
  val cacheable: Boolean = false,
  val cacheTtl: CachePromptTtl? = null,
) {
  constructor(role: Role, content: String) : this(role, listOf(Content.StringContent(content)))

  constructor(role: Role, content: String, cacheable: Boolean)
    : this(role, listOf(Content.StringContent(content)), cacheable)

  constructor(role: Role, content: String, cacheable: Boolean, cacheTtl: CachePromptTtl?)
    : this(role, listOf(Content.StringContent(content)), cacheable, cacheTtl)

  val stringContents: String = contents.filterIsInstance<Content.StringContent>().joinToString("\n") { it.string }
}
