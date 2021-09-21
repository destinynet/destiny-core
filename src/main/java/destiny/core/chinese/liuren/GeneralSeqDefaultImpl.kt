/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.core.chinese.liuren.General.*
import destiny.tools.ArrayTools
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Pithy.KEY_GENERAL_SEQ
import java.io.Serializable
import java.util.*


@Impl([Domain(KEY_GENERAL_SEQ, GeneralSeqDefaultImpl.VALUE, default = true)])
class GeneralSeqDefaultImpl : IGeneralSeq, Serializable {

  override fun toString(locale: Locale): String {
    return GeneralSeq.Default.asDescriptive().toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return GeneralSeq.Default.asDescriptive().getDescription(locale)
  }

  override fun next(from: General, n: Int): General {
    return get(getIndex(from) + n)
  }

  companion object {

    const val VALUE = "default"

    private val ARRAY = arrayOf(貴人, 螣蛇, 朱雀, 六合, 勾陳, 青龍, 天空, 白虎, 太常, 玄武, 太陰, 天后)

    private operator fun get(index: Int): General {
      return ArrayTools[ARRAY, index]
    }

    private fun getIndex(g: General): Int {
      // 與原本 enum 排序相同，可以直接 binary search
      return Arrays.binarySearch(ARRAY, g)
    }
  }
}
