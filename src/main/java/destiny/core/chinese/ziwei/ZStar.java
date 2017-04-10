/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei;

/**
 * 14 顆主星
 * */
public enum ZStar {
  紫微(StarSystem.紫微),
  天機(StarSystem.紫微),
  太陽(StarSystem.紫微),
  武曲(StarSystem.紫微),
  天同(StarSystem.紫微),
  廉貞(StarSystem.紫微),

  天府(StarSystem.天府),
  太陰(StarSystem.天府),
  貪狼(StarSystem.天府),
  巨門(StarSystem.天府),
  天相(StarSystem.天府),
  天梁(StarSystem.天府),
  七殺(StarSystem.天府),
  破軍(StarSystem.天府),
  ;

  /** 星系 */
  private final StarSystem starSystem;

  ZStar(StarSystem starSystem) {
    this.starSystem = starSystem;
  }

  public StarSystem getStarSystem() {
    return starSystem;
  }
}
