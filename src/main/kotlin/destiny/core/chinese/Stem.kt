package destiny.core.chinese

import destiny.core.ILoop
import destiny.core.chinese.FiveElement.*

/** 天干系統  */
enum class Stem : Comparable<Stem>, IFiveElement, IYinYang , ILoop<Stem> {

  甲, 乙, 丙, 丁, 戊,
  己, 庚, 辛, 壬, 癸;


  /** 甲[0] ... 癸[9]  */
  val index: Int = ordinal

  /** 甲[1] ... 癸[10]  */
  val indexFromOne: Int = ordinal + 1


  /**
   * 取得下 n 個天干為何
   * n = 0 : 傳回自己
   */
  override fun next(n: Int): Stem {
    val targetIndex = (ordinal + n).mod(entries.size)
    return entries[targetIndex]
  }


  /**
   * 取得此天干，領先另一個天干，多少距離. 其值一定為正值
   * 甲 領先 甲 0 步
   * 甲 領先 乙 9 步
   * ...
   * 甲 領先 癸 1 步
   */
  fun getAheadOf(other: Stem): Int {
    val steps = index - other.index
    return if (steps >= 0) steps else steps + 10
  }

  /**
   * 實作 [IFiveElement] 的 getFiveElement()
   */
  override val fiveElement: FiveElement
    get() = when (getIndex(this)) {
      0, 1 -> 木
      2, 3 -> 火
      4, 5 -> 土
      6, 7 -> 金
      8, 9 -> 水
      else -> throw AssertionError("HeavenlyStems Error : cannot getFiveElements() : " + toString())
    }

  override val booleanValue: Boolean
    get() = getIndex(this) % 2 == 0

  val combined: Pair<Stem, FiveElement>
    get() {
      val stem = entries[(ordinal + 5) % 10]
      val element = when (ordinal % 5) {
        0    -> 土
        1    -> 金
        2    -> 水
        3    -> 木
        4    -> 火
        else -> throw RuntimeException("impossible")
      }
      return stem to element
    }

  /**
   * 十二運算法 - 根據天干的陰陽性決定順逆行
   * 陽干順行，陰干逆行
   */
  fun twelve(twelve: Twelve): Branch {
    // 每個天干的長生起始位置
    val startBranch = when (this) {
      甲 -> Branch.亥  // 甲木長生在亥
      乙 -> Branch.午  // 乙木長生在午
      丙 -> Branch.寅  // 丙火長生在寅
      丁 -> Branch.酉  // 丁火長生在酉
      戊 -> Branch.寅  // 戊土長生在寅（同丙火）
      己 -> Branch.酉  // 己土長生在酉（同丁火）
      庚 -> Branch.巳  // 庚金長生在巳
      辛 -> Branch.子  // 辛金長生在子
      壬 -> Branch.申  // 壬水長生在申
      癸 -> Branch.卯  // 癸水長生在卯
    }

    val twelveIndex = twelve.ordinal

    return if (this.booleanValue) {
      // 陽干順行：長生、沐浴、冠帶、臨官...
      startBranch.next(twelveIndex)
    } else {
      // 陰干逆行：長生、沐浴、冠帶、臨官...（從自己的長生位開始逆行）
      startBranch.prev(twelveIndex)
    }
  }

  companion object {

    /** 從五行 以及 陰陽 建立天干  */
    operator fun get(fiveElement: FiveElement, yinYang: Boolean): Stem {
      return when (fiveElement) {
        木 -> if (yinYang) 甲 else 乙
        火 -> if (yinYang) 丙 else 丁
        土 -> if (yinYang) 戊 else 己
        金 -> if (yinYang) 庚 else 辛
        水 -> if (yinYang) 壬 else 癸
      }
    }

    /**
     * 抓取天干的 index , 為 0-based <BR></BR>
     * 0 為 甲
     * 1 為 乙
     * ...
     * 9 為 癸
     * @param index
     * @return
     */
    operator fun get(index: Int): Stem {
      return entries[index.mod(entries.size)]
    }

    operator fun get(c: Char): Stem? {
      return entries.firstOrNull { it.name == c.toString() }
    }

    /** 甲[0] ... 癸[9]  */
    fun getIndex(hs: Stem): Int {
      return entries.indexOf(hs)
    }

    fun Stem.combinedCount(vararg elements: Stem): Int {
      return elements.count { this.combined.first == it }
    }
  }

}
