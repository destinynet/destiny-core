/** 2009/10/20 下午11:52:15 by smallufo */
package destiny.utils;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 一些加解密的工具
 * 這是整個 destiny-core 內，唯一使用到 3rd-party library 的 class (org.apache.commons.codec.binary.Base64)
 * 測試一下，隨便加入一行
 */
public class Cryptor implements Serializable
{
  /** DES 加密 , key 為 8 bytes (64 bits) */
  public final static String getDesEncodedString(String DES_KEY , String raw)
  {
    try
    {
      Key key = new SecretKeySpec(DES_KEY.getBytes() , "DES");
      Cipher cipher;
      cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

      byte[] timeDebugArray = raw.getBytes();
      cipher.init(Cipher.ENCRYPT_MODE,key);
      byte[] encryptedObject=cipher.doFinal(timeDebugArray);
      byte[] base64bytes = Base64.encodeBase64(encryptedObject);
      String result = new String(base64bytes);
      String urlSafe = result.replace('/', '_'); //for URL-safe , 把 '/' 換成 '_'

      //去除掉尾巴的等號 '='
      if (!urlSafe.contains("="))
        return urlSafe;
      else
        return urlSafe.substring(0 , urlSafe.indexOf('='));
    }
    catch (Exception ignored)
    {
      ignored.printStackTrace();
    }
    return null;
  }
  
  public final static String getDesDecodedString(String DES_KEY , String encodedString)
  {
    int rest = encodedString.length() % 4;
    int padding = 0;
    if (rest != 0)
      padding = 4-rest;
    StringBuffer sb = new StringBuffer();
    for(int i=0 ; i < padding ; i ++)
      sb.append('=');
    encodedString = encodedString + sb.toString();
    
    //原始的 encodedString 是 url-safe , 要轉成 base64 的 '/'
    encodedString = encodedString.replace('_', '/');
    try
    {
      Key key = new SecretKeySpec(DES_KEY.getBytes() , "DES");
      Cipher cipher;

      cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      byte[] base64bytes = encodedString.getBytes();
      byte[] decodedBase64bytes = Base64.decodeBase64(base64bytes);

      cipher.init(Cipher.DECRYPT_MODE,key);
      byte[] array = cipher.doFinal(decodedBase64bytes);
      return new String(array).trim();
    }
    catch (java.security.GeneralSecurityException e)
    {
      e.printStackTrace();
    }
    return null;

  }
  
  public final static String MD5(String text)
  {
    String md5Text = "";
    try
    {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      md5Text = new BigInteger(1, digest.digest((text).getBytes())).toString(16);
    }
    catch (Exception e)
    {
      System.err.println("Error in call to MD5");
    }

    if (md5Text.length() == 31)
    {
      md5Text = "0" + md5Text;
    }
    return md5Text;
  }

}

