/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive

/** 12宮的順序  */
interface IHouseSeq : Descriptive {

  val houseSeq: HouseSeq

  /** 從「命宮」開始，依序取得 12宮  */
  val houses: Array<House>

  /** 取得 下 n 個 宮位 是什麼。 若 n = 0 , 則傳回自己  */
  fun next(from: House, n: Int): House

  fun prev(from: House, n: Int): House {
    return next(from, 0 - n)
  }

  /**
   * 此 宮位 , 「領先」另一個宮位，幾步
   * 此值一定為正值
   * 如果某些情況（例如 「全書派」要找不存在的「相貌宮」） ，就會傳回 -1
   */
  fun getAheadOf(h1: House, h2: House): Int
}
