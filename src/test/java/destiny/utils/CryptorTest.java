/** 2009/10/21 上午12:29:52 by smallufo */
package destiny.utils;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

public class CryptorTest
{
  private final static String DES_KEY = "2ed9e917";
  
  @Test
  public void testEncodeDecodeDes()
  {
    System.out.println(Cryptor.getDesEncodedString(DES_KEY, "1"));
    System.out.println(Cryptor.getDesEncodedString(DES_KEY, "01"));
    System.out.println(Cryptor.getDesEncodedString(DES_KEY, "00000001"));
    System.out.println(Cryptor.getDesEncodedString(DES_KEY, "00000001"));
    
    String uuid = UUID.randomUUID().toString();
    String des = Cryptor.getDesEncodedString(DES_KEY, uuid);
    System.out.println("uuid = " + uuid + " , des = " + des);
    System.out.println("decoded = " + Cryptor.getDesDecodedString(DES_KEY, des+"0"));
    
    System.out.println("from uuid = " + UUID.fromString(uuid+"F"));
  }
  
  /**
   *  Thread-Safe 測試 , key = "2ed9e917"
   *  1 -> PDrOZ9oIGtw
   *  2 -> A4XspjXk9NQ
   * @throws Exception 
   */
  public void testGetDesEncodedString() throws Exception
  {
    Random r = new Random(System.currentTimeMillis());
    
    int SIZE = 100;
    
    CryptorThread[] ta = new CryptorThread[SIZE];
    
    for(int i=0 ; i < SIZE ; i ++)
    {
      if (r.nextBoolean())
        ta[i] = new CryptorThread(DES_KEY , "1");
      else
        ta[i] = new CryptorThread(DES_KEY , "2");
      
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

