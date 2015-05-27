/**
 * Created by smallufo on 2015-05-15.
 */
package destiny.core.chinese;

import org.junit.Test;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;
import static org.junit.Assert.assertSame;

public class StemBranchUtilsTest {

  /** 五虎遁年起月(干) */
  @Test
  public void testGetMonthStem() {
    assertSame(丙 , StemBranchUtils.getMonthStem(甲 , 寅)); // 甲年 => 丙寅月
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

  /**
   * 五鼠遁日起時(干)
   */
  @Test
  public void testGetHourStem() {
    assertSame(甲 , StemBranchUtils.getHourStem(甲 , 子)); // 甲日 => 甲子時
    assertSame(乙 , StemBranchUtils.getHourStem(甲 , 丑));
    assertSame(丙 , StemBranchUtils.getHourStem(甲 , 寅));
    assertSame(丁 , StemBranchUtils.getHourStem(甲 , 卯));
    assertSame(戊 , StemBranchUtils.getHourStem(甲 , 辰));
    assertSame(己 , StemBranchUtils.getHourStem(甲 , 巳));
    assertSame(庚 , StemBranchUtils.getHourStem(甲 , 午));
    assertSame(辛 , StemBranchUtils.getHourStem(甲 , 未));
    assertSame(壬 , StemBranchUtils.getHourStem(甲 , 申));
    assertSame(癸 , StemBranchUtils.getHourStem(甲 , 酉));
    assertSame(甲 , StemBranchUtils.getHourStem(甲 , 戌));
    assertSame(乙 , StemBranchUtils.getHourStem(甲 , 亥));

    assertSame(丙 , StemBranchUtils.getHourStem(乙 , 子));
    assertSame(丁 , StemBranchUtils.getHourStem(乙 , 丑));
    assertSame(戊 , StemBranchUtils.getHourStem(乙 , 寅));
    assertSame(己 , StemBranchUtils.getHourStem(乙 , 卯));
    assertSame(庚 , StemBranchUtils.getHourStem(乙 , 辰));
    assertSame(辛 , StemBranchUtils.getHourStem(乙 , 巳));
    assertSame(壬 , StemBranchUtils.getHourStem(乙 , 午));
    assertSame(癸 , StemBranchUtils.getHourStem(乙 , 未));
    assertSame(甲 , StemBranchUtils.getHourStem(乙 , 申));
    assertSame(乙 , StemBranchUtils.getHourStem(乙 , 酉));
    assertSame(丙 , StemBranchUtils.getHourStem(乙 , 戌));
    assertSame(丁 , StemBranchUtils.getHourStem(乙 , 亥));

    assertSame(戊 , StemBranchUtils.getHourStem(丙 , 子));
    assertSame(己 , StemBranchUtils.getHourStem(丙 , 丑));
    assertSame(庚 , StemBranchUtils.getHourStem(丙 , 寅));
    assertSame(辛 , StemBranchUtils.getHourStem(丙 , 卯));
    assertSame(壬 , StemBranchUtils.getHourStem(丙 , 辰));
    assertSame(癸 , StemBranchUtils.getHourStem(丙 , 巳));
    assertSame(甲 , StemBranchUtils.getHourStem(丙 , 午));
    assertSame(乙 , StemBranchUtils.getHourStem(丙 , 未));
    assertSame(丙 , StemBranchUtils.getHourStem(丙 , 申));
    assertSame(丁 , StemBranchUtils.getHourStem(丙 , 酉));
    assertSame(戊 , StemBranchUtils.getHourStem(丙 , 戌));
    assertSame(己 , StemBranchUtils.getHourStem(丙 , 亥));

    assertSame(庚 , StemBranchUtils.getHourStem(丁 , 子));
    assertSame(辛 , StemBranchUtils.getHourStem(丁 , 丑));
    assertSame(壬 , StemBranchUtils.getHourStem(丁 , 寅));
    assertSame(癸 , StemBranchUtils.getHourStem(丁 , 卯));
    assertSame(甲 , StemBranchUtils.getHourStem(丁 , 辰));
    assertSame(乙 , StemBranchUtils.getHourStem(丁 , 巳));
    assertSame(丙 , StemBranchUtils.getHourStem(丁 , 午));
    assertSame(丁 , StemBranchUtils.getHourStem(丁 , 未));
    assertSame(戊 , StemBranchUtils.getHourStem(丁 , 申));
    assertSame(己 , StemBranchUtils.getHourStem(丁 , 酉));
    assertSame(庚 , StemBranchUtils.getHourStem(丁 , 戌));
    assertSame(辛 , StemBranchUtils.getHourStem(丁 , 亥));

    assertSame(壬 , StemBranchUtils.getHourStem(戊 , 子));
    assertSame(癸 , StemBranchUtils.getHourStem(戊 , 丑));
    assertSame(甲 , StemBranchUtils.getHourStem(戊 , 寅));
    assertSame(乙 , StemBranchUtils.getHourStem(戊 , 卯));
    assertSame(丙 , StemBranchUtils.getHourStem(戊 , 辰));
    assertSame(丁 , StemBranchUtils.getHourStem(戊 , 巳));
    assertSame(戊 , StemBranchUtils.getHourStem(戊 , 午));
    assertSame(己 , StemBranchUtils.getHourStem(戊 , 未));
    assertSame(庚 , StemBranchUtils.getHourStem(戊 , 申));
    assertSame(辛 , StemBranchUtils.getHourStem(戊 , 酉));
    assertSame(壬 , StemBranchUtils.getHourStem(戊 , 戌));
    assertSame(癸 , StemBranchUtils.getHourStem(戊 , 亥));

    assertSame(甲 , StemBranchUtils.getHourStem(己 , 子));
    assertSame(乙 , StemBranchUtils.getHourStem(己 , 丑));
    assertSame(丙 , StemBranchUtils.getHourStem(己 , 寅));
    assertSame(丁 , StemBranchUtils.getHourStem(己 , 卯));
    assertSame(戊 , StemBranchUtils.getHourStem(己 , 辰));
    assertSame(己 , StemBranchUtils.getHourStem(己 , 巳));
    assertSame(庚 , StemBranchUtils.getHourStem(己 , 午));
    assertSame(辛 , StemBranchUtils.getHourStem(己 , 未));
    assertSame(壬 , StemBranchUtils.getHourStem(己 , 申));
    assertSame(癸 , StemBranchUtils.getHourStem(己 , 酉));
    assertSame(甲 , StemBranchUtils.getHourStem(己 , 戌));
    assertSame(乙 , StemBranchUtils.getHourStem(己 , 亥));

    assertSame(丙 , StemBranchUtils.getHourStem(庚 , 子));
    assertSame(丁 , StemBranchUtils.getHourStem(庚 , 丑));
    assertSame(戊 , StemBranchUtils.getHourStem(庚 , 寅));
    assertSame(己 , StemBranchUtils.getHourStem(庚 , 卯));
    assertSame(庚 , StemBranchUtils.getHourStem(庚 , 辰));
    assertSame(辛 , StemBranchUtils.getHourStem(庚 , 巳));
    assertSame(壬 , StemBranchUtils.getHourStem(庚 , 午));
    assertSame(癸 , StemBranchUtils.getHourStem(庚 , 未));
    assertSame(甲 , StemBranchUtils.getHourStem(庚 , 申));
    assertSame(乙 , StemBranchUtils.getHourStem(庚 , 酉));
    assertSame(丙 , StemBranchUtils.getHourStem(庚 , 戌));
    assertSame(丁 , StemBranchUtils.getHourStem(庚 , 亥));

    assertSame(戊 , StemBranchUtils.getHourStem(辛 , 子));
    assertSame(己 , StemBranchUtils.getHourStem(辛 , 丑));
    assertSame(庚 , StemBranchUtils.getHourStem(辛 , 寅));
    assertSame(辛 , StemBranchUtils.getHourStem(辛 , 卯));
    assertSame(壬 , StemBranchUtils.getHourStem(辛 , 辰));
    assertSame(癸 , StemBranchUtils.getHourStem(辛 , 巳));
    assertSame(甲 , StemBranchUtils.getHourStem(辛 , 午));
    assertSame(乙 , StemBranchUtils.getHourStem(辛 , 未));
    assertSame(丙 , StemBranchUtils.getHourStem(辛 , 申));
    assertSame(丁 , StemBranchUtils.getHourStem(辛 , 酉));
    assertSame(戊 , StemBranchUtils.getHourStem(辛 , 戌));
    assertSame(己 , StemBranchUtils.getHourStem(辛 , 亥));

    assertSame(庚 , StemBranchUtils.getHourStem(壬 , 子));
    assertSame(辛 , StemBranchUtils.getHourStem(壬 , 丑));
    assertSame(壬 , StemBranchUtils.getHourStem(壬 , 寅));
    assertSame(癸 , StemBranchUtils.getHourStem(壬 , 卯));
    assertSame(甲 , StemBranchUtils.getHourStem(壬 , 辰));
    assertSame(乙 , StemBranchUtils.getHourStem(壬 , 巳));
    assertSame(丙 , StemBranchUtils.getHourStem(壬 , 午));
    assertSame(丁 , StemBranchUtils.getHourStem(壬 , 未));
    assertSame(戊 , StemBranchUtils.getHourStem(壬 , 申));
    assertSame(己 , StemBranchUtils.getHourStem(壬 , 酉));
    assertSame(庚 , StemBranchUtils.getHourStem(壬 , 戌));
    assertSame(辛 , StemBranchUtils.getHourStem(壬 , 亥));

    assertSame(壬 , StemBranchUtils.getHourStem(癸 , 子));
    assertSame(癸 , StemBranchUtils.getHourStem(癸 , 丑));
    assertSame(甲 , StemBranchUtils.getHourStem(癸 , 寅));
    assertSame(乙 , StemBranchUtils.getHourStem(癸 , 卯));
    assertSame(丙 , StemBranchUtils.getHourStem(癸 , 辰));
    assertSame(丁 , StemBranchUtils.getHourStem(癸 , 巳));
    assertSame(戊 , StemBranchUtils.getHourStem(癸 , 午));
    assertSame(己 , StemBranchUtils.getHourStem(癸 , 未));
    assertSame(庚 , StemBranchUtils.getHourStem(癸 , 申));
    assertSame(辛 , StemBranchUtils.getHourStem(癸 , 酉));
    assertSame(壬 , StemBranchUtils.getHourStem(癸 , 戌));
    assertSame(癸 , StemBranchUtils.getHourStem(癸 , 亥));
  }
}