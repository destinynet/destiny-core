/**
 * @author smallufo
 * Created on 2007/12/10 at 上午 10:30:01
 */
package destiny.core.astrology

/** 中心系統  */
enum class Centric(val nameKey: String) {
  /** 地心  */
  GEO("Centric.GEO"),
  /** 日心  */
  HELIO("Centric.HELIO"),
  /** 地表  */
  TOPO("Centric.TOPO"),
  /** 質心  */
  BARY("Centric.BARY");
}
