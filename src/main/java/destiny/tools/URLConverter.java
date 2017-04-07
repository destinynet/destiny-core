/**
 * @author smallufo 
 * Created on 2007/2/16 at 上午 1:00:13
 */
package destiny.tools;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO : 將 URL (http / ftp / email ... ) 轉換成 HTML 的 link , 目前 (2009/11/10) 只完成了 http , 而且無法處理中時電子報的怪網址 (有逗點)
 */
public class URLConverter implements Serializable
{
  public URLConverter()
  {
  }

  @NotNull
  public String convert(@NotNull String initialText)
  {
    StringBuffer result = new StringBuffer(initialText.length());

    Pattern p = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?+%/.\\w]+)?" , Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    Matcher m = p.matcher(initialText);
    while (m.find())
    {
      String href = m.group();
      if (href.startsWith("@"))
      {
        continue;
      }

      // ignore links that are already hyperlinks  
      if (href.startsWith("href"))
      {
        continue;
      }

      // TODO: add more top domains
      if (StringUtils.indexOfAny(href.toLowerCase() , ".com" , ".net" , ".org" , ".to" , ".tw" ,".ly" , ".cc" , ".us") != -1) {
        if (!href.startsWith("http://") && !href.startsWith("HTTP://")) {
          // add on the http:// if necessary  
          m.appendReplacement(result, "<a href=\"" + "http://" + href + "\" target=\"_blank\">" + href + "</a>");
        }
        else
          m.appendReplacement(result, "<a href=\"" + href + "\" target=\"_blank\">" + href + "</a>");
      }
    }
    m.appendTail(result);
    return result.toString();
  }
}
