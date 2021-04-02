package destiny.core

enum class Scale {
  YEAR,
  MONTH,
  DAY,
  HOUR;

  fun toChineseChar(): String {
    return when (this) {
      YEAR -> "年"
      MONTH -> "月"
      DAY -> "日"
      HOUR -> "時"
    }
  }
}
