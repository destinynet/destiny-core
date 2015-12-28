package destiny.core.calendar.eightwords;

import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranchOptional;
import destiny.tools.Hashids;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static org.junit.Assert.assertEquals;

public class EightWordsNullableTest {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void testGetList() {
    EightWordsNullable ewn1 = new EightWordsNullable(StemBranchOptional.get("甲子") , StemBranchOptional.get("乙丑") , StemBranchOptional.get("丙寅") , StemBranchOptional.get("丁卯"));
    EightWordsNullable ewn2 = EightWordsNullable.getFromIntList(ewn1.getIntList());
    assertEquals(ewn1 , ewn2);

    ewn1 = new EightWordsNullable(
      StemBranchOptional.get(甲 , null) ,
      StemBranchOptional.get(null , 丑) ,
      StemBranchOptional.get(丙 , null) ,
      StemBranchOptional.get(null ,卯));
    ewn2 = EightWordsNullable.getFromIntList(ewn1.getIntList());
    assertEquals(ewn1 , ewn2);


    EightWordsNullable empty = new EightWordsNullable(StemBranchOptional.empty() , StemBranchOptional.empty() , StemBranchOptional.empty() , StemBranchOptional.empty());
    EightWordsNullable empty2 = EightWordsNullable.getFromIntList(empty.getIntList());
    assertEquals(empty , empty2);

    Hashids hashids = new Hashids("ewn");
    String encoded = hashids.encode(empty.getIntList().stream().mapToLong(i->i).toArray());
    logger.info("encoded = {}" , encoded);

    EightWords ew1 = new EightWords(甲 , 子 , 乙 , 丑 , 丙 , 寅 , 丁 , 卯);
    logger.info("ints = {}" , ew1.getIntList());
    EightWordsNullable ew2 = EightWordsNullable.getFromIntList(ew1.getIntList());
    logger.info("ew2 = {}" , ew2);

  }

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
      StemBranchOptional.get(甲 , 子) ,
      StemBranchOptional.get(Stem.乙 , 丑) ,
      StemBranchOptional.get(Stem.丙 , 寅) ,
      StemBranchOptional.get(Stem.丁 , 卯));
    assertEquals(ew1 , ew2);

    ew1 = new EightWordsNullable(StemBranchOptional.get("甲子") , StemBranchOptional.empty() , StemBranchOptional.get("丙寅") , StemBranchOptional.empty());
    ew2 = new EightWordsNullable(
      StemBranchOptional.get(甲 , 子) ,
      StemBranchOptional.empty() ,
      StemBranchOptional.get(Stem.丙 , 寅) ,
      StemBranchOptional.empty());
    assertEquals(ew1 , ew2);


    ew1 = new EightWordsNullable(StemBranchOptional.get("甲子") ,
      StemBranchOptional.empty() ,
      StemBranchOptional.get(Stem.丙 , null) ,
      StemBranchOptional.get(null , 卯)
    );
    ew2 = new EightWordsNullable(
      StemBranchOptional.get(甲 , 子) ,
      StemBranchOptional.empty() ,
      StemBranchOptional.get(Stem.丙 , null) ,
      StemBranchOptional.get(null , 卯));
    assertEquals(ew1 , ew2);

  }

}