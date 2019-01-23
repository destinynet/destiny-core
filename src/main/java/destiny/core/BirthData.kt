/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core

import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 最精要、計算 (modern版) 八字、斗數、占星 的元素
 */
interface IBirthData {
  val gender: Gender
  val time: ChronoLocalDateTime<*>
  val location: ILocation
}

/** 承上 , mutable 版本 */
interface IBirthDataMutable : IBirthData {
  override var gender: Gender
  override var time: ChronoLocalDateTime<*>
  override var location: ILocation
}

data class BirthData(
  override val gender: Gender,
  override val time: ChronoLocalDateTime<*>,
  override val location: ILocation
                    ) : IBirthData, Serializable


/**
 * 加上姓名、地名 等資料
 */
interface IBirthDataNamePlace : IBirthData {
  val name: String?
  val place: String?
}

/** 承上 , mutable 版本 */
interface IBirthDataNamePlaceMutable : IBirthDataNamePlace , IBirthDataMutable {
  override var name: String?
  override var place: String?
}

data class BirthDataNamePlace(
  val birthData: BirthData,
  override val name: String?,
  override val place: String?) : IBirthDataNamePlace, IBirthData by birthData, Serializable {

  constructor(gender: Gender, time: ChronoLocalDateTime<*>, location: ILocation, name: String?, place: String?)
    : this(BirthData(gender, time, location), name, place)
}