package destiny.core

/** 3x3 Grid */
enum class TriGrid {
  B,   // 底
  LB,  // 左下
  L, // 左
  LU,  // 左上
  U, // 上
  RU, // 右上
  R, // 右
  RB, // 右下
  C; // 中間

  /** 順時針 */
  fun clockWise(): TriGrid? {
    return when (this) {
      C -> null
      B -> LB
      LB -> L
      L -> LU
      LU -> U
      U -> RU
      RU -> R
      R -> RB
      RB -> B
    }
  }
}