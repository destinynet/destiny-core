/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:21:29
 */ 
package destiny.astrology.classical.rules;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.LocaleStringIF;
import destiny.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class AbstractRule implements RuleIF , Serializable , LocaleStringIF
{
  private Logger logger = LoggerFactory.getLogger(getClass());

  protected String resource;
  
  private Locale locale =  Locale.getDefault();
  
  /** 名稱key */
  protected String nameKey;
  
  private String commentKey=null;
  
  /** 裡面的 objects 不能直接拿來作為 MessageFormat 的 參數，要先經過 getCommentParameters(locale) 處理，取得應該替換 正確的字串 */ 
  private Object[] commentParameters;

  public AbstractRule(String resource)
  {
    this.nameKey = getClass().getSimpleName();
    this.resource = resource;
  }
  
  @Override
  public final boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    logger.info("'{}' : isApplicable({})" , getClass().getSimpleName() ,  planet);
    Tuple<String , Object[]> result = getResult(planet, horoscopeContext);
    if (result == null)
      return false;
    
    commentKey = result.getFirst();
    commentParameters = result.getSecond();
    return true;
  }
  
  /**
   * Tuple<String , Object[]> 參數：
   * String 為 ResourceBundle 取得的 key , 前面要 prepend '[rule_name].'
   * Object[] 為 MessageFormat.format(pattern , Object[]) 後方的參數
   */
  protected abstract Tuple<String , Object[]> getResult(Planet planet , HoroscopeContext horoscopeContext);
  
  /** 名稱 */
  @Override
  public String getName()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }
  
  /** 名稱 */
  @Override
  public String getName(Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }

  /** 設定註解參數 , 檢查參數是否是 LocaleStringIF , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters */
  private Object[] getCommentParemeters(Locale locale)
  {
    Object[] newCommentParameters = new Object[commentParameters.length];
    for(int i=0 ; i < commentParameters.length ; i++)
    {
      if (commentParameters[i] instanceof LocaleStringIF)
        newCommentParameters[i] = ((LocaleStringIF) commentParameters[i]).toString(locale);
      else if (commentParameters[i] instanceof Double)
        newCommentParameters[i] = ((Double)commentParameters[i]).toString().substring(0, 5); //避免 double 輸出太長
      else
        newCommentParameters[i] = commentParameters[i];
    }
    return newCommentParameters;
  }
  
  /** 取得註解 */
  @Override
  public String getComment()
  {
    return getComment(locale);
  }
  
  /** 取得某 Locale 之下的註解 */ 
  @Override
  public String getComment(Locale locale)
  {
    try
    {
      String pattern = ResourceBundle.getBundle(resource , locale).getString(nameKey+"."+this.commentKey);
      String result = MessageFormat.format(pattern, getCommentParemeters(locale));
      //System.err.println("result = " + result);
      return result;  
    }
    catch(MissingResourceException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  
  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }

  @Override
  public String toString(Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }


  public void setLocale(Locale locale)
  {
    this.locale = locale;
  }


  
}
