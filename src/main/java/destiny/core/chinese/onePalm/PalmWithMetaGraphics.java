/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.onePalm;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.Branch;
import destiny.font.FontRepository;

import java.awt.*;
import java.util.Locale;

public class PalmWithMetaGraphics {

  public static void render(Graphics2D g, int width, Color bg, Color fore, PalmWithMeta palmWithMeta, double padding) {

    PalmGraphics.render(g , width , bg , fore , palmWithMeta.getPalm() , padding);

    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


    float cellW = (float) (width / 4.0);
    float cellH = (float) (width / 4.0);

    int metaW = (int) (cellW * 2) - 10;
    int metaH = (int) (cellH * 2) - 10;

    Graphics2D metaFrame = (Graphics2D) g.create((int) cellW + 20, (int) cellH + 20, metaW, metaH);
    renderMeta(metaFrame, palmWithMeta, metaW, metaH, fore);
  }

  /** 繪製 meta data 男女、生日、地點... */
  private static void renderMeta(Graphics2D g, PalmWithMeta palmWithMeta, int width, int height, Color fore) {
    int fontSize = width / 20;
    int fontH = height / 12;

    Palm palm = palmWithMeta.getPalm();

    StringBuilder sb = new StringBuilder();
    sb.append(Palm.getStar(palm.getYear()).substring(1, 2));
    sb.append(Palm.getStar(palm.getMonth()).substring(1, 2));
    sb.append(Palm.getStar(palm.getDay()).substring(1, 2));
    sb.append(Palm.getStar(palm.getHour()).substring(1, 2));


    g.setColor(Color.RED);
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize*2));
    g.drawString(sb.toString(), 1 , fontH);

    g.setColor(fore);
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));
    g.drawString("性別:" + palm.getGender().toString() , 1 , fontH*2);
    Time t1 = palmWithMeta.getLmt();
    String lmt = String.format("%04d-%02d-%02d %02d:%02d", t1.getYear(), t1.getMonth(), t1.getDay(), t1.getHour(), t1.getMinute());
    g.drawString("LMT:" + lmt , 1 , fontH*3);

    Time t2 = Time.getGMTfromLMT(palmWithMeta.getLmt() , palmWithMeta.getLoc());
    String gmt = String.format("%04d-%02d-%02d %02d:%02d", t2.getYear(), t2.getMonth(), t2.getDay(), t2.getHour(), t2.getMinute());
    g.drawString("GMT:" + gmt , 1 , fontH*4);

    Branch hour = palmWithMeta.getHourImpl().getHour(t1 , palmWithMeta.getLoc());
    g.drawString("農曆：" + palmWithMeta.getChineseDate() + hour + "時" , 1 , fontH * 5);

    Location loc = palmWithMeta.getLoc();
    String locString = String.format("%d%s%d'%.3g\" %d%s%d'%.3g\"", loc.getLongitudeDegree(), loc.getEastWest().toString(Locale.ENGLISH).charAt(0), loc.getLongitudeMinute(), loc.getLongitudeSecond()
      , loc.getLatitudeDegree(), loc.getNorthSouth().toString(Locale.ENGLISH).charAt(0), loc.getLatitudeMinute(), loc.getLatitudeSecond());
    g.drawString("地點：" + locString , 1 , fontH*6);

    g.drawString("地名：" + palmWithMeta.getPlace() , 1 , fontH*7);

    g.drawString("順逆：" + palmWithMeta.getPositiveImpl().getTitle(Locale.TAIWAN) , 1 , fontH * 8);
    g.drawString("時辰劃分：" + palmWithMeta.getHourImpl().getTitle(Locale.TAIWAN) , 1 , fontH * 9);
    g.drawString("子正是：" + palmWithMeta.getMidnightImpl().getTitle(Locale.TAIWAN) , 1 , fontH * 10);
    g.drawString("換日：" + (palmWithMeta.isChangeDayAfterZi() ? "子初換日" : "子正換日") , 1 , fontH * 11);
  }
}
