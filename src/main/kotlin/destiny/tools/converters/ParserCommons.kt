/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import java.net.URLDecoder

object ParserCommons {

  /**
   * 找不到對應的名稱（大小寫須相符）時傳回 [defaulted]，不拋例外
   */
  fun <E : Enum<E>> parseEnum(key: String, clazz: Class<E>, map: Map<String, String>, defaulted: E): E {
    val name = map.getOrDefault(key, defaulted.name)
    return clazz.enumConstants.firstOrNull { it.name == name } ?: defaulted
  }

  /**
   * 承上 , 只是這若是無此值，就傳回 null
   */
  fun <E : Enum<E>> parseEnum(key: String, clazz: Class<E>, map: Map<String, String>): E? {
    return map[key]?.trim()?.let { s ->
      URLDecoder.decode(s, "UTF-8").replace(" ", "+")
    }?.let { name ->
      clazz.enumConstants.firstOrNull { it.name.equals(name, true) }
    }
  }
}
