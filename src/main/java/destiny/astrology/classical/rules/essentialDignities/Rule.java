/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:35:27
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.classical.EssentialDefaultImpl;
import destiny.astrology.classical.EssentialIF;
import destiny.astrology.classical.rules.AbstractRule;

public abstract class Rule extends AbstractRule implements Applicable
{
  private final static String resource = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities";
  
  /** 具備計算 Ptolemy's Table of Essential Dignities and Deblities 的所有介面 */
  protected EssentialIF essentialImpl = new EssentialDefaultImpl();
  
  public Rule()
  {
    super(resource);
  }

  public EssentialIF getEssentialImpl()
  {
    return essentialImpl;
  }

  public void setEssentialImpl(EssentialIF essentialImpl)
  {
    this.essentialImpl = essentialImpl;
  }


}
