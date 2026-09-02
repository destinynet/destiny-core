/**
 * Created by smallufo on 2025-08-08.
 */
package destiny.core.astrology

import destiny.core.EventRole
import destiny.core.Situation
import destiny.core.calendar.YearMonthRange
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.*

class AbstractEventTest {

  @Test
  fun testGroupAdjacentEvents_MonthOnly() {
    val events = listOf(
      MonthEvent(YearMonth.of(2018, 12), Situation.OTHERS, "事件A"),
      MonthEvent(YearMonth.of(2019, 2), Situation.OTHERS, "事件B"), // 與事件A鄰近 (因為 12月+1月+2月 連續)

      MonthEvent(YearMonth.of(2022, 5), Situation.OTHERS, "事件C"), // 獨立事件

      MonthEvent(YearMonth.of(2022, 11), Situation.OTHERS, "事件D"),
      MonthEvent(YearMonth.of(2022, 12), Situation.OTHERS, "事件E") // 與事件D鄰近
    )

    events.groupAdjacentEvents(extMonth = 1).also { groups ->
      assertEquals(3, groups.size)
      val expected = listOf(
        listOf(
          MonthEvent(YearMonth.of(2018, 12), Situation.OTHERS, "事件A"),
          MonthEvent(YearMonth.of(2019, 2), Situation.OTHERS, "事件B")
        ),
        listOf(MonthEvent(YearMonth.of(2022, 5), Situation.OTHERS, "事件C")),

        listOf(
          MonthEvent(YearMonth.of(2022, 11), Situation.OTHERS, "事件D"),
          MonthEvent(YearMonth.of(2022, 12), Situation.OTHERS, "事件E")
        )
      )
      assertEquals(expected, groups)
    }
  }

  /**
   * 混合 MonthEvent 和 DayEvent
   */
  @Test
  fun testGroupAdjacentEvents_MixedTypes() {
    val events = listOf(
      MonthEvent(YearMonth.of(2025, 1), Situation.OTHERS, "升職"),
      DayEvent(LocalDate.of(2025, 3, 15), Situation.OTHERS, "婚禮"), // 3月與1月鄰近 (extMonth=1)

      MonthEvent(YearMonth.of(2025, 7), Situation.OTHERS, "搬家") // 7月與3月不鄰近
    )

    val groups = events.groupAdjacentEvents(extMonth = 1)
    assertEquals(2, groups.size)
    val expected = listOf(
      listOf(
        MonthEvent(YearMonth.of(2025, 1), Situation.OTHERS, "升職"),
        DayEvent(LocalDate.of(2025, 3, 15), Situation.OTHERS, "婚禮")
      ),
      listOf(
        MonthEvent(YearMonth.of(2025, 7), Situation.OTHERS, "搬家")
      )
    )
    assertEquals(expected, groups)
  }

  /**
   * 只包含 DayEvent，並測試跨年度
   */
  @Test
  fun testGroupAdjacentEvents_DayOnly_AcrossYears() {
    val events = listOf(
      DayEvent(LocalDate.of(2023, 1, 10), Situation.OTHERS, "A公司面試"),
      DayEvent(LocalDate.of(2023, 3, 5), Situation.OTHERS, "拿到A公司Offer"), // 3月與1月鄰近
      DayEvent(LocalDate.of(2023, 12, 25), Situation.OTHERS, "聖誕節車禍"),
      DayEvent(LocalDate.of(2024, 1, 30), Situation.OTHERS, "康復出院")  // 隔年1月與12月鄰近
    )

    val groups = events.groupAdjacentEvents(extMonth = 1)
    assertEquals(2, groups.size)
    val expected = listOf(
      listOf(
        DayEvent(LocalDate.of(2023, 1, 10), Situation.OTHERS, "A公司面試"),
        DayEvent(LocalDate.of(2023, 3, 5), Situation.OTHERS, "拿到A公司Offer")
      ),
      listOf(
        DayEvent(LocalDate.of(2023, 12, 25), Situation.OTHERS, "聖誕節車禍"),
        DayEvent(LocalDate.of(2024, 1, 30), Situation.OTHERS, "康復出院")
      )
    )
    assertEquals(expected, groups)
  }


  /**
   * 新測試：邊界條件 - 空列表
   */
  @Test
  fun testGroupAdjacentEvents_EmptyList() {
    val events = emptyList<AbstractEvent>()
    val groups = events.groupAdjacentEvents()
    assertEquals(1, groups.size)
    assertTrue { groups[0].isEmpty() }
  }

  /**
   * 新測試：邊界條件 - 單一事件
   */
  @Test
  fun testGroupAdjacentEvents_SingleEvent() {
    val events = listOf(MonthEvent(YearMonth.of(2022, 5), Situation.OTHERS, "單一事件"))
    val groups = events.groupAdjacentEvents()
    assertEquals(1, groups.size)
    assertEquals(listOf(events), groups)
  }

  /**
   * 新測試：測試 extMonth = 0 的情況
   * 只有在同一個月或相鄰月份的事件才會被分在同一組
   */
  @Test
  fun testGroupAdjacentEvents_ExtMonthZero() {
    val events = listOf(
      MonthEvent(YearMonth.of(2022, 1), Situation.OTHERS, "事件A"),
      MonthEvent(YearMonth.of(2022, 2), Situation.OTHERS, "事件B"), // 與A相鄰
      MonthEvent(YearMonth.of(2022, 4), Situation.OTHERS, "事件C")  // 與B不相鄰 (因為2月+0+4月不連續)
    )

    // extMonth = 0, range of 2022-02 is [2022-02, 2022-02]
    // range of 2022-04 is [2022-04, 2022-04]
    // 2022-04 is not after 2022-02.plus(1) -> 2022-03 , it's after. So new group
    val groups = events.groupAdjacentEvents(extMonth = 0)
    assertEquals(2, groups.size)
    val expected = listOf(
      listOf(
        MonthEvent(YearMonth.of(2022, 1), Situation.OTHERS, "事件A"),
        MonthEvent(YearMonth.of(2022, 2), Situation.OTHERS, "事件B")
      ),
      listOf(
        MonthEvent(YearMonth.of(2022, 4), Situation.OTHERS, "事件C")
      )
    )
    assertEquals(expected, groups)
  }

  /**
   * 新測試：測試 extMonth = 2 的情況
   * 更大的 extMonth 會讓更多事件被分在同一組
   */
  @Test
  fun testGroupAdjacentEvents_ExtMonthTwo() {
    val events = listOf(
      MonthEvent(YearMonth.of(2022, 1), Situation.OTHERS, "事件A"), // range: [2021-11, 2022-03]
      MonthEvent(YearMonth.of(2022, 4), Situation.OTHERS, "事件B"), // range: [2022-02, 2022-06], 與A重疊
      MonthEvent(YearMonth.of(2022, 8), Situation.OTHERS, "事件C")  // range: [2022-06, 2022-10], 與B重疊
    )
    val groups = events.groupAdjacentEvents(extMonth = 2)
    assertEquals(1, groups.size)
    assertEquals(listOf(events), groups)
  }

  @Nested
  inner class PeriodEventTest {

    private val json = Json

    /** 有起爆日 → 排得出日盤；沒有 → 只做月級掃描。 */
    @Test
    fun grainDependsOnIgnition() {
      val withIgnition = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = LocalDate.of(2026, 7, 31),
        ignition = LocalDate.of(2026, 6, 20),
        situation = Situation.REPUTATION_CRISIS, details = "連環爆"
      )
      assertEquals(EventGrain.DAY, withIgnition.grain())
      assertTrue(withIgnition.grain().canDayLevelTransit)
      assertEquals(BirthDataGrain.DAY, withIgnition.grain().chartGrain)
      assertEquals(LocalDate.of(2026, 6, 20).atTime(12, 0), withIgnition.chartTime())

      val fuzzy = withIgnition.copy(ignition = null)
      assertEquals(EventGrain.MONTH, fuzzy.grain())
      assertFalse(fuzzy.grain().canDayLevelTransit)
      assertNull(fuzzy.grain().chartGrain)
      assertNull(fuzzy.chartTime())
    }

