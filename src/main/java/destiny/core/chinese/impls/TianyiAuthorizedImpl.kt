/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ITianyi
import destiny.core.chinese.IYinYang
import destiny.core.chinese.Stem
import java.io.Serializable

class TianyiAuthorizedImpl : ITianyi, Serializable {

  /**
   * 《協紀辨方書》 《蠡海集》
   * 甲戊庚牛羊，乙己鼠猴鄉；丙丁豬雞位，壬癸兔蛇藏；六辛逢馬虎，此是貴人鄉。
   * (甲羊戊庚牛)
   *
   *
   * 《大六壬探源》
   * 論旦貴：甲羊戊庚牛，乙猴己鼠求，丙雞丁豬位，壬兔癸蛇游，六辛逢虎上，陽貴日中儔。
   * 論暮貴：甲牛戊庚羊，乙鼠己猴鄉，丙豬丁雞位，壬蛇癸兔藏，六辛逢午馬，陰貴夜時當。
   * 指的亦是此種排列方法
   *
   *
   * 《六壬類聚》、《六壬摘要》、《壬學瑣記》及《六壬秘笈》亦是此法
   *
   *
   * 《考原》
   * 陽貴歌曰：庚戊見牛甲在羊，乙猴己鼠丙雞方，
   * 　　　　　丁豬癸蛇壬是兔，六辛逢虎貴為陽。
   * 陰貴歌曰：甲貴陰牛庚戊羊，乙貴在鼠己猴鄉，
   * 　　　　　丙豬丁雞辛遇馬，壬蛇癸兔屬陰方。
   */
  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    when (stem) {
      Stem.甲 -> return if (yinYang.booleanValue) 未 else 丑
      Stem.戊, Stem.庚 -> return if (yinYang.booleanValue) 丑 else 未

      Stem.乙 -> return if (yinYang.booleanValue) 申 else 子
      Stem.己 -> return if (yinYang.booleanValue) 子 else 申

      Stem.丙 -> return if (yinYang.booleanValue) 酉 else 亥
      Stem.丁 -> return if (yinYang.booleanValue) 亥 else 酉

      Stem.壬 -> return if (yinYang.booleanValue) 卯 else 巳
      Stem.癸 -> return if (yinYang.booleanValue) 巳 else 卯

      Stem.辛 -> return if (yinYang.booleanValue) 寅 else 午

      else -> throw AssertionError("$stem at $yinYang")
    }
  }


}
