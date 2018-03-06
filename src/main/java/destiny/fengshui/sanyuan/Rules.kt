/**
 * Created by smallufo on 2018-03-06.
 */
package destiny.fengshui.sanyuan


enum class MntDir {
  山, 向
}



/** 全局 特徵 */
sealed class ChartRule {
  data class 合十(val mntDir: MntDir) : ChartRule()
  data class 伏吟元旦盤(val mntDir: MntDir) : ChartRule()
  data class 反吟(val mntDir: MntDir) : ChartRule()
}

/** 單宮 特徵 */
sealed class BlockRule {
  data class 合十(val mntDir: MntDir) : BlockRule()
  data class 伏吟元旦盤(val mntDir: MntDir) : BlockRule()
  data class 伏吟天盤(val mntDir: MntDir) : BlockRule()
  data class 反吟元旦盤(val mntDir: MntDir) : BlockRule()
}