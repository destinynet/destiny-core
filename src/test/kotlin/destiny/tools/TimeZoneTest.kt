/** 2009/10/20 下午10:53:51 by smallufo  */
package destiny.tools

import java.time.*
import java.time.zone.ZoneOffsetTransition
import java.time.zone.ZoneRulesException
import java.util.*
import kotlin.test.*

/**
 * 兩件事：
 *
 *  1. 舊 [TimeZone] 與新 [ZoneId] 的**互通性邊界** —— 哪些能無損互轉、哪些不能、為什麼。
 *  2. 本專案排盤會碰到的幾個**歷史時區轉換**（台灣日治末期、台灣 1974-75 日光節約、新加坡 1982 併入東八區），
 *     因為出生時間換算 GMT 時，這幾段是最容易算錯的區間。
 *
 * 斷言一律建立在 [java.time.zone.ZoneRules] 的**轉換點**上，而非寫死 epoch 毫秒 ——
 * 舊版測試把新加坡的轉換點寫成 `378664200000L`，而 tzdb 後來把該轉換從
 * 「本地 1982-01-01 00:00 (+07:30)」修正為「本地 1981-12-31 23:30 (+07:30)」，
 * 那個魔術數字就此指向錯誤的時刻（且因為舊測試只有 log 沒有斷言，沒人發現）。
 *
 * 本檔撰寫時的環境：JDK 21 / tzdb 2026a，`ZoneId` 604 個、`TimeZone` 632 個。
 * 數量本身會隨 tzdata 增減，所以不斷言數量，只斷言「關係」。
 */
class TimeZoneTest {

  /**
   * 台北與上海現今同為東八區，但**不是**同一個時區：
   * 台灣 1974-75 實施日光節約時，上海並未實施。
   */
  @Test
  fun testNotEquals() {
    val tp = TimeZone.getTimeZone("Asia/Taipei")
    val sh = TimeZone.getTimeZone("Asia/Shanghai")

    assertNotEquals(tp, sh)
    assertFalse(tp.hasSameRules(sh))

    val summer1975 = Instant.parse("1975-06-01T00:00:00Z")
    assertEquals(ZoneOffset.ofHours(9), TAIPEI.rules.getOffset(summer1975))
    assertEquals(ZoneOffset.ofHours(8), ZoneId.of("Asia/Shanghai").rules.getOffset(summer1975))
  }

  /**
   * [ZoneId] → [TimeZone] 是全射：每個 zoneId 都轉得到同名的 TimeZone。
   *
   * 這個斷言有實質意義 —— [TimeZone.getTimeZone] 對不認得的 id **不會拋例外，而是默默回傳 GMT**，
   * 所以「id 相同」才是唯一能證明沒被吞掉的檢查。
   */
  @Test
  fun `每個 ZoneId 都能無損轉成同名的 TimeZone`() {
    val zoneIds = ZoneId.getAvailableZoneIds()
    assertTrue(zoneIds.size > 500, "zoneId 數量異常偏少：${zoneIds.size}")

    zoneIds.forEach { id ->
      assertEquals(id, TimeZone.getTimeZone(id).id, "ZoneId '$id' 轉 TimeZone 後變了樣（很可能被吞成 GMT）")
    }
  }

  /**
   * 反方向則**不是**全射：[TimeZone] 比 [ZoneId] 多出來的那些 id，
   * 恰好就是 [ZoneId.SHORT_IDS] 那批三字母縮寫（CTT、PST、EST……）。
   *
   * 原因見 [ZoneId.of] 說明：三字母縮寫並不唯一（例如 CST 可指美國中部或中國標準時），
   * 故新 API 拒收。但**並非所有三字母 id 都不行** —— GMT、UTC 不在 SHORT_IDS 裡，仍可直接轉。
   *
   * 參照 http://stackoverflow.com/a/41683097/298430
   */
  @Test
  fun `轉不成 ZoneId 的 TimeZone id，恰好是 SHORT_IDS 那批三字母縮寫`() {
    val unconvertible: Set<String> = TimeZone.getAvailableIDs().filter { id ->
      try {
        ZoneId.of(id)
        false
      } catch (e: DateTimeException) {
        true
      }
    }.toSet()

    assertEquals(ZoneId.SHORT_IDS.keys, unconvertible)
    assertTrue(unconvertible.all { it.length == 3 }, "SHORT_IDS 應清一色是三字母：$unconvertible")

    assertEquals("GMT", ZoneId.of("GMT").id)
    assertEquals("UTC", ZoneId.of("UTC").id)
  }

  /**
   * 三字母縮寫透過舊 API 進來時，會依 [ZoneId.SHORT_IDS] 的對應表落地成具名時區。
   */
  @Test
  fun `TimeZone PST 經 SHORT_IDS 對應到美西時區`() {
    assertEquals("America/Los_Angeles", ZoneId.SHORT_IDS["PST"])
    assertEquals(ZoneId.of("America/Los_Angeles"), TimeZone.getTimeZone("PST").toZoneId())
  }

  /**
   * `TimeZone("EST").toZoneId()` 的**字面值**隨 JDK／tzdata 版本擺盪：
   * 有的版本給 `-05:00`（[ZoneId.SHORT_IDS] 的字面對應），有的給 `America/Panama`（tzdb 的等價具名時區）。
   *
   * 兩者都對，所以舊版測試寫死 `America/Panama`，換一套 JDK 就誤報一次
   * （IntelliJ 與 Maven 若用不同 JDK，就會「IDE 綠、CLI 紅」），最後只好 `@Ignore` 掉。
   *
   * 這裡改成斷言**不隨版本擺盪的部分**：偏移固定 -05:00、且全年無日光節約。
   * 兩種落地方式都滿足，測試因此重新活過來。
   */
  @Test
  fun `EST 只能經由 TimeZone 取得，且恆為 -05-00 而無日光節約`() {
    assertFailsWith<ZoneRulesException> { ZoneId.of("EST") }

    val zoneId = TimeZone.getTimeZone("EST").toZoneId()
    // 挑北半球盛夏：若這個時區帶有 DST 規則，這裡就會露餡
    val midSummer = Instant.parse("2026-07-01T00:00:00Z")
    assertEquals(ZoneOffset.ofHours(-5), zoneId.rules.getOffset(midSummer))
    assertEquals(Duration.ZERO, zoneId.rules.getDaylightSavings(midSummer))
  }

  /**
   * [TimeZone.getAvailableIDs] 帶偏移量參數時，比對的是 **rawOffset（標準時偏移，不含 DST）**。
   * 撈出來的東八區 id 裡混著三字母縮寫（CTT），那些照樣轉不成 ZoneId；其餘都能原名轉換。
   */
  @Test
  fun `以 rawOffset 篩出的東八區 id，除三字母縮寫外都能原名轉成 ZoneId`() {
    val eastEight = 8 * 60 * 60 * 1000
    val ids = TimeZone.getAvailableIDs(eastEight)

    assertTrue("Asia/Taipei" in ids)
    assertTrue("CTT" in ids, "三字母縮寫確實混在其中")

    ids.forEach { id ->
      assertEquals(eastEight, TimeZone.getTimeZone(id).rawOffset, "'$id' 的 rawOffset 應為東八區")
      if (id !in ZoneId.SHORT_IDS.keys) {
        assertEquals(id, ZoneId.of(id).id, "'$id' 應能原名轉成 ZoneId")
      }
    }
  }

  /**
   * 新加坡於本地 1981-12-31 23:30 撥快 30 分鐘，從 +07:30 併入東八區。
   *
   * 注意這是**標準時的永久調整**，不是日光節約 —— `inDaylightTime` 兩側都是 false。
   * 1982 之前出生於新加坡者，換算 GMT 要用 +07:30。
   */
  @Test
  fun `新加坡 1982 年初從 +07-30 併入東八區`() {
    val transition = SINGAPORE.transitionAfter("1981-01-01T00:00:00Z")

    assertTrue(transition.isGap, "撥快 30 分鐘，本地時間出現空缺")
    assertEquals(LocalDateTime.of(1981, 12, 31, 23, 30), transition.dateTimeBefore)
    assertEquals(LocalDateTime.of(1982, 1, 1, 0, 0), transition.dateTimeAfter)
    assertEquals(ZoneOffset.ofHoursMinutes(7, 30), transition.offsetBefore)
    assertEquals(ZoneOffset.ofHours(8), transition.offsetAfter)

    assertEquals(Duration.ZERO, SINGAPORE.rules.getDaylightSavings(transition.instant.minusMillis(1)))
    assertEquals(Duration.ZERO, SINGAPORE.rules.getDaylightSavings(transition.instant))
  }

  /**
   * 台灣日治末期（1937-10-01 起）併入日本時間東九區，直到 1945-09-21 才回到東八區。
   *
   * **這段在 tzdb 裡是「標準時」，不是日光節約**（`getDaylightSavings` 為零）——
   * 舊版測試註解宣稱「台灣於 1945/5/1 凌晨 0 時進入 DST」，與現行 tzdb 不符；
   * 該測試從頭到尾只有 println 沒有斷言，所以錯了十幾年也沒人發現。
   */
  @Test
  fun `台灣日治末期為東九區的標準時，而非日光節約`() {
    val enter = TAIPEI.transitionAfter("1937-01-01T00:00:00Z")
    assertTrue(enter.isGap)
    assertEquals(LocalDateTime.of(1937, 10, 1, 0, 0), enter.dateTimeBefore)
    assertEquals(ZoneOffset.ofHours(8), enter.offsetBefore)
    assertEquals(ZoneOffset.ofHours(9), enter.offsetAfter)

    val leave = TAIPEI.transitionAfter("1945-01-01T00:00:00Z")
    assertTrue(leave.isOverlap)
    assertEquals(LocalDateTime.of(1945, 9, 21, 1, 0), leave.dateTimeBefore)
    assertEquals(LocalDateTime.of(1945, 9, 21, 0, 0), leave.dateTimeAfter)
    assertEquals(ZoneOffset.ofHours(9), leave.offsetBefore)
    assertEquals(ZoneOffset.ofHours(8), leave.offsetAfter)

    val duringWar = LocalDateTime.of(1945, 5, 1, 0, 30).toInstant(ZoneOffset.ofHours(9))
    assertEquals(Duration.ZERO, TAIPEI.rules.getDaylightSavings(duringWar))
    assertFalse(TimeZone.getTimeZone(TAIPEI).inDaylightTime(Date.from(duringWar)))
  }

  /**
   * 民國 63、64 年（西元 1974-1975）日光節約時間：4月1日至9月30日。
   *
   * 結束那一刻值得留意：轉換點的**夏令**表述是 10/01 00:00，撥慢一小時後落在**標準時**的 09/30 23:00。
   * 舊版測試對著 log 裡的 23:00 寫下「有點詭異，為什麼晚上 23 點就結束 DST??」—— 答案就在這裡，
   * 同一瞬間的兩種寫法而已（[ZoneOffsetTransition.getDateTimeBefore] vs [ZoneOffsetTransition.getDateTimeAfter]）。
   */
  @Test
  fun `台灣 1974 與 1975 年的日光節約時間為 4月1日至9月30日`() {
    val tz = TimeZone.getTimeZone(TAIPEI)

    for (year in 1974..1975) {
      val start = TAIPEI.transitionAfter("$year-01-01T00:00:00Z")
      assertTrue(start.isGap, "$year 進入日光節約，本地時間出現空缺")
      assertEquals(LocalDateTime.of(year, 4, 1, 0, 0), start.dateTimeBefore)
      assertEquals(LocalDateTime.of(year, 4, 1, 1, 0), start.dateTimeAfter, "$year 4/1 零時撥快一小時")
      assertEquals(ZoneOffset.ofHours(8), start.offsetBefore)
      assertEquals(ZoneOffset.ofHours(9), start.offsetAfter)

      val end = TAIPEI.transitionAfter("$year-07-01T00:00:00Z")
      assertTrue(end.isOverlap, "$year 結束日光節約，本地時間重複一小時")
      assertEquals(LocalDateTime.of(year, 10, 1, 0, 0), end.dateTimeBefore, "夏令表述")
      assertEquals(LocalDateTime.of(year, 9, 30, 23, 0), end.dateTimeAfter, "同一瞬間的標準時表述")
      assertEquals(ZoneOffset.ofHours(9), end.offsetBefore)
      assertEquals(ZoneOffset.ofHours(8), end.offsetAfter)

      assertEquals(Duration.ZERO, TAIPEI.rules.getDaylightSavings(start.instant.minusMillis(1)))
      assertEquals(Duration.ofHours(1), TAIPEI.rules.getDaylightSavings(start.instant))
      assertEquals(Duration.ofHours(1), TAIPEI.rules.getDaylightSavings(end.instant.minusMillis(1)))
      assertEquals(Duration.ZERO, TAIPEI.rules.getDaylightSavings(end.instant))

      // 舊 API 的說法一致；rawOffset 恆為標準時的東八區，不含 DST
      assertTrue(tz.inDaylightTime(Date.from(start.instant)))
      assertFalse(tz.inDaylightTime(Date.from(end.instant)))
      assertEquals(8 * 60 * 60 * 1000, tz.rawOffset)
    }
  }

  companion object {
    private val TAIPEI: ZoneId = ZoneId.of("Asia/Taipei")
    private val SINGAPORE: ZoneId = ZoneId.of("Asia/Singapore")

    /** 取得 [utcInstant] 之後的第一個時區轉換點 */
    private fun ZoneId.transitionAfter(utcInstant: String): ZoneOffsetTransition {
      return rules.nextTransition(Instant.parse(utcInstant)) ?: fail("$id 在 $utcInstant 之後找不到時區轉換")
    }
  }
}
