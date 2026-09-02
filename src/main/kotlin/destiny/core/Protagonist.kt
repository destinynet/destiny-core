package destiny.core

import kotlinx.serialization.Serializable

/**
 * 主角 —— 這件事**是誰身上的事**。與 [EventRole] / [Agency] **正交**。
 *
 * 兩者正交的判別測試：「她安排母親動手術」= 她是 [EventRole.INITIATOR]、
 * 主角是 [CLOSE_OTHER]，**兩者同時成立**。
 * ⇒ 把它塞成 [EventRole] 的第三個值，就是「兩根軸擠進一個符號」的老病換一層重演。
 *
 * ⚠️ 目前**只用於判讀側**（預測某段期間的主角可能不是當事人本人）。
 * 進料側不設此欄：使用者時間軸上「身邊的人出事」由 `Situation` 自己承載
 * （`FAMILY_LOSS` / `FAMILY_HEALTH_CRISIS`），不必每一筆都問。
 */
@Serializable
enum class Protagonist {
  /** 當事人本人 */
  SELF,

  /** 身邊的人（家人、伴侶、密友、共事者）—— 當事人被波及，但主角不是他 */
  CLOSE_OTHER,
}
