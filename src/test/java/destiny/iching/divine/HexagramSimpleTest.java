/**
 * @author smallufo
 * Created on 2011/4/18 at 下午12:30:43
 */
package destiny.iching.divine;

import org.junit.Test;

import destiny.iching.HexagramSimple;

public class HexagramSimpleTest
{
  @Test
  public void testRun()
  {
    Boolean[] lines1 = new Boolean[] {true , true , true , true , true , true};
    System.out.println(HexagramSimple.getIndex(lines1));
    
    Boolean[] lines2 = new Boolean[] {false , false , false , false , false , false};
    System.out.println(HexagramSimple.getIndex(lines2));
    
    Boolean[] lines64 = new Boolean[] {false , true , false , true , false , true};
    System.out.println(HexagramSimple.getIndex(lines64));
  }
}
