/**
 * Created by smallufo on 2018-03-06.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol


enum class MntDir {
  山, 向
}

enum class MntDirSpec {
  到山到向,
  上山下水,
  雙星到山,
  雙星到向
}


/** 全局 特徵 */
sealed class ChartRule {

  data class 合十(val mntDir: MntDir) : ChartRule()
  data class 伏吟元旦盤(val mntDir: MntDir) : ChartRule()

  data class 反吟(val mntDir: MntDir) : ChartRule()

  object 父母三般卦 : ChartRule() // 每宮內的都是 147 , 258 or 369

  // 亦稱「全盤連茹格」
  object 連珠三般卦 : ChartRule() // 每宮內的數字 都是差一 , 例如 123 , 789 , 891 , 912

  /** 七星打劫 , 必定是 [MntDirSpec.雙星到向] 的情形 */
  data class Robbery(
    val symbol: Symbol,         // 必定只會有「離」「坎」兩個可能 . 「離」為真打劫；「坎」為假打劫
    val map: Map<Symbol, Int>   // 乾->1 , 震->4 , 離->7 這樣的 mapping
                    ) : ChartRule()

  object 八純卦 : ChartRule() //大凶格局
}

/** 單宮 特徵 */
sealed class BlockRule {
  data class 合十(val mntDir: MntDir) : BlockRule()
  data class 伏吟元旦盤(val mntDir: MntDir) : BlockRule()
  data class 伏吟天盤(val mntDir: MntDir) : BlockRule()
  data class 反吟元旦盤(val mntDir: MntDir) : BlockRule()
}

