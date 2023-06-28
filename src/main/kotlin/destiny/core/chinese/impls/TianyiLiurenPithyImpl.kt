/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.Descriptive
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 此為 web 大六壬 內定版本
 *
 * 《大六壬金口訣》《六壬神課金口訣》
 * http://ctext.org/wiki.pl?if=gb&chapter=237248
 * line 105 :
 * 甲戊庚牛日順行，其它書以甲戊兼牛羊，庚辛逢馬虎之例，非也。神術非它術，其用甲戊庚乃天上三奇，故不可行也羊夜逆行，
 * 乙己鼠日順行猴日夜逆行鄉，丙丁豬日順行雞夜逆行位，壬癸蛇日逆行兔夜順行藏，六辛逢馬日逆行虎夜順行，此是貴人方。
 *
 * 《大六壬心鏡》推導天乙貴人：
 * 甲戊庚游大小吉，乙己神傳晝夜分，丙丁早亥暮居酉，六辛常午複來寅，壬癸立處於已卯，不降天罡作貴人
 *
 *
 * 甲戊庚日旦治大吉（丑），暮治小吉（未）；
 * 乙己日旦治神後（子），暮治傳送（申）；
 * 丙丁日旦治登明（亥），暮治從魁（酉）；
 * 六辛日旦治勝光（午），暮治功曹（寅）；
 * 壬癸日旦治太乙（巳），暮治太沖（卯）
 */
class TianyiLiurenPithyImpl : ITianyi,
                              Descriptive by Tianyi.LiurenPithy.asDescriptive(),
                              Serializable {

  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    return when (stem) {
      甲, 戊, 庚 -> if (yinYang.booleanValue) 丑 else 未

      乙, 己    -> if (yinYang.booleanValue) 子 else 申

      丙, 丁    -> if (yinYang.booleanValue) 亥 else 酉

      壬, 癸    -> if (yinYang.booleanValue) 巳 else 卯

      辛       -> if (yinYang.booleanValue) 午 else 寅
    }
  }
}
