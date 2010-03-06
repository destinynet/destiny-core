/**
 * @author smallufo 
 * Created on 2008/5/11 at 下午 3:35:25
 */
package destiny.utils.test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RBControl
{
  public static void main(String[] args)
  {
    test(Locale.CHINA);
    test(new Locale("zh", "HK"));
    test(Locale.TAIWAN);
    test(Locale.CANADA);
  }

  private static void test(Locale locale)
  {
    ResourceBundle rb = ResourceBundle.getBundle("destiny.utils.test.RBControl", locale, new ResourceBundle.Control()
    {
      @Override
      public List<Locale> getCandidateLocales(String baseName, Locale locale)
      {
        if (baseName == null)
          throw new NullPointerException();
        if (locale.equals(new Locale("zh", "HK")))
        {
          return Arrays.asList(locale, Locale.TAIWAN,
          // no Locale.CHINESE here
          Locale.ROOT);
        }
        else if (locale.equals(Locale.TAIWAN))
        {
          return Arrays.asList(locale,
          // no Locale.CHINESE here
          Locale.ROOT);
        }
        return super.getCandidateLocales(baseName, locale);
      }
      
    });
    System.out.println("locale: " + locale);
    System.out.println("\tregion: " + rb.getString("region"));
    System.out.println("\tlanguage: " + rb.getString("language"));
  }
}
