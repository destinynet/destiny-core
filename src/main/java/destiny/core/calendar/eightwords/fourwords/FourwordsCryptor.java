/** 2009/10/20 下午11:46:47 by smallufo */
package destiny.core.calendar.eightwords.fourwords;

import java.io.Serializable;

import destiny.utils.Cryptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FourwordsCryptor implements Serializable
{
  private String desKey;
  
  public FourwordsCryptor(@NotNull String key)
  {
    this.desKey = Cryptor.MD5(key).substring(0,8);
    //System.out.println("key = " + desKey);
  }
  
  @Nullable
  public String getEncodedString(@NotNull String raw)
  {
    return Cryptor.getDesEncodedString(desKey, raw);
  }


  @Nullable
  public String getDecodedString(@NotNull String encodedString)
  {
    return Cryptor.getDesDecodedString(desKey, encodedString);
  }

}

