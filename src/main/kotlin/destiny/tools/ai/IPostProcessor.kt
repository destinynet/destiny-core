package destiny.tools.ai

import java.util.*

/**
 * 將一串字串，存到 DB 之前，要做的預先處理，例如，只取出 json 格式 , or 確保符合所需的 fields
 */
interface IPostProcessor {

  /**
   * @return 最終結果 , 以及，是否有被修改過
   */
  fun process(raw: String, locale: Locale?): Pair<String, Boolean>
}
