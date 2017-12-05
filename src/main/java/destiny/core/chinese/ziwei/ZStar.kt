/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.astrology.Point

/**
 * 紫微斗數的諸星
 *
 * 紫微斗數雖然長期來一直有《全書》和《全集》之分，而有些星及神煞是全書、全集均有安，有解釋
 * ( 如天馬、化祿、化權、化科、紅鸞、天空、地劫、化忌、天傷、天使、天刑、天姚、天哭、天虛 )
 *
 * 有些是《全書》、《全集》有安，但沒有解釋( 如天喜、三台、八座、台輔、封誥、龍池、鳳閣、截空、旬空 )；
 *
 * 有些是《全書》沒有，但《全集》有安 (如天才、天壽、天官、天福、恩光、天貴、孤辰、寡宿、劫煞、華蓋、桃花煞、大耗、破碎、地空 )；
 *
 * 而比較悲哀是百家的爭「鳴」，相互「加料」，例如有些星像解神、天巫、蜚廉、天月、陰煞等是《全書》、《全集》都沒有的星，
 * 但現在坊間的紫微斗數卻是處處可見，更不用再提哈雷、天廚等那些子虛烏有的星了；
 *
 * 不過，儘管只有《全書》和《全集》之分，在李亨利老師的考證中，其二者的差別仍然很大，而至少有九項的差異是需要讓學習紫微斗數的人士了解的，如：
 * 一、命主
 * 　　在《全書》中，是以命宮的地支為主。而《全集》是以生年地支為主。
 *
 * 二、四化 ： 見 [ITransFour]
 *
 */
abstract class ZStar : Point, Comparable<ZStar>  {

  val type: Type

  enum class Type {
    主星,
    年, // 年的干支
    年干,
    年支,
    月,
    日,
    時,
    月日, // 三台、八座
    日時, // 恩光、天貴
    年月時, // 天才、天壽
    宮位, // 宮位決定 : 天使、天傷
    // 以下雜星
    博士, // 博士12神
    長生, // 長生12神煞
    將前, // 將前12星
    歲前
    // 歲前12星
  }


  constructor(nameKey: String, resource: String, type: Type) : super(nameKey, resource) {
    this.type = type
  }

  constructor(nameKey: String, resource: String, abbrKey: String, type: Type) : super(nameKey, resource, abbrKey) {
    this.type = type
  }

  override fun compareTo(o: ZStar): Int {
    val typeCompare = this.type.compareTo(o.type)
    return if (typeCompare != 0)
      typeCompare
    else {
      this.toString().compareTo(o.toString())
    }
  }
}
