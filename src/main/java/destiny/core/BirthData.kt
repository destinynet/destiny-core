/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core

import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/** 時間、地點 */
interface ITimeLoc {
  val time: ChronoLocalDateTime<*>
  val location: ILocation


}

data class TimeLoc(
  override val time: ChronoLocalDateTime<*>,
  override val location: ILocation) : ITimeLoc, Serializable

data class TimeLocMutable(
  override var time: ChronoLocalDateTime<*>,
  override var location: ILocation) : ITimeLoc, Serializable


/**
 * 最精要、計算 (modern版) 八字、斗數、占星 的元素
 */
interface IBirthData : ITimeLoc {
  val gender: Gender
}


data class BirthData(
  val timeLoc: ITimeLoc,
  override val gender: Gender
                    ) : IBirthData, ITimeLoc by timeLoc, Serializable

/** 承上 , mutable 版本 */
interface IBirthDataMutable : IBirthData, Serializable {
  override var gender: Gender
  override var time: ChronoLocalDateTime<*>
  override var location: ILocation
}


/**
 * 加上姓名、地名 等資料
 */
interface IBirthDataNamePlace : IBirthData {
  val name: String?
  val place: String?
}

/** 承上 , mutable 版本 */
interface IBirthDataNamePlaceMutable : IBirthDataNamePlace, IBirthDataMutable {
  override var name: String?
  override var place: String?
}

data class BirthDataNamePlace(
  val birthData: IBirthData,
  override val name: String?,
  override val place: String?) : IBirthDataNamePlace, IBirthData by birthData, Serializable {

  constructor(gender: Gender, time: ChronoLocalDateTime<*>, location: ILocation, name: String?, place: String?)
    : this(BirthData(TimeLoc(time, location), gender), name, place)
}