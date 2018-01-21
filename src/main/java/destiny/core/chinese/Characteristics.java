package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Branch.巳;

/**
 * @author smallufo
 * @date 2002/8/26
 * @time 下午 01:19:37
 */
public class Characteristics {
  private Stem 年干;
  private Stem 月干;
  private Stem 日干;
  private Stem 時干;
  private Branch 年支;
  private Branch 月支;
  private Branch 日支;
  private Branch 時支;
  
  @NotNull
  private final Branch[] 空亡 = new Branch[2];
  @NotNull
  private final String[] 六獸 = new String[6];
  private Branch 驛馬 ;
  private Branch 桃花 ;
  @NotNull
  private final Branch[] 天乙貴人 = new Branch[2];

  public Characteristics()
  {
  }
  
  public Characteristics(Stem 年干, Branch 年支,Stem 月干, Branch 月支,Stem 日干, Branch 日支,Stem 時干, Branch 時支)
  {
    this.年干=年干;
    this.年支=年支;
    this.月干=月干;
    this.月支=月支;
    this.日干=日干;
    this.日支=日支;
    this.時干=時干;
    this.時支=時支;
    
  }
  
  @NotNull
  public String[] get六獸()
  {
    if ((日干 == Stem.甲)||(日干 == Stem.乙))
    {
      六獸[0] = "青龍";六獸[1] = "朱雀";六獸[2] = "勾陳";六獸[3] = "螣蛇";六獸[4] = "白虎";六獸[5] = "玄武";
    }
    else if ((日干 == Stem.丙)||(日干 == Stem.丁))
    {
      六獸[0] = "朱雀";六獸[1] = "勾陳";六獸[2] = "螣蛇";六獸[3] = "白虎";六獸[4] = "玄武";六獸[5] = "青龍";
    }
    else if (日干 == Stem.戊)
    {
      六獸[0] = "勾陳";六獸[1] = "螣蛇";六獸[2] = "白虎";六獸[3] = "玄武";六獸[4] = "青龍";六獸[5] = "朱雀";
    }
    else if (日干 == Stem.己)
    {
      六獸[0] = "螣蛇";六獸[1] = "白虎";六獸[2] = "玄武";六獸[3] = "青龍";六獸[4] = "朱雀";六獸[5] = "勾陳";
    }
    else if ((日干 == Stem.庚)||(日干 == Stem.辛))
    {
      六獸[0] = "白虎";六獸[1] = "玄武";六獸[2] = "青龍";六獸[3] = "朱雀";六獸[4] = "勾陳";六獸[5] = "螣蛇";
    }
    else if ((日干 == Stem.壬)||(日干 == Stem.癸))
    {
      六獸[0] = "玄武";六獸[1] = "青龍";六獸[2] = "朱雀";六獸[3] = "勾陳";六獸[4] = "螣蛇";六獸[5] = "白虎";
    }
    return 六獸;
  }
  
  public Branch get驛馬() {
    switch (BranchTools.INSTANCE.trilogy(日支)) {
      case 水: return 寅;
      case 木: return 巳;
      case 金: return 亥;
      case 火: return 申;
      default: throw new AssertionError(日支);
    }
//    if (日支 == 申 || 日支 == 子 || 日支 == 辰)
//      驛馬 = 寅;
//    else if (日支 == 巳 || 日支 == 酉 || 日支 == 丑)
//      驛馬 = 亥;
//    else if (日支 == 寅 || 日支 == 午 || 日支 == 戌)
//      驛馬 = 申;
//    else if (日支 == 亥 || 日支 == 卯 || 日支 == 未)
//      驛馬 = 巳;
//
//    return 驛馬;
  }
  
  public Branch get桃花()
  {
    if (日支 == 申 || 日支 == 子 || 日支 == 辰)
      桃花 = 酉;
    else if (日支 == 巳 || 日支 == 酉 || 日支 == 丑)
      桃花 = 午;
    else if (日支 == 寅 || 日支 == 午 || 日支 == 戌)
      桃花 = 卯;
    else if (日支 == 亥 || 日支 == 卯 || 日支 == 未)
      桃花 = 子;
    
    return 桃花;
  }
  
  @NotNull
  public Branch[] get天乙貴人()
  {
    if (日干 == Stem.甲 || 日干 == Stem.戊 || 日干 == Stem.庚)
    {
      天乙貴人[0] = 丑;天乙貴人[1] = 未;
    }
    else if (日干 == Stem.乙 || 日干 == Stem.己)
    {
      天乙貴人[0] = 子;天乙貴人[1] = 申;
    }
    else if (日干 == Stem.丙 || 日干 == Stem.丁)
    {
      天乙貴人[0] = 酉;天乙貴人[1] = 亥;
    }
    else if (日干 == Stem.辛)
    {
      天乙貴人[0] = 寅;天乙貴人[1] = 午;
    }
    else if (日干 == Stem.壬 || 日干 == Stem.癸)
    {
      天乙貴人[0] = 卯;天乙貴人[1] = 巳;
    }
    return 天乙貴人;
  }
  
  public Branch get羊刃()
  {
    Branch 羊刃;
    if (日干 == Stem.甲)
      羊刃 = 卯;
    else if (日干 == Stem.乙)
      羊刃 = 辰;
    else if (日干 == Stem.丙 || 日干 == Stem.戊)
      羊刃 = 午;
    else if (日干 == Stem.丁 || 日干 == Stem.己)
      羊刃 = 未;
    else if (日干 == Stem.庚)
      羊刃 = 酉;
    else if (日干 == Stem.辛)
      羊刃 = 戌;
    else if (日干 == Stem.壬)
      羊刃 = 子;
    else
      羊刃 = 丑;
    
    return 羊刃;
  }
  
  @NotNull
  public Branch[] get空亡(int index)
  {
    switch (index)
    {
      case 0:EmptyEnergies(年干,年支);break;
      case 1:EmptyEnergies(月干,月支);break;
      case 2:EmptyEnergies(日干,日支);break;
      case 3:EmptyEnergies(時干,時支);break;
    }
    return 空亡;
    
  }
  
  
  private void EmptyEnergies(Stem 天干, @NotNull Branch 地支)
  {
    if (Stem.Companion.getIndex(天干) - Companion.getIndex(地支) ==0)
    {
      空亡[0]= 戌;
      空亡[1]= 亥;
    }
    else if (( Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == -10)|| (Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == 2))
    {
      空亡[0]= 申;
      空亡[1]= 酉;
    }
    else if ((Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == -8)|| (Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == 4))
    {
      空亡[0]= 午;
      空亡[1]= 未;
    }
    else if ((Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == -6)|| (Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == 6))
    {
      空亡[0]= 辰;
      空亡[1]= 巳;
    }
    else if ((Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == -4)|| (Stem.Companion.getIndex(天干) - Companion.getIndex(地支) == 8))
    {
      空亡[0]= 寅;
      空亡[1]= 卯;
    }
    else
    {
      空亡[0]= 子;
      空亡[1]= 丑;
    }
    
  }

}
