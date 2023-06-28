/**
 * Created by smallufo on 2015-06-29.
 */
package destiny.tools

import java.util.*
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.collections.set

interface IChainLinks {

  fun <T : Any> chain(map: Map<T, T>): Set<List<T>>

  fun <T : Any> Collection<List<T>>.chain(map : Map<T, T>): Set<List<T>> {
    return this@IChainLinks.chain(map)
  }
}


/**
 * 漂亮、無 recursive 的解法：
 * http://stackoverflow.com/a/31105227/298430
 */
object ChainSlow : IChainLinks {
  override fun <T : Any> chain(map: Map<T, T>): Set<List<T>> {
    return map.keys.asSequence()
      .filter { from: T -> !map.containsValue(from) }
      .map { from: T ->
        generateSequence(from) { k: T ->
          map[k]
        }.toList()
      }.toSet()
  }
}


/**
 * 更驚人的解法，比前一個快百倍！
 * http://stackoverflow.com/a/31108666/298430
 */
object ChainFast : IChainLinks {
  override fun <T : Any> chain(map: Map<T, T>): Set<List<T>> {
    return map.entries.stream().collect(chaining())
  }

  private fun <T> chaining(): Collector<Map.Entry<T, T>, MutableMap<T, ArrayDeque<T>>, Set<List<T>>> {

    val accumulator = BiConsumer { m: MutableMap<T, ArrayDeque<T>>, entry: Map.Entry<T, T> ->
      var k = m.remove(entry.key)
      val v = m.remove(entry.value)
      if (k == null && v == null) {
        k = ArrayDeque<T>().apply {
          addLast(entry.key)
          addLast(entry.value)
        }
        m[entry.key] = k
        m[entry.value] = k
      } else if (k == null) {
        v!!.addFirst(entry.key)
        m[entry.key] = v
      } else if (v == null) {
        k.addLast(entry.value)
        m[entry.value] = k
      }
//      else { // comment out to prevent ConcurrentModificationException
//        k.addAll(v)
//        m[k.last] = k
//      }
    }
    val combiner = BinaryOperator<MutableMap<T, ArrayDeque<T>>> { _, _ -> throw UnsupportedOperationException() }

    val finisher = Function<MutableMap<T, ArrayDeque<T>>, Set<List<T>>> { m: Map<T, ArrayDeque<T>> ->
      m.entries.stream()
        .filter { e: Map.Entry<T, ArrayDeque<T>> -> e.value.first == e.key }
        .map { e: Map.Entry<T, ArrayDeque<T>> -> ArrayList(e.value) }
        .collect(Collectors.toSet())
    }
    val supplier: Supplier<MutableMap<T, ArrayDeque<T>>> = Supplier<MutableMap<T, ArrayDeque<T>>> { HashMap() }
    return Collector.of(supplier, accumulator, combiner, finisher)
  }

}
