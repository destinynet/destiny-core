/**
 * Created by smallufo on 2018-03-13.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.calendar.chinese.Yuan
import destiny.core.chinese.StemBranch
import kotlin.test.Test
import kotlin.test.assertSame

class ISanYuanTest {

  @Test
  fun testCenter() {
    assertSame(1 , ISanYuan.getCenter(Yuan.LOW , StemBranch.己卯))
    assertSame(5 , ISanYuan.getCenter(Yuan.LOW , StemBranch.乙亥))
    assertSame(8 , ISanYuan.getCenter(Yuan.LOW , StemBranch.己亥)) // 2019
  }


  @Test
  fun testYuan() {

    assertSame(Yuan.UP, ISanYuan.getYuan(1864))
    assertSame(Yuan.UP, ISanYuan.getYuan(1923))

    assertSame(Yuan.MID, ISanYuan.getYuan(1924))
    assertSame(Yuan.MID, ISanYuan.getYuan(1983))

    assertSame(Yuan.LOW , ISanYuan.getYuan(1984))
    assertSame(Yuan.LOW , ISanYuan.getYuan(2043))

    // 此三元 之後
    assertSame(Yuan.UP , ISanYuan.getYuan(2044))

    // 此三元 之前
    // 前一年 , 為下元之末
    assertSame(Yuan.LOW, ISanYuan.getYuan(1863))
    // 再往前推180年，亦為下元之末
    assertSame(Yuan.LOW, ISanYuan.getYuan(1683))

    // 下元之末 後一年，為上元 之始
    assertSame(Yuan.UP, ISanYuan.getYuan(1684))

    // 再往前推 1620年 (180x9) , 也為上元之始
    assertSame(Yuan.UP, ISanYuan.getYuan(64))

    // 上元之始前一年，為下元之末
    assertSame(Yuan.LOW, ISanYuan.getYuan(63))
    // 下元之始
    assertSame(Yuan.LOW, ISanYuan.getYuan(4))

    // 中元之末
    assertSame(Yuan.MID, ISanYuan.getYuan(3))
    assertSame(Yuan.MID, ISanYuan.getYuan(1)) // 西元元年
    assertSame(Yuan.MID, ISanYuan.getYuan(0)) // 西元前一年
    assertSame(Yuan.MID, ISanYuan.getYuan(-1)) // 西元前2年
    assertSame(Yuan.MID, ISanYuan.getYuan(-56)) // 西元前57年

    assertSame(Yuan.UP, ISanYuan.getYuan(-57)) // 西元前58年
  }
}
