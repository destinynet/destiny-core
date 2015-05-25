/**
 * @author smallufo
 * @date 2002/9/23
 * @time 上午 08:14:50
 */
package destiny.FengShui.SanYuan;

import destiny.astrology.Utils;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 存放 24 山的資料
 */
public abstract class AbstractMountainCompass extends AbstractCompass
{
  @NotNull
  private static final ArrayList<Mountain> MountainList = new ArrayList<>();
  static
  {
    MountainList.add(Mountain.子);
    MountainList.add(Mountain.癸);
    MountainList.add(Mountain.丑);
    MountainList.add(Mountain.艮);
    MountainList.add(Mountain.寅);
    MountainList.add(Mountain.甲);
    MountainList.add(Mountain.卯);
    MountainList.add(Mountain.乙);
    MountainList.add(Mountain.辰);
    MountainList.add(Mountain.巽);
    MountainList.add(Mountain.巳);
    MountainList.add(Mountain.丙);
    MountainList.add(Mountain.午);
    MountainList.add(Mountain.丁);
    MountainList.add(Mountain.未);
    MountainList.add(Mountain.坤);
    MountainList.add(Mountain.申);
    MountainList.add(Mountain.庚);
    MountainList.add(Mountain.酉);
    MountainList.add(Mountain.辛);
    MountainList.add(Mountain.戌);
    MountainList.add(Mountain.乾);
    MountainList.add(Mountain.亥);
    MountainList.add(Mountain.壬);
  }
  
  /**
   * 取得 "子" 山的起始度數 (地盤正針得傳回 352.5 度)
   * 由繼承此 Class 的子物件去實作
   */
  @Override
  public abstract double getInitDegree();
  
  @Override
  public double getStepDegree()
  {
    return 15;
  }
  
  /**
   * 取得某個山的起始度數
   */
  @Override
  public double getStartDegree(Object o)
  {
    return Utils.getNormalizeDegree(MountainList.indexOf(o) * getStepDegree() + getInitDegree() );
  }
  
  /**
   * 取得某個山的結束度數
   */
  @Override
  public double getEndDegree(Object o)
  {
    return Utils.getNormalizeDegree( (MountainList.indexOf(o)+1) * getStepDegree() + getInitDegree() );
  }
  
  /**
   * 取得目前這個度數位於哪個山當中
   */
  public Object getMountain(double degree)
  {
    int index = (int) ((degree + 360 - getInitDegree()) / getStepDegree()) ;
    if (index >= 24)
      index = index-24;
    else if (index < 0 )
      index = index+24;
    return MountainList.get(index);
  }
  
  
  /**
   * http://www.neighbor168.com/name543/house11.htm
   * <pre>
   * 寅－－內藏甲、丙、戊，已知甲丙屬陽，故寅為陽。 
   * 申－－內藏庚、壬、戊，已知庚壬屬陽，故申屬陽。 
   * 巳－－內藏庚、丙、戊，已知庚丙屬陽，故巳屬陽。 
   * 亥－－內藏甲、壬、戊，已知甲壬屬陽，故亥屬陽。 
   * 
   * 辰－－內藏乙、癸、戊，已知乙癸屬陰，故辰屬陰。 
   * 戌－－內藏辛、丁、戊，已知辛丁屬陰，故戌屬陰。 
   * 丑－－內藏癸、辛、己，已知癸辛屬陰，故丑屬陰。 
   * 未－－內藏丁、乙、己，己知丁乙屬陰，故未屬陰。
   * </pre> 
   */
  public boolean getYinYang(@NotNull Mountain m)
  {
    if (m == Mountain.乾 || m == Mountain.坤 || m == Mountain.巽 || m == Mountain.艮 )
      return true;
    else if (m.getValue() instanceof Stem)
    {
      // 陽干傳回陽 , 陰干傳回陰
      return ((Stem)(m.getValue())).getBooleanValue();
    }
    else if (m.getValue() instanceof Branch)
    {
      int index = Branch.getIndex((Branch) m.getValue());
      //寅巳申亥
      return index == 2 || index == 5 || index == 8 || index == 11;
    }
    else
      throw new RuntimeException("Cannot find YinYang from " + m);
  }
  
}
