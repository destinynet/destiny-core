/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.Branch;
import destiny.font.FontRepository;
import destiny.utils.Tuple;

import java.awt.*;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * (0,0)             (3,0)
 * ------+-----+-----+-----+
 * |     |     |     |     |
 * |     |     |     |     |
 * +-----+-----+-----+-----+
 * |     |(1,1)      |     |
 * |     |           |     |
 * +-----+           +-----+
 * |     |           |     |
 * |     |     (2,3) |     |
 * +-----+-----+-----+-----+
 * |     |     |     |     |
 * |     |     |     |     |
 * +-----+-----+-----+-----+
 */
public class PalmGraphics {

  public static void render(Graphics2D g , int width , Color bg, Color fore , Palm palm , double padding) {
    int w = width;
    int h = width;

    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(bg);
    g.fillRect(0, 0, w, h);

    g.setColor(fore);

    // 左上角
    int x = (int) (width * (1-padding)/2);
    int y = x;
    int mainWidth = (int) (width * padding);

    Graphics2D mainFrame = (Graphics2D) g.create(x, y, mainWidth, mainWidth);
    renderMain(mainFrame , mainWidth , bg , fore , palm);
  }

  /**
   * 繪製主要的命盤（沒有 padding）
   */
  private static void renderMain(Graphics2D g , int width , Color bg , Color fore , Palm palm) {

    float cellW = (float) (width / 4.0);
    float cellH = (float) (width / 4.0);

    // 繪製外框
    g.drawRect(0 , 0 , width-1 , width-1);
    g.drawLine(0, (int) cellH, width, (int) cellH);
    g.drawLine(0, (int) (cellH * 3), width, (int) (cellH * 3));
    g.drawLine((int) cellW, 0, (int) cellW, width);
    g.drawLine((int) (cellW * 3), 0, (int) (cellW * 3), width);
    g.drawLine(0, (int) (cellH * 2), (int) cellW, (int) (cellH * 2));
    g.drawLine((int) (cellW * 3), (int) (cellH * 2), (int) (cellW * 4), (int) (cellH * 2));
    g.drawLine((int) (cellW * 2), 0, (int) (cellH * 2), (int) (cellW * 1));
    g.drawLine((int) (cellW * 2), (int) (cellH * 3), (int) (cellH * 2), (int) (cellW * 4));

//    try {
//      BufferedImage img = ImageIO.read(new File("/Users/smallufo/temp/girl.jpg"));
//      g.drawImage( img , (int)cellW+1 , (int)cellH+1 , (int)(cellW*2)-1 , (int)(cellH*2)-1 , null);
//    } catch (IOException ignored) {
//    }

    if (palm instanceof PalmWithMeta) {
      // meta data
      int metaW = (int)(cellW*2)-10;
      int metaH = (int)(cellH*2)-10;
      Graphics2D metaFrame = (Graphics2D) g.create((int)cellW+10 , (int)cellH+10 , metaW , metaH);
      renderMeta(metaFrame , (PalmWithMeta) palm, metaW , metaH , fore);
    }


    for(Branch branch : Branch.iterable()) {
      drawCell(g , branch , cellW , cellH , fore , palm);
    }
  }

  /** 繪製 meta data 男女、生日、地點... */
  private static void renderMeta(Graphics2D g , PalmWithMeta palm , int width , int height , Color fore) {

    int fontSize = width / 20;
    int fontH = height / 12;

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
    Time t1 = palm.getLmt();
    String lmt = String.format("%04d-%02d-%02d %02d:%02d", t1.getYear(), t1.getMonth(), t1.getDay(), t1.getHour(), t1.getMinute());
    g.drawString("LMT:" + lmt , 1 , fontH*3);

    Time t2 = Time.getGMTfromLMT(palm.getLmt() , palm.getLoc());
    String gmt = String.format("%04d-%02d-%02d %02d:%02d", t2.getYear(), t2.getMonth(), t2.getDay(), t2.getHour(), t2.getMinute());
    g.drawString("GMT:" + gmt , 1 , fontH*4);

    Branch hour = palm.getHourImpl().getHour(t1 , palm.getLoc());
    g.drawString("農曆：" + palm.getChineseDate() + hour + "時" , 1 , fontH * 5);

    Location loc = palm.getLoc();
    String locString = String.format("%d%s%d'%.3g\" %d%s%d'%.3g\"", loc.getLongitudeDegree(), loc.getEastWest().toString(Locale.ENGLISH).charAt(0), loc.getLongitudeMinute(), loc.getLongitudeSecond()
      , loc.getLatitudeDegree(), loc.getNorthSouth().toString(Locale.ENGLISH).charAt(0), loc.getLatitudeMinute(), loc.getLatitudeSecond());
    g.drawString("地點：" + locString , 1 , fontH*6);

    g.drawString("地名：" + palm.getPlace() , 1 , fontH*7);

    g.drawString("順逆：" + palm.getPositiveImpl().getTitle(Locale.TAIWAN) , 1 , fontH * 8);
    g.drawString("時辰劃分：" + palm.getHourImpl().getTitle(Locale.TAIWAN) , 1 , fontH * 9);
    g.drawString("子正是：" + palm.getMidnightImpl().getTitle(Locale.TAIWAN) , 1 , fontH * 10);
    g.drawString("換日：" + (palm.isChangeDayAfterZi() ? "子初換日" : "子正換日") , 1 , fontH * 11);
  }

  /**
   * 繪製單一 cell
   */
  private static void drawCell(Graphics2D g , Branch branch , float cellW , float cellH , Color fore , Palm palm) {
    Tuple<Float , Float> t = getCellCoordinate(branch , cellW , cellH);
    float x = t.getFirst();
    float y = t.getSecond();

    // 地支字體
    g.setColor(Color.GRAY);
    int fontSize = (int) (cellH / 4);
    g.setFont(new Font(FontRepository.getFontLiHei() , Font.PLAIN, fontSize));

    // 寫上「子、丑、寅、卯」等地支，在 cell 正中間
    g.drawString(branch.toString(), x + (cellW - fontSize) / 2, y + fontSize + (cellH - fontSize) / 2);

    // 在每個 cell 的右下角，標注「XX星」
    g.setColor(fore);
    fontSize = (int) (cellH / 7);
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));
    float starX = (float) (x + cellW - fontSize * 3 - fontSize * 0.2);
    float starY = (float) (y + cellH - fontSize * 0.5);
    g.drawString(Palm.getStar(branch) + "星", starX, starY);

    // 在每個 cell 的左下角，標注「XX道」
    g.setColor(fore);
    fontSize = (int) (cellH / 7);
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));
    float daoX = (float) (x + fontSize * 0.2);
    float daoY = (float) (y + cellH - fontSize * 0.5);
    g.drawString(Palm.getDao(branch) + "道", daoX, daoY);

    // 在每個 cell 的左上角，列出其所包含的 Pillar(s)
    g.setColor(Color.RED);
    fontSize = (int) (cellH / 6);
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));
    float pillarX = (float) (x + fontSize * 0.2);
    float pillarY = (float) (y + fontSize + fontSize*0.2);
    if (palm.getPillars(branch).size() > 0) {
      String pillars = palm.getPillars(branch).stream().map(Enum::toString).collect(Collectors.joining(" "));
      g.drawString(pillars , pillarX, pillarY);
    }

    // 在每個 cell 左邊中間，列出 house 名稱 (命宮...)
    g.setColor(Color.BLUE);
    fontSize = (int) (cellH / 6);
    g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, fontSize));
    float houseX = (float) (x + fontSize * 0.2);
    float houseY = (float) (y + cellH / 2.0);
    g.drawString(palm.getHouse(branch).toString() , houseX , houseY);
  }

  /**
   * 取得某宮的「左上角」座標
   *
   * (0,0)             (3,0)
   * ------+-----+-----+-----+
   * |  巳 |     |     |  申 |
   * |     |     |     |     |
   * +-----+-----+-----+-----+
   * |     |(1,1)      |     |
   * |     |           |     |
   * +-----+           +-----+
   * |     |           |     |
   * |     |     (2,3) |     |
   * +-----+-----+-----+-----+
   * |     |     |  子 |     |
   * |     |     |     |     |
   * +-----+-----+-----+-----+
   */
  private static Tuple<Float , Float> getCellCoordinate(Branch branch , float cellW , float cellH) {
    switch (branch) {
      case 子 : return Tuple.of(cellW * 2 , cellH * 3);
      case 丑 : return Tuple.of(cellW * 1 , cellH * 3);
      case 寅 : return Tuple.of(cellW * 0 , cellH * 3);
      case 卯 : return Tuple.of(cellW * 0 , cellH * 2);
      case 辰 : return Tuple.of(cellW * 0 , cellH * 1);
      case 巳 : return Tuple.of(cellW * 0 , cellH * 0);
      case 午 : return Tuple.of(cellW * 1 , cellH * 0);
      case 未 : return Tuple.of(cellW * 2 , cellH * 0);
      case 申 : return Tuple.of(cellW * 3 , cellH * 0);
      case 酉 : return Tuple.of(cellW * 3 , cellH * 1);
      case 戌 : return Tuple.of(cellW * 3 , cellH * 2);
      case 亥 : return Tuple.of(cellW * 3 , cellH * 3);
      default : throw new AssertionError("error");
    }
  }


}
