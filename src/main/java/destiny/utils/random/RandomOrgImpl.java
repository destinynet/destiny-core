/**
 * @author smallufo 
 * Created on 2008/5/2 at 上午 1:26:29
 */ 
package destiny.utils.random;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Random.org 的 Integer Generator 實作 <br>
 * 參考資料 <br>
 * http://www.random.org/clients/http/
 */
public class RandomOrgImpl implements RandomIF , Serializable
{
  public RandomOrgImpl()
  {
  }

  @Override
  public int[] getIntegers(int count, int min, int max)
  {
    String url = "http://www.random.org/integers/?num="+count+"&min="+min+"&max="+max+"&col=1&base=10&format=plain&rnd=new";
    try
    {
      URL u = new URL(url);
      HttpURLConnection uc = (HttpURLConnection) u.openConnection();
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      InputStream is = uc.getInputStream();
      byte[] buf=new byte[10];
      int i;
      while ((i=is.read(buf)) != -1)
        baos.write(buf, 0, i);
      is.close();
      StringTokenizer st = new StringTokenizer(baos.toString());
      if (st.countTokens() != count)
        throw new RuntimeException("Cannot get " + count + " random integers from random.org !");
      
      int[] results = new int[count];
      for(int j=0 ; j<count ; j++)
        results[j] = Integer.valueOf(st.nextToken()).intValue();
      return results;
    }
    catch (RuntimeException e)
    {
      throw new RuntimeException(e);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
