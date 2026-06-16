/**
 * Created by smallufo on 2024-07-13.
 */
package destiny.tools.ai.model

import destiny.core.EnumTest
import destiny.tools.KotlinLogging
import destiny.tools.parseJsonToMap
import org.junit.jupiter.api.Nested
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FengShuiDomainTest : EnumTest()  {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testString() {
    testEnums(FengShuiDomain::class, locales = listOf(Locale.TAIWAN, Locale.ENGLISH, Locale.JAPANESE))
  }

  @Nested
  inner class ParseStringToMap {

    @Test
    fun invalid() {
      val json = """
        invalid json
      """.trimIndent()

      val domainMap = parseJsonToMap<FengShuiDomain>(json)
      assertTrue(domainMap.isEmpty())
    }

    @Test
    fun complete() {
      val raw = """
        {
          "ENVIRONMENT": "這間建築物位於新竹市竹北區光明六路東一段附近,位置相當優越。此地地理環境優美,四面環山,附近有許多景觀優美的公園,如東興圳公園、水圳森林公園、興隆公園自然森林遊戲場等,生活機能相當完善。此外,周圍還有遠東百貨、新竹縣政府等大型設施,娛樂與生活機能都非常便利。總的來說,這個地點非常適合居住,擁有優質的生活環境。",
          "FOOD": "此地附近有眾多餐廳及速食店,包括星巴克、麥當勞、陶板屋、森森燒肉、老井極上燒肉等,外食選擇相當豐富。此外,也有許多特色小店,如煉瓦良茶、先喝道等,滿足各種美食需求。附近也有全家、7-ELEVEN等生活機能店,可方便購買日常所需。",
          "RECREATION": "此地周遭有遠東百貨、Broadway cinemas等大型商業設施,提供多樣的娛樂選擇。此外,附近還有不少健身中心,如KGYM健身房、SoFit微．健身等,滿足了居民的健身需求。園區內也有許多親子遊憩設施,如魔豆歷險公園、水圳森林公園等,營造了良好的休閒環境。",
          "TRANSPORTATION": "此地位於新竹高鐵站附近,交通相當便利。不僅有高鐵可搭乘,附近也有多條公車路線通過,可以快速抵達市區中心。此外,機場距離也不算太遠,生活上相當便利。",
          "EDUCATION": "此地周圍有多所學校,包括十興國小、勝利國中等,提供良好的教育資源。同時,附近也有國立陽明交通大學和國立清華大學,為居民帶來優秀的學習及研究資源。此外,也有多家圖書館,如新竹縣圖書館等,供居民閱讀學習。",
          "MEDICAL": "此地附近有東元綜合醫院、新竹臺大分院生醫醫院等大型醫療院所,醫療資源相當充足。此外,也有不少專科診所,如皮膚科、牙科等,滿足了居民的各類就醫需求。",
          "NIMBY": "此地周遭並無明顯的嫌惡設施,並未發現有殯儀館、焚化廠、監獄或是發電廠等環境污染源。整體而言,此地的居住環境相當優良。",
          "CULTURE": "此地附近有不少文化資源,包括新竹縣政府、新竹地方法院等重要行政機構,以及國立陽明交通大學和國立清華大學等知名學府。同時,也有一些歷史古蹟和文化場所,如頭前溪河濱公園、水圳森林公園等,滿足了居民的文化需求。",
          "CONCLUSION": "綜合以上分析,這個地點非常適合作為居住場所。它擁有優美的自然環境,生活機能完善,交通便捷,教育及醫療資源充足,也沒有明顯的嫌惡設施。此外,還有不少文化設施,滿足了居民的多方面需求。因此,這個地點非常適合作為居住、養老或投資的選擇。"
        }
      """.trimIndent()
      val domainMap = parseJsonToMap<FengShuiDomain>(raw)
      println(domainMap)
      assertEquals(FengShuiDomain.entries.size, domainMap.size)
    }

    @Test
    fun incomplete() {
      val raw = """
        {
          "ENVIRONMENT": "這間建築物位於新竹市竹北區光明六路東一段附近,位置相當優越。此地地理環境優美,四面環山,附近有許多景觀優美的公園,如東興圳公園、水圳森林公園、興隆公園自然森林遊戲場等,生活機能相當完善。此外,周圍還有遠東百貨、新竹縣政府等大型設施,娛樂與生活機能都非常便利。總的來說,這個地點非常適合居住,擁有優質的生活環境。",
          "CONCLUSION": "綜合以上分析,這個地點非常適合作為居住場所。它擁有優美的自然環境,生活機能完善,交通便捷,教育及醫療資源充足,也沒有明顯的嫌惡設施。此外,還有不少文化設施,滿足了居民的多方面需求。因此,這個地點非常適合作為居住、養老或投資的選擇。"
        }
      """.trimIndent()
      val domainMap = parseJsonToMap<FengShuiDomain>(raw)
      assertEquals(2, domainMap.size)
    }

    @Test
    fun contaminated() {
      val raw = """
        {
          "ENVIRONMENT": "這間建築物位於新竹市竹北區光明六路東一段附近,位置相當優越。此地地理環境優美,四面環山,附近有許多景觀優美的公園,如東興圳公園、水圳森林公園、興隆公園自然森林遊戲場等,生活機能相當完善。此外,周圍還有遠東百貨、新竹縣政府等大型設施,娛樂與生活機能都非常便利。總的來說,這個地點非常適合居住,擁有優質的生活環境。",
          "XXX": "XXX",
          "CONCLUSION": "綜合以上分析,這個地點非常適合作為居住場所。它擁有優美的自然環境,生活機能完善,交通便捷,教育及醫療資源充足,也沒有明顯的嫌惡設施。此外,還有不少文化設施,滿足了居民的多方面需求。因此,這個地點非常適合作為居住、養老或投資的選擇。"
        }
      """.trimIndent()
      val domainMap = parseJsonToMap<FengShuiDomain>(raw)
      assertEquals(2, domainMap.size)
    }
  }
}
