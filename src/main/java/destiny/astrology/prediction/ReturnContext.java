/**
 * @author smallufo 
 * Created on 2008/5/29 at 上午 3:15:13
 */ 
package destiny.astrology.prediction;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

/** 
 * 返照法演算法 , 可以計算 Planet 的返照
 */
public class ReturnContext implements DiscreteIF , Conversable , Serializable {

  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他 */
  private Planet planet = Planet.SUN;
  
  /** 是否逆推，內定是順推 */
  private boolean converse = false;
  
  /** 是否消除歲差，內定是不計算歲差 */
  private boolean precession = false;
  
  /** 計算星體的介面 */
  private StarPositionWithAzimuthIF starPositionWithAzimuthImpl;
  
  /** 計算星體到黃道幾度的時刻，的介面 */
  private StarTransitIF starTransitImpl;
  
  private HouseCuspIF houseCuspImpl;
  
  private ApsisWithAzimuthIF apsisWithAzimuthImpl;
  
  /** 出生時間 , LMT */
  private LocalDateTime natalLmt;
  
  /** 出生地點 */
  private Location natalLoc;
  
  /** 欲計算的目標時間，通常是當下，now，以LMT型態 */
  private LocalDateTime nowLmt;
  
  /** 現在所處的地點 */
  private Location nowLoc;
  
  /** 交角 , 通常是 0 , 代表回歸到原始度數 */
  private double orb = 0;
  
  /** 最完整的 constructor , 連是否逆推 , 是否考慮歲差，都要帶入 */
  public ReturnContext(StarPositionWithAzimuthIF positionWithAzimuthImpl ,
                       StarTransitIF starTransitImpl,
                       HouseCuspIF houseCuspImpl ,
                       ApsisWithAzimuthIF apsisWithAzimuthImpl ,
                       LocalDateTime natalLmt , Location natalLoc ,
                       LocalDateTime nowLmt , Location nowLoc ,
                       Planet planet ,
                       double orb ,
                       boolean converse ,
                       boolean precession)
  {
    this.starPositionWithAzimuthImpl = positionWithAzimuthImpl;
    this.starTransitImpl = starTransitImpl;
    this.houseCuspImpl = houseCuspImpl;
    this.apsisWithAzimuthImpl = apsisWithAzimuthImpl;
    this.natalLmt = natalLmt;
    this.natalLoc = natalLoc;
    this.nowLmt = nowLmt;
    this.nowLoc = nowLoc;
    this.planet = planet;
    this.orb = orb;
    this.converse = converse;
    this.precession = precession;
  }


  
  /** 較簡易的 constructor , 內定是 交角0度、「順推」、不考慮歲差 */
  /*
  public ReturnContext(StarPositionIF starPositionImpl ,StarTransitIF starTransitImpl,  Time natalLmt , Location natalLoc , Time nowLmt , Location nowLoc , Planet planet)
  {
    this.starPositionImpl = starPositionImpl;
    this.starTransitImpl = starTransitImpl;
    this.natalLmt = natalLmt;
    this.natalLoc = natalLoc;
    this.nowLmt = nowLmt;
    this.nowLoc = nowLoc;
    this.planet = planet;
    this.orb = 0;
    this.converse = false;
    this.precession = false;
  }
  */
  
  
  /** 對外主要的 method , 取得 return 盤 */
  @NotNull
  public HoroscopeContext getReturnHoroscope() {
    LocalDateTime natalGmt = Time.getGmtFromLmt(natalLmt , natalLoc);
    LocalDateTime nowGmt = Time.getGmtFromLmt(nowLmt , nowLoc);

    LocalDateTime convergentGmt = getConvergentTime(natalGmt , nowGmt);
    LocalDateTime convergentLmt = Time.getLmtFromGmt(convergentGmt , nowLoc);

    HouseSystem houseSystem = HouseSystem.PLACIDUS;
    Coordinate coordinate = Coordinate.ECLIPTIC;
    Centric centric = Centric.GEO;
    double temperature = 20;
    double pressure = 1013.25;
    NodeType nodeType = NodeType.MEAN;

    return new HoroscopeContext(convergentLmt , nowLoc , houseSystem , coordinate , centric , temperature , pressure , starPositionWithAzimuthImpl , houseCuspImpl , apsisWithAzimuthImpl , nodeType);
  }
  
  
  /**
   * 實作 {@link Mappable }, 注意，在 {@link AbstractProgression}的實作中，並未要求是GMT；但在這裡，必須<b>要求是GMT</b> ！
   * 傳回值也是GMT！
   */
  @Override
  public LocalDateTime getConvergentTime(@NotNull LocalDateTime natalGmtTime, @NotNull LocalDateTime nowGmtTime) {
    Coordinate coordinate = (precession) ? Coordinate.SIDEREAL : Coordinate.ECLIPTIC;
    //先計算出生盤中，該星體的黃道位置
    double natalPlanetDegree = starPositionWithAzimuthImpl.getPosition(planet , natalGmtTime , Centric.GEO , coordinate).getLongitude();

    //再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    if (!converse) {
      //順推
      return starTransitImpl.getNextTransitGmt(planet , Utils.getNormalizeDegree(natalPlanetDegree+orb) , coordinate , nowGmtTime, false); //false 代表逆推，往before算
    }
    else {
      // converse == true , 逆推
      //從出生時間往前(before)推
      Duration d = Duration.between(nowGmtTime , natalGmtTime).abs();
      LocalDateTime beforeNatalGmtTime = LocalDateTime.from(natalGmtTime).minus(d);
      //Time beforeNatalGmtTime = new Time(natalGmtTime , 0-(nowGmtTime.diffSeconds(natalGmtTime)));
      //要確認最後一個參數，到底是要用 true , 還是 false , 要找相關定義 , 我覺得這裡應該是順推
      return starTransitImpl.getNextTransitGmt(planet , Utils.getNormalizeDegree(natalPlanetDegree+orb) , coordinate , beforeNatalGmtTime, true); //true 代表順推 , 往 after 算
    }
  }


  
  @Override
  public void setConverse(boolean value)
  {
    this.converse = value;
  }

  /** 是否逆推 , true 代表「是」，逆推！ */
  @Override
  public boolean isConverse()
  {
    return converse;
  }

  /** 是否消除歲差 */
  public boolean isPrecession()
  {
    return precession;
  }

  /** 設定是否消除歲差 */
  public void setPrecession(boolean value)
  {
    this.precession = value;
  }

  public void setStarPositionWithAzimuthImpl(StarPositionWithAzimuthIF starPositionWithAzimuthImpl)
  {
    this.starPositionWithAzimuthImpl = starPositionWithAzimuthImpl;
  }

  public void setStarTransitImpl(StarTransitIF starTransitImpl)
  {
    this.starTransitImpl = starTransitImpl;
  }

  public double getOrb()
  {
    return orb;
  }

  public void setOrb(double orb)
  {
    this.orb = orb;
  }



}
