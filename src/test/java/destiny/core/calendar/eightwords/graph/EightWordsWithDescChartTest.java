package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.eightwords.EightWordsNullable;
import destiny.core.chinese.StemBranch;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class EightWordsWithDescChartTest {

  @Test
  public void testChart1() throws IOException {
    long t0 = System.currentTimeMillis();

    EightWordsWithDescChart c = new EightWordsWithDescChart(600 , Color.WHITE , Color.BLACK , Optional.empty()
      , new EightWordsNullable(StemBranch.get("甲午") , StemBranch.get("庚午") , StemBranch.get("戊申") , StemBranch.get("辛酉"))
      , EightWordsChart.Direction.R2L);

    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());

    File pngFile = new File("/Users/smallufo/temp/chartWithDescR2L.png");
    ImageIO.write(c, "png", pngFile);
    long t1 = System.currentTimeMillis();

    System.out.println("takes " + (t1-t0) + " millis");
  }

    @Test
  public void testChart2() throws IOException {
    long t0 = System.currentTimeMillis();

    EightWordsWithDescChart c = new EightWordsWithDescChart(600 , Color.WHITE , Color.BLACK , Optional.empty()
      , new EightWordsNullable(StemBranch.get("甲午") , StemBranch.get("庚午") , StemBranch.get("戊申") , StemBranch.get("辛酉"))
      , EightWordsChart.Direction.L2R);

    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());

    File pngFile = new File("/Users/smallufo/temp/chartWithDescL2R.png");
    ImageIO.write(c, "png", pngFile);
    long t1 = System.currentTimeMillis();

    System.out.println("takes " + (t1-t0) + " millis");
  }

}