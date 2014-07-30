/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 12:48:35
 */
package destiny.FengShui.SanYuan;

import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.iching.Symbol;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/** 山 */
public class Mountain implements Serializable
{
  private Object value;
  
  public final static Mountain 子 = new Mountain(EarthlyBranches.子);
  public final static Mountain 癸 = new Mountain(HeavenlyStems  .癸);
  public final static Mountain 丑 = new Mountain(EarthlyBranches.丑);
  public final static Mountain 艮 = new Mountain(Symbol         .艮);
  public final static Mountain 寅 = new Mountain(EarthlyBranches.寅);
  public final static Mountain 甲 = new Mountain(HeavenlyStems  .甲);
  public final static Mountain 卯 = new Mountain(EarthlyBranches.卯);
  public final static Mountain 乙 = new Mountain(HeavenlyStems  .乙);
  public final static Mountain 辰 = new Mountain(EarthlyBranches.辰);
  public final static Mountain 巽 = new Mountain(Symbol         .巽);
  public final static Mountain 巳 = new Mountain(EarthlyBranches.巳);
  public final static Mountain 丙 = new Mountain(HeavenlyStems  .丙);
  public final static Mountain 午 = new Mountain(EarthlyBranches.午);
  public final static Mountain 丁 = new Mountain(HeavenlyStems  .丁);
  public final static Mountain 未 = new Mountain(EarthlyBranches.未);
  public final static Mountain 坤 = new Mountain(Symbol         .坤);
  public final static Mountain 申 = new Mountain(EarthlyBranches.申);
  public final static Mountain 庚 = new Mountain(HeavenlyStems  .庚);
  public final static Mountain 酉 = new Mountain(EarthlyBranches.酉);
  public final static Mountain 辛 = new Mountain(HeavenlyStems  .辛);
  public final static Mountain 戌 = new Mountain(EarthlyBranches.戌);
  public final static Mountain 乾 = new Mountain(Symbol         .乾);
  public final static Mountain 亥 = new Mountain(EarthlyBranches.亥);
  public final static Mountain 壬 = new Mountain(HeavenlyStems  .壬);
  
  @NotNull
  private final static Mountain[] ountainArray ;
  static
  {
    ountainArray = new Mountain[]
    { 子 , 癸 , 丑 , 艮 , 寅 , 甲 , 卯 , 乙 , 辰 , 巽 , 巳 , 丙 ,
      午 , 丁 , 未 , 坤 , 申 , 庚 , 酉 , 辛 , 戌 , 乾 , 亥 , 壬
    };
  } 
  
  protected Mountain(Object o)
  {
    this.value = o;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  @NotNull
  public static Mountain[] getMountains()
  {
    return ountainArray;
  }
  
  public static int getIndex(@NotNull Mountain m)
  {
    int index = -1;
    for (int i=0 ; i < ountainArray.length ; i++)
    {
      if ( m.equals(ountainArray[i]) )
        index = i;
    }
    return index;
  
  
  }
  
  public Mountain getOppositeMountain()
  {
    return ountainArray[ normalize(Mountain.getIndex(this)+12) ];
  }
  
  public static Mountain getOppositeMountain(@NotNull Mountain m)
  {
    return ountainArray[ normalize(Mountain.getIndex(m)+12) ];
  }
  
  private static int normalize(int value)
  {
    if (value > 23 )
      return normalize( value-24);
    else if (value < 0)
      return normalize (value+24);
    else     
      return value;
  }  
  
  public String toString()
  {
    return value.toString();
  }
  
}
