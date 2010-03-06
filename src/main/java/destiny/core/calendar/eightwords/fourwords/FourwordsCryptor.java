/** 2009/10/20 下午11:46:47 by smallufo */
package destiny.core.calendar.eightwords.fourwords;

import java.io.Serializable;

import destiny.utils.Cryptor;

public class FourwordsCryptor implements Serializable
{
  private String desKey;
  
  public FourwordsCryptor(String key)
  {
    this.desKey = Cryptor.MD5(key).substring(0,8);
    //System.out.println("key = " + desKey);
  }
  
  public String getEncodedString(String raw)
  {
    return Cryptor.getDesEncodedString(desKey, raw);
  }


  public String getDecodedString(String encodedString)
  {
    return Cryptor.getDesDecodedString(desKey, encodedString);
  }

}

