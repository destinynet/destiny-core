/** 2009/10/12 上午3:22:12 by smallufo */
package destiny.core.calendar.eightwords.fourwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;

/** 讀取 1440.txt */
public class FourWordsImpl implements FourWordsIF , Serializable 
{
  private final static Map<FourWordsKey , String> map = Collections.synchronizedMap(new HashMap<FourWordsKey , String>());
  static
  {
    URL url = FourWordsImpl.class.getResource("1440.txt");
    File file;
    BufferedReader bReader = null;
    
    try
    {
      file = new File(url.toURI());
      FileReader fReader = new FileReader(file);
      bReader = new BufferedReader(fReader);
      
      String line = null;
      while ((line = bReader.readLine()) != null)
      {
        StringTokenizer st = new StringTokenizer(line , " ");
        String first = st.nextToken();
        
        // TODO : 去掉 BOM Header , 完整的做法應該參考這裡 http://samsharehome.blogspot.com/2009/01/utf-8-bominputstreamreader.html
        HeavenlyStems dayStem = HeavenlyStems.getHeavenlyStems(first.length() == 1 ? first.charAt(0) : first.charAt(1));
        EarthlyBranches monthBranch = EarthlyBranches.valueOf(st.nextToken());
        EarthlyBranches hourBranch = EarthlyBranches.valueOf(st.nextToken());
        String value = st.nextToken();
        
        FourWordsKey key = new FourWordsKey(dayStem, monthBranch, hourBranch);
        map.put(key, value);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        bReader.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public FourWordsImpl()
  {
  }

  @Override
  public String getResult(EightWords ew)
  {
    FourWordsKey key = new FourWordsKey(ew.getDayStem(), ew.getMonthBranch(), ew.getHourBranch());
    return map.get(key);
  }

}

