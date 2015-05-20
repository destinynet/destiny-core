/**
 * Created by smallufo on 2015-04-16.
 */
package destiny.astrology.chart.astrolog;

import destiny.astrology.HoroscopeContext;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.font.FontRepository;

import java.awt.*;
import java.util.Locale;

/**
 * 畫面左下角標註日期時間等資訊
 */
public class AstrologChartWithMetaText extends AstrologChart {

  public AstrologChartWithMetaText(HoroscopeContext context, int width , Graphics2D g) {
    super(context.getHoroscope(), width , g);

    // 畫面左下角標註日期時間等資訊
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int height = width;

    int fontSize = width / 60;
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));
    g.getFontMetrics(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));

    g.setColor(Color.decode("#aaaaaa"));

    Time t1 = context.getLmt();
    String lmt = String.format("LMT %04d-%02d-%02d %02d:%02d", t1.getYear(), t1.getMonth(), t1.getDay(), t1.getHour(), t1.getMinute());
    g.drawString(lmt, fontSize, height - fontSize * 3);

    Time t2 = context.getGmt();
    String gmt = String.format("GMT %04d-%02d-%02d %02d:%02d", t2.getYear(), t2.getMonth(), t2.getDay(), t2.getHour(), t2.getMinute());
    g.drawString(gmt, fontSize, height - fontSize * 2);

    Location loc = context.getLocation();
    String locString = String.format("%d%s%d'%.3g\" %d%s%d'%.3g\"", loc.getLongitudeDegree(), loc.getEastWest().toString(Locale.ENGLISH).charAt(0), loc.getLongitudeMinute(), loc.getLongitudeSecond()
      , loc.getLatitudeDegree(), loc.getNorthSouth().toString(Locale.ENGLISH).charAt(0), loc.getLatitudeMinute(), loc.getLatitudeSecond());
    g.drawString(locString , fontSize , height - fontSize );
  }
}
