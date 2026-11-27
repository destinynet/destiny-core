package destiny.tools

import kotlin.enums.enumEntries

/**
 * 以 enum 為 key 的策略 map（`Map<XxxImpl, IXxx>`）必須涵蓋 enum 的每一個值。
 *
 * 使用者可以在 config 裡選任何一個 enum 值，所以少接一個 impl 就是接線錯誤；
 * 在建構時就檢查，啟動即失敗，而不是等到有人選到那個值才在取值處爆掉。
 *
 * @param name 出錯時用來辨認是哪個 map（通常傳參數名）
 */
inline fun <reified K : Enum<K>, V> Map<K, V>.requireAllEnumKeys(name: String) {
  val missing = enumEntries<K>().filterNot { it in this }
  require(missing.isEmpty()) { "$name 缺少 ${K::class.simpleName} 的實作：$missing" }
}
