/** 2009/11/21 上午1:30:38 by smallufo */
package destiny.core.calendar;

@Deprecated
public interface DstIF
{
  /** 日光節約時間 */
  public enum Dst 
  {
    NO ,            //沒有日光節約時間
    YES_Deducted,   //有日光節約時間，已扣除
    YES_UnDeducted, //有日光節約時間，未扣除
    YES_DontKnow    //有日光節約時間，不知是否有扣除（視為「未扣除」）
  }
  
  public Dst getDst();
  
  public void setDst(Dst dst);

}

