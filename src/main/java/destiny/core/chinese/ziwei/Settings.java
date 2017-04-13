/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import java.io.Serializable;

public class Settings implements Serializable {

  private final FireBell fireBell;

  public Settings(FireBell fireBell) {this.fireBell = fireBell;}

  public FireBell getFireBell() {
    return fireBell;
  }
}
