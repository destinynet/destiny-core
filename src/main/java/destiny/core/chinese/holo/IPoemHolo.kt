/**
 * Created by smallufo on 2019-03-18.
 */
package destiny.core.chinese.holo

import destiny.iching.IHexagram
import destiny.iching.IHexagramText


interface ILifeHoloPoemHexagram : ILifeHoloHexagram, IPoemHexagram, IHoloLifeDescHexagram

/**
 * 因為有 Diamond problem ( [ILifeHoloHexagram] , 與 [IPoemHexagram] 同時都實作 [IHexagram] )
 * 所以無法 兩者介面皆 delegate , 只能擇一 一一實作 (這裡挑 [IPoemHexagram] , 因為它只要實作兩個 methods 即可)
 */
data class LifeHoloPoemHexagram(
  val lifeHoloHexagram: ILifeHoloHexagram,
  val poemHexagram: IPoemHexagram,
  val lifeDescHexagram: IHoloLifeDescHexagram ,
  val hexagramText : IHexagramText) : ILifeHoloPoemHexagram
  , ILifeHoloHexagram by lifeHoloHexagram, IPoemHexagram {

  override val poems: List<String> = poemHexagram.poems

  override fun getLinePoem(lineIndex: Int): ILinePoem {
    return poemHexagram.getLinePoem(lineIndex)
  }

  override val hexContent: String = lifeDescHexagram.hexContent

  override fun getLineContent(lineIndex: Int): String {
    return lifeDescHexagram.getLineContent(lineIndex)
  }

}

interface IPoemHolo : IHolo {
  override val hexagramCongenital: ILifeHoloPoemHexagram
  override val hexagramAcquired: ILifeHoloPoemHexagram
}

data class PoemHolo(
  val holo: IHolo,
  override val hexagramCongenital: ILifeHoloPoemHexagram,
  override val hexagramAcquired: ILifeHoloPoemHexagram
) : IPoemHolo, IHolo by holo
