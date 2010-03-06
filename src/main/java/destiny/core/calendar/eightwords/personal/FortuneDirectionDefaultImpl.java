/*
 * @author smallufo
 * @date 2005/5/17
 * @time 上午 07:49:08
 */
package destiny.core.calendar.eightwords.personal;

import java.io.Serializable;

import destiny.core.Gender;
import destiny.core.chinese.YinYang;

/**
 * 大運的順逆，內定演算法：陽男陰女順行；陰男陽女逆行
 */
public class FortuneDirectionDefaultImpl implements FortuneDirectionIF , Serializable
{
  public FortuneDirectionDefaultImpl()
  {
  }
  
  /** 大運的順逆，內定演算法：陽男陰女順行；陰男陽女逆行 */
  public boolean isForward(EightWordsPersonContext personContext)
  {
    if ( (personContext.getGender() == Gender.男 && personContext.getEightWords().getYear().getStem().getYinYang() == YinYang.陽) ||
         (personContext.getGender() == Gender.女 && personContext.getEightWords().getYear().getStem().getYinYang() == YinYang.陰) )
      return true;
    else
      return false;
  }

}
