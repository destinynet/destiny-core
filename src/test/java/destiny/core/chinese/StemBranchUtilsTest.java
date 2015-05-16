/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.chinese;

import org.junit.Test;

import static destiny.core.chinese.EarthlyBranches.*;
import static destiny.core.chinese.HeavenlyStems.*;
import static org.junit.Assert.assertSame;

public class StemBranchUtilsTest {

  /** 五虎遁月 */
  @Test
  public void testGetMonthStem() throws Exception {
    assertSame(丙 , StemBranchUtils.getMonthStem(甲 , 寅));
    assertSame(戊 , StemBranchUtils.getMonthStem(乙 , 寅));
    assertSame(庚 , StemBranchUtils.getMonthStem(丙 , 寅));
    assertSame(壬 , StemBranchUtils.getMonthStem(丁 , 寅));
    assertSame(甲 , StemBranchUtils.getMonthStem(戊 , 寅));
    assertSame(丙 , StemBranchUtils.getMonthStem(己 , 寅));
    assertSame(戊 , StemBranchUtils.getMonthStem(庚 , 寅));
    assertSame(庚 , StemBranchUtils.getMonthStem(辛 , 寅));
    assertSame(壬 , StemBranchUtils.getMonthStem(壬 , 寅));
    assertSame(甲 , StemBranchUtils.getMonthStem(癸 , 寅));

    assertSame(丁 , StemBranchUtils.getMonthStem(甲 , 卯));
    assertSame(己 , StemBranchUtils.getMonthStem(乙 , 卯));
    assertSame(辛 , StemBranchUtils.getMonthStem(丙 , 卯));
    assertSame(癸 , StemBranchUtils.getMonthStem(丁 , 卯));
    assertSame(乙 , StemBranchUtils.getMonthStem(戊 , 卯));
    assertSame(丁 , StemBranchUtils.getMonthStem(己 , 卯));
    assertSame(己 , StemBranchUtils.getMonthStem(庚 , 卯));
    assertSame(辛 , StemBranchUtils.getMonthStem(辛 , 卯));
    assertSame(癸 , StemBranchUtils.getMonthStem(壬 , 卯));
    assertSame(乙 , StemBranchUtils.getMonthStem(癸 , 卯));

    assertSame(乙 , StemBranchUtils.getMonthStem(甲 , 亥));
    assertSame(丁 , StemBranchUtils.getMonthStem(乙 , 亥));
    assertSame(己 , StemBranchUtils.getMonthStem(丙 , 亥));
    assertSame(辛 , StemBranchUtils.getMonthStem(丁 , 亥));
    assertSame(癸 , StemBranchUtils.getMonthStem(戊 , 亥));
    assertSame(乙 , StemBranchUtils.getMonthStem(己 , 亥));
    assertSame(丁 , StemBranchUtils.getMonthStem(庚 , 亥));
    assertSame(己 , StemBranchUtils.getMonthStem(辛 , 亥));
    assertSame(辛 , StemBranchUtils.getMonthStem(壬 , 亥));
    assertSame(癸 , StemBranchUtils.getMonthStem(癸 , 亥));


    assertSame(丙 , StemBranchUtils.getMonthStem(甲 , 子));
    assertSame(戊 , StemBranchUtils.getMonthStem(乙 , 子));
    assertSame(庚 , StemBranchUtils.getMonthStem(丙 , 子));
    assertSame(壬 , StemBranchUtils.getMonthStem(丁 , 子));
    assertSame(甲 , StemBranchUtils.getMonthStem(戊 , 子));
    assertSame(丙 , StemBranchUtils.getMonthStem(己 , 子));
    assertSame(戊 , StemBranchUtils.getMonthStem(庚 , 子));
    assertSame(庚 , StemBranchUtils.getMonthStem(辛 , 子));
    assertSame(壬 , StemBranchUtils.getMonthStem(壬 , 子));
    assertSame(甲 , StemBranchUtils.getMonthStem(癸 , 子));


    assertSame(丁 , StemBranchUtils.getMonthStem(甲 , 丑));
    assertSame(己 , StemBranchUtils.getMonthStem(乙 , 丑));
    assertSame(辛 , StemBranchUtils.getMonthStem(丙 , 丑));
    assertSame(癸 , StemBranchUtils.getMonthStem(丁 , 丑));
    assertSame(乙 , StemBranchUtils.getMonthStem(戊 , 丑));
    assertSame(丁 , StemBranchUtils.getMonthStem(己 , 丑));
    assertSame(己 , StemBranchUtils.getMonthStem(庚 , 丑));
    assertSame(辛 , StemBranchUtils.getMonthStem(辛 , 丑));
    assertSame(癸 , StemBranchUtils.getMonthStem(壬 , 丑));
    assertSame(乙 , StemBranchUtils.getMonthStem(癸 , 丑));
  }
}