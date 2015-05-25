/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:06:07
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;

import java.io.Serializable;
import java.util.*;


/** 地支藏干標準實作 
 * 潘文欽有另一套說法： 
 * http://destiny.to/ubbthreads/files/1704571-%E8%97%8F%E5%B9%B2.jpg
 * */
public class HiddenStemsStandardImpl implements HiddenStemsIF , Serializable
{
  private final static Map<Branch, List<Stem>> map = Collections.synchronizedMap(new HashMap<>());
  static
  {
    map.put(Branch.子, new ArrayList<>( Arrays.asList(new Stem[] {Stem.癸})));
    
    map.put(Branch.丑, new ArrayList<>( Arrays.asList(new Stem[] {Stem.己 , Stem.癸 , Stem.辛})));
    
    map.put(Branch.寅, new ArrayList<>( Arrays.asList(new Stem[] {Stem.甲 , Stem.丙 , Stem.戊})));
    
    map.put(Branch.卯, new ArrayList<>( Arrays.asList(new Stem[] {Stem.乙})));
    
    map.put(Branch.辰, new ArrayList<>( Arrays.asList(new Stem[] {Stem.戊 , Stem.乙 , Stem.癸})));
    
    map.put(Branch.巳, new ArrayList<>( Arrays.asList(new Stem[] {Stem.丙 , Stem.戊 , Stem.庚})));
    
    map.put(Branch.午, new ArrayList<>( Arrays.asList(new Stem[] {Stem.丁 , Stem.己})));
    
    map.put(Branch.未, new ArrayList<>( Arrays.asList(new Stem[] {Stem.己 , Stem.丁 , Stem.乙})));
    
    map.put(Branch.申, new ArrayList<>( Arrays.asList(new Stem[] {Stem.庚 , Stem.壬 , Stem.戊})));
    
    map.put(Branch.酉, new ArrayList<>( Arrays.asList(new Stem[] {Stem.辛})));
    
    map.put(Branch.戌, new ArrayList<>( Arrays.asList(new Stem[] {Stem.戊 , Stem.辛 , Stem.丁})));
    
    map.put(Branch.亥, new ArrayList<>( Arrays.asList(new Stem[] {Stem.壬 , Stem.甲})));
  }
  
  public List<Stem> getHiddenStems(Branch branch)
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
