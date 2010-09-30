/**
 * @author smallufo
 * @date 2002/8/20
 * @time 下午 04:04:28
 */
package destiny.IChing.divine;

import destiny.IChing.HexagramIF;
import destiny.core.chinese.StemBranch;

/** 伏神介面 */
public interface HiddenEnergyIF
{
  /** 取得這個伏神系統的名稱 */
  public String getName();
  
  /**
   * 取得第幾爻的伏神
   * 1 <= LineIndex <=6
   */
  public StemBranch getStemBranch(HexagramIF hexagram  , SettingsOfStemBranchIF settings , int LineIndex);
}
