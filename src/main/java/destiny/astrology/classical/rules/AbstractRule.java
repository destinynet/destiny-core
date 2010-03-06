/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:21:29
 */ 
package destiny.astrology.classical.rules;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import destiny.utils.LocaleUtils;

public abstract class AbstractRule implements RuleIF , Serializable
{
  protected String resource;
  
  private Locale locale =  Locale.getDefault();
  
  /** 名稱key */
  protected String nameKey;
  
  /** 儲存註解 */
  Map<Locale , String> localeCommentMap = Collections.synchronizedMap(new HashMap<Locale , String>());

  public AbstractRule(String nameKey , String resource)
  {
    this.nameKey = nameKey;
    this.resource = resource;
  }
  
  protected void addComment(Locale locale , String value)
  {
    this.localeCommentMap.put(locale , value);
  }

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

  
  /** 取得註解 */
  @Override
  public String getComment()
  {
    return LocaleUtils.getString(localeCommentMap, locale);
  }
  
  /** 取得某 Locale 之下的註解 */ 
  @Override
  public String getComment(Locale locale)
  {
    return LocaleUtils.getString(localeCommentMap, locale);
  }
  
  
  @Override
  public String toString()
  {
    String str = ResourceBundle.getBundle(resource , locale).getString(nameKey);
    return str + "\n" + LocaleUtils.getString(localeCommentMap, locale) ;
  }

  public String toString(Locale locale)
  {
    String str = ResourceBundle.getBundle(resource , locale).getString(nameKey);
    return str + "\n" + LocaleUtils.getString(localeCommentMap, locale);
  }


  public void setLocale(Locale locale)
  {
    this.locale = locale;
  }
  
}
