/**
 * @author smallufo
 * Created on 2007/7/24 at 下午 1:34:43
 */
package destiny.astrology;

import destiny.tools.LocaleStringIF;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 抽象class , 代表星盤上的一「點」，可能是實星（行星 {@link Planet}, 小行星 {@link Asteroid}, 恆星 {@link FixedStar}），
 * 虛星 (漢堡星 {@link Hamburger}) , 也可能只是交點 (例如：黃白交點 {@link LunarPoint})
 * 目前繼承圖如下：
 * <pre>
 *                     Point
 *                       |
 *                     Star
 *                       |
 *   +---------+---------+--------+-----------------+
 *   |         |         |        |                 |      
 * Planet  Asteroid  FixedStar LunarPoint(A)    Hamburger
 *  行星    小行星     恆星     日月交點         漢堡虛星  
 *                                |
 *                                |
 *                       +--------+--------+
 *                       |                 |
 *                   LunarNode         LunarApsis
 *                   [TRUE/MEAN]       [MEAN/OSCU]
 *                   North/South   PERIGEE (近)/APOGEE (遠)
 * </pre>
 */
public abstract class Point implements Serializable , LocaleStringIF {

  private final String resource;

  /** 名稱key , nameKey 相等，則此 Point 視為 equals! */
  private final String nameKey;

  /** 縮寫key , 為了輸出美觀所用 , 限定兩個 bytes , 例如 : 日(SU) , 月(MO) , 冥(PL) , 升(No) , 強(So) , 穀 , 灶 ...*/
  @Nullable
  String abbrKey;

  public Point(String nameKey, String resource) {
    this.nameKey = nameKey;
    this.resource = resource;
  }

  public Point(String nameKey, String resource , @Nullable String abbrKey) {
    this.nameKey = nameKey;
    this.resource = resource;
    this.abbrKey = abbrKey;
  }

  /** 名稱 */
  public String getName(@NotNull Locale locale) {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey);
  }

  /** 名稱 */
  private String getName() {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey);
  }

  /** toString 直接取名稱 */
  @Override
  public String toString() {
    return getName();
  }

  /** toString 直接取名稱 (name) */
  @Override
  public String toString(@NotNull Locale locale) {
    return ResourceBundle.getBundle(resource , locale).getString(nameKey);
  }

  /** 取得縮寫 , 如果沒有傳入縮寫，則把 name 取前兩個 bytes */
  public String getAbbreviation(@NotNull Locale locale) {
    if (abbrKey != null) {
      return ResourceBundle.getBundle(resource, locale).getString(abbrKey);
    }
    else {
      String name = ResourceBundle.getBundle(resource, locale).getString(nameKey);
      return getAbbr(locale, name);
    }
  }

  /** 取得縮寫 , 如果沒有傳入縮寫，則把 name 取前兩個 bytes */
  public String getAbbreviation() {
    if (abbrKey != null) {
      //System.out.print("get abbrKey of " + this +" , abbrKey = " + abbrKey);
      return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(abbrKey);
    }
    else {
      String name = ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey);
      return getAbbr(Locale.getDefault(), name);
    }
  }

  /**
   * 處理縮寫
   */
  @NotNull
  private String getAbbr(@Nullable Locale locale , @NotNull String value)
  {
    if (locale != null && locale.getLanguage().equals("zh") && locale.getCountry().equals("TW")) {
      byte[] byteArray;
      byte[] arr = new byte[2];
      try {
        byteArray = String.valueOf(value.toCharArray()).getBytes("Big5");

        System.arraycopy(byteArray, 0, arr, 0, 2);
      } catch (UnsupportedEncodingException ignored) {
      }
      return new String(arr);
    }
    else {
      return value.substring(0, 2);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nameKey == null) ? 0 : nameKey.hashCode());
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Point other = (Point) obj;
    if (nameKey == null) {
      return other.nameKey == null;
    }
    else
      return nameKey.equals(other.nameKey);
  }


}
