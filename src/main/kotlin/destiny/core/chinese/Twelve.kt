/**
 * Created by smallufo on 2025-06-07.
 */
package destiny.core.chinese

import destiny.core.ILoop


enum class Twelve : ILoop<Twelve> {
  生,
  敗,
  冠,
  祿,
  旺,
  衰,
  病,
  死,
  墓,
  絕,
  胎,
  養;

  override fun next(n: Int): Twelve {
    val targetIndex = (entries.indexOf(this) + n).mod(entries.size)
    return entries[targetIndex]
  }
}
