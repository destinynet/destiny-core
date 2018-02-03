/**
 * Created by smallufo on 2018-02-03.
 */
package destiny.iching.divine

import destiny.core.chinese.StemBranch
import destiny.iching.Hexagram
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * 京房伏神：每爻都有伏神
 */
class HiddenEnergyGingFangImplTest {

  val impl = HiddenEnergyGingFangImpl()

  @Test
  fun `1 乾為天`() {
    assertSame(StemBranch.丁未, impl.getStemBranch(Hexagram.乾, SettingsGingFang(), 6))
    assertSame(StemBranch.丁酉, impl.getStemBranch(Hexagram.乾, SettingsGingFang(), 5))
    assertSame(StemBranch.丁亥, impl.getStemBranch(Hexagram.乾, SettingsGingFang(), 4))
    assertSame(StemBranch.丁丑, impl.getStemBranch(Hexagram.乾, SettingsGingFang(), 3))
    assertSame(StemBranch.丁卯, impl.getStemBranch(Hexagram.乾, SettingsGingFang(), 2))
    assertSame(StemBranch.丁巳, impl.getStemBranch(Hexagram.乾, SettingsGingFang(), 1))
  }

  @Test
  fun `2 天風姤`() {
    assertSame(StemBranch.丁未, impl.getStemBranch(Hexagram.姤, SettingsGingFang(), 6))
    assertSame(StemBranch.丁酉, impl.getStemBranch(Hexagram.姤, SettingsGingFang(), 5))
    assertSame(StemBranch.丁亥, impl.getStemBranch(Hexagram.姤, SettingsGingFang(), 4))
    assertSame(StemBranch.甲辰, impl.getStemBranch(Hexagram.姤, SettingsGingFang(), 3))
    assertSame(StemBranch.甲寅, impl.getStemBranch(Hexagram.姤, SettingsGingFang(), 2))
    assertSame(StemBranch.甲子, impl.getStemBranch(Hexagram.姤, SettingsGingFang(), 1))
  }

  @Test
  fun `3 天山遯`() {
    assertSame(StemBranch.丁未, impl.getStemBranch(Hexagram.遯, SettingsGingFang(), 6))
    assertSame(StemBranch.丁酉, impl.getStemBranch(Hexagram.遯, SettingsGingFang(), 5))
    assertSame(StemBranch.丁亥, impl.getStemBranch(Hexagram.遯, SettingsGingFang(), 4))
    assertSame(StemBranch.甲辰, impl.getStemBranch(Hexagram.遯, SettingsGingFang(), 3))
    assertSame(StemBranch.甲寅, impl.getStemBranch(Hexagram.遯, SettingsGingFang(), 2))
    assertSame(StemBranch.甲子, impl.getStemBranch(Hexagram.遯, SettingsGingFang(), 1))
  }

  @Test
  fun `4 天地否`() {
    assertSame(StemBranch.丁未, impl.getStemBranch(Hexagram.否, SettingsGingFang(), 6))
    assertSame(StemBranch.丁酉, impl.getStemBranch(Hexagram.否, SettingsGingFang(), 5))
    assertSame(StemBranch.丁亥, impl.getStemBranch(Hexagram.否, SettingsGingFang(), 4))
    assertSame(StemBranch.甲辰, impl.getStemBranch(Hexagram.否, SettingsGingFang(), 3))
    assertSame(StemBranch.甲寅, impl.getStemBranch(Hexagram.否, SettingsGingFang(), 2))
    assertSame(StemBranch.甲子, impl.getStemBranch(Hexagram.否, SettingsGingFang(), 1))
  }

  @Test
  fun `5 風地觀`() {
    assertSame(StemBranch.壬戌, impl.getStemBranch(Hexagram.觀, SettingsGingFang(), 6))
    assertSame(StemBranch.壬申, impl.getStemBranch(Hexagram.觀, SettingsGingFang(), 5))
    assertSame(StemBranch.壬午, impl.getStemBranch(Hexagram.觀, SettingsGingFang(), 4))
    assertSame(StemBranch.甲辰, impl.getStemBranch(Hexagram.觀, SettingsGingFang(), 3))
    assertSame(StemBranch.甲寅, impl.getStemBranch(Hexagram.觀, SettingsGingFang(), 2))
    assertSame(StemBranch.甲子, impl.getStemBranch(Hexagram.觀, SettingsGingFang(), 1))
  }

  @Test
  fun `6 山地剝`() {
    assertSame(StemBranch.壬戌, impl.getStemBranch(Hexagram.剝, SettingsGingFang(), 6))
    assertSame(StemBranch.壬申, impl.getStemBranch(Hexagram.剝, SettingsGingFang(), 5))
    assertSame(StemBranch.壬午, impl.getStemBranch(Hexagram.剝, SettingsGingFang(), 4))
    assertSame(StemBranch.甲辰, impl.getStemBranch(Hexagram.剝, SettingsGingFang(), 3))
    assertSame(StemBranch.甲寅, impl.getStemBranch(Hexagram.剝, SettingsGingFang(), 2))
    assertSame(StemBranch.甲子, impl.getStemBranch(Hexagram.剝, SettingsGingFang(), 1))
  }

  @Test
  fun `7 火地晉`() {
    assertSame(StemBranch.壬戌, impl.getStemBranch(Hexagram.晉, SettingsGingFang(), 6))
    assertSame(StemBranch.壬申, impl.getStemBranch(Hexagram.晉, SettingsGingFang(), 5))
    assertSame(StemBranch.壬午, impl.getStemBranch(Hexagram.晉, SettingsGingFang(), 4))
    assertSame(StemBranch.甲辰, impl.getStemBranch(Hexagram.晉, SettingsGingFang(), 3))
    assertSame(StemBranch.甲寅, impl.getStemBranch(Hexagram.晉, SettingsGingFang(), 2))
    assertSame(StemBranch.甲子, impl.getStemBranch(Hexagram.晉, SettingsGingFang(), 1))
  }

  @Test
  fun `8 火天大有`() {
    assertSame(StemBranch.壬戌, impl.getStemBranch(Hexagram.大有, SettingsGingFang(), 6))
    assertSame(StemBranch.壬申, impl.getStemBranch(Hexagram.大有, SettingsGingFang(), 5))
    assertSame(StemBranch.壬午, impl.getStemBranch(Hexagram.大有, SettingsGingFang(), 4))
    assertSame(StemBranch.丁丑, impl.getStemBranch(Hexagram.大有, SettingsGingFang(), 3))
    assertSame(StemBranch.丁卯, impl.getStemBranch(Hexagram.大有, SettingsGingFang(), 2))
    assertSame(StemBranch.丁巳, impl.getStemBranch(Hexagram.大有, SettingsGingFang(), 1))
  }
}