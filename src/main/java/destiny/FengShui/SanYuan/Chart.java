/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 02:27:05
 * 
 * 2009/12/15 把與視覺相關的 ColorCanvas 移出到 {@link destiny.FengShui.SanYuan.ChartColorCanvasWrapper} , 讓此 Chart 純粹只有資料結構
 */
package destiny.FengShui.SanYuan;

import destiny.iching.Symbol;
import destiny.iching.SymbolAcquired;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * 三元盤的 presentation model 
 */
public class Chart implements Serializable {
  /**
   * 元運 , 其值只能為 1~9
   */
  private int period = 7; //內訂是七運
  
  /**
   * 座山
   */
  private Mountain mountain = Mountain.子; //內訂是子山
  
  /**
   * 從哪個卦看去 ?
   * 意即，九宮格的底邊，是哪個卦（後天）
   * 內定以坎卦作為底邊，即，底邊為北方
   */
  private Symbol view = Symbol.坎; 
  
  @NotNull
  private ChartBlock[] blocks = new ChartBlock[10]; // 0 不用
  //private ChartBlock[][] chartCoordinate = new ChartBlock[3][3];

  @NotNull
  private EarthlyCompass 地盤 = new EarthlyCompass();

  @NotNull
  private AcquiredSymbolCompass 後天八卦盤 = new AcquiredSymbolCompass();
  
  /**
   * Constructor , 設定年運以及座山 , 內訂觀點為坎
   */
  public Chart(int period , Mountain mountain)
  {
    this.period = period;
    this.mountain = mountain;
    calculate();
  }
  
  /**
   * constructor , 設定年運、座山，以及底邊方向
   */
  public Chart(int period, Mountain mountain, Symbol view) {
    this.period = period;
    this.mountain = mountain;
    this.view = view;
    calculate();
  }
  
  private void calculate() {
    /** 
     * 先將chartBlockList initialize 
     * 設定其宮位 , 該元運置於 blocks[1] , 順推
     */
    int 元運 = getPeriod();
    for (int i = 1; i <= 9; i++) {
      blocks[i] = new ChartBlock(normalize(元運++));
    }

    //                                    |-----------------|
    //設定方向（以卦來表示），尚未實作轉盤   // | 9巽 | 5離 | 7坤 |
    blocks[9].setSymbol(Symbol.巽);    // |[0,0]|[0,1]|[0,2]|
    blocks[8].setSymbol(Symbol.震);    // |-----|-----|-----|
    blocks[4].setSymbol(Symbol.艮);    // | 8震 |  1  | 3兌 |
    blocks[5].setSymbol(Symbol.離);    // |[1,0]|[1,1]|[1,2]|
    blocks[6].setSymbol(Symbol.坎);    // |-----|-----|-----|
    blocks[7].setSymbol(Symbol.坤);    // | 4艮 | 6坎 | 2乾 |
    blocks[3].setSymbol(Symbol.兌);    // |[2,0]|[2,1]|[2,2]|
    blocks[2].setSymbol(Symbol.乾);    // |-----------------|

    //決定座山/向 是位於哪一卦中
    //詢問此山/向 的中心點度數為：
    double midMountain;
    if (Math.abs
       (地盤.getEndDegree  (getMountain()) - 
        地盤.getStartDegree(getMountain()) ) 
       > 180 )
      midMountain = 0;
    else
      midMountain =  
      (地盤.getEndDegree(getMountain()) +
       地盤.getStartDegree(getMountain()) ) / 2;
    
    double midDirection;
    if (midMountain == 180.0)
      midDirection = 0;
    else
      midDirection =(
      地盤.getEndDegree(getMountain().getOppositeMountain()) + 
      地盤.getStartDegree(getMountain().getOppositeMountain()) ) / 2 ;

    Symbol 飛佈山盤卦 = (Symbol) 後天八卦盤.getSymbol(midMountain );
    Symbol 飛佈向盤卦 = (Symbol) 後天八卦盤.getSymbol(midDirection);
    
    //搜尋 blocks[1~9] , 分別找尋 飛佈山盤卦 以及 飛佈向盤卦 , 取得其 period 值
    int mountainStart  = 0;
    int directionStart = 0;
    for (int i=1 ; i <=9 ; i++) {
      if (blocks[i].getSymbol() == 飛佈山盤卦 )
        mountainStart = blocks[i].getPeriod();
      if (blocks[i].getSymbol() == 飛佈向盤卦 )
        directionStart = blocks[i].getPeriod();
    }

    // TODO : 以後改用 SymbolAcquired.getSymbol : Optional<Symbol>  的演算法
    Symbol 原始山盤卦 = SymbolAcquired.getSymbolNullable(mountainStart);
    Symbol 原始向盤卦 = SymbolAcquired.getSymbolNullable(directionStart) ;
    
    //填入山盤
    fillMountain (mountainStart  , isConverse(原始山盤卦 , 飛佈山盤卦 , getMountain()) );
    //填入向盤
    fillDirection(directionStart , isConverse(原始向盤卦 , 飛佈向盤卦 , getMountain().getOppositeMountain())  );
  } //calculate()
  
  
  /**
   * 查詢某卦方位裡面的資料結構 (ChartBlock) , 如果查的是 null , 則傳回中宮
   */
  @Nullable
  public ChartBlock getChartBlock(Symbol s)
  {
    for(ChartBlock chartBlock : blocks)
    {
      if (chartBlock != null && chartBlock.getSymbol() == s)
        return chartBlock;
    }
    throw new RuntimeException("Cannot find ChartBlock " + s);
  }
  
