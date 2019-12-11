/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem
import java.io.Serializable
import java.util.*

/**
 * 參考文件
 * http://numerologys.net/index.php/紫微斗數:四化異同 , or 短網址 : http://bit.ly/2sC9B4q
 *
 * 參考文件
 * http://destiny66.com/xuetang/tzyy_wei/question/question.htm
 *
 * 以下是不同古書和派別中所流傳的十干四化版一灠表：  ( source : http://bit.ly/2ZpKZeW )
 * 全集、中州派、 欽天門、全書、閩派、中州派、 北派
 * 甲 廉破武陽 廉破武陽 廉破武陽 廉破武陽
 * 乙 機梁紫陰 機梁紫陰 機梁紫陰 機梁紫陰
 * 丙 同機昌廉 同機昌廉 同機昌廉 同機昌廉
 * 丁 陰同機巨 陰同機巨 陰同機巨 陰同機巨
 * 戊 貪陰弼機 貪陰弼機 貪陰陽機 貪陰弼機
 * 已 武貪梁曲 武貪梁曲 武貪梁曲 武貪梁曲
 * 庚 陽武陰同 陽武同陰 陽武府同 陽武同相
 * 辛 巨陽曲昌 巨陽曲昌 巨陽曲昌 巨陽曲昌
 * 壬 梁紫輔武 梁紫府武 梁紫府武 梁紫輔武
 * 癸 破巨陰貪 破巨陰貪 破巨陰貪 破巨陰貪
 * 以上戊、庚和壬干中是各派爭議的部份。可以看到：
 * （1）《全書》中只有右弼化科而左輔不化科，而庚干天同化忌改為太陰化忌，原因可能是「天同不化忌」的理論。
 * （2）中州派的比較特別，強調左輔右弼不加入四化，所以取而代之的是太陽和天府化科。庚干也是以天府化科，是比其他的派別更別樹一幟。
 * （3）北派沿用《全集》的左輔右弼化科。可是，庚干卻是天同化科和天相化忌。天同化科而不化忌的原因除了是限隨《全書》之外，
 *    也有可能是「天同福星不化忌」之說所影響。另外，天相化忌是其他派別沒有出現過的，相傳這是由其他術數的原理中推敲出來的結果。
 */
abstract class TransFourAbstractImpl : ITransFour, Serializable {

  protected abstract val table: Collection<Triple<Stem, ITransFour.Value, ZStar>>

  override fun getStarOf(stem: Stem, value: ITransFour.Value): ZStar {
    return table
      .filter { it.first == stem && it.second == value}
      .map { it.third }
      .first()
  }

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(TransFourAbstractImpl::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }
}
