/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import com.google.common.collect.HashBiMap
import destiny.core.Descriptive
import org.slf4j.LoggerFactory
import java.io.Serializable

interface MapConverter<T> {
  val key: String
  fun getImpl(map: Map<String, String>): T
  fun addToMap(map: MutableMap<String, String>, impl: T)
}


open class KAbstractImpls<T>(override val key: String,
                             private val defaultImpl: T,
                             private val defaultImplKey: String) : Serializable,
  MapConverter<T> {

  /** T 的實作者有哪些 , 及其 參數的 value 為何  */
  private val implValueMap = HashBiMap.create<T, String>()

  private val logger = LoggerFactory.getLogger(javaClass)!!

  init {
    addImpl(defaultImpl, defaultImplKey)
  }

  fun addImpl(t: T, value: String) {
    implValueMap[t] = value
  }

  fun getImpls(): List<T> {
    return implValueMap.keys.toList()
  }


  fun getStringValue(t: T): String {
    return implValueMap[t] ?: {
      logger.warn("cannot get ParameterValue from {} . implValueMap = {}. returning default : {}",
                  t, implValueMap, defaultImpl)
      implValueMap[defaultImpl]!!
    }.invoke()
  }

  fun getStringValue(t: () -> T): String {
    return getStringValue(t.invoke())
  }

  override fun getImpl(map: Map<String, String>): T {
    val implKey = map.getOrDefault(key, defaultImplKey)
    return getImpl(implKey)
  }

  override fun addToMap(map: MutableMap<String, String>, impl: T) {
    map[key] = getStringValue(impl)
  }

  /** 從 parameter value 找出其對應的實作是哪一個  */
  private fun getImpl(implKey: String): T {
    val t = implValueMap.inverse()[implKey]
    return t ?: defaultImpl
  }
}

class DescriptiveImpls<T : Descriptive>(
  key: String,
  defaultImpl: T,
  defaultImplKey: String) : KAbstractImpls<T>(key, defaultImpl, defaultImplKey)