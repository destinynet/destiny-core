/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem

/**
 * 與某宮位形成固定關係 的排列方式
 * 目前只有 [StarMinor.天傷] , [StarMinor.天使] 會用到
 * 實作於 [StarMinor.fun天傷_陽順陰逆] , [StarMinor.fun天使_陽順陰逆]
 */
abstract class HouseHouseDepYearStemGenderImpl internal constructor(star: ZStar) :
  HouseAbstractImpl<Triple<Branch, Stem, Gender>>(star) {

  override fun getBranch(t: Triple<Branch, Stem, Gender>): Branch {
    throw RuntimeException("error")
  }

}
