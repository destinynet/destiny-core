/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import com.google.common.collect.HashBiMap
import destiny.core.Descriptive
import destiny.tools.Domain
import destiny.tools.Impl
import mu.KotlinLogging
import java.io.Serializable


/**
 * Context 與 Map<String , String> 互換
 */
interface IContextMap<T> : Serializable {
  fun getMap(context: T): Map<String, String>

  fun getMapExceptDefault(context: T, defaultContextProvider: () -> T): Map<String, String> {
    val ctxMap = getMap(context)
    val defMap = getMap(defaultContextProvider.invoke())

    val ctxWithoutDefMap = ctxMap.filter { (k, v) -> defMap[k] == null || defMap[k] != v }
    val defWithoutCtxMap = defMap.filter { (k, _) -> ctxMap[k] == null }
    return ctxWithoutDefMap.plus(defWithoutCtxMap)
  }

  fun getContext(map: Map<String, String>): T?
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

abstract class EnumMapConverterWithDefault<T : Enum<T>>(override val key: String,
                                                        override val defaultImpl: T) : MapConverterWithDefault<T> {

  override fun getMap(context: T): Map<String, String> {
    return mapOf(key to context.name)
  }

  override fun getContext(map: Map<String, String>): T? {
    return ParserCommons.parseEnum(key, defaultImpl::class.java, map)
  }
}

interface IAbstractImpls<T> : MapConverterWithDefault<T> {

  val impls: List<T>
  fun getImpl(implKey: String): T
  fun getStringValue(t: T): String
  fun getStringValue(t: () -> T): String
}

private fun <T : Any> Array<T>.getImpls(domainKey: String): List<Pair<T, Impl>> {
  return this.map { t: T ->
    val impl: Impl? = t::class.annotations.firstOrNull { it is Impl } as? Impl
    t to impl
  }.filter { (_, impl) -> impl != null }.map { (t, impl) -> t to impl!! }
}

fun <T : Any> Array<T>.findDefaultImplAndStringValue(domainKey: String): Pair<T, String> {
  return this.getImpls(domainKey).map { (t, impl) ->
      val domain: Domain? = impl.value.firstOrNull { domain -> domain.key == domainKey && domain.default }
      t to domain
    }.filter { (_, domain) -> domain != null }.map { (t, domain) -> t to domain!!.value }.first()
}


private fun <T : Any> Array<T>.findNonDefaultImplAndKey(domainKey: String): Map<T, String> {
  return this.getImpls(domainKey).map { (t, impl) ->
      val domain: Domain? = impl.value.firstOrNull { domain -> domain.key == domainKey && !domain.default }
      t to domain
    }.filter { (_, domain) -> domain != null }.associate { (t, domain) -> t to domain!!.value }
}


open class AbstractImpls<T : Any>(
  override val key: String, final override val defaultImpl: T, private val defaultImplKey: String
) : Serializable, IAbstractImpls<T> {

  constructor(key: String, pair: Pair<T, String>) : this(key, pair.first, pair.second)

  constructor(key: String, vararg impls: T) : this(key, impls.findDefaultImplAndStringValue(key)) {
    impls.findNonDefaultImplAndKey(key).forEach { (impl, string) ->
      addImpl(impl, string)
    }
  }

  /** T 的實作者有哪些 , 及其 參數的 value 為何  */
  private val implValueMap = HashBiMap.create<T, String>()

  init {
    addImpl(defaultImpl, defaultImplKey)
  }

  fun addImpl(t: T, value: String) {
    implValueMap[t] = value
  }

  override val impls: List<T>
    get() = implValueMap.keys.toList()


  override fun getStringValue(t: T): String {
    return implValueMap[t] ?: run {
      logger.warn(
        "cannot get value from {} . implValueMap = {}. returning default : {}", t, implValueMap, defaultImpl
      )
      implValueMap.getValue(defaultImpl)
    }
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

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}

interface IDescriptiveImpls<T : Descriptive> : IAbstractImpls<T>


open class DescriptiveImpls<T : Descriptive>(
  key: String, defaultImpl: T, defaultImplKey: String
) : AbstractImpls<T>(key, defaultImpl to defaultImplKey), IDescriptiveImpls<T> {

  constructor(key: String, pair: Pair<T, String>) : this(key, pair.first, pair.second)

  constructor(key: String, vararg impls: T) : this(key, impls.findDefaultImplAndStringValue(key)) {
    impls.findNonDefaultImplAndKey(key).forEach { (impl, string) ->
      addImpl(impl, string)
    }
  }
}
