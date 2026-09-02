package destiny.core

import kotlinx.serialization.Serializable


/**
 * 主詞 —— 這件事**是他做的，還是發生在他身上的**。⚠️ **判讀側專用**。
 *
 * 幾乎每一個 [Situation] 都有一個**共用同一組幾何的鏡像**：
 * 「他提分手」↔「他被提分手」、「他賣出資產」↔「他被迫變現」。
 * 兩邊在星象上往往分不出，在生活裡卻是完全相反的兩件事 ——
 * **一個沒有主詞的預測在實務上不可用。**
 *
 * ## 與 [EventRole] 的關係：**語意相同，值域不同，用在不同側**
 *
 * [ACTIVE] ≡ [EventRole.INITIATOR]、[PASSIVE] ≡ [EventRole.RECIPIENT]，同一根軸。
 * 這兩個 enum 不是兩件事，別把它們當成兩根獨立的維度 —— 差別只有一個：
 * **本 enum 多一個 [INDETERMINATE]**，因而只能用在判讀側。
 *
 * | | 進料側（使用者申報的事實） | 判讀側（模型在不確定下的主張） |
 * |---|---|---|
 * | 型別層「哪些主詞可能」 | [Situation.roles] | — |
 * | 逐筆／逐條的答案 | `AbstractEvent.role`（[EventRole]，`null` ＝ 未答） | 本 enum |
 * | 「分不出來」怎麼表達 | 欄位留 `null`（未答） | [INDETERMINATE]（答了：分不出） |
 *
 * ⚠️ 兩者的「分不出來」**語意不同**，不可互相轉換：進料側的 `null` 是「沒有人回答這個問題」，
 * 判讀側的 [INDETERMINATE] 是「模型看過幾何，主張這一格分不出主客」。
 * 前者是缺漏，後者是**結論**。⇒ [EventRole] 刻意不提供 `toAgency()`（理由見該 enum 的 KDoc），
 * 本 enum 同樣不提供反向轉換。
 *
 * ## 用在哪裡
 *
 * `TriggerRule` / `TypeGroup` / `ForecastWindow` —— 都是模型的產出，不是使用者的申報。
 * 在那個位置，[INDETERMINATE] 是**合法且受歡迎**的答案：假裝分得出來比誠實地說分不出來糟得多。
 * 但必須一併說明「要分出來需要什麼資料」，否則它會變成不作答的藉口。
 */
@Serializable
enum class Agency {
  /** 他是發起的一方：他選擇、他推進、他取得 */
  ACTIVE,

  /** 他是承受的一方：由他人或外部條件決定，他回應 */
  PASSIVE,

  /** 在現有的資料精度下分不出主客 */
  INDETERMINATE,
}
