/** 2009/12/15 上午2:56:48 by smallufo */
package destiny.FengShui.SanYuan;

import destiny.iching.Symbol;
import destiny.iching.SymbolAcquired;
import destiny.tools.canvas.ColorCanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * 將 Chart 包裝成 ColorCanvas 
 */
public class ChartColorCanvasWrapper implements Serializable
{
  private final Chart chart;

  @NotNull
  private final ChartBlock[][] chartCoordinate = new ChartBlock[3][3];
  
  public ChartColorCanvasWrapper(@NotNull Chart chart) {
    this.chart = chart;
    
    //chartCoordinate[0][0] = chart.getChartBlock(Symbol.巽); // |  9  |  5  |  7  |
    //chartCoordinate[0][1] = chart.getChartBlock(Symbol.離); // |[0,0]|[0,1]|[0,2]|
    //chartCoordinate[0][2] = chart.getChartBlock(Symbol.坤); // |-----|-----|-----|
    //chartCoordinate[1][0] = chart.getChartBlock(Symbol.震); // |  8  |  1  |  3  |
    chartCoordinate[1][1] = chart.getChartBlock(null);       // |[1,0]|[1,1]|[1,2]|
    //chartCoordinate[1][2] = chart.getChartBlock(Symbol.兌); // |-----|-----|-----|
    //chartCoordinate[2][0] = chart.getChartBlock(Symbol.艮); // |  4  |  6  |  2  |
    //chartCoordinate[2][1] = chart.getChartBlock(Symbol.坎); // |[2,0]|[2,1]|[2,2]|
    //chartCoordinate[2][2] = chart.getChartBlock(Symbol.乾); // |-----------------|

    Symbol tempSymbol = chart.getView(); //底邊
    //從 [2,1] (底邊) 開始，順時鐘設定八個卦的方位
    chartCoordinate[2][1] = chart.getChartBlock(tempSymbol);    //底邊
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[2][0] = chart.getChartBlock(tempSymbol);    //左下
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[1][0] = chart.getChartBlock(tempSymbol);    //左邊
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[0][0] = chart.getChartBlock(tempSymbol);    //左上
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[0][1] = chart.getChartBlock(tempSymbol);    //上方
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[0][2] = chart.getChartBlock(tempSymbol);    //右上
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[1][2] = chart.getChartBlock(tempSymbol);    //右邊
    tempSymbol = SymbolAcquired.Companion.getClockwiseSymbol(tempSymbol);
    chartCoordinate[2][2] = chart.getChartBlock(tempSymbol);    //右下
  }
  
