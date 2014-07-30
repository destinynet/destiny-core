package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

/**
 * @author smallufo
 * @date 2002/8/26
 * @time 下午 01:19:37
 */
public class Characteristics
{
  private HeavenlyStems  年干;
  private HeavenlyStems 月干;
  private HeavenlyStems 日干;
  private HeavenlyStems 時干;
  private EarthlyBranches 年支;
  private EarthlyBranches 月支;
  private EarthlyBranches 日支;
  private EarthlyBranches 時支;
  
  @NotNull
  public EarthlyBranches[] 空亡 = new EarthlyBranches[2];
  public String[] 六獸 = new String[6];
  public EarthlyBranches 驛馬 ;
  public EarthlyBranches 桃花 ;
  public EarthlyBranches[] 天乙貴人 = new EarthlyBranches[2];
  public EarthlyBranches 羊刃 ;
  
  public Characteristics()
  {
  }
  
  public Characteristics(HeavenlyStems 年干, EarthlyBranches 年支,HeavenlyStems 月干, EarthlyBranches 月支,HeavenlyStems 日干, EarthlyBranches 日支,HeavenlyStems 時干, EarthlyBranches 時支)
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
  
  public String[] get六獸()
  {
    if ((日干 == HeavenlyStems.甲)||(日干 == HeavenlyStems.乙))
    {
      六獸[0] = "青龍";六獸[1] = "朱雀";六獸[2] = "勾陳";六獸[3] = "螣蛇";六獸[4] = "白虎";六獸[5] = "玄武";
    }
    else if ((日干 == HeavenlyStems.丙)||(日干 == HeavenlyStems.丁))
    {
      六獸[0] = "朱雀";六獸[1] = "勾陳";六獸[2] = "螣蛇";六獸[3] = "白虎";六獸[4] = "玄武";六獸[5] = "青龍";
    }
    else if (日干 == HeavenlyStems.戊)
    {
      六獸[0] = "勾陳";六獸[1] = "螣蛇";六獸[2] = "白虎";六獸[3] = "玄武";六獸[4] = "青龍";六獸[5] = "朱雀";
    }
    else if (日干 == HeavenlyStems.己)
    {
      六獸[0] = "螣蛇";六獸[1] = "白虎";六獸[2] = "玄武";六獸[3] = "青龍";六獸[4] = "朱雀";六獸[5] = "勾陳";
    }
    else if ((日干 == HeavenlyStems.庚)||(日干 == HeavenlyStems.辛))
    {
      六獸[0] = "白虎";六獸[1] = "玄武";六獸[2] = "青龍";六獸[3] = "朱雀";六獸[4] = "勾陳";六獸[5] = "螣蛇";
    }
    else if ((日干 == HeavenlyStems.壬)||(日干 == HeavenlyStems.癸))
    {
      六獸[0] = "玄武";六獸[1] = "青龍";六獸[2] = "朱雀";六獸[3] = "勾陳";六獸[4] = "螣蛇";六獸[5] = "白虎";
    }
    return 六獸;
  }
  
  public EarthlyBranches get驛馬()
  {
    if (日支 == EarthlyBranches.申 || 日支 == EarthlyBranches.子 || 日支 == EarthlyBranches.辰)
      驛馬 = EarthlyBranches.寅;
    else if (日支 == EarthlyBranches.巳 || 日支 == EarthlyBranches.酉 || 日支 == EarthlyBranches.丑)
      驛馬 = EarthlyBranches.亥;
    else if (日支 == EarthlyBranches.寅 || 日支 == EarthlyBranches.午 || 日支 == EarthlyBranches.戌)
      驛馬 = EarthlyBranches.申;
    else if (日支 == EarthlyBranches.亥 || 日支 == EarthlyBranches.卯 || 日支 == EarthlyBranches.未)
      驛馬 = EarthlyBranches.巳;
    
    return 驛馬;
  }
  
