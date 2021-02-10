/**
 * @author smallufo
 * Created on 2008/4/5 at 上午 11:21:41
 */
package destiny.core.astrology.prediction

/**
 * 設定此推運可否從出生時「往前(before)推」 <br></br>
 * Progression , Transit 都具備這種特徵 <br></br>
 * Return 也有 Converse Solar/Lunar Return 盤
 */
interface Conversable {

  /**
   * 是否「逆推」 , true : 逆推
   * 設定此推運是否要從出生時間「往前 (before) 推」
   */
  val converse: Boolean
}
