/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.TianyiIF;
import destiny.core.chinese.impls.TianyiAuthorizedImpl;
import destiny.core.chinese.impls.TianyiLiurenPithyImpl;
import destiny.core.chinese.impls.TianyiOceanImpl;
import destiny.core.chinese.impls.TianyiZiweiBookImpl;

import java.io.Serializable;

public class Settings implements Serializable {

  /** {@link LuckyStar#天魁} , {@link LuckyStar#天鉞} (貴人) 算法 */
  public enum Tianyi {
    ZIWEI_BOOK,   /** 紫微斗數全書 {@link TianyiZiweiBookImpl} */
    AUTHORIZED ,  /** 協紀辨方書 {@link TianyiAuthorizedImpl} */
    OCEAN,        /** 淵海子平  {@link TianyiOceanImpl} */
    LIUREN_PITHY  /** 大六壬金口訣 {@link TianyiLiurenPithyImpl} */
  }

  private final Tianyi tianyi;

  /** {@link UnluckyStar#火星} ,  {@link UnluckyStar#鈴星} 設定 */
  public enum FireBell {
    全集,  // (年支、時支) -> 地支
    全書   // 年支 -> 地支
  }
  private final FireBell fireBell;

  /** {@link LuckyStar#天馬} */
  public enum Horse {
    年馬,  /** 年馬 (子由使用) */
    月馬   /** 月馬  */
  }
  private final Horse horse;


  /** {@link MinorStar#天傷}、 {@link MinorStar#天使} 計算方式 */
  public enum HurtAngel {
    FIXED,    /** 天傷固定於交友宮 {@link MinorStar#fun天傷_fixed交友} 、 天使固定疾厄宮 {@link MinorStar#fun天使_fixed疾厄} */
    YINYANG   /** 陽順陰逆 {@link MinorStar#fun天傷_陽順陰逆} 、 {@link MinorStar#fun天使_陽順陰逆} */
  }
  private final HurtAngel hurtAngel;

  public Settings(Tianyi tianyi, FireBell fireBell, Horse horse, HurtAngel hurtAngel) {
    this.tianyi = tianyi;
    this.fireBell = fireBell;
    this.horse = horse;
    this.hurtAngel = hurtAngel;
  }

  public Tianyi getTianyi() {
    return tianyi;
  }

  public TianyiIF getTianyiImpl() {
    switch (tianyi) {
      case ZIWEI_BOOK: return new TianyiZiweiBookImpl();
      case AUTHORIZED: return new TianyiAuthorizedImpl();
      case OCEAN: return new TianyiOceanImpl();
      case LIUREN_PITHY: return new TianyiLiurenPithyImpl();
      default: throw new AssertionError("error : " + tianyi);
    }
  }

  public FireBell getFireBell() {
    return fireBell;
  }

  public Horse getHorse() {
    return horse;
  }

  public HurtAngel getHurtAngel() {
    return hurtAngel;
  }
}