  @NotNull
  public ColorCanvas getColorCanvas()
  {
    /* 56 x 25
    ┌────────┬────────┬────────┐
    │1    3    2    1│1    3    2    1│1    3    2    1│
    │                │                │                │
    │2    1    96   9│2    1    96   9│2    1    96   9│
    │                │                │                │
    │3    乾   一   8│3    乾   一   8│3    乾   一   8│
    │                │                │                │
    │4    5    6    7│4    5    6    7│4    5    6    7│
    ├────────┼────────┼────────┤
    │1    3    2    1│1    3    2    1│1    3    2    1│
    │                │                │                │
    │2    1    96   9│2    1    96   9│2    1    96   9│
    │                │                │                │
    │3    乾   一   8│3    乾   一   8│3    乾   一   8│
    │                │                │                │
    │4    5    6    7│4    5    6    7│4    5    6    7│
    ├────────┼────────┼────────┤
    │1    3    2    1│1    3    2    1│1    3    2    1│
    │                │                │                │
    │2    1    96   9│2    1    96   9│2    1    96   9│
    │                │                │                │
    │3    乾   一   8│3    乾   一   8│3    乾   一   8│
    │                │                │                │
    │4    5    6    7│4    5    6    7│4    5    6    7│
    └────────┴────────┴────────┘
    */
    //calculate();
    
    ColorCanvas chartCanvas = new ColorCanvas(29,56,"　");
    
    chartCanvas.setText("┌────────┬────────┬────────┐" ,  1 , 1 );
    chartCanvas.setText("│                │                │                │" ,  2 , 1 );
    chartCanvas.setText("│                │                │                │" ,  3 , 1 );
    chartCanvas.setText("│                │                │                │" ,  4 , 1 );
    chartCanvas.setText("│                │                │                │" ,  5 , 1 );
    chartCanvas.setText("│                │                │                │" ,  6 , 1 );
    chartCanvas.setText("│                │                │                │" ,  7 , 1 );
    chartCanvas.setText("│                │                │                │" ,  8 , 1 );
    chartCanvas.setText("├────────┼────────┼────────┤" ,  9 , 1 );
    chartCanvas.setText("│                │                │                │" , 10 , 1 );
    chartCanvas.setText("│                │                │                │" , 11 , 1 );
    chartCanvas.setText("│                │                │                │" , 12 , 1 );
    chartCanvas.setText("│                │                │                │" , 13 , 1 );
    chartCanvas.setText("│                │                │                │" , 14 , 1 );
    chartCanvas.setText("│                │                │                │" , 15 , 1 );
    chartCanvas.setText("│                │                │                │" , 16 , 1 );
    chartCanvas.setText("├────────┼────────┼────────┤" , 17 , 1 );
    chartCanvas.setText("│                │                │                │" , 18 , 1 );
    chartCanvas.setText("│                │                │                │" , 19 , 1 );
    chartCanvas.setText("│                │                │                │" , 20 , 1 );
    chartCanvas.setText("│                │                │                │" , 21 , 1 );
    chartCanvas.setText("│                │                │                │" , 22 , 1 );
    chartCanvas.setText("│                │                │                │" , 23 , 1 );
    chartCanvas.setText("│                │                │                │" , 24 , 1 );
    chartCanvas.setText("├────────┴────────┴────────┤" , 25 , 1 );
    chartCanvas.setText("│　XX運，XX山XX向。觀點：背29朝33　　　　　　　　　　│" , 26 , 1 );
    chartCanvas.setText("│　　　　　　　　　　　　　　　　　　　　　　　　　　│" , 27 , 1 );
    chartCanvas.setText("│　　Destiny 命理網　熱情製作 http://destiny.to　　　│" , 28 , 1 );
    chartCanvas.setText("└──────────────────────────┘" , 29 , 1 );

    chartCanvas.setText(getChineseString(chart.getPeriod()) , 26 , 5); // XX運
    chartCanvas.setText(chart.getMountain().toString() , 26 , 11); // XX山
    chartCanvas.setText(chart.getMountain().getOpposite().toString() , 26 , 15 ); // XX向
    chartCanvas.setText(chart.getView().toString() , 26 , 29 ); // 背XX
    chartCanvas.setText(SymbolAcquired.Companion.getOppositeSymbol(chart.getView()).toString() , 26 , 33 ); // 朝XX
    
    ColorCanvas[] chartBlockCanvasArray =  
    { 
      new ColorCanvas(7,16,"　") , new ColorCanvas(7,16,"　") , new ColorCanvas(7,16,"　") ,
      new ColorCanvas(7,16,"　") , new ColorCanvas(7,16,"　") , new ColorCanvas(7,16,"　") ,
      new ColorCanvas(7,16,"　") , new ColorCanvas(7,16,"　") , new ColorCanvas(7,16,"　") 
    };
    
    for (int y=0 ;y<=2 ; y++)
    {
      for (int x=0 ;x<=2 ; x++)
      {
        //設定中文 一 ~ 九
        chartBlockCanvasArray[x*3+y].setText(getChineseString(chartCoordinate[x][y].getPeriod()) , 5,11);
        //設定卦 (方向) , 中宮則填 "中"
        chartBlockCanvasArray[x*3+y].setText(getSymbolString(chartCoordinate[x][y].getSymbol()) , 5,7);
        //設定山盤
        chartBlockCanvasArray[x*3+y].setText( String.valueOf(chartCoordinate[x][y].getMountain()) , 3,11);
        //設定向盤
        chartBlockCanvasArray[x*3+y].setText( String.valueOf(chartCoordinate[x][y].getDirection()) , 3,12);
      }
    }

    chartCanvas.add(chartBlockCanvasArray[0] ,  2 , 3 );
    chartCanvas.add(chartBlockCanvasArray[1] ,  2 , 21);
    chartCanvas.add(chartBlockCanvasArray[2] ,  2 , 39);
    chartCanvas.add(chartBlockCanvasArray[3] , 10 , 3 );
    chartCanvas.add(chartBlockCanvasArray[4] , 10 , 21);
    chartCanvas.add(chartBlockCanvasArray[5] , 10 , 39);
    chartCanvas.add(chartBlockCanvasArray[6] , 18 , 3 );
    chartCanvas.add(chartBlockCanvasArray[7] , 18 , 21);
    chartCanvas.add(chartBlockCanvasArray[8] , 18 , 39);
    
    return chartCanvas;
  }
  
  /** 將數字 1~9 轉成中文 */
  @NotNull
  private static String getChineseString(int i)
  {
    switch (i)
    {
      case 1 : return "一" ;
      case 2 : return "二" ;
      case 3 : return "三" ;
      case 4 : return "四" ;
      case 5 : return "五" ;
      case 6 : return "六" ;
      case 7 : return "七" ;
      case 8 : return "八" ;
      case 9 : return "九" ;
      default : return "　" ;
    }
  }
  

  @NotNull
  private static String getSymbolString(@Nullable Symbol s)
  {
    if (s == null)
      return "中";
    else
      return s.toString();
  }

}

