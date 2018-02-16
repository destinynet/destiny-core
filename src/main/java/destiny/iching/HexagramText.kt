/**
 * Created by smallufo on 2018-02-15.
 */
package destiny.iching

import java.io.Serializable


data class LineText(/** 爻辭 */
                    val expression : String ,
                    /** 象曰 */
                    val image: String ) : Serializable

data class HexagramText(/** 短卦名 */
                        val shortName : String,
                        /** 完整卦名 */
                        val fullName:String ,
                        /** 卦辭 */
                        val expression : String ,
                        /** 象曰 */
                        val image: String ,
                        /** 彖曰 */
                        val judgement: String ,
                        /** 六爻 */
                        private val lineTexts : List<LineText>,
                        /** 用九、用六 的爻辭、象曰 */
                        val extraLine: LineText?
                       ) : Serializable {
  /** 取得第幾爻 , 1<= index <= 6 */
  fun getLineFromOne(index : Int) : LineText {
    return lineTexts[index-1]
  }
}