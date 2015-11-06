/** 2009/12/8 上午12:47:51 by smallufo */
package destiny.tools.location;

import java.util.Locale;
import java.util.Optional;

public interface PlacenameTranslator
{
  /** 要將此 placename 翻譯成哪個 locale 的地名 , placename 一定要是英文 */
  Optional<String> getName(String placename , Locale locale);
}

