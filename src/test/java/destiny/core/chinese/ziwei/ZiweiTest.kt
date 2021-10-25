/**
 * Created by smallufo on 2021-10-25.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import destiny.core.chinese.NaYin
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.ziwei.House.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class ZiweiTest {

  private val seq = HouseSeqDefaultImpl()

  /**
   * 命宮決定後，逆時針飛佈 12宮
   *
   * 已知：
   * 農曆三月、戌時 , 命宮在午
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  fun testGetHouseBranch() {
    assertSame(午, Ziwei.getHouseBranch(3, 戌, 命宮, seq))
    assertSame(巳, Ziwei.getHouseBranch(3, 戌, 兄弟, seq))
    assertSame(辰, Ziwei.getHouseBranch(3, 戌, 夫妻, seq))
    assertSame(卯, Ziwei.getHouseBranch(3, 戌, 子女, seq))
    assertSame(寅, Ziwei.getHouseBranch(3, 戌, 財帛, seq))
    assertSame(丑, Ziwei.getHouseBranch(3, 戌, 疾厄, seq))
    assertSame(子, Ziwei.getHouseBranch(3, 戌, 遷移, seq))
    assertSame(亥, Ziwei.getHouseBranch(3, 戌, 交友, seq))
    assertSame(戌, Ziwei.getHouseBranch(3, 戌, 官祿, seq))
    assertSame(酉, Ziwei.getHouseBranch(3, 戌, 田宅, seq))
    assertSame(申, Ziwei.getHouseBranch(3, 戌, 福德, seq))
    assertSame(未, Ziwei.getHouseBranch(3, 戌, 父母, seq))
  }

  /**
   * 承上 ，取得宮位天干
   *
   * 已知：
   * 丁酉年
   * 農曆三月、戌時 , 命宮在午
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  fun testHouseWithStem() {
    assertSame(丙午, Ziwei.getHouse(Stem.丁, 3, 戌, 命宮, seq))
    assertSame(乙巳, Ziwei.getHouse(Stem.丁, 3, 戌, 兄弟, seq))
    assertSame(甲辰, Ziwei.getHouse(Stem.丁, 3, 戌, 夫妻, seq))
    assertSame(癸卯, Ziwei.getHouse(Stem.丁, 3, 戌, 子女, seq))
    assertSame(壬寅, Ziwei.getHouse(Stem.丁, 3, 戌, 財帛, seq))
    assertSame(癸丑, Ziwei.getHouse(Stem.丁, 3, 戌, 疾厄, seq))
    assertSame(壬子, Ziwei.getHouse(Stem.丁, 3, 戌, 遷移, seq))
    assertSame(辛亥, Ziwei.getHouse(Stem.丁, 3, 戌, 交友, seq))
    assertSame(庚戌, Ziwei.getHouse(Stem.丁, 3, 戌, 官祿, seq))
    assertSame(己酉, Ziwei.getHouse(Stem.丁, 3, 戌, 田宅, seq))
    assertSame(戊申, Ziwei.getHouse(Stem.丁, 3, 戌, 福德, seq))
    assertSame(丁未, Ziwei.getHouse(Stem.丁, 3, 戌, 父母, seq))
  }



  /**
   * 根據此頁面範例 http://www.freehoro.net/ZWDS/Tutorial/PaiPan/19-0_Zi_Liu_NianDouJun.php
   * 如某女士於陽曆1970年10月1日10時10分，為陰曆(庚戌)年9月2日巳時出生，是為陽女。
   * 若現在為西元2012年陰曆過年後(歲次為壬辰)，則為虛歲43歲。
   * 依子年斗君位於酉宮、流年支為辰作為條件，查表得知流年斗君位於丑宮。
   */
  @Test
  fun test流年斗君1() {
    assertSame(丑, Ziwei.getFlowYearAnchor(辰, 9, 巳))
  }

  /**
   * 根據此頁面範例 https://goo.gl/zwWsmO
   * 農曆：(民國)56年11月×日辰時
   *
   *
   * 一個在2002年新暦2月25日・中六合彩的命例:
   * 男:壬午年壬寅月甲子日36歳,大運乙巳
   * 天府天馬同宮,雙祿在辰午二宮夾輔,壬年祿存在亥照會,祿馬同鄕主横財,「斗君子」. // ==> 流年午年 , 斗君在子
   *
   *
   * 農暦正月十四日命宮在丑,
   * 甲子日廉貞在卯福徳宮化祿,
   * 天盤財帛宮在大運流年雙化禄,
   * 此日買中六合彩中齊六個字,發了一筆橫財。
   */
  @Test
  fun test流年斗君2() {
    assertSame(子, Ziwei.getFlowYearAnchor(午, 11, 辰))
  }

  /**
   * 測試命宮 (main)
   * 已知：
   * 農曆三月、戌時 , 命宮在午 ,
   *
   * 106丁酉年 , 命宮 為「丙午」
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  fun testGetMainHouseBranch() {

    assertSame(午, Ziwei.getMainHouseBranch(3, 戌))

    assertSame(丙午, Ziwei.getMainHouse(Stem.丁, 3, 戌))
  }


  /**
   * 身宮
   *
   * 已知：
   * 農曆三月、戌時 , 命宮在寅
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  fun testBodyHouse() {
    assertSame(寅, Ziwei.getBodyHouseBranch(3, 戌))

    // 丁酉年 3月14日，子時，身命同宮 , 都在 辰
    assertSame(辰, Ziwei.getMainHouseBranch(3, 子))
    assertSame(辰, Ziwei.getBodyHouseBranch(3, 子))
  }

  /**
   * 納音 五行局
   *
   * 已知：
   * 壬寅命宮 => 水二局[NaYin.天河水]
   * 比對資料 : https://goo.gl/w4snjL
   */
  @Test
  fun testGetNaYin() {
    assertEquals("金箔金", NaYin.getDesc(壬寅, Locale.TAIWAN))
  }
}
