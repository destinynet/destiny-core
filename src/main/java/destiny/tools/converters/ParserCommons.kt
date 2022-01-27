/**
 * Created by smallufo on 2019-01-14.
 */
package destiny.tools.converters

import com.google.common.base.Enums
import org.apache.commons.lang3.EnumUtils

object ParserCommons {

  /**
   * http://stackoverflow.com/a/4014447/298430
   * 避免 exception , 最佳解：用 Guava
   * https://www.stubbornjava.com/posts/java-enum-lookup-by-name-or-field-without-throwing-exceptions
   *
   * 有 non-null 的 default value
   */
  fun <E : Enum<E>> parseEnum(key: String, clazz: Class<E>, map: Map<String, String>, defaulted: E): E {
    val name = map.getOrDefault(key, defaulted.name)
    return Enums.getIfPresent(clazz, name).or(defaulted)
  }

  /**
   * 承上 , 只是這若是無此值，就傳回 null
   */
  fun <E : Enum<E>> parseEnum(key: String, clazz: Class<E>, map: Map<String, String>): E? {

    return map[key]?.trim()?.let { name -> EnumUtils.getEnumIgnoreCase(clazz, name) }
  }
}
