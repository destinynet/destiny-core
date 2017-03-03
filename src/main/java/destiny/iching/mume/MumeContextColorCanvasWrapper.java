/**
 * @author smallufo 
 * Created on 2007/2/11 at 上午 3:50:01
 */ 
package destiny.iching.mume;

import destiny.core.calendar.eightwords.EightWords;
import destiny.iching.HexagramIF;
import destiny.iching.Symbol;
import destiny.iching.contentProviders.HexagramNameFullIF;
import destiny.tools.ColorCanvas.ColorCanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

/**
 * 將 梅花易的 Context (MumeContext) 包裝成彩色 ColorCanvas
 * @author smallufo
 */
public class MumeContextColorCanvasWrapper implements Serializable
{

  private MumeContext mumeContext;
  private String metaData;
  private String dateInfo =""; //日期/時間/地點經緯度 等資料
  private EightWords eightWords; //八字
  private HexagramNameFullIF hexagramNameFull;
  
  public MumeContextColorCanvasWrapper(HexagramNameFullIF hexagramNameFull)
  {
    this.hexagramNameFull = hexagramNameFull;
  }
  

  @NotNull
  @Override
  public String toString()
  {
    ColorCanvas c = new ColorCanvas(12, 74, "　");
    ColorCanvas siteCanvas = new ColorCanvas(2 , 74 , "　");
    
    try
    {
      siteCanvas.setText("Destiny 命理網", 1, 1, Optional.empty() , Optional.empty() , Optional.empty(), Optional.of(new URL("http://destiny.to")), Optional.of("Destiny命理網"), false);
    }
    catch (MalformedURLException ignored)
    {
    }
    siteCanvas.setText(" http://destiny.to ", 1, 17);
    siteCanvas.setText("梅花易數線上排盤" + "　" + metaData , 2, 1);
    c.add(siteCanvas, 1, 1);
    ColorCanvas dateCanvas = new ColorCanvas(1 , 38 , "　");
    dateCanvas.setText(dateInfo, 1, 1);
    c.add(dateCanvas, 3, 1);
    
    ColorCanvas 八字Canvas = new ColorCanvas(3 , 8 , "　");
    八字Canvas.setText("時日月年", 1, 1);
    八字Canvas.setText(eightWords.getHourStem().toString(), 2, 1); //時干
    八字Canvas.setText(eightWords.getHourBranch().toString(), 3, 1); //時支
    八字Canvas.setText(eightWords.getDayStem().toString(), 2, 3); //日干
    八字Canvas.setText(eightWords.getDayBranch().toString(), 3, 3); //日支
    八字Canvas.setText(eightWords.getMonthStem().toString(), 2, 5); //月干
    八字Canvas.setText(eightWords.getMonthBranch().toString(), 3, 5); //月支
    八字Canvas.setText(eightWords.getYearStem().toString(), 2, 7); //年干
    八字Canvas.setText(eightWords.getYearBranch().toString(), 3, 7); //年支
    c.add(八字Canvas, 1, 51);
    
    //純粹五個卦 , 不包含時間等其他資訊
    ColorCanvas mainCanvas = new ColorCanvas(8, 74 , "　");
    
    //體卦變卦
    int motivate = getMumeContext().getMotivate(); //動爻
    if (motivate == 1 || motivate == 2 || motivate == 3)
    {
      //體卦上卦
      mainCanvas.setText("體卦", 4, 1);
      //用卦下卦
      mainCanvas.setText("用卦", 7, 1);
    }
    else
    {
      //用卦上卦
      mainCanvas.setText("用卦", 4, 1);
      //體卦下卦
      mainCanvas.setText("體卦", 7, 1);
    }
    
    ColorCanvas 本卦canvas = new ColorCanvas(8 , 12 , "　");
    本卦canvas.setText("【本　卦】", 1, 1);
    
    本卦canvas.add(getColorCanvas(mumeContext.getHexagram()), 2, 1);
    mainCanvas.add(本卦canvas, 1, 5);
    
    //變爻
    if (mumeContext.getHexagram().getLine(mumeContext.getMotivate()))
      mainCanvas.setText("◎", 9-mumeContext.getMotivate() ,17 );
    else
      mainCanvas.setText("〤", 9-mumeContext.getMotivate() ,17 );
    
    ColorCanvas 變卦Canvas = new ColorCanvas(8, 12 , "　");
    變卦Canvas.setText("【變　卦】", 1, 1);
    變卦Canvas.add(getColorCanvas(  mumeContext.getTargetHexagram()), 2, 1);
    mainCanvas.add(變卦Canvas , 1 , 21);
    
    ColorCanvas 互卦Canvas = new ColorCanvas(8, 12 , "　");
    互卦Canvas.setText("【互　卦】", 1, 1);
    互卦Canvas.add(getColorCanvas(mumeContext.getHexagram().getMiddleSpanHexagram()), 2, 1);
    mainCanvas.add(互卦Canvas , 1 , 35);
    
    ColorCanvas 錯卦Canvas = new ColorCanvas(8, 12 , "　");
    錯卦Canvas.setText("【錯　卦】", 1, 1);
    錯卦Canvas.add(getColorCanvas(mumeContext.getHexagram().getInterlacedHexagram()), 2, 1);
    mainCanvas.add(錯卦Canvas , 1 , 49);
    
    ColorCanvas 綜卦Canvas = new ColorCanvas(8, 12 , "　");
    綜卦Canvas.setText("【綜　卦】", 1, 1);
    綜卦Canvas.add(getColorCanvas(mumeContext.getHexagram().getReversedHexagram()), 2, 1);
    mainCanvas.add(綜卦Canvas , 1 , 63);
    
    c.add(mainCanvas , 5 , 1);
    return c.getHtmlOutput();
  }
  
