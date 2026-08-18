/**
 * Created by smallufo on 2017-03-23.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals

class UBBCodeConverterTest {

  private val converter = UBBCodeConverter()

  private val quoteOpen = "<hr noshade size=1 ><blockquote>"
  private val quoteClose = "</blockquote><hr noshade size=1>"

  @Test
  fun reQuote() {
    // 單行
    assertEquals(
      "123 $quoteOpen 內文 $quoteClose 456",
      converter.getAll("123 [quote] 內文 [/quote] 456")
    )

    // 前後夾帶 \n 與 <br> ：不影響轉換
    assertEquals(
      "123 \n<br>$quoteOpen 內文 $quoteClose \n<br>456",
      converter.getAll("123 \n<br>[quote] 內文 [/quote] \n<br>456")
    )

    // 內文含 <br> ：照樣轉換
    assertEquals(
      "123 \n<br>$quoteOpen 內文<br>哈囉 $quoteClose \n<br>456",
      converter.getAll("123 \n<br>[quote] 內文<br>哈囉 [/quote] \n<br>456")
    )
  }

  /**
   * **已知限制**：`[quote]` 的內文若含真正的換行字元（而非 `<br>`），正規表示式吃不到，
   * 整段原封不動吐回去。
   *
   * 這個 case 原本與上面三個並列在 `reQuote` 裡，但只有第一個有斷言、其餘三個只有 `logger.info`
   * —— 於是「第四種輸入根本沒被轉換」這件事就這樣被蓋了多年。
   * 這裡把現況釘成明確斷言：哪天改成支援換行，測試會紅，提醒回來更新這份說明。
   */
  @Test
  fun `quote 內文含換行字元時不會被轉換`() {
    val input = "123 \n<br>[quote] 內文\n哈囉 [/quote] \n<br>456"
    assertEquals(input, converter.getAll(input))
  }

  @Test
  fun testOthers() {
    assertEquals(
      """<a href=mailto:service@google.com>service@google.com</a>""",
      converter.getAll("""[email]service@google.com[/email]""")
    )
    assertEquals(
      """<i>italic1</i> <i>斜體2</i>""",
      converter.getAll("""[i]italic1[/i] [i]斜體2[/i]""")
    )
  }
}
