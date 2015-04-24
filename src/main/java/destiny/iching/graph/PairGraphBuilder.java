/**
 * Created by smallufo on 2015-04-24.
 */
package destiny.iching.graph;

import destiny.core.chart.Constants;
import destiny.font.FontRepository;
import destiny.iching.HexagramIF;
import destiny.utils.Tuple;
import destiny.utils.Tuple4;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashSet;

import static destiny.core.chart.Constants.GOLDEN_RATIO;


/**
 * reference {@link AbstractPairBufferImage}
 * 橫向兩個卦
┌─────────────────────────────┐
│                             │
│                             │
│     ██████        ██████    │
│                             │
│     ██  ██        ██  ██    │
│                             │
│     ██████        ██████    │
│                             │
│     ██  ██  x --> ██████    │
│                             │
│     ██████  O --> ██  ██    │
│                             │
│     ██  ██        ██  ██    │
│                             │
│                             │
└─────────────────────────────┘
              ↑   ↑
              │   └─ getArrowX()
              │
              └─ getOxX()

 具備 「OX」的 x 軸座標 : getOxX()
 以及 取得箭頭頂點的 X 座標 getArrowX()
 */
public class PairGraphBuilder {

  /** 整張圖 */
  protected Graphics2D fullG;

  /** Golden + Padding */
  protected Graphics2D srcGraph;

  /** Golden + Padding */
  protected Graphics2D dstGraph;

  protected final int singleW;
  protected final int singleH;

  protected final HexagramIF src;
  protected final HexagramIF dst;

  protected final int width;
  protected final int height;

  protected final double paddingT;
  protected final double paddingR;
  protected final double paddingB;
  protected final double paddingL;

  protected final double singlePaddingX;
  protected final double singlePaddingY;

  public enum Type {MERGED , GOLDEN}

  protected Type type;

  protected final Color bg;
  protected final Color fore;

  private java.util.Set<GraphicsProcessor> processors = new HashSet<>();

  public PairGraphBuilder(Graphics2D g , HexagramIF src , HexagramIF dst , Type type, Constants.WIDTH_HEIGHT which, int value , Color bg , Color fore) {
    this.fullG = g;
    this.src = src;
    this.dst = dst;
    this.type = type;
    this.width = (which == Constants.WIDTH_HEIGHT.WIDTH  ? value :
             (type == Type.GOLDEN) ? (int)(value * GOLDEN_RATIO) :
                                     (int)(value / GOLDEN_RATIO)*2 );

    this.height = (which == Constants.WIDTH_HEIGHT.HEIGHT ? value :
             (type == Type.GOLDEN) ? (int)(value / GOLDEN_RATIO) :
                                     (int)(value / 2 * GOLDEN_RATIO));
    this.bg = bg;
    this.fore = fore;

    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // 先填上背景色
    g.setColor(bg);
    g.fillRect(0, 0, width, height);


    // 計算單一卦象的寬、高、以及四方 padding
    Tuple<Integer , Integer> singleWH = getSingleWH(Constants.WIDTH_HEIGHT.HEIGHT, height);
    this.singleW = singleWH.getFirst();
    this.singleH = singleWH.getSecond();

    Tuple4<Double , Double , Double , Double> paddings = getPaddings(Constants.WIDTH_HEIGHT.HEIGHT, height);
    this.paddingT = paddings.getFirst();
    this.paddingR = paddings.getSecond();
    this.paddingB = paddings.getThird();
    this.paddingL = paddings.getFourth();

    this.singlePaddingX = singleW * (2 - GOLDEN_RATIO) / 2;
    this.singlePaddingY = singleH * (2 - GOLDEN_RATIO) / 2;

    /** 左半邊為本卦 : Golden + Padding */
    srcGraph = (Graphics2D) g.create(0 , 0 , singleW , singleH);
    srcGraph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    srcGraph.setColor(fore);

    /** 右半邊為變卦 : Golden + Padding */
    dstGraph = (Graphics2D) g.create(width - singleW , 0 , singleW , singleH);
    dstGraph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    dstGraph.setColor(fore);

    // 繪製左半部的本卦
    BaseHexagramGraphic.render(srcGraph , src , singleW , singleH , bg , fore , paddingT , paddingR , paddingB , paddingL);
    // 繪製右半部的變卦
    BaseHexagramGraphic.render(dstGraph , dst , singleW , singleH , bg , fore , paddingT , paddingR , paddingB , paddingL);

    boolean drawRulers = true;
    if (drawRulers) {
      // 繪製輔助線條
      g.setColor(Color.decode("#999999"));
      // 本卦右邊的直線
      g.draw(new Line2D.Double(singleW, 0, singleW, height));
      // 變卦左邊的直線
      g.draw(new Line2D.Double(width - singleW, 0, width - singleW, height));
    }

    double oxX = singleW - paddingR/2.0;

    double rowHigh = (singleH - paddingT - paddingB) / 11.0;

    for (int i = 6; i >= 1; i--) {
      if (src.getLine(i) != dst.getLine(i)) {
        // 有變爻
        // 圈叉的 Y 座標
        //double oxY = paddingY + rowHigh*(12.5-2*i);
        double oxY = singlePaddingY + rowHigh * (12.5 - 2 * i);

        g.setColor(Color.RED);
        g.setFont(new Font(FontRepository.getFontLiHei(), Font.PLAIN, (int) rowHigh));
        g.setStroke(new BasicStroke((float) (rowHigh / 8.0)));

        double radius = (rowHigh / GOLDEN_RATIO) / 2;
        if (src.getLine(i)) {
          // 陽變陰 , 畫圓
          // 從左上角，寬高分別是 radius * 2
          Ellipse2D.Double circle = new Ellipse2D.Double(oxX - radius, oxY - radius, radius * 2, radius * 2);
          g.draw(circle);
        }
        else {
          // 陰變陽 , 畫一個叉叉
          g.draw(new Line2D.Double(oxX - radius, oxY - radius, oxX + radius, oxY + radius));
          g.draw(new Line2D.Double(oxX - radius, oxY + radius, oxX + radius, oxY - radius));
        }

        // 畫向右的箭頭
        double arrowX = width - singleW + singlePaddingX - radius * 2;
        double arrowY = oxY;

        g.draw(new Line2D.Double(oxX + radius * 2, oxY, arrowX, arrowY));
        // 往左上角畫去
        g.draw(new Line2D.Double(arrowX, arrowY, arrowX - radius, arrowY - radius));
        // 往左下角畫去
        g.draw(new Line2D.Double(arrowX, arrowY, arrowX - radius, arrowY + radius));
      } // 有變爻
    } // 6 to 1
  } // PairGraphBuilder()

  public PairGraphBuilder withGraphicsProcessor(GraphicsProcessor p) {
    p.process(this);
    return this;
//    this.processors.add(p);
//    return this;
  }

  protected double getRowHigh() {
    return (singleH - paddingT - paddingB) / 11.0;
  }

  private static Tuple<Integer, Integer> getSingleWH(Constants.WIDTH_HEIGHT which, int value) {
    int w = (which == Constants.WIDTH_HEIGHT.WIDTH  ? value : (int)(value / GOLDEN_RATIO));
    int h = (which == Constants.WIDTH_HEIGHT.HEIGHT ? value : (int)(value * GOLDEN_RATIO));
    return Tuple.of(w , h);
  }

  private static Tuple4<Double , Double , Double , Double> getPaddings(Constants.WIDTH_HEIGHT which, int value) {
    double t = (which == Constants.WIDTH_HEIGHT.WIDTH ? (value * GOLDEN_RATIO - value) / 2 : (value - value / GOLDEN_RATIO) / 2);
    double r = (which == Constants.WIDTH_HEIGHT.WIDTH ? value * (2 - GOLDEN_RATIO) / 2 : (value / GOLDEN_RATIO) * (2 - GOLDEN_RATIO) / 2);
    double b = (which == Constants.WIDTH_HEIGHT.WIDTH ? (value * GOLDEN_RATIO - value) / 2 : (value - value / GOLDEN_RATIO) / 2);
    double l = (which == Constants.WIDTH_HEIGHT.WIDTH ? value * (2 - GOLDEN_RATIO) / 2 : (value / GOLDEN_RATIO) * (2 - GOLDEN_RATIO) / 2);
    return Tuple4.of(t, r, b, l);
  }

//  public void build() {
//    for(GraphicsProcessor p : processors)
//      p.process(this);
//  }
}
