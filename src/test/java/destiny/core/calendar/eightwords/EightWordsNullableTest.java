package destiny.core.calendar.eightwords;

import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranchNullable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EightWordsNullableTest {

  @Test
  public void testEquals() {
    EightWordsNullable ew1 = new EightWordsNullable();
    EightWordsNullable ew2 = new EightWordsNullable();
    assertEquals(ew1 , ew2); //兩個都是 null 八字,應該 equals

    ew1 = new EightWordsNullable(StemBranchNullable.get("甲子") , StemBranchNullable.get("乙丑") , StemBranchNullable.get("丙寅") , StemBranchNullable.get("丁卯"));
    ew2 = new EightWordsNullable(StemBranchNullable.get("甲子") , StemBranchNullable.get("乙丑") , StemBranchNullable.get("丙寅") , StemBranchNullable.get("丁卯"));
    assertEquals(ew1 , ew2);

    ew1 = new EightWordsNullable(StemBranchNullable.get("甲子") , StemBranchNullable.get("乙丑") , StemBranchNullable.get("丙寅") , StemBranchNullable.get("丁卯"));
    ew2 = new EightWordsNullable(
      StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.子) ,
      StemBranchNullable.get(HeavenlyStems.乙 , EarthlyBranches.丑) ,
      StemBranchNullable.get(HeavenlyStems.丙 , EarthlyBranches.寅) ,
      StemBranchNullable.get(HeavenlyStems.丁 , EarthlyBranches.卯));
    assertEquals(ew1 , ew2);

    ew1 = new EightWordsNullable(StemBranchNullable.get("甲子") , StemBranchNullable.empty() , StemBranchNullable.get("丙寅") , StemBranchNullable.empty());
    ew2 = new EightWordsNullable(
      StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.子) ,
      StemBranchNullable.empty() ,
      StemBranchNullable.get(HeavenlyStems.丙 , EarthlyBranches.寅) ,
      StemBranchNullable.empty());
    assertEquals(ew1 , ew2);


    ew1 = new EightWordsNullable(StemBranchNullable.get("甲子") , StemBranchNullable.empty() , StemBranchNullable.get(HeavenlyStems.丙 , null) , StemBranchNullable.get(null , EarthlyBranches.卯));
    ew2 = new EightWordsNullable(
      StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.子) ,
      StemBranchNullable.empty() ,
      StemBranchNullable.get(HeavenlyStems.丙 , null) ,
      StemBranchNullable.get(null , EarthlyBranches.卯));
    assertEquals(ew1 , ew2);

  }

}