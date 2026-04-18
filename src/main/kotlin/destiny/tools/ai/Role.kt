package destiny.tools.ai

/**
 * 對話訊息的角色。
 *
 * - [USER] / [ASSISTANT]：標準雙向對話角色
 * - [SYSTEM]：系統提示（背景指令、user profile、natal data 等）
 *     - **Provider-native 支援**（Claude / OpenAI / Gemini 等）：翻譯為該 API 的
 *       top-level `system` field 或 `system` role（不佔 messages[]）
 *     - **不支援的 provider**：fallback 為 `"user"` role（等同既有 workaround，
 *       preserves backward compat；不要用 `"assistant"`，否則會讓 LLM 以為自己說過這段）
 */
enum class Role {
  USER,
  ASSISTANT,
  SYSTEM,
}
