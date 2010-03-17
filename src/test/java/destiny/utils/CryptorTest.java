/** 2009/10/21 上午12:29:52 by smallufo */
package destiny.utils;

import java.util.Random;

import junit.framework.TestCase;

public class CryptorTest extends TestCase
{
  private final static String desKey = "2ed9e917";
  
  /**
   *  Thread-Safe 測試 , key = "2ed9e917"
   *  1 -> PDrOZ9oIGtw
   *  2 -> A4XspjXk9NQ
   * @throws Exception 
   */
  public void testGetDesEncodedString() throws Exception
  {
    Random r = new Random(System.currentTimeMillis());
    
    int SIZE = 10000;
    
    CryptorThread[] ta = new CryptorThread[SIZE];
    
    for(int i=0 ; i < SIZE ; i ++)
    {
      if (r.nextBoolean())
        ta[i] = new CryptorThread(desKey , "1");
      else
        ta[i] = new CryptorThread(desKey , "2");
      
    }
    
    for(Thread t : ta)
      t.start();
    
    for(Thread t : ta)
      t.join();
    
    for(CryptorThread t : ta)
    {
      if (t.getRaw().equals("1"))
        assertEquals("PDrOZ9oIGtw" , t.getResult());
      else
        assertEquals("A4XspjXk9NQ" , t.getResult());
    }
  }
  
  private class CryptorThread extends Thread
  {
    private String key;
    private String raw;
    private String result;
    
    public CryptorThread(String key , String raw)
    {
      this.key = key;
      this.raw = raw;
    }

    @Override
    public void run()
    {
      this.result = Cryptor.getDesEncodedString(key, raw);
    }
    
    public String getRaw()
    {
      return raw;
    }
    
    public String getResult()
    {
      return result;
    }
  }

}

