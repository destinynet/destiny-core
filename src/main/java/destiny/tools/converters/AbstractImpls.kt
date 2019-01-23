/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import com.google.common.collect.HashBiMap
import destiny.core.Descriptive
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * Context 與 Map<String , String> 互換
 */
interface IContextMap<T> {
  fun getMap(context: T): Map<String, String>
  fun getContext(map: Map<String, String>): T?
}

interface IContextMapWithDefault<T> : IContextMap<T> {
  override fun getContext(map: Map<String, String>): T
}

interface MapConverter<T> : IContextMap<T> {
  val key: String
}

interface MapConverterWithDefault<T> : MapConverter<T> {
  val defaultImpl : T

  fun getContextWithDefault(map: Map<String, String>): T {
    return getContext(map) ?:defaultImpl
  }
}

interface IAbstractImpls<T> : MapConverterWithDefault<T> {

  val impls: List<T>
  fun getImpl(implKey: String): T
  fun getStringValue(t: T): String
  fun getStringValue(t: () -> T): String
}

open class AbstractImpls<T>(override val key: String,
                            override val defaultImpl: T,
                            private val defaultImplKey: String) : Serializable, IAbstractImpls<T> {

  /** T 的實作者有哪些 , 及其 參數的 value 為何  */
  private val implValueMap = HashBiMap.create<T, String>()

  private val logger = LoggerFactory.getLogger(javaClass)!!

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
      logger.warn("cannot get ParameterValue from {} . implValueMap = {}. returning default : {}",
                  t, implValueMap, defaultImpl)
      implValueMap[defaultImpl]!!
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
  defaultImplKey: String) : AbstractImpls<T>(key, defaultImpl, defaultImplKey),
  IDescriptiveImpls<T>