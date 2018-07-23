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


sealed class Paragraph(open val content: String) : Serializable {
  data class Normal(override val content: String) : Paragraph(content)
  data class Scripture(override val content: String) : Paragraph(content)
}


interface IPatternDescription {
  val pattern: IPattern
  val name: String
    get() = pattern.name
  val notes: String?
    get() = pattern.notes
  val paras: List<Paragraph>
}


interface IPlateDescriptionsFactory {
  fun getPatternDescriptions(plate: IPlate , pContext: IPatternContext): List<IPatternDescription>
}

data class PatternDescription(
  override val pattern: IPattern,
  override val paras: List<Paragraph>) : IPatternDescription, Serializable


class PatternContext(
  override val target: IPatternContext.Target) : IPatternContext, Serializable

interface IPatternFactory {
  /** 可以指定宮位 (傳入地支) */
  fun getPattern(it: IPlate, pContext: IPatternContext): IPattern?
}


fun IPlate.getClassicalPatterns(pContext: IPatternContext): List<IPattern> {
  return ClassicalPattern.values().map { factory ->
    factory.getPattern(this, pContext)
  }.filter { p -> p != null }
    .map { p -> p!! }
    .toList()
}


/** 單純命宮實作 */
abstract class PatternSingleImpl : IPatternFactory, Serializable {
  override fun getPattern(it: IPlate, pContext: IPatternContext): IPattern? {
    return getSingle(it, pContext)
  }

  abstract fun getSingle(it: IPlate, pContext: IPatternContext): IPattern?
}

/** 支援多重宮位 */
abstract class PatternMultipleImpl : IPatternFactory, Serializable {
  override fun getPattern(it: IPlate, pContext: IPatternContext): IPattern? {
    return when (pContext.target) {
      IPatternContext.Target.MAIN -> getMultiple(it, setOf(it.mainHouse.branch), pContext)
      IPatternContext.Target.EVERY -> getMultiple(it, Branch.values().toSet(), pContext)
    }
  }

  abstract fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): IPattern?
}

