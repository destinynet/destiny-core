/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:06:07
 */
package destiny.core.calendar.eightwords.personal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;


/** 地支藏干標準實作 
 * 潘文欽有另一套說法： 
 * http://destiny.xfiles.to/ubbthreads/files/1704571-%E8%97%8F%E5%B9%B2.jpg
 * */
public class HiddenStemsStandardImpl implements HiddenStemsIF , Serializable
{
  private final static Map<EarthlyBranches , List<HeavenlyStems>> map = Collections.synchronizedMap(new HashMap<>());
  static
  {
    map.put(EarthlyBranches.子, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.癸})));
    
    map.put(EarthlyBranches.丑, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.己 , HeavenlyStems.癸 , HeavenlyStems.辛})));
    
    map.put(EarthlyBranches.寅, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.甲 , HeavenlyStems.丙 , HeavenlyStems.戊})));
    
    map.put(EarthlyBranches.卯, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.乙})));
    
    map.put(EarthlyBranches.辰, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.戊 , HeavenlyStems.乙 , HeavenlyStems.癸})));
    
    map.put(EarthlyBranches.巳, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.丙 , HeavenlyStems.戊 , HeavenlyStems.庚})));
    
    map.put(EarthlyBranches.午, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.丁 , HeavenlyStems.己})));
    
    map.put(EarthlyBranches.未, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.己 , HeavenlyStems.丁 , HeavenlyStems.乙})));
    
    map.put(EarthlyBranches.申, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.庚 , HeavenlyStems.壬 , HeavenlyStems.戊})));
    
    map.put(EarthlyBranches.酉, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.辛})));
    
    map.put(EarthlyBranches.戌, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.戊 , HeavenlyStems.辛 , HeavenlyStems.丁})));
    
    map.put(EarthlyBranches.亥, new ArrayList<>( Arrays.asList(new HeavenlyStems[] {HeavenlyStems.壬 , HeavenlyStems.甲})));
  }
  
  public List<HeavenlyStems> getHiddenStems(EarthlyBranches branch)
  {
    return map.get(branch);
    /*
    List<HeavenlyStems> result = Collections.synchronizedList(new ArrayList<HeavenlyStems>());
    switch (branch.getIndex())
    {
      case 0 : //子
        result.add(HeavenlyStems.癸);
        break;
      case 1 : //丑
        result.add(HeavenlyStems.己);
        result.add(HeavenlyStems.癸);
        result.add(HeavenlyStems.辛);
        break;
      case 2 : //寅
        result.add(HeavenlyStems.甲);
        result.add(HeavenlyStems.丙);
        result.add(HeavenlyStems.戊);
        break;
      case 3 : //卯
        result.add(HeavenlyStems.乙);
        break;
      case 4 : //辰
        result.add(HeavenlyStems.戊);
        result.add(HeavenlyStems.乙);
        result.add(HeavenlyStems.癸);
        break;
      case 5 : //巳
        result.add(HeavenlyStems.丙);
        result.add(HeavenlyStems.戊);
        result.add(HeavenlyStems.庚);
        break;
      case 6 : //午
        result.add(HeavenlyStems.丁);
        result.add(HeavenlyStems.己); //有藏己嗎？
        break;
      case 7 : //未
        result.add(HeavenlyStems.己);
        result.add(HeavenlyStems.丁);
        result.add(HeavenlyStems.乙);
        break;
      case 8 : //申
        result.add(HeavenlyStems.庚);
        result.add(HeavenlyStems.壬);
        result.add(HeavenlyStems.戊);
        break;
      case 9 : //酉
        result.add(HeavenlyStems.辛);
        break;
      case 10 : //戌
        result.add(HeavenlyStems.戊);
        result.add(HeavenlyStems.辛);
        result.add(HeavenlyStems.丁);
        break;
      case 11 : //亥
        result.add(HeavenlyStems.壬);
        result.add(HeavenlyStems.甲);
        //result.add(HeavenlyStems.戊); 沒有戊嗎？ 
    }
    return result;
    */
  }

}
