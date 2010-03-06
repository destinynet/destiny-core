/** 2009/12/8 上午12:47:51 by smallufo */
package destiny.utils.location;

import java.io.IOException;
import java.util.Locale;

public interface PlacenameTranslator
{
  /** 要將此 placename 翻譯成哪個 locale 的地名 , placename 一定要是英文 */
  public String getName(String placename , Locale locale) throws IOException;
}

