/**
 * @author smallufo
 * Created on 2005/4/6 at 下午 04:07:42
 */
package destiny.core.calendar.eightwords;


import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 十神 , 天干之間互相的互動關係 : 比間，劫財，正財...
 */
public enum Reactions
{
  比肩("比肩"),
  劫財("劫財"),
  正財("正財"),
  偏財("偏財"),
  正印("正印"),
  偏印("偏印"),
  食神("食神"),
  傷官("傷官"),
  正官("正官"),
  七殺("七殺");

  private final String value;

  Reactions(String value )
  {
    this.value = value;
  }

  /** 比肩.getPairReaction() == 劫財 */
  @NotNull
  public Reactions getPairReaction() {
    switch (this) {
      case 比肩: return 劫財;
      case 劫財: return 比肩;
      case 正印: return 偏印;
      case 偏印: return 正印;
      case 正官: return 七殺;
      case 七殺: return 正官;
      case 食神: return 傷官;
      case 傷官: return 食神;
      case 正財: return 偏財;
      case 偏財: return 正財;
    }
    throw new RuntimeException("RuntimeException occurs when getPairReaction() of " + this.toString());
  }

  public String toString()
  {
    return value;
  }

  public String toString(Locale locale) {
    return ResourceBundle.getBundle(Reactions.class.getName(), locale).getString(name());
  }

  /** 縮寫 */
  public String getAbbreviation(Locale locale) {
    return ResourceBundle.getBundle(Reactions.class.getName(), locale).getString(name()+"_ABBR");
  }

}
