/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:21:29
 */ 
package destiny.astrology.classical.rules;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class AbstractRule implements RuleIF , Serializable , LocaleStringIF {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private final String resource;
  
  private Locale locale =  Locale.getDefault();
  
  /** 名稱key */
  private final String nameKey;
  
  protected AbstractRule(String resource)
  {
    this.nameKey = getClass().getSimpleName();
    this.resource = resource;
  }
  
  @Override
  public final boolean isApplicable(Planet planet, Horoscope h) {
    logger.debug("'{}' : isApplicable({})" , getClass().getSimpleName() ,  planet);
    return getResult(planet, h).isPresent();
  }
  
  /**
   * Tuple<String , Object[]> 參數：
   * String 為 ResourceBundle 取得的 key , 前面要 prepend '[rule_name].'
   * Object[] 為 MessageFormat.format(pattern , Object[]) 後方的參數
   */
  protected abstract Optional<Tuple2<String, Object[]>> getResult(Planet planet, Horoscope h);
  
  /** 名稱 */
  @Override
  public String getName()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }
  
  /** 名稱 */
  @Override
  public String getName(@NotNull Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }

  /** 設定註解參數 , 檢查參數是否是 LocaleStringIF , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters */
  @NotNull
  private Object[] getCommentParameters(Locale locale , Object[] commentParameters) {
    Object[] newCommentParameters = new Object[commentParameters.length];
    for (int i = 0; i < commentParameters.length; i++) {
      if (commentParameters[i] instanceof LocaleStringIF)
        newCommentParameters[i] = ((LocaleStringIF) commentParameters[i]).toString(locale);
      else if (commentParameters[i] instanceof Double)
        newCommentParameters[i] = commentParameters[i].toString().substring(0, 5); //避免 double 輸出太長
      else
        newCommentParameters[i] = commentParameters[i];
    }
    return newCommentParameters;
  }


  /** 取得某 Locale 之下的註解 */
  @Override
  public Optional<String> getComment(Planet planet, Horoscope h, @NotNull Locale locale) {
    return getResult(planet, h).map(tuple -> {
      String commentKey = tuple.v1();
      Object[] commentParameters = tuple.v2();
      String pattern = ResourceBundle.getBundle(resource, locale).getString(nameKey + "." + commentKey);
      return MessageFormat.format(pattern, getCommentParameters(locale , commentParameters));
    });
  }

  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }

  @Override
  public String toString(@NotNull Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }


  public void setLocale(Locale locale)
  {
    this.locale = locale;
  }


  
}
