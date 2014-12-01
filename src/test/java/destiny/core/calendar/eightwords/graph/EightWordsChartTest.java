package destiny.core.calendar.eightwords.graph;

import destiny.core.calendar.eightwords.EightWordsNullable;
import destiny.core.chart.Constants;
import destiny.core.chinese.StemBranch;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import destiny.core.calendar.eightwords.Direction;

public class EightWordsChartTest {

  @Test
  public void testChart1() throws IOException {
    long t0 = System.currentTimeMillis();

    EightWordsChart c = new EightWordsChart(Constants.WIDTH_HEIGHT.WIDTH , 600 , Color.WHITE , Color.BLACK , Color.BLUE
      , new EightWordsNullable(StemBranch.get("甲子") , StemBranch.get("乙丑") , StemBranch.get("丙寅") , StemBranch.get("丁卯")), Direction.R2L);

    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());

    File pngFile = new File("/Users/smallufo/temp/chart1.png");
    ImageIO.write(c, "png", pngFile);
    long t1 = System.currentTimeMillis();

    System.out.println("takes " + (t1-t0) + " millis");
  }

  @Test
  public void testChart2() throws IOException {
    long t0 = System.currentTimeMillis();

    EightWordsChart c = new EightWordsChart(Constants.WIDTH_HEIGHT.WIDTH , 600 , Color.WHITE , Color.BLACK , Color.BLUE
      , new EightWordsNullable(StemBranch.get("甲子") , StemBranch.get("乙丑") , StemBranch.get("丙寅") , StemBranch.get("丁卯")), Direction.R2L);

    System.out.println("width = " + c.getWidth() + " , height = " + c.getHeight());

    File pngFile = new File("/Users/smallufo/temp/chart2.png");
    ImageIO.write(c, "png", pngFile);
    long t1 = System.currentTimeMillis();

    System.out.println("takes " + (t1-t0) + " millis");
  }

}