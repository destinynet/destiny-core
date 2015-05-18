/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.calendar.eightwords.onePalm;

import destiny.core.chinese.EarthlyBranches;
import destiny.font.FontRepository;
import destiny.utils.Tuple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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


    try {
      BufferedImage img = ImageIO.read(new File("/Users/smallufo/temp/girl.jpg"));
      g.drawImage( img , (int)cellW+1 , (int)cellH+1 , (int)(cellW*2)-1 , (int)(cellH*2)-1 , null);
    } catch (IOException ignored) {
    }


    for(EarthlyBranches branch : EarthlyBranches.iterable()) {
      drawCell(g , branch , cellW , cellH , fore , palm);
    }
  }

  /**
   * 繪製單一 cell
   */
  private static void drawCell(Graphics2D g , EarthlyBranches branch , float cellW , float cellH , Color fore , Palm palm) {
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
    g.setColor(fore);
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
  private static Tuple<Float , Float> getCellCoordinate(EarthlyBranches branch , float cellW , float cellH) {
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