    @Test
    fun yearMonthRange_spansToEnd() {
      val e = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = LocalDate.of(2026, 7, 31),
        situation = Situation.REPUTATION_CRISIS, details = "連環爆"
      )
      assertEquals(YearMonth.of(2026, 6), e.yearMonth())          // 代表月 = 起始月
      assertEquals(YearMonthRange(YearMonth.of(2026, 6), YearMonth.of(2026, 7)), e.yearMonthRange())
    }

    /** 進行中者刻意不外推到「現在」—— 資料模型不該知道 today。 */
    @Test
    fun ongoing_rangeStopsAtStart() {
      val e = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = null,
        situation = Situation.REPUTATION_CRISIS, details = "尚未止息"
      )
      assertTrue(e.ongoing())
      assertEquals(YearMonthRange(YearMonth.of(2026, 6), YearMonth.of(2026, 6)), e.yearMonthRange())
    }

    /** [effectiveYearMonthRange]：進行中者由呼叫端以 viewMonth 提供上界；已結束者與點事件不受影響。 */
    @Test
    fun effectiveRange_extendsOngoingToUpperBound() {
      val ongoing = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = null,
        situation = Situation.REPUTATION_CRISIS, details = "尚未止息"
      )
      val viewMonth = YearMonth.of(2026, 9)
      assertEquals(YearMonthRange(YearMonth.of(2026, 6), viewMonth), ongoing.effectiveYearMonthRange(viewMonth))
      // 上界為 null → 原樣（資料模型不知道 today 的預設行為）
      assertEquals(ongoing.yearMonthRange(), ongoing.effectiveYearMonthRange(null))
      // 上界早於（或等於）既有終點 → 不縮短
      assertEquals(ongoing.yearMonthRange(), ongoing.effectiveYearMonthRange(YearMonth.of(2026, 5)))

      val closed = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = LocalDate.of(2026, 7, 31),
        situation = Situation.REPUTATION_CRISIS, details = "已止息"
      )
      assertEquals(closed.yearMonthRange(), closed.effectiveYearMonthRange(viewMonth), "已結束者不外推")

      val point = DayEvent(LocalDate.of(2026, 6, 20), Situation.OTHERS, "點事件")
      assertEquals(point.yearMonthRange(), point.effectiveYearMonthRange(viewMonth), "點事件不受影響")
    }

    /** 點事件退化成 start == endInclusive，故舊行為完全不變。 */
    @Test
    fun pointEvents_degenerateToSingleMonth() {
      assertEquals(
        YearMonthRange(YearMonth.of(2025, 3), YearMonth.of(2025, 3)),
        DayEvent(LocalDate.of(2025, 3, 15), Situation.OTHERS, "婚禮").yearMonthRange()
      )
      assertEquals(
        YearMonthRange(YearMonth.of(2025, 3), YearMonth.of(2025, 3)),
        MonthEvent(YearMonth.of(2025, 3), Situation.OTHERS, "婚禮").yearMonthRange()
      )
    }

    /**
     * 分群的關鍵差異：長跨度事件會把落在它**尾段**的事件拉進同一群。
     *
     * 只看代表月（2026-01）的舊邏輯會判成兩群 —— 7 月那筆離 1 月太遠。
     */
    @Test
    fun groupAdjacent_periodPullsInEventsInsideItsTail() {
      val period = PeriodEvent(
        from = LocalDate.of(2026, 1, 5), to = LocalDate.of(2026, 8, 20),
        situation = Situation.LEGAL_PROCEEDING_START, details = "纏訟"
      )
      val inTail = DayEvent(LocalDate.of(2026, 7, 10), Situation.OTHERS, "開庭")

      val groups = listOf(period, inTail).groupAdjacentEvents(extMonth = 1)
      assertEquals(1, groups.size)
      assertEquals(listOf(period, inTail), groups[0])
    }

    /**
     * 進行中的事件把「它燒到的月份裡發生的其他事件」拉進同一群 ——
     * 六月起延燒未止的危機 + 十一月的獨立事件，在 viewMonth = 十二月看盤時屬同一段敘事。
     * 不給上界（舊行為）則判成兩群（與 [groupAdjacent_farAwayEventStaysSeparate] 同距離）。
     */
    @Test
    fun groupAdjacent_ongoingPullsInLaterEventsUpToUpperBound() {
      val ongoing = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = null,
        situation = Situation.REPUTATION_CRISIS, details = "延燒未止"
      )
      val later = DayEvent(LocalDate.of(2026, 11, 10), Situation.OTHERS, "危機期間的另一事件")

      val without = listOf(ongoing, later).groupAdjacentEvents(extMonth = 1)
      assertEquals(2, without.size, "無上界 → 進行中事件停在起始月，兩群")

      val with = listOf(ongoing, later).groupAdjacentEvents(extMonth = 1, ongoingUpperBound = YearMonth.of(2026, 12))
      assertEquals(1, with.size, "有上界 → 延燒區間涵蓋十一月，同一群")
      assertEquals(listOf(ongoing, later), with[0])
    }

    /** 但真正離群的仍該分開 —— 別讓「涵蓋」變成無條件合併。 */
    @Test
    fun groupAdjacent_farAwayEventStaysSeparate() {
      val period = PeriodEvent(
        from = LocalDate.of(2026, 1, 5), to = LocalDate.of(2026, 3, 20),
        situation = Situation.LEGAL_PROCEEDING_START, details = "纏訟"
      )
      val far = DayEvent(LocalDate.of(2026, 11, 10), Situation.OTHERS, "無關的事")

      val groups = listOf(period, far).groupAdjacentEvents(extMonth = 1)
      assertEquals(2, groups.size)
      assertEquals(listOf(period), groups[0])
      assertEquals(listOf(far), groups[1])
    }

    @Test
    fun serializationRoundTrip() {
      val full: AbstractEvent = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = LocalDate.of(2026, 7, 31),
        ignition = LocalDate.of(2026, 6, 22),
        situation = Situation.REPUTATION_CRISIS, details = "連環爆", sentiment = EventSentiment.NEGATIVE
      )
      assertEquals(full, json.decodeFromString<AbstractEvent>(json.encodeToString(full)))

      val ongoing: AbstractEvent = PeriodEvent(
        from = LocalDate.of(2026, 6, 20),
        situation = Situation.REPUTATION_CRISIS, details = "尚未止息"
      )
      assertEquals(ongoing, json.decodeFromString<AbstractEvent>(json.encodeToString(ongoing)))
    }

    /**
     * 進行中者的序列化出口必須附加顯式 `"ongoing": true` ——
     * 「to 欄位缺席」對 LLM 而言與「已結束」無法區分。
     * 輸入端寬容剝除此標記（roundtrip 對稱由 [serializationRoundTrip] 一併驗證）。
     */
    @Test
    fun ongoingMarker_emittedOnExportStrippedOnImport() {
      val ongoing: AbstractEvent = PeriodEvent(
        from = LocalDate.of(2026, 6, 20),
        situation = Situation.REPUTATION_CRISIS, details = "尚未止息"
      )
      val encoded = json.encodeToString(ongoing)
      assertTrue("\"ongoing\":true" in encoded.replace(" ", ""), "出口須含 ongoing 標記：$encoded")

      val closed: AbstractEvent = PeriodEvent(
        from = LocalDate.of(2026, 6, 20), to = LocalDate.of(2026, 7, 31),
        situation = Situation.REPUTATION_CRISIS, details = "已止息"
      )
      assertFalse("ongoing" in json.encodeToString(closed), "已結束者不得有標記")

      // 輸入端剝除：帶標記的 JSON（例如曾被序列化的素材）能正常解析
      val parsed = json.decodeFromString<AbstractEvent>(
        """{"from":"2026-06-20","ongoing":true,"situation":"REPUTATION_CRISIS","details":"尚未止息"}"""
      )
      assertEquals(ongoing, parsed)
    }

    /** 有 `from` 就走 PeriodEvent；沒有才回頭看 `date` 的字面格式。點事件的分派不受影響。 */
    @Test
    fun deserialize_discriminatesByFromKey() {
      val period = json.decodeFromString<AbstractEvent>(
        """{"from":"2026-06-20","to":"2026-07-31","situation":"REPUTATION_CRISIS","details":"連環爆"}"""
      )
      assertTrue(period is PeriodEvent)
      assertEquals(EventGrain.MONTH, period.grain())

      assertTrue(json.decodeFromString<AbstractEvent>("""{"date":"2026-06","situation":"OTHERS","details":"x"}""") is MonthEvent)
      assertTrue(json.decodeFromString<AbstractEvent>("""{"date":"2026-06-20","situation":"OTHERS","details":"x"}""") is DayEvent)
      assertTrue(json.decodeFromString<AbstractEvent>("""{"date":"2026-06-20T14:30","situation":"OTHERS","details":"x"}""") is MinuteEvent)
    }

    /** from 與 date 並存代表上游搞混了，寧可炸掉也不要沉默地丟掉一半資訊。 */
    @Test
    fun deserialize_rejectsBothFromAndDate() {
      assertFailsWith<IllegalArgumentException> {
        json.decodeFromString<AbstractEvent>(
          """{"from":"2026-06-20","date":"2026-06-20","situation":"OTHERS","details":"x"}"""
        )
      }
    }

    @Test
    fun rejectsInvalidBounds() {
      assertFailsWith<IllegalArgumentException> {
        PeriodEvent(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 6, 1), null, Situation.OTHERS, "to 早於 from")
      }
      assertFailsWith<IllegalArgumentException> {
        PeriodEvent(
          LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1), LocalDate.of(2026, 5, 1),
          Situation.OTHERS, "ignition 在區間之前"
        )
      }
      assertFailsWith<IllegalArgumentException> {
        PeriodEvent(
          LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 1),
          Situation.OTHERS, "ignition 在區間之後"
        )
      }
    }
  }

  /**
   * 主詞（[AbstractEvent.role]）—— `null` **只有一個語意：未答**。
   *
   * 型別層不再提供預設（[Situation.roles] 只列舉可能，不猜最常見），
   * 故單值 situation 由 [AbstractEvent.effectiveRole] 從 `roles` 補，
   * 兩值 situation 未答就是真的不知道。
   */
  @Nested
  inner class RoleTest {

    /** 合法組合可建構 */
    @Test
    fun `role 落在 situation_roles 之內`() {
      val e = DayEvent(LocalDate.of(2020, 3, 3), Situation.VIOLENCE, "x", role = EventRole.INITIATOR)
      assertEquals(EventRole.INITIATOR, e.role)
      assertNull(e.roleConflict())
      assertEquals(EventRole.INITIATOR, e.effectiveRole())
    }

    /**
     * 非法組合仍會炸 —— ⚠️ 守衛**沒有消失，只是退化**：
     * 從「型別先驗 vs 逐筆申報可能互相矛盾」的跨層一致性檢查，
     * 降為「這個值在不在值域內」的成員檢查。
     *
     * 四個子型別都要炸（判斷邏輯只有一份，各自呼叫）。
     */
    @Test
    fun `role 不在 situation_roles 之內時炸掉`() {
      assertFailsWith<IllegalArgumentException> {
        DayEvent(LocalDate.of(2020, 3, 3), Situation.MAJOR_ILLNESS, "x", role = EventRole.INITIATOR)
      }
      assertFailsWith<IllegalArgumentException> {
        MonthEvent(YearMonth.of(2020, 3), Situation.ENTREPRENEURSHIP, "創業", null, EventRole.RECIPIENT)
      }
      assertFailsWith<IllegalArgumentException> {
        MinuteEvent(java.time.LocalDateTime.of(2020, 3, 3, 14, 30), Situation.MAJOR_ILLNESS, "確診", null, EventRole.INITIATOR)
      }
      assertFailsWith<IllegalArgumentException> {
        PeriodEvent(LocalDate.of(2020, 3, 1), null, null, Situation.MAJOR_ILLNESS, "療程", null, EventRole.INITIATOR)
      }
    }

    /** null ＝ 未答，唯一語意。單值 situation 的主詞由 situation 自己給。 */
    @Test
    fun `null 是未答，effectiveRole 由 situation 補`() {
      assertNull(DayEvent(LocalDate.of(2020, 3, 3), Situation.VIOLENCE, "x").role)
      assertEquals(
        EventRole.RECIPIENT,
        DayEvent(LocalDate.of(2020, 3, 3), Situation.MAJOR_ILLNESS, "x").effectiveRole()
      )
      assertEquals(
        EventRole.INITIATOR,
        DayEvent(LocalDate.of(2020, 3, 3), Situation.ENTREPRENEURSHIP, "x").effectiveRole()
      )
      assertNull(DayEvent(LocalDate.of(2020, 3, 3), Situation.VIOLENCE, "x").effectiveRole())
    }

    /**
     * ⭐ 手寫 descriptor 的欄位名稱 —— [AbstractEventSerializer.descriptor] 是**手寫**的，
     * 欄位改名時編譯器不會提醒。直接斷言 element 名稱，漏改就直接紅，
     * 而不是靠 roundtrip 間接發現。
     */
    @Test
    fun `descriptor 的 element 名稱`() {
      val names = AbstractEventSerializer.descriptor.elementNames.toList()
      assertEquals(
        listOf("date", "from", "to", "ignition", "ongoing", "situation", "details", "sentiment", "role"),
        names
      )
      assertFalse("eventType" in names)
      assertFalse("agency" in names)
    }

    @Test
    fun `序列化 roundtrip 帶得動 role`() {
      val e = DayEvent(LocalDate.of(2020, 3, 3), Situation.RELATIONSHIP_START, "在一起", EventSentiment.POSITIVE, EventRole.RECIPIENT)
      val json = Json.encodeToString(AbstractEventSerializer, e as AbstractEvent)
      assertTrue(json.contains("\"role\""), json)
      assertTrue(json.contains("\"situation\""), json)
      assertEquals(e, Json.decodeFromString(AbstractEventSerializer, json))
    }

    /** 沒有 role 的舊 JSON 必須照樣讀得進來（欄位是選填的，既有素材多半沒有） */
    @Test
    fun `無 role 的 JSON 仍可反序列化`() {
      val old = """{"date":"2020-03-03","situation":"MAJOR_ILLNESS","details":"確診"}"""
      val e = Json.decodeFromString(AbstractEventSerializer, old)
      assertNull(e.role)
      assertEquals(EventRole.RECIPIENT, e.effectiveRole())
    }
  }
}