  public EarthlyBranches get桃花()
  {
    if (日支 == EarthlyBranches.申 || 日支 == EarthlyBranches.子 || 日支 == EarthlyBranches.辰)
      桃花 = EarthlyBranches.酉;
    else if (日支 == EarthlyBranches.巳 || 日支 == EarthlyBranches.酉 || 日支 == EarthlyBranches.丑)
      桃花 = EarthlyBranches.午;
    else if (日支 == EarthlyBranches.寅 || 日支 == EarthlyBranches.午 || 日支 == EarthlyBranches.戌)
      桃花 = EarthlyBranches.卯;
    else if (日支 == EarthlyBranches.亥 || 日支 == EarthlyBranches.卯 || 日支 == EarthlyBranches.未)
      桃花 = EarthlyBranches.子;
    
    return 桃花;
  }
  
  public EarthlyBranches[] get天乙貴人()
  {
    if (日干 == HeavenlyStems.甲 || 日干 == HeavenlyStems.戊 || 日干 == HeavenlyStems.庚)
    {
      天乙貴人[0] = EarthlyBranches.丑;天乙貴人[1] = EarthlyBranches.未;
    }
    else if (日干 == HeavenlyStems.乙 || 日干 == HeavenlyStems.己)
    {
      天乙貴人[0] = EarthlyBranches.子;天乙貴人[1] = EarthlyBranches.申;
    }
    else if (日干 == HeavenlyStems.丙 || 日干 == HeavenlyStems.丁)
    {
      天乙貴人[0] = EarthlyBranches.酉;天乙貴人[1] = EarthlyBranches.亥;
    }
    else if (日干 == HeavenlyStems.辛)
    {
      天乙貴人[0] = EarthlyBranches.寅;天乙貴人[1] = EarthlyBranches.午;
    }
    else if (日干 == HeavenlyStems.壬 || 日干 == HeavenlyStems.癸)
    {
      天乙貴人[0] = EarthlyBranches.卯;天乙貴人[1] = EarthlyBranches.巳;
    }
    return 天乙貴人;
  }
  
  public EarthlyBranches get羊刃()
  {
    if (日干 == HeavenlyStems.甲)
      羊刃 = EarthlyBranches.卯;
    else if (日干 == HeavenlyStems.乙)
      羊刃 = EarthlyBranches.辰;
    else if (日干 == HeavenlyStems.丙 || 日干 == HeavenlyStems.戊)
      羊刃 = EarthlyBranches.午;
    else if (日干 == HeavenlyStems.丁 || 日干 == HeavenlyStems.己)
      羊刃 = EarthlyBranches.未;
    else if (日干 == HeavenlyStems.庚)
      羊刃 = EarthlyBranches.酉;
    else if (日干 == HeavenlyStems.辛)
      羊刃 = EarthlyBranches.戌;
    else if (日干 == HeavenlyStems.壬)
      羊刃 = EarthlyBranches.子;
    else
      羊刃 = EarthlyBranches.丑;
    
    return 羊刃;
  }
  
  public EarthlyBranches[] get空亡(int index)
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
  
  
  private void EmptyEnergies(HeavenlyStems 天干, EarthlyBranches 地支)
  {
    if (HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) ==0)
    {
      空亡[0]=EarthlyBranches.戌;
      空亡[1]=EarthlyBranches.亥;
    }
    else if (( HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == -10)|| (HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == 2))
    {
      空亡[0]=EarthlyBranches.申;
      空亡[1]=EarthlyBranches.酉;
    }
    else if ((HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == -8)|| (HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == 4))
    {
      空亡[0]=EarthlyBranches.午;
      空亡[1]=EarthlyBranches.未;
    }
    else if ((HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == -6)|| (HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == 6))
    {
      空亡[0]=EarthlyBranches.辰;
      空亡[1]=EarthlyBranches.巳;
    }
    else if ((HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == -4)|| (HeavenlyStems.getIndex(天干) - EarthlyBranches.getIndex(地支) == 8))
    {
      空亡[0]=EarthlyBranches.寅;
      空亡[1]=EarthlyBranches.卯;
    }
    else
    {
      空亡[0]=EarthlyBranches.子;
      空亡[1]=EarthlyBranches.丑;
    }
    
  }

}
