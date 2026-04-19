/**
 * Created by smallufo on 2026-04-20.
 */
package destiny.tools.ai


/**
 * Provider-agnostic prompt-cache TTL 抽象 — 各家 LLM 的 cache lifetime 大相逕庭，本 enum
 * 只暴露 user-meaningful 的「短／長」兩檔，讓 caller 用**行為 intent** 而非具體秒數表達需求：
 *
 * - [SHORT]：「一波連續追問」—— user 在幾分鐘內連續問相關問題（如「下週一呢？」「下週二呢？」）
 *            的典型節奏。Anthropic 對應 `"5m"` ephemeral（write 1.25x）、OpenAI `"in_memory"`、
 *            Gemini 預設 300s TTL。
 * - [LONG]：「user 離開去查資料再回來」—— 典型如擇日（查行事曆）、合盤（確認對方生辰）等需要
 *           user 查證後接續對話的場景。Anthropic 對應 `"1h"` ephemeral（beta，write 2x）、
 *           OpenAI `"24h"` (opt-in)、Gemini 3600s TTL。
 *
 * 具體 API value 的 mapping 住在各 provider impl（[destiny.tools.ai.llm.ClaudeImpl] 等）。
 */
enum class CachePromptTtl {
  /** 一波連續追問節奏，5-min 量級 */
  SHORT,
  /** user 查資料後接續對話節奏，1-hour 量級 */
  LONG,
}
