/**
 * @author smallufo
 * @date 2003/1/13
 * @time 下午 05:01:39
 */
package destiny.IChing;

import java.io.Serializable;

/**
 * 用九用六，只有 乾、坤 兩卦才有
 */
public class ExtraLine implements Serializable
{
  /** 爻辭 */
  private String lineExpression;
  /** 爻象 */
  private String lineImage;
  
  public ExtraLine(String lineExpression , String lineImage)
  {
    this.lineExpression = lineExpression;
    this.lineImage = lineImage;
  }

  /** 爻辭 */
  public String getLineExpression()
  {
    return lineExpression;
  }
  
  /** 爻象 */
  public String getLineImage()
  {
    return lineImage;
  }
}
