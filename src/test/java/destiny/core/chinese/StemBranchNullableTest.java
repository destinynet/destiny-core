package destiny.core.chinese;

import org.junit.Test;
import static org.junit.Assert.*;

public class StemBranchNullableTest {

  @Test
  public void testGet() throws Exception {
    StemBranchNullable sbn1 = StemBranchNullable.get(Stem.甲 , Branch.子);
    StemBranchNullable sbn2 = StemBranchNullable.get(Stem.甲 , Branch.子);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(Stem.癸 , Branch.亥);
    sbn2 = StemBranchNullable.get(Stem.癸 , Branch.亥);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(Stem.甲 , Branch.子);
    sbn2 = StemBranchNullable.get("甲子");
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(Stem.甲 , null);
    sbn2 = new StemBranchNullable(Stem.甲 , null);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(null , Branch.子);
    sbn2 = new StemBranchNullable(null , Branch.子);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(null , null);
    sbn2 = StemBranchNullable.empty();
    assertEquals(sbn1 , sbn2);

  }

  @Test
  public void testFail() {
    try {
      StemBranchNullable.get(Stem.甲 , Branch.丑);
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }

    try {
      StemBranchNullable.get(Stem.乙 , Branch.子);
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }
}