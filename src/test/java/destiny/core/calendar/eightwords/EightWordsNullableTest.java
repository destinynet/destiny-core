package destiny.core.calendar.eightwords;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranchOptional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EightWordsNullableTest {

  @Test
  public void testEquals() {
    EightWordsNullable ew1 = new EightWordsNullable();
    EightWordsNullable ew2 = new EightWordsNullable();
    assertEquals(ew1 , ew2); //兩個都是 null 八字,應該 equals

    ew1 = new EightWordsNullable(StemBranchOptional.get("甲子") , StemBranchOptional.get("乙丑") , StemBranchOptional.get("丙寅") , StemBranchOptional.get("丁卯"));
    ew2 = new EightWordsNullable(StemBranchOptional.get("甲子") , StemBranchOptional.get("乙丑") , StemBranchOptional.get("丙寅") , StemBranchOptional.get("丁卯"));
    assertEquals(ew1 , ew2);

    ew1 = new EightWordsNullable(StemBranchOptional.get("甲子") , StemBranchOptional.get("乙丑") , StemBranchOptional.get("丙寅") , StemBranchOptional.get("丁卯"));
    ew2 = new EightWordsNullable(
      StemBranchOptional.get(Stem.甲 , Branch.子) ,
      StemBranchOptional.get(Stem.乙 , Branch.丑) ,
      StemBranchOptional.get(Stem.丙 , Branch.寅) ,
      StemBranchOptional.get(Stem.丁 , Branch.卯));
    assertEquals(ew1 , ew2);

    ew1 = new EightWordsNullable(StemBranchOptional.get("甲子") , StemBranchOptional.empty() , StemBranchOptional.get("丙寅") , StemBranchOptional.empty());
    ew2 = new EightWordsNullable(
      StemBranchOptional.get(Stem.甲 , Branch.子) ,
      StemBranchOptional.empty() ,
      StemBranchOptional.get(Stem.丙 , Branch.寅) ,
      StemBranchOptional.empty());
    assertEquals(ew1 , ew2);


    ew1 = new EightWordsNullable(StemBranchOptional.get("甲子") ,
      StemBranchOptional.empty() ,
      StemBranchOptional.get(Stem.丙 , null) ,
      StemBranchOptional.get(null , Branch.卯)
    );
    ew2 = new EightWordsNullable(
      StemBranchOptional.get(Stem.甲 , Branch.子) ,
      StemBranchOptional.empty() ,
      StemBranchOptional.get(Stem.丙 , null) ,
      StemBranchOptional.get(null , Branch.卯));
    assertEquals(ew1 , ew2);

  }

}