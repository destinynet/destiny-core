/**
 * @author smallufo
 * Created on 2008/7/24 at 上午 4:13:26
 */
package destiny.core;

import destiny.core.calendar.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.chrono.IsoEra;

/** 一個命盤最基本的必備元素 : 性別 / 時間 / 地點 */
public class BirthData implements GenderIF, TimeIF, DateIF, HmsIF, LocationIF, Serializable {

  private Gender gender;

  private LocalDateTime time;

  private Location location;

  public BirthData(Gender gender, LocalDateTime time, Location location) {
    this.gender = gender;
    this.time = time;
    this.location = location;
  }

  @Override
  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  @Override
  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  @Override
  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public boolean isAd() {
    return time.toLocalDate().getEra() == IsoEra.CE;
  }

  @Override
  public int getYear() {
    return time.getYear();
  }

  @Override
  public int getMonth() {
    return time.getMonthValue();
  }

  @Override
  public int getDay() {
    return time.getDayOfMonth();
  }

  @Override
  public int getHour() {
    return time.getHour();
  }

  @Override
  public int getMinute() {
    return time.getMinute();
  }

  @Override
  public double getSecond() {
    return time.getSecond();
  }

}
