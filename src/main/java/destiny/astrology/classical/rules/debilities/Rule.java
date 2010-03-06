/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 4:40:57
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.classical.rules.AbstractRule;

public abstract class Rule extends AbstractRule implements Applicable
{
  private final static String resource = "destiny.astrology.classical.rules.debilities.Debilities";
  
  public Rule(String nameKey)
  {
    super(nameKey, resource);
  }

}
