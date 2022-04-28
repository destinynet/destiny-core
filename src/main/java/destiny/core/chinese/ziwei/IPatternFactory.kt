/**
 * Created by smallufo on 2018-07-03.
 */
package destiny.core.chinese.ziwei

import destiny.core.IPattern
import destiny.core.IPatternParasDescription
import destiny.core.chinese.Branch
import java.io.Serializable


interface ZPatternContext {

  enum class Target {
    /** 只針對命宮 */
    MAIN,
    /** 計算所有宮位 */
    EVERY
  }

  val target: Target
}


@Deprecated("")
interface IPlateDescriptionsFactory {
  fun getPatternDescriptions(plate: IPlate, pContext: ZPatternContext): List<IPatternParasDescription>

  fun getDescription(pattern: IPattern): IPatternParasDescription?
}


class PatternContext(
  override val target: ZPatternContext.Target) : ZPatternContext, Serializable

interface IPatternFactory {
  /** 可以指定宮位 (傳入地支) */
  fun getPattern(it: IPlate, pContext: ZPatternContext): ZPattern?
}

/** 單純命宮實作 */
abstract class PatternSingleImpl : IPatternFactory, Serializable {
  override fun getPattern(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return getSingle(it, pContext)
  }

  abstract fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern?
}

/** 支援多重宮位 */
abstract class PatternMultipleImpl : IPatternFactory, Serializable {
  override fun getPattern(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return when (pContext.target) {
      ZPatternContext.Target.MAIN  -> getMultiple(it, setOf(it.mainHouse.branch), pContext)
      ZPatternContext.Target.EVERY -> getMultiple(it, Branch.values().toSet(), pContext)
    }
  }

  abstract fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern?
}

