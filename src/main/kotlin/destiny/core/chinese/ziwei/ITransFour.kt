/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.chinese.Stem
import java.util.*

enum class T4Value {

  祿,
  權,
  科,
  忌;

  fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(ZStar::class.java.name, locale).getString(name)
  }
}
/**
 * 四化
 * 參考實作 http://destiny66.com/xuetang/tzyy_wei/question/question.htm
 *
 * 四化
 *
 * 　庚年及壬年的安法不同。《全書》庚年在卷二的安法是「日武同陰」 [TransFourFullBookImpl]
 * ，但是在卷四古例安法中則為「日武陰同」。 [TransFourFullCollectImpl]
 *
 * 壬年的安法為「梁紫府武」。 [TransFourFullCollectImpl]
 *
 * 　　《全集》庚年安法為「日武同相」， [TransFourNorthImpl]
 * 壬年安法為「梁紫左武」。 [TransFourNorthImpl]
 *
 * 　　由此可知，某些重四化的流派，其四化的安法是以《全集》為主。至於尚有其它安法，則非是。
 *
 * 或是對照此整理圖表 http://imgur.com/fniMA7X
 */
interface ITransFour : Descriptive {

  val transFour: TransFour

  /** 取得「某天干（可能是本命年、大限、或是流年、流月、流日、流時）的某四化」是哪顆星  */
  fun getStarOf(stem: Stem, value: T4Value): ZStar

  /** 類似前者，但逆算：計算此星於此干，是否有四化，若有的話，其為何者  */
  fun getValueOf(star: ZStar, stem: Stem): T4Value? {
    // 先把本年四化的四顆星都找出來
    val map = T4Value.entries.associateBy { type -> getStarOf(stem, type) }

    return map[star]
  }

}
