/**
 * @author smallufo
 * Created on 2008/4/28 at 上午 2:51:36
 */
package destiny.oracles

import destiny.tools.random.RandomService
import kotlin.math.abs
import kotlin.math.pow

object ClauseChooser {

  /**
   * 「餘數抽籤」法
   * 從n個阿拉伯數字，取出一個條文
   * 演算法 : 直接求餘數即可
   */
  fun <T> choose(digits: List<Int>, oracle: IOracle<T>): Pair<Int, T> {

    require(digits.all { it in 0..9 }) {
      "Some tokens inside $digits is out of 0..9"
    }


    //開始計算 digits 的值
    var sum = 0
    for (i in digits.indices)
      sum += 10.0.pow((digits.size - i - 1).toDouble()).toInt() * digits[i]


    val index = sum % oracle.size.let {
      if (it == 0)
        oracle.size
      else
        it
    }

    return index to oracle.getClause(index)!!
  }

  fun <T> choose(randomService: RandomService, seed: String, oracle: IOracle<T>): Pair<Int, T> {
    require(seed.isNotEmpty()) {
      "Question is null !"
    }

    val seedHash = seed.hashCode()
    val random = randomService.getIntegers(1, 1, 10000)[0]

    val index = abs(seedHash * random % oracle.clauses.size).let {
      if (it == 0)
        oracle.size
      else
        it
    }

    val selectedClause = oracle.getClause(index)!!
    return Pair(index, selectedClause)
  }


}
