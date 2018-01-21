/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:36:05
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.calendar.eightwords.Reactions
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem


/** 天干/地支/十神 的工具箱  */
class ReactionsUtil(
  /** 實作 地支藏干  */
  private val hiddenStemsImpl: IHiddenStems) {

  /**
   * 取得天干的相互關係
   * actor == 乙 , actee == 甲 , 傳回劫財 (乙是甲的劫財)
   * actor == 丙 , actee == 甲 , 傳回食神 (丙是甲的食神)
   * @param actor 作用者（天干）
   * @param actee 被作用者（日干）
   * @return 天干十神
   */
  fun getReaction(actor: Stem, actee: Stem): Reactions {
    if (actor.fiveElement.isProducingTo(actee.fiveElement)) {
      return if (actor.booleanValue == actee.booleanValue)
        Reactions.偏印
      else
        Reactions.正印
    }

    if (actor.fiveElement.isDominatorOf(actee.fiveElement)) {
      return if (actor.booleanValue == actee.booleanValue)
        Reactions.七殺
      else
        Reactions.正官
    }

    if (actor.fiveElement.isDominatedBy(actee.fiveElement)) {
      return if (actor.booleanValue == actee.booleanValue)
        Reactions.偏財
      else
        Reactions.正財
    }

    if (actor.fiveElement.equals(actee.fiveElement)) {
      return if (actor.booleanValue == actee.booleanValue)
        Reactions.比肩
      else
        Reactions.劫財
    }

    if (actor.fiveElement.isProducedBy(actee.fiveElement)) {
      return if (actor.booleanValue == actee.booleanValue)
        Reactions.食神
      else
        Reactions.傷官
    }

    throw RuntimeException("Error occurred when Reactions.getReaction($actor , $actee)!")
  }

  /**
   * 地支藏干對天干的關係
   * @param actor 作用者（地支）
   * @param actee 被作用者（日干）
   * @return 地支十神 List <Reactions>
  </Reactions> */
  fun getReactions(actor: Branch, actee: Stem): List<Reactions> {

    return this.hiddenStemsImpl.getHiddenStems(actor)
      .map { each -> this.getReaction(each, actee) }
      .toList()

  }

  companion object {

    /**
     * 從天干以及其關係，取得其目標天干，例如：甲的劫財，傳回乙
     * @param actor 主角
     * @param reactions 相對關係
     * @return 傳回目標天干
     */
    fun getHeavenlyStems(actor: Stem, reactions: Reactions): Stem {
      return when (reactions) {
        Reactions.比肩 -> actor
        Reactions.劫財 -> Stem.get(actor.fiveElement, !actor.booleanValue)
        Reactions.正印 -> Stem.get(actor.fiveElement.producer, !actor.booleanValue)
        Reactions.偏印 -> Stem.get(actor.fiveElement.producer, actor.booleanValue)
        Reactions.食神 -> Stem.get(actor.fiveElement.product, actor.booleanValue)
        Reactions.傷官 -> Stem.get(actor.fiveElement.product, !actor.booleanValue)
        Reactions.正官 -> Stem.get(actor.fiveElement.dominator, !actor.booleanValue)
        Reactions.七殺 -> Stem.get(actor.fiveElement.dominator, actor.booleanValue)
        Reactions.正財 -> Stem.get(actor.fiveElement.dominateOver, !actor.booleanValue)
        Reactions.偏財 -> Stem.get(actor.fiveElement.dominateOver, actor.booleanValue)
      }
    }
  }


}
