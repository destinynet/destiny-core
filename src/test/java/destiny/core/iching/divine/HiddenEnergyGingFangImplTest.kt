/**
 * Created by smallufo on 2018-02-03.
 */
package destiny.core.iching.divine

import destiny.core.chinese.StemBranch
import destiny.core.iching.Hexagram.*
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * 京房伏神：每爻都有伏神
 */
class HiddenEnergyGingFangImplTest {

  val impl = HiddenEnergyGingFangImpl()

  @Test
  fun `1 乾為天`() {
    impl.run {
      assertSame(StemBranch.丁未, getStemBranch(乾, SettingsGingFang(), 6))
      assertSame(StemBranch.丁酉, getStemBranch(乾, SettingsGingFang(), 5))
      assertSame(StemBranch.丁亥, getStemBranch(乾, SettingsGingFang(), 4))
      assertSame(StemBranch.丁丑, getStemBranch(乾, SettingsGingFang(), 3))
      assertSame(StemBranch.丁卯, getStemBranch(乾, SettingsGingFang(), 2))
      assertSame(StemBranch.丁巳, getStemBranch(乾, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `2 天風姤`() {
    impl.run {
      assertSame(StemBranch.丁未, getStemBranch(姤, SettingsGingFang(), 6))
      assertSame(StemBranch.丁酉, getStemBranch(姤, SettingsGingFang(), 5))
      assertSame(StemBranch.丁亥, getStemBranch(姤, SettingsGingFang(), 4))
      assertSame(StemBranch.甲辰, getStemBranch(姤, SettingsGingFang(), 3))
      assertSame(StemBranch.甲寅, getStemBranch(姤, SettingsGingFang(), 2))
      assertSame(StemBranch.甲子, getStemBranch(姤, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `3 天山遯`() {
    impl.run {
      assertSame(StemBranch.丁未, getStemBranch(遯, SettingsGingFang(), 6))
      assertSame(StemBranch.丁酉, getStemBranch(遯, SettingsGingFang(), 5))
      assertSame(StemBranch.丁亥, getStemBranch(遯, SettingsGingFang(), 4))
      assertSame(StemBranch.甲辰, getStemBranch(遯, SettingsGingFang(), 3))
      assertSame(StemBranch.甲寅, getStemBranch(遯, SettingsGingFang(), 2))
      assertSame(StemBranch.甲子, getStemBranch(遯, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `4 天地否`() {
    impl.run {
      assertSame(StemBranch.丁未, getStemBranch(否, SettingsGingFang(), 6))
      assertSame(StemBranch.丁酉, getStemBranch(否, SettingsGingFang(), 5))
      assertSame(StemBranch.丁亥, getStemBranch(否, SettingsGingFang(), 4))
      assertSame(StemBranch.甲辰, getStemBranch(否, SettingsGingFang(), 3))
      assertSame(StemBranch.甲寅, getStemBranch(否, SettingsGingFang(), 2))
      assertSame(StemBranch.甲子, getStemBranch(否, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `5 風地觀`() {
    impl.run {
      assertSame(StemBranch.壬戌, getStemBranch(觀, SettingsGingFang(), 6))
      assertSame(StemBranch.壬申, getStemBranch(觀, SettingsGingFang(), 5))
      assertSame(StemBranch.壬午, getStemBranch(觀, SettingsGingFang(), 4))
      assertSame(StemBranch.甲辰, getStemBranch(觀, SettingsGingFang(), 3))
      assertSame(StemBranch.甲寅, getStemBranch(觀, SettingsGingFang(), 2))
      assertSame(StemBranch.甲子, getStemBranch(觀, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `6 山地剝`() {
    impl.run {
      assertSame(StemBranch.壬戌, getStemBranch(剝, SettingsGingFang(), 6))
      assertSame(StemBranch.壬申, getStemBranch(剝, SettingsGingFang(), 5))
      assertSame(StemBranch.壬午, getStemBranch(剝, SettingsGingFang(), 4))
      assertSame(StemBranch.甲辰, getStemBranch(剝, SettingsGingFang(), 3))
      assertSame(StemBranch.甲寅, getStemBranch(剝, SettingsGingFang(), 2))
      assertSame(StemBranch.甲子, getStemBranch(剝, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `7 火地晉`() {
    impl.run {
      assertSame(StemBranch.壬戌, getStemBranch(晉, SettingsGingFang(), 6))
      assertSame(StemBranch.壬申, getStemBranch(晉, SettingsGingFang(), 5))
      assertSame(StemBranch.壬午, getStemBranch(晉, SettingsGingFang(), 4))
      assertSame(StemBranch.甲辰, getStemBranch(晉, SettingsGingFang(), 3))
      assertSame(StemBranch.甲寅, getStemBranch(晉, SettingsGingFang(), 2))
      assertSame(StemBranch.甲子, getStemBranch(晉, SettingsGingFang(), 1))
    }
  }

  @Test
  fun `8 火天大有`() {
    impl.run {
      assertSame(StemBranch.壬戌, getStemBranch(大有, SettingsGingFang(), 6))
      assertSame(StemBranch.壬申, getStemBranch(大有, SettingsGingFang(), 5))
      assertSame(StemBranch.壬午, getStemBranch(大有, SettingsGingFang(), 4))
      assertSame(StemBranch.丁丑, getStemBranch(大有, SettingsGingFang(), 3))
      assertSame(StemBranch.丁卯, getStemBranch(大有, SettingsGingFang(), 2))
      assertSame(StemBranch.丁巳, getStemBranch(大有, SettingsGingFang(), 1))
    }
  }
}
