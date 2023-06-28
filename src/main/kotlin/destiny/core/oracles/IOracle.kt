/**
 * @author smallufo
 * Created on 2008/4/28 at 上午 1:51:37
 */
package destiny.core.oracles

import kotlin.reflect.KClass

interface IOracle<T : IClause> {

  /** 取得該籤詩集的所有條文 */
  val clauses: List<T>

  val size: Int

  fun getLocatorMap(): Map<KClass<out Any>, Locator<*, T>>

  /**
   * 定位(查詢) 出單一籤詩
   * @param matcher 通常為 data class (or Int / Long) , 確保 pattern matching 可定位出最多1組籤詩
   */
  @Suppress("UNCHECKED_CAST")
  fun <M : Any> getClause(matcher : M) : T? {
    return getLocatorMap()
      .filter { (k,_) -> k == matcher::class }
      .values.firstOrNull()
      ?.let { loc -> loc as Locator<M, T> }
      ?.getClauseFromMatcher(matcher)
  }

  @Suppress("UNCHECKED_CAST")
  fun getIntLocator(clause : IClause) : Int {
    val locator: Locator<Int, T> = (getLocatorMap()[Int::class] as Locator<Int, T>)
    return locator.getMatcherFromClause(clause)
  }

}

interface Locator<in M, T> {

  fun getClauseFromMatcher(matcher: M): T?

  fun getMatcherFromClause(clause: IClause): @UnsafeVariance M
}

