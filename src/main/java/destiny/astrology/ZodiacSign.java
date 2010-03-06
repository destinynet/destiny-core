/**
 * @author smallufo 
 * Created on 2007/8/29 at 下午 3:10:18
 */ 
package destiny.astrology;

import java.util.Locale;
import java.util.ResourceBundle;

import destiny.core.chinese.YinYang;
import destiny.utils.LocaleStringIF;

/** 黃道十二宮 */
public enum ZodiacSign implements LocaleStringIF
{
  /** Aries 戌/牡羊 */
  ARIES      ("ZodiacSign.ARIES"       , "ZodiacSign.ARIES_ABBR"       , Element.FIRE  , Quality.CARDINAL , YinYang.陽 ,   0),
  /** Taurus 酉/金牛 */
  TAURUS     ("ZodiacSign.TAURUS"      , "ZodiacSign.TAURUS_ABBR"      , Element.EARTH , Quality.FIXED    , YinYang.陰 , 30),
  /** Gemini 申/雙子 */
  GEMINI     ("ZodiacSign.GEMINI"      , "ZodiacSign.GEMINI_ABBR"      , Element.AIR   , Quality.MUTABLE  , YinYang.陽 , 60),
  /** Cancer 未/巨蟹 */
  CANCER     ("ZodiacSign.CANCER"      , "ZodiacSign.CANCER_ABBR"      , Element.WATER , Quality.CARDINAL , YinYang.陰 ,  90),
  /** Leo 午/獅子 */
  LEO        ("ZodiacSign.LEO"         , "ZodiacSign.LEO_ABBR"         , Element.FIRE  , Quality.FIXED    , YinYang.陽 , 120),
  /** Virgo 巳/處女 */
  VIRGO      ("ZodiacSign.VIRGO"       , "ZodiacSign.VIRGO_ABBR"       , Element.EARTH , Quality.MUTABLE  , YinYang.陰 , 150),
  /** Libra 辰/天秤 */
  LIBRA      ("ZodiacSign.LIBRA"       , "ZodiacSign.LIBRA_ABBR"       , Element.AIR   , Quality.CARDINAL , YinYang.陽 , 180),
  /** Scorpio 卯/天蠍 */
  SCORPIO    ("ZodiacSign.SCORPIO"     , "ZodiacSign.SCORPIO_ABBR"     , Element.WATER , Quality.FIXED    , YinYang.陰 , 210),
  /** Sagittarius 寅/射手 */
  SAGITTARIUS("ZodiacSign.SAGITTARIUS" , "ZodiacSign.SAGITTARIUS_ABBR" , Element.FIRE  , Quality.MUTABLE  , YinYang.陽 , 240),
  /** Capricorn 丑/摩羯 */
  CAPRICORN  ("ZodiacSign.CAPRICORN"   , "ZodiacSign.CAPRICORN_ABBR"   , Element.EARTH , Quality.CARDINAL , YinYang.陰 , 270),
  /** Aquarius 子/水瓶 */
  AQUARIUS   ("ZodiacSign.AQUARIUS"    , "ZodiacSign.AQUARIUS_ABBR"    , Element.AIR   , Quality.FIXED    , YinYang.陽 , 300),
  /** Pisces 亥/雙魚 */
  PISCES     ("ZodiacSign.PISCES"      , "ZodiacSign.PISCES_ABBR"      , Element.WATER , Quality.MUTABLE  , YinYang.陰 , 330);
  
  private final static String resource = "destiny.astrology.Sign";
  
  private String nameKey;
  private String abbrKey;
  
  /** 四正 (火/土/風/水) */
  private Element element;
  
  /** 三方 (基本/固定/變動) */
  private Quality quality;
  
  /** 陰陽 */
  private YinYang yinYang;
  
  /** 黃道起始度數 */
  private int degree; 
  
  private ZodiacSign(String nameKey , String abbrKey , Element element , Quality quality , YinYang yinYang , int degree)
  {
    this.nameKey = nameKey;
    this.abbrKey = abbrKey;
    this.element = element;
    this.quality = quality;
    this.yinYang = yinYang;
    this.degree = degree;
  }
  
  /** 取得黃道帶上的某度，屬於哪個星座 */
  public static ZodiacSign getZodiacSign(double degree)
  {
    int index = (int) (Utils.getNormalizeDegree(degree) / 30);
    return values()[index]; 
  }
  
  @Override
  public String toString()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
  }
  
  @Override
  public String toString(Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }
  
  /** 縮寫 */
  public String getAbbreviation()
  {
    return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(abbrKey);
  }
  
  public String getAbbreviation(Locale locale)
  {
    return ResourceBundle.getBundle(resource , locale).getString(abbrKey);
  }
  
  /** 取得對沖的星座 */
  public ZodiacSign getOppositeSign()
  {
    return ZodiacSign.values()[normalize(this.getIndex() + 6)];
  }
  
  /** 取得星座的 index , 為 0-based , 牡羊座為 0 , 金牛座為 1 , ... , 雙魚座為 11 */ 
  public int getIndex()
  {
    for (int i=0 ; i < ZodiacSign.values().length ; i++)
      if (this == ZodiacSign.values()[i])
        return i;
    throw new RuntimeException("Error!");
  }
  
  private static int normalize(int value)
  {
    if (value > 11 )
      return normalize( value-12);
    else if (value < 0)
      return normalize (value+12);
    else     
      return value;
  }

  /** 取得四大元素之一 */
  public Element getElement()
  {
    return element;
  }

  /** 基本/固定/變動 */
  public Quality getQuality()
  {
    return quality;
  }

  /** 取得黃道帶上的起始度數 */
  public int getDegree()
  {
    return degree;
  }

  public YinYang getYinYang()
  {
    return yinYang;
  }  

}
