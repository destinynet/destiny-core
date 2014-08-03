package destiny.core.chinese;

import org.junit.Test;
import static org.junit.Assert.*;

public class StemBranchNullableTest {

  @Test
  public void testGet() throws Exception {
    StemBranchNullable sbn1 = StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.子);
    StemBranchNullable sbn2 = StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.子);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(HeavenlyStems.癸 , EarthlyBranches.亥);
    sbn2 = StemBranchNullable.get(HeavenlyStems.癸 , EarthlyBranches.亥);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.子);
    sbn2 = StemBranchNullable.get("甲子");
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(HeavenlyStems.甲 , null);
    sbn2 = new StemBranchNullable(HeavenlyStems.甲 , null);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(null , EarthlyBranches.子);
    sbn2 = new StemBranchNullable(null , EarthlyBranches.子);
    assertEquals(sbn1 , sbn2);

    sbn1 = StemBranchNullable.get(null , null);
    sbn2 = StemBranchNullable.empty();
    assertEquals(sbn1 , sbn2);

  }

  @Test
  public void testFail() {
    try {
      StemBranchNullable.get(HeavenlyStems.甲 , EarthlyBranches.丑);
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }

    try {
      StemBranchNullable.get(HeavenlyStems.乙 , EarthlyBranches.子);
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }
}