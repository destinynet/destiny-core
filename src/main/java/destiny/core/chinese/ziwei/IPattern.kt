/**
 * Created by smallufo on 2018-07-03.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import java.io.Serializable


interface IPatternContext {

  enum class Target {
    /** 只針對命宮 */
    MAIN,
    /** 計算所有宮位 */
    EVERY
  }

  val target: Target
}

class PatternContext(
  override val target: IPatternContext.Target) : IPatternContext, Serializable

interface IPattern {

  /** 可以指定宮位 (傳入地支) */
  fun getPattern(it: IPlate, pContext: IPatternContext): Pattern?
}

/** 單純命宮實作 */
abstract class PatternSingleImpl : IPattern, Serializable {
  override fun getPattern(it: IPlate, pContext: IPatternContext): Pattern? {
    return getSingle(it, pContext)
  }

  abstract fun getSingle(it: IPlate, pContext: IPatternContext): Pattern?
}

/** 支援多重宮位 */
abstract class PatternMultipleImpl : IPattern, Serializable {
  override fun getPattern(it: IPlate, pContext: IPatternContext): Pattern? {
    return when (pContext.target) {
      IPatternContext.Target.MAIN -> getMultiple(it, setOf(it.mainHouse.branch), pContext)
      IPatternContext.Target.EVERY -> getMultiple(it, Branch.values().toSet(), pContext)
    }
  }

  abstract fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern?
}

