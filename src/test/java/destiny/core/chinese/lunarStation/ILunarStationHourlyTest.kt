/**
 * Created by smallufo on 2021-03-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation.*
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import kotlin.test.Test
import kotlin.test.assertSame


internal class ILunarStationHourlyTest {

  /**
   * https://www.ptt.cc/bbs/threeprogram/M.1387087265.A.DA2.html
   *
   * 在時上起氣將之禽名，順行地支，到時禽上停止，接著倒回來推到時上。注意：
   * 來回所數的輪迴完全一致！我們舉例說明：
   * 2008年11月22日星期六，我們查萬年曆為戊子年 癸亥月 丙寅日 癸巳時
   * 二元甲子，日禽丙寅日胃土雉，時禽癸巳時婁金狗。氣將奎木狼
   * 我們根據起法，從時辰開始，巳時（第一位起氣將奎木狼），午（按28宿次序順
   * 行，婁金狗），這個時候時禽婁金狗出現了，我們開始往回數，午（第一位婁）
   * ，巳時（按28宿次序順行，胃土雉）ok！翻禽出來了，就是胃土雉。
   *
   * 再如2008年11月22日星期六，戊子年癸亥月丙寅日辛卯時
   * 二元甲子，日禽丙寅日胃土雉，時禽辛卯壁水狳，氣將奎木狼，我們根據起法，
   * 從時辰開始排列，卯時（第一位，氣將奎木狼），辰（按28星宿次序順行，婁）
   * 巳時（胃），午（昴），未（畢）。。。。。。午（第二十八位壁水狳），這個
   * 時候壁水狳出現了，我們開始往回數地支，午（第一位壁水狳），巳（按28宿次
   * 序順行，奎木狼），辰（婁金狗），卯（胃），寅（昴），丑（畢），子（觜）
   * ，亥（參），戌（井），酉（鬼），申（柳）。。。。。。卯（第二十八位，室
   * 火豬），翻禽出現了，就是室火豬。
   */
  @Test
  fun testOpponentSample1() {
    assertSame(胃, ILunarStationHourly.getOpponent(DayIndex(2, StemBranch.丙寅), 婁))
    assertSame(室, ILunarStationHourly.getOpponent(DayIndex(2, StemBranch.丙寅), 壁))
  }

  /**
   * 鍾義明《擇日精萃》 , page 573
   */
  @Test
  fun testOpponentSample2() {
    assertSame(角, ILunarStationHourly.getOpponent(DayIndex(7, StemBranch.癸卯), 心))
  }

  /**
   * 例一：二元甲子，日禽丙寅日胃土雉，時禽癸巳時婁金狗。氣將奎木狼，翻禽胃
   * 土雉
   * 巳時（婁金狗），午（按28宿次序，胃土雉），注意，翻禽出現了，我們開始逆
   * 推回去，午（胃），巳時（昴日雞），出來了，倒將是昴日雞。
   * 這個時間我們可以這樣羅列:
   *
   * 二元甲子，丙寅日，癸巳時
   * 氣將：奎木狼
   * 日禽：胃土雉
   * 時禽：婁金狗
   * 翻禽：胃土雉
   * 倒將：昴日雞
   *
   * 例二：二元甲子，日禽丙寅日胃土雉，時禽辛卯時壁水狳。氣將奎木狼，翻禽室
   * 火豬。
   *
   * 卯時(壁），辰（奎），巳（婁),午（胃）。。。。。。午（室），注意翻禽出
   * 現了，我們逆數地支，午（室），巳（順行28宿次序，壁），辰（奎），卯（胃
   * ）。。。。。。卯（危），倒將出現，為危月燕。
   *
   * 二元甲子，丙寅日，辛卯時
   * 氣將：奎木狼
   * 日禽：胃土雉
   * 時禽：壁水狳
   * 翻禽：室火豬
   * 倒將：危月燕
   */
  @Test
  fun testReversedSample1() {
    assertSame(昴, ILunarStationHourly.getReversed(DayIndex(2, StemBranch.丙寅), 婁))
    assertSame(危, ILunarStationHourly.getReversed(DayIndex(2, StemBranch.丙寅), 壁))
  }

  /**
   * 譬如上例
   * 二元甲子，丙寅日，癸巳時
   * 氣將：奎木狼
   * 日禽：胃土雉
   * 時禽：婁金狗
   * 翻禽：胃土雉
   * 倒將：昴日雞
   *
   * 時禽是婁金狗，那麼根據歌訣，「金牛頭」意思是時禽是金宿，那麼就用牛金牛
   * 開始數起，從哪個地支開始數呢？從寅數起（起活曜都從寅開始逆數）
   *
   * 寅（牛金牛，按28宿順行），丑（女），子（虛），亥（危），戌（室），酉（
   * 壁），申（奎），未（婁），時禽出現了，那麼我們開始從未（婁），順地支而
   * 數到時辰巳，未（婁），申（胃），酉（昴），戌（畢），亥（觜），子（參）
   * ，丑（井），寅（鬼），卯（柳），辰（星），巳時（張），出現了時，那麼張
   * 月鹿就是此時的活曜。
   */
  @Test
  fun testLive1Sample1() {
    assertSame(張, ILunarStationHourly.getLive1(婁, Branch.巳))
  }

  /**
   * 鍾義明《擇日精萃》 , page 575
   *
   * 民國83年農曆10月11日 午時 (陽曆 1994/11/13 )
   */
  @Test
  fun testLive1Sample2() {
    assertSame(危, ILunarStationHourly.getLive1(心, Branch.午))
  }

  /**
   * 二元甲子，丙寅日，癸巳時
   * 氣將：奎木狼
   * 日禽：胃土雉
   * 時禽：房日兔 （我副將）
   * 翻禽：胃土雉（彼正將）
   * 巳時為房日兔，按口訣，畢月烏加在寅上，逆行，則按二十八宿次序，觜火
   * 猴加丑，參水猿加子，井木犴加亥，鬼金羊加戌，柳土獐加酉，依次類推，房日
   * 兔加丑，然後順行，則心月狐加寅，尾火虎加卯，箕水豹加辰，斗木獬加巳，則
   * 活曜為斗木獬。（注意此處使用的時禽是不分七元的起法）
   */
  @Test
  fun testLive2Sample() {
    assertSame(斗, ILunarStationHourly.getLive2(房, Branch.巳))
  }
}
