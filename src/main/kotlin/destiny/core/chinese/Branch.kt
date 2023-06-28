package destiny.core.chinese


import destiny.core.ILoop
import destiny.tools.ArrayTools

/**
 * 地支系統
 */
enum class Branch : ILoop<Branch> {

  子, 丑, 寅, 卯, 辰, 巳,
  午, 未, 申, 酉, 戌, 亥;

  /** 取得對沖 的地支  */
  val opposite: Branch
    get() = Branch[index + 6]

  /** 取得 六合 的地支  */
  val combined: Branch
    get() {
      return when (this) {
        子 -> 丑
        丑 -> 子
        寅 -> 亥
        卯 -> 戌
        辰 -> 酉
        巳 -> 申

        午 -> 未
        未 -> 午
        申 -> 巳
        酉 -> 辰
        戌 -> 卯
        亥 -> 寅
      }
    }

  /**
   * 子[0] ~ 亥[11]
   */
  val index: Int
    get() = getIndex(this)

  /**
   * 子[1] ~ 亥[12]
   */
  val indexFromOne: Int
    get() = index + 1

  /** 三合 */
  val trinities : Set<Branch>
    get() = setOf(this, next(4), next(8))

  /** 刑 */
  val quads : Set<Branch>
    get() = setOf(this, next(3), next(6), next(9))

  /**
   * 取得下 n 個地支為何
   * n = 0 : 傳回自己
   */
  override fun next(n: Int): Branch {
    return get(getIndex(this) + n)
  }


  /**
   * 此地支「領先」另一個地支多少距離. 其值一定為正值
   * 子領先子 0
   * 子領先丑 11
   * ...
   * 子領先亥 1
   */
  fun getAheadOf(other: Branch): Int {
    val steps = index - other.index
    return if (steps >= 0) steps else steps + 12
  }


  companion object {

    /**
     * 抓取地支的 index , 為 0-based <BR></BR>
     * 0 為 子
     * 1 為 丑
     * ...
     * 11 為 亥
     */
    operator fun get(index: Int): Branch {
      return ArrayTools[values(), index]
    }


    operator fun get(c: Char): Branch? {
      return values().firstOrNull { it.name == c.toString() }
    }

    /**
     * 子[0] ~ 亥[11]
     */
    fun getIndex(eb: Branch): Int {
      return values().indexOf(eb)
    }
  }
}

/** 兩地支「拱」哪個地支 */
fun Collection<Branch?>.trine(): Branch? {
  return this.takeIf { it.size >= 2 }?.toList()?.let { list ->
    val a = list[0]
    val b = list[1]
    return if (a != null && b != null) {
      when {
        a.getAheadOf(b) == 4 -> a.next(4)
        b.getAheadOf(a) == 4 -> b.next(4)
        else -> null
      }
    } else
      null
  }
}

/** 兩地支「夾」哪個地支 */
fun Collection<Branch?>.grip(): Branch? {
  return this.takeIf { it.size >= 2 }?.toList()?.let { list ->
    val a = list[0]
    val b = list[1]
    return if (a != null && b != null) {
      when {
        a.getAheadOf(b) == 2 -> b.next
        b.getAheadOf(a) == 2 -> a.next
        else -> null
      }
    } else
      null
  }
}
