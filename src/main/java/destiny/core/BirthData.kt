/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.locationOf
import kotlinx.serialization.SerialName
import java.io.Serializable
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 時間、地點 */
interface ITimeLoc : Serializable {
  val time: ChronoLocalDateTime<*>
  val location: ILocation

  val gmtJulDay: Double
    get() {
      return TimeTools.getGmtJulDay(time, location)
    }
}

@kotlinx.serialization.Serializable
@SerialName("TimeLoc")
data class TimeLoc(
  override val time: ChronoLocalDateTime<*>,
  override val location: ILocation) : ITimeLoc

interface ITimeLocMutable : ITimeLoc {
  override var time: ChronoLocalDateTime<*>
  override var location: ILocation
}

data class TimeLocMutable(
  override var time: ChronoLocalDateTime<*>,
  override var location: ILocation) : ITimeLocMutable {

  companion object {
    fun withDefault(): ITimeLoc {
      return TimeLoc(LocalDateTime.now(), locationOf(Locale.getDefault()))
    }
  }
}


/**
 * 最精要、計算 (modern版) 八字、斗數、占星 的元素
 */
interface IBirthData : ITimeLoc {
  val gender: Gender
}


data class BirthData(
  val timeLoc: ITimeLoc,
  override val gender: Gender
                    ) : IBirthData, ITimeLoc by timeLoc {

  constructor(time: ChronoLocalDateTime<*>, location: ILocation, gender: Gender) : this(TimeLoc(time, location), gender)
}

/** 承上 , mutable 版本 */
interface IBirthDataMutable : IBirthData , ITimeLocMutable {
  override var time: ChronoLocalDateTime<*>
  override var location: ILocation
  override var gender: Gender
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
  override val place: String?) : IBirthDataNamePlace, IBirthData by birthData {

  constructor(gender: Gender, time: ChronoLocalDateTime<*>, location: ILocation, name: String?, place: String?)
    : this(BirthData(TimeLoc(time, location), gender), name, place)
}
