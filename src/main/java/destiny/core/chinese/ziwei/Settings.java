/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import java.io.Serializable;

public class Settings implements Serializable {

  /** {@link UnluckyStar#火星} ,  {@link UnluckyStar#鈴星} 設定 */
  public enum FireBell {
    全集,  // (年支、時支) -> 地支
    全書    // 年支 -> 地支
  }

  private final FireBell fireBell;

  public enum Horse {
    年馬,  // 年馬 (子由使用)
    月馬   // 月馬
  }

  /** {@link LuckyStar#天馬} */
  private final Horse horse;


  /** {@link MinorStar#天傷}、 {@link MinorStar#天使} 計算方式 */
  public enum HurtAngel {
    FIXED,    /** 天傷固定於交友宮 {@link MinorStar#fun天傷_fixed交友} 、 天使固定疾厄宮 {@link MinorStar#fun天使_fixed疾厄} */
    YINYANG   /** 陽順陰逆 {@link MinorStar#fun天傷_陽順陰逆} 、 {@link MinorStar#fun天使_陽順陰逆} */
  }

  private final HurtAngel hurtAngel;

  public Settings(FireBell fireBell, Horse horse, HurtAngel hurtAngel) {this.fireBell = fireBell;
    this.horse = horse;
    this.hurtAngel = hurtAngel;
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