  @NotNull
  private ColorCanvas getColorCanvas(@NotNull HexagramIF hexagram)
  {
    ColorCanvas cc = new ColorCanvas(7 , 12 , "　");
    
    String name = hexagramNameFull.getNameFull(hexagram, Locale.TRADITIONAL_CHINESE);
    //卦名
    if (name.length() == 4)
      cc.setText(" " + name+" ", 1, 1 );
    else
      cc.setText(name, 1, 3 );
    cc.add(getColorCanvas(hexagram.getUpperSymbol()), 2, 1);
    cc.add(getColorCanvas(hexagram.getLowerSymbol()), 5, 1);
    return cc;
  }

  @Nullable
  private ColorCanvas getColorCanvas(@NotNull Symbol s)
  {
    String color; //卦的顏色
    switch(s.getFiveElement())
    {
      case 木: color = "GREEN"; break;
      case 火: color = "RED"; break;
      case 土: color = "CC6633"; break;
      case 金: color = "999999"; break;
      case 水: color = "BLACK"; break;
      default: throw new RuntimeException("impossible");
    }
    ColorCanvas cc = new ColorCanvas(3,12,"　", Optional.of(color) , Optional.empty());
    for (int i=3 ; i >=1 ; i--)
    {
      if (s.getBooleanValue(i))
        cc.setText("▅▅▅▅▅", 4-i, 1);
      else
        cc.setText("▅▅　▅▅", 4-i, 1);
    }
    cc.setText( String.valueOf(s.getName())        , 2, 11);
    cc.setText( String.valueOf(s.getFiveElement()) , 3, 11);
    return cc;
  }


  public MumeContext getMumeContext()
  {
    return mumeContext;
  }

  public void setMumeContext(MumeContext mumeContext)
  {
    this.mumeContext = mumeContext;
  }


  public String getDateInfo()
  {
    return dateInfo;
  }


  public void setDateInfo(String dateInfo)
  {
    this.dateInfo = dateInfo;
  }


  public EightWords getEightWords()
  {
    return eightWords;
  }


  public void setEightWords(EightWords eightWords)
  {
    this.eightWords = eightWords;
  }


  public String getMetaData()
  {
    return metaData;
  }


  public void setMetaData(String metaData)
  {
    this.metaData = metaData;
  }
}
