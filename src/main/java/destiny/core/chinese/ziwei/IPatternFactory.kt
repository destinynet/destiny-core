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


enum class PatternType {
  GOOD, EVIL
}

interface IPattern {
  val name: String
  val type: PatternType
  val notes: String?
}


enum class ParaType {
  NORMAL,
  SCRIPTURE
}

sealed class Paragraph(val paraType: ParaType, val content: String) {
  class Normal(content: String) : Paragraph(ParaType.NORMAL, content)
  class Scripture(content: String) : Paragraph(ParaType.SCRIPTURE, content)
}


interface IPatternDescription : IPattern {
  val paras: List<Paragraph>
}

interface IPatternDescriptionFactory {
  fun getPatternDescription(pattern: IPattern): IPatternDescription?
}


data class PatternDescription(
  val pattern: IPattern,
  override val paras: List<Paragraph>
                             ) : IPatternDescription, IPattern by pattern, Serializable


class PatternContext(
  override val target: IPatternContext.Target) : IPatternContext, Serializable

interface IPatternFactory {
  /** 可以指定宮位 (傳入地支) */
  fun getPattern(it: IPlate, pContext: IPatternContext): IPattern?
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

