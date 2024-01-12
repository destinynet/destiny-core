/**
 * @author smallufo
 * Created on 2005/7/2 at 上午 05:53:54
 */
package destiny.core.chinese

interface IFiveElement {

  val fiveElement: FiveElement

  fun isSame(f: IFiveElement): Boolean {
    return f.fiveElement == this.fiveElement
  }

  /** 此五行是否生另一五行  */
  fun isProducingTo(f: IFiveElement): Boolean {
    return f.fiveElement.producer == this.fiveElement
  }

  /** 此五行是否被另一五行所生  */
  fun isProducedBy(f: IFiveElement): Boolean {
    return f.fiveElement.product == this.fiveElement
  }

  /** 此五行是否剋另一五行 , Dominator : 支配者  */
  fun isDominatorOf(f: IFiveElement): Boolean {
    return f.fiveElement.dominator == this.fiveElement
  }

  /** 此五行是否被另一五行所剋  */
  fun isDominatedBy(f: IFiveElement): Boolean {
    return f.fiveElement.dominateOver == this.fiveElement
  }
}
