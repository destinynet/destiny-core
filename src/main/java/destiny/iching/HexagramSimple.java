/**
 * @author smallufo
 * Created on 2011/4/18 at 下午12:23:56
 */
package destiny.iching;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HexagramSimple implements Serializable
{
  @NotNull
  private static final Map<Boolean[] , Integer> map = new HashMap<>();
  static
  {
    map.put(new Boolean[] {true,true,true,true,true,true,},1);
    map.put(new Boolean[] {false,false,false,false,false,false,},2);
    map.put(new Boolean[] {true,false,false,false,true,false,},3);
    map.put(new Boolean[] {false,true,false,false,false,true,},4);
    map.put(new Boolean[] {true,true,true,false,true,false,},5);
    map.put(new Boolean[] {false,true,false,true,true,true,},6);
    map.put(new Boolean[] {false,true,false,false,false,false,},7);
    map.put(new Boolean[] {false,false,false,false,true,false,},8);
    map.put(new Boolean[] {true,true,true,false,true,true,},9);
    map.put(new Boolean[] {true,true,false,true,true,true,},10);
    map.put(new Boolean[] {true,true,true,false,false,false,},11);
    map.put(new Boolean[] {false,false,false,true,true,true,},12);
    map.put(new Boolean[] {true,false,true,true,true,true,},13);
    map.put(new Boolean[] {true,true,true,true,false,true,},14);
    map.put(new Boolean[] {false,false,true,false,false,false,},15);
    map.put(new Boolean[] {false,false,false,true,false,false,},16);
    map.put(new Boolean[] {true,false,false,true,true,false,},17);
    map.put(new Boolean[] {false,true,true,false,false,true,},18);
    map.put(new Boolean[] {true,true,false,false,false,false,},19);
    map.put(new Boolean[] {false,false,false,false,true,true,},20);
    map.put(new Boolean[] {true,false,false,true,false,true,},21);
    map.put(new Boolean[] {true,false,true,false,false,true,},22);
    map.put(new Boolean[] {false,false,false,false,false,true,},23);
    map.put(new Boolean[] {true,false,false,false,false,false,},24);
    map.put(new Boolean[] {true,false,false,true,true,true,},25);
    map.put(new Boolean[] {true,true,true,false,false,true,},26);
    map.put(new Boolean[] {true,false,false,false,false,true,},27);
    map.put(new Boolean[] {false,true,true,true,true,false,},28);
    map.put(new Boolean[] {false,true,false,false,true,false,},29);
    map.put(new Boolean[] {true,false,true,true,false,true,},30);
    map.put(new Boolean[] {false,false,true,true,true,false,},31);
    map.put(new Boolean[] {false,true,true,true,false,false,},32);
    map.put(new Boolean[] {false,false,true,true,true,true,},33);
    map.put(new Boolean[] {true,true,true,true,false,false,},34);
    map.put(new Boolean[] {false,false,false,true,false,true,},35);
    map.put(new Boolean[] {true,false,true,false,false,false,},36);
    map.put(new Boolean[] {true,false,true,false,true,true,},37);
    map.put(new Boolean[] {true,true,false,true,false,true,},38);
    map.put(new Boolean[] {false,false,true,false,true,false,},39);
    map.put(new Boolean[] {false,true,false,true,false,false,},40);
    map.put(new Boolean[] {true,true,false,false,false,true,},41);
    map.put(new Boolean[] {true,false,false,false,true,true,},42);
    map.put(new Boolean[] {true,true,true,true,true,false,},43);
    map.put(new Boolean[] {false,true,true,true,true,true,},44);
    map.put(new Boolean[] {false,false,false,true,true,false,},45);
    map.put(new Boolean[] {false,true,true,false,false,false,},46);
    map.put(new Boolean[] {false,true,false,true,true,false,},47);
    map.put(new Boolean[] {false,true,true,false,true,false,},48);
    map.put(new Boolean[] {true,false,true,true,true,false,},49);
    map.put(new Boolean[] {false,true,true,true,false,true,},50);
    map.put(new Boolean[] {true,false,false,true,false,false,},51);
    map.put(new Boolean[] {false,false,true,false,false,true,},52);
    map.put(new Boolean[] {false,false,true,false,true,true,},53);
    map.put(new Boolean[] {true,true,false,true,false,false,},54);
    map.put(new Boolean[] {true,false,true,true,false,false,},55);
    map.put(new Boolean[] {false,false,true,true,false,true,},56);
    map.put(new Boolean[] {false,true,true,false,true,true,},57);
    map.put(new Boolean[] {true,true,false,true,true,false,},58);
    map.put(new Boolean[] {false,true,false,false,true,true,},59);
    map.put(new Boolean[] {true,true,false,false,true,false,},60);
    map.put(new Boolean[] {true,true,false,false,true,true,},61);
    map.put(new Boolean[] {false,false,true,true,false,false,},62);
    map.put(new Boolean[] {true,false,true,false,true,false,},63);
    map.put(new Boolean[] {false,true,false,true,false,true,},64);
  }
  
  public HexagramSimple()
  {
  }

  public static int getIndex(Boolean[] lines)
  {
    for(Map.Entry<Boolean[], Integer> entry : map.entrySet())
    {
      if(Arrays.equals(lines, entry.getKey()))
        return entry.getValue();
    }
    return 0;
  }
}
