/**
 * @author smallufo 
 * @date 2002/8/19 , @date 2009/6/23 改以 HexagramIF 計算
 * @time 下午 11:04:01
 */
package destiny.iching;
/**
 * 取得卦的排列順序 , 1 <= int <= 64
 * <BR>只有 HexagramDefaultComparator(周易卦序) 以及
 * <BR>HexagramDivinationComparator(六爻卦序) 會使用到
 */
public interface HexagramSequenceIF
{
  /**
   * 傳回卦序
   * @param hexagram 一個卦的資料(Hexagram)
   * @return 卦序 1 <= int <=64
   */
  int getIndex(IHexagram hexagram);
  
  
  /**
   * 從卦序傳回卦
   * @param index : 1<=index <=64
   * @return
   */
  Hexagram getHexagram(int index);

}
