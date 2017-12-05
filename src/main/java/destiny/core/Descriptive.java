/**
 * @author smallufo
 * Created on 2006/5/5 at 上午 04:20:08
 */
package destiny.core;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface Descriptive {

  /** 取得名稱 */
  @NotNull
  String getTitle(@NotNull Locale locale);

  /** 詳細描述 */
  @NotNull
  String getDescription(@NotNull Locale locale);
}
