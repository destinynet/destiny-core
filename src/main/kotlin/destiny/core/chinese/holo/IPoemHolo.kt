/**
 * Created by smallufo on 2019-03-18.
 */
package destiny.core.chinese.holo

import destiny.core.iching.IHexagramText


/**
 * 先天卦、後天卦 with 終身卦的斷語 and 詩詞
 */
data class LifeHoloPoemHexagram(
  /** 加上六條 [HoloLine] , 內含 6年 or 9年 的流年資料結構 */
  val lifeHoloHexagram: ILifeHoloHexagram,
  /** 詩詞 */
  val poemHexagram: IPoemHexagram,
  /** 終身卦的斷語 */
  val lifeDescHexagram: IHoloLifeDescHexagram,
  val hexagramText: IHexagramText) : ILifeHoloHexagram by lifeHoloHexagram, IPoemHexagram by poemHexagram

interface IPoemHolo : IHolo {
  override val hexagramCongenital: ILifeHoloHexagram
  override val hexagramAcquired: ILifeHoloHexagram
}

data class PoemHolo(
  val holo: IHolo,
  override val hexagramCongenital: ILifeHoloHexagram,
  override val hexagramAcquired: ILifeHoloHexagram
) : IPoemHolo, IHolo by holo
