/**
 * @author smallufo 
 * Created on 2007/12/31 at 下午 10:41:10
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import junit.framework.TestCase;

public class AccidentalDignitiesBeanTest extends TestCase
{

  public void testGetRules()
  {
    AccidentalDignitiesBean bean = new AccidentalDignitiesBean();
    
    assert(bean.getRules().size() > 1);
    for(Applicable each : bean.getRules())
    {
      assertNotSame('!' , each.getName().charAt(0));
    }
  }

}
