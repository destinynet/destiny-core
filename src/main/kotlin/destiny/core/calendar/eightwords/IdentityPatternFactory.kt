/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.Scale
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.trilogy

interface IdentityPatternFactory {
  fun IEightWords.getPatterns(): Set<IdentityPattern>
}

object IdentityPatterns {

  val trilogy = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<IdentityPattern.Trilogy> {
      return Sets.combinations(getScaleMap().entries.map { (scale: Scale, sb: IStemBranch) -> scale to sb.branch }.toSet(), 3).filter { triples: Set<Pair<Scale, Branch>> ->
        val tripleList = triples.toList()
        val pair1 = tripleList[0]
        val pair2 = tripleList[1]
        val pair3 = tripleList[2]
        trilogy(pair1.second, pair2.second, pair3.second) != null
      }.map { triples: Set<Pair<Scale, Branch>> ->
        IdentityPattern.Trilogy(triples)
      }.toSet()
    }
  }
}

fun IEightWords.getIdentityPatterns(): Set<IdentityPattern> {
  return setOf(
    IdentityPatterns.trilogy
  ).flatMap { factory: IdentityPatternFactory ->
    with(factory) {
      this@getIdentityPatterns.getPatterns()
    }
  }.toSet()
}
