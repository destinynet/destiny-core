/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import com.google.common.collect.HashBiMap
import destiny.core.Descriptive
import destiny.tools.Impl
import mu.KotlinLogging
import java.io.Serializable


/**
 * Context 與 Map<String , String> 互換
 */
interface IContextMap<T> {
  fun getMap(context: T): Map<String, String>
  fun getMapExceptDefault(context: T): Map<String, String> = getMap(context)
  fun getContext(map: Map<String, String>): T?

  fun <T> MutableMap<String, String>.putAllExceptDefault(ctxMap: IContextMap<T>,
                                                         defaultValue: T, value: T) {
    if (value != defaultValue) {
      putAll(ctxMap.getMap(value))
    }
  }
}

interface IContextMapWithDefault<T> : IContextMap<T> {
  override fun getContext(map: Map<String, String>): T
}

interface MapConverter<T> : IContextMap<T> {
  val key: String
}

interface MapConverterWithDefault<T> : MapConverter<T> {
  val defaultImpl: T

  fun getContextWithDefault(map: Map<String, String>): T {
    return getContext(map) ?: defaultImpl
  }
}

interface IAbstractImpls<T> : MapConverterWithDefault<T> {

  val impls: List<T>
  fun getImpl(implKey: String): T
  fun getStringValue(t: T): String
  fun getStringValue(t: () -> T): String

  override fun getMapExceptDefault(context: T): Map<String, String> {
    return if (context != defaultImpl) {
      getMap(context)
    } else {
      emptyMap()
    }
  }
}

private val <T : Any> Array<T>.findDefaultImplAndStringValue: Pair<T, String>
  get() {
    return this.map { t: T ->
        val impl: Impl? = t::class.annotations.firstOrNull { it is Impl } as? Impl
        t to impl
      }.filter { (_, impl) -> impl != null }
      .map { (t, impl) -> t to impl!! }
      .filter { (_, impl) -> impl.default }
      .map { (t, impl) -> t to impl.value }
      .first()
  }

private val <T : Any> Array<T>.findNonDefaultImplAndKey: Map<T, String>
  get() {
    return this.map { t: T ->
        val impl: Impl? = t::class.annotations.firstOrNull { it is Impl } as? Impl
        t to impl
      }.filter { (_, impl) -> impl != null }
      .map { (t, impl) -> t to impl!! }
      .filter { (_, impl) -> !impl.default }
      .map { (t, impl) -> t to impl.value }
      .toMap()
  }

open class AbstractImpls<T : Any>(override val key: String,
                                  final override val defaultImpl: T,
                                  private val defaultImplKey: String) : Serializable, IAbstractImpls<T> {

  constructor(key: String, pair: Pair<T, String>) : this(key, pair.first, pair.second)

//  constructor(key: String, vararg impls: T) : this(key, impls.findDefaultImplAndStringValue) {
//    impls.findNonDefaultImplAndKey.forEach { (impl, string) ->
//      addImpl(impl, string)
//    }
//  }

  /** T 的實作者有哪些 , 及其 參數的 value 為何  */
  private val implValueMap = HashBiMap.create<T, String>()

  private val logger = KotlinLogging.logger { }

  init {
    addImpl(defaultImpl, defaultImplKey)
  }

  fun addImpl(t: T, value: String) {
    implValueMap[t] = value
  }

  override val impls: List<T>
    get() = implValueMap.keys.toList()


  override fun getStringValue(t: T): String {
    return implValueMap[t] ?: {
      logger.warn("cannot get value from {} . implValueMap = {}. returning default : {}",
                  t, implValueMap, defaultImpl)
      implValueMap.getValue(defaultImpl)
    }.invoke()
  }

  override fun getStringValue(t: () -> T): String {
    return getStringValue(t.invoke())
  }

  override fun getContext(map: Map<String, String>): T {
    val implKey = map.getOrDefault(key, defaultImplKey)
    return getImpl(implKey)
  }


  override fun getMap(context: T): Map<String, String> {
    return mapOf(key to getStringValue(context))
  }


  /** 從 parameter value 找出其對應的實作是哪一個  */
  override fun getImpl(implKey: String): T {
    val t = implValueMap.inverse()[implKey]
    return t ?: defaultImpl
  }
}

interface IDescriptiveImpls<T : Descriptive> : IAbstractImpls<T>


open class DescriptiveImpls<T : Descriptive>(
  key: String,
  defaultImpl: T,
  defaultImplKey: String) : AbstractImpls<T>(key, defaultImpl to defaultImplKey),
  IDescriptiveImpls<T> {

  constructor(key: String, pair: Pair<T, String>) : this(key, pair.first, pair.second)

  constructor(key: String, vararg impls: T) : this(key, impls.findDefaultImplAndStringValue) {
    impls.findNonDefaultImplAndKey.forEach { (impl, string) ->
      addImpl(impl, string)
    }
  }
}