  private boolean isConverse(@Nullable Symbol 原始卦 , Symbol 飛佈卦 , @NotNull Mountain m)
  {
    boolean isConverse = false;
    if (原始卦 == null)
    {
      if (地盤.getYinYang(m))
        isConverse = false;
      else
        isConverse = true;
    }
    else
    {
      int index = (int) ((地盤.getStartDegree(m)- 後天八卦盤.getStartDegree(飛佈卦) ) / 15);
      /*
       * index = 0 => 地元
       * index = 1 => 天元
       * index = 2 => 人元
       */
      double degree = 後天八卦盤.getStartDegree(原始卦) + index*15 +1; //最後的 +1 是確保結果能坐落於該山中
      if (地盤.getYinYang((Mountain) (地盤.getMountain(degree))))
        isConverse = false;
      else
        isConverse = true;
    }
    return isConverse;
  }
  
  /**
   * 將 int 值侷限在 1 到 9 之間
   */
  private static int normalize(int value)
  {
    if (value > 9 )
      return normalize( value-9);
    else if (value < 1)
      return normalize (value+9);
    else     
      return value;
  }
  
  
  /**
   * 填滿山盤，傳入座山 , 以及是否逆飛
   */
  private void fillMountain(int start , boolean converse)
  {
    if (!converse)
    {
      //順飛
      for (int i=1 ; i <=9 ; i++)
      {
        blocks[i].setMountain(normalize(start + i + 8));
      }
    }
    else
    {
      //逆飛
      for (int i=1 ; i <=9 ; i++)
      {
        blocks[i].setMountain(normalize(start - i + 10));
      }
    }
  }//fillMountain
  
  
  /**
   * 填滿向盤，傳入向山 , 以及是否逆飛
   */
  private void fillDirection(int start , boolean converse)
  {
    if (!converse)
    {
      //順飛
      for (int i=1 ; i <=9 ; i++)
      {
        blocks[i].setDirection(normalize(start + i + 8));
      }
    }
    else
    {
      //逆飛
      for (int i=1 ; i <=9 ; i++)
      {
        blocks[i].setDirection(normalize(start - i + 10));
      }
    }
  }//fillMountain
  
  /**
   * 傳回元運
   */
  public int getPeriod() 
  {
    return period; 
  }
  
  /**
   * 傳回座山
   */
  public Mountain getMountain() 
  { 
    return mountain; 
  }
  
  /**
   * 傳回底邊
   */
  public Symbol getView() 
  { 
    return view; 
  }
}
