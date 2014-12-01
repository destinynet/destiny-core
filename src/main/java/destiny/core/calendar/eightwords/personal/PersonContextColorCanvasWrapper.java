/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.Time;
import destiny.core.calendar.TimeDecoratorChinese;
import destiny.core.calendar.eightwords.ContextColorCanvasWrapper;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.StemBranch;
import destiny.utils.ColorCanvas.AlignUtil;
import destiny.utils.ColorCanvas.ColorCanvas;
import destiny.utils.Decorator;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PersonContextColorCanvasWrapper extends ContextColorCanvasWrapper {

  /** 預先儲存已經計算好的結果 */
  private final PersonContextModel viewModel;

  /** 地支藏干的實作，內定採用標準設定 */
  private HiddenStemsIF hiddenStemsImpl;

  private ColorCanvas cc;

  public enum OutputMode {HTML , TEXT}

  private OutputMode outputMode = OutputMode.HTML;

  private PersonContextModel.FortuneOutputFormat fortuneOutputFormat = PersonContextModel.FortuneOutputFormat.虛歲;

  private final Decorator<Time> timeDecorator = new TimeDecoratorChinese();

  public PersonContextColorCanvasWrapper(@NotNull PersonContextModel model, String locationName, HiddenStemsIF hiddenStemsImpl, String linkUrl) {
    super(model.getPersonContext(), model.getPersonContext().getLmt() , model.getPersonContext().getLocation() , locationName , hiddenStemsImpl , linkUrl);
    this.viewModel = model;
    this.hiddenStemsImpl = hiddenStemsImpl;
  }

  public void setOutputMode(OutputMode mode)
  {
    this.outputMode = mode;
  }

  /** 取得八字命盤 */
  @NotNull
  @Override
  public String toString()
  {
    PersonContext personContext = viewModel.getPersonContext();

    cc = new ColorCanvas(30,70,"　");

    ColorCanvas metaDataColorCanvas = getMetaDataColorCanvas();

    cc.add(metaDataColorCanvas , 1 , 1); // 國曆 農曆 經度 緯度 短網址 等 MetaData

    cc.setText("性別：", 1, 59);
    cc.setText(personContext.getGender().toString() , 1, 65); // '男' or '女'
    cc.setText("性" , 1 , 67);

    cc.setText("八字：" , 9 , 1);

    EightWords eightWords = personContext.getEightWords();

    ReactionsUtil reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);

    cc.add(getEightWordsColorCanvas() , 9 , 9); // 純粹八字盤


    ColorCanvas 大運直 = new ColorCanvas(9,24,"　" );
    ColorCanvas 大運橫 = new ColorCanvas(8,70,"　" , Optional.empty() , Optional.empty());

    for (int i=1 ; i <= viewModel.getFortuneDatas().size() ; i++) {
      FortuneData fortuneData = viewModel.getFortuneDatas().get(i-1);
      int startFortune = fortuneData.getStartFortune();
      int   endFortune = fortuneData.getEndFortune();
      StemBranch nextStemBranch = fortuneData.getStemBranch();
      Time startFortuneLmt = fortuneData.getStartFortuneLmt();
      Time   endFortuneLmt = fortuneData.getEndFortuneLmt();

      大運直.setText(AlignUtil.alignRight(startFortune, 6) , i , 1 , "green" , null , "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt));
      大運直.setText("→" , i , 9 , "green" );
      大運直.setText(AlignUtil.alignRight(endFortune , 6) , i , 13 , "green" , null , "終運時刻：" + timeDecorator.getOutputString(endFortuneLmt));
      大運直.setText(nextStemBranch.toString() , i , 21 , "green");

      大運橫.setText(AlignUtil.alignCenter(startFortune , 6) , 1 , 73-8*i , "green" , null , "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt));
      Reactions reaction = reactionsUtil.getReaction(nextStemBranch.getStem() , eightWords.getDay().getStem());
      大運橫.setText(reaction.toString().substring(0,1) , 2 , 75-8*i , "gray");
      大運橫.setText(reaction.toString().substring(1,2) , 3 , 75-8*i , "gray");
      大運橫.setText(nextStemBranch.getStem()  .toString() , 4 , 75-8*i , "red");
      大運橫.setText(nextStemBranch.getBranch().toString() , 5 , 75-8*i , "red");
      大運橫.add(地支藏干(nextStemBranch.getBranch() , eightWords.getDay().getStem()) , 6 , 73-8*i);
    }

    cc.setText("大運（"+fortuneOutputFormat +"）", 8, 55);
    cc.add(大運直, 9, 47);
    cc.add(大運橫 , 20 , 1);

    ColorCanvas 節氣 = new ColorCanvas(2 , cc.getWidth() ,  "　");
    SolarTerms prevMajorSolarTerms = viewModel.getPrevMajorSolarTerms();
    SolarTerms nextMajorSolarTerms = viewModel.getNextMajorSolarTerms();


    Time prevMajorSolarTermsTime = new Time(personContext.getLmt() , personContext.getTargetMajorSolarTermsSeconds(-1) );
    節氣.setText(prevMajorSolarTerms.toString() , 1 , 1);
    節氣.setText("：" , 1, 5);
    節氣.setText(this.timeDecorator.getOutputString(prevMajorSolarTermsTime) , 1,7);

    Time nextMajorSolarTermsTime = new Time(personContext.getLmt() , personContext.getTargetMajorSolarTermsSeconds(1) );
    節氣.setText(nextMajorSolarTerms.toString() , 2 , 1);
    節氣.setText("：" , 2, 5);
    節氣.setText(this.timeDecorator.getOutputString(nextMajorSolarTermsTime) , 2,7);

    cc.add(節氣 , 29 , 1);

    switch(this.outputMode)
    {
      case TEXT:
        return cc.toString();
      case HTML:
        return cc.getHtmlOutput();
      default:
        return cc.getHtmlOutput();
    }
  } // toString()

  /**
   * 設定大運輸出的格式
   */
  public void setFortuneOutputFormat(PersonContextModel.FortuneOutputFormat fortuneOutputFormat)
  {
    this.fortuneOutputFormat = fortuneOutputFormat;
  }
}
