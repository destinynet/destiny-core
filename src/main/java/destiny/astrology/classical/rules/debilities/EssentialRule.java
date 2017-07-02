/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:48:47
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.classical.EssentialDefaultImpl;
import destiny.astrology.classical.EssentialIF;

abstract class EssentialRule extends Rule {

  /** 具備計算 Ptolemy's Table of Essential Dignities and Debilities 的所有介面 */
  EssentialIF essentialImpl = new EssentialDefaultImpl();

  EssentialRule() {
    super();
  }

  public EssentialIF getEssentialImpl() {
    return essentialImpl;
  }

  public void setEssentialImpl(EssentialIF essentialImpl) {
    this.essentialImpl = essentialImpl;
  }


}
