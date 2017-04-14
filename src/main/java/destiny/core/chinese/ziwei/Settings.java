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

  private final Horse horse;

  public Settings(FireBell fireBell, Horse horse) {this.fireBell = fireBell;
    this.horse = horse;
  }

  public FireBell getFireBell() {
    return fireBell;
  }

  public Horse getHorse() {
    return horse;
  }
}
