/**
 * @author smallufo
 * @date 2002/9/23
 * @time 上午 11:19:48
 */
package destiny.core.fengshui

import destiny.iching.Symbol

/** 後天八卦盤 */
val acquiredSymbolCompass = AcquiredSymbolCompass()

/**
 * 正針24山(地盤) , 子山從 352.5 度開始
 */
class EarthlyCompass : AbstractMountainCompass() {

  override val initDegree: Double
    get() = 352.5 // 360-7.5

  /** 此座山 是位於哪一卦中 */
  override fun getSymbol(mnt: Mountain): Symbol {
    return getSymbolCenter(mnt).let { deg ->
      acquiredSymbolCompass.get(deg)
    }
  }
}

/**
 * 人盤 (比地盤 逆時針轉 7.5度)
 */
class HumanCompass : AbstractMountainCompass() {
  override val initDegree: Double
    get() = 345.0

  /** 此座山 是位於哪一卦中 */
  override fun getSymbol(mnt: Mountain): Symbol {
    return getSymbolCenter(mnt).let { deg ->
      acquiredSymbolCompass.get(deg)
    }
  }
}


/**
 * 天盤 (比地盤 順時針轉 7.5 度)
 */
class HeavenlyCompass : AbstractMountainCompass() {

  override val initDegree: Double
    get() = 0.0

  /** 此座山 是位於哪一卦中 */
  override fun getSymbol(mnt: Mountain): Symbol {
    return getSymbolCenter(mnt).let { deg ->
      acquiredSymbolCompass.get(deg)
    }
  }
}
