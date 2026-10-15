package destiny.core


/** 性別  */
enum class Gender(val male: Boolean) {

  M(true),
  F(false);

  /** 表單、DB、JSON 用的一碼：`M`／`F` */
  val code: String get() = name

  companion object {
    /**
     * `M`／`F`（不分大小寫、容忍前後空白）→ enum；不認得、空白、null 都回 null，**不替呼叫端猜預設**。
     * 2026-10-16 之前全站 13 處各自寫 `if (s == "M") M else F`，預設方向還不一致（表單未知→M、LIFF 未知→F）。
     */
    fun of(code: String?): Gender? {
      val c = code?.trim()?.takeIf { it.isNotEmpty() } ?: return null
      return entries.firstOrNull { it.name.equals(c, ignoreCase = true) }
    }
  }
}
