/**
 * Created by smallufo on 2019-10-13.
 */
package destiny.astrology

sealed class Arabic(nameKey:String , abbrKey: String) : Star(nameKey, abbrKey, Star::class.java.name), Comparable<Arabic> {

  object POF : Arabic("Arabic.POF" , "Arabic.POF_ABBR")

  override fun compareTo(other: Arabic): Int {
    if (this == other)
      return 0

    return array.indexOf(this) - array.indexOf(other)
  }

  companion object {
    val array by lazy {
      arrayOf(POF)
    }
    val list by lazy { listOf(*array) }
  }
}
