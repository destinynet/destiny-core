/**
 * @author smallufo 
 * Created on 2008/12/13 at 上午 4:23:19
 */
package destiny.astrology.chart.astrolog;

import destiny.astrology.*;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.OrientalComparator;
import destiny.astrology.chart.PointImageResourceReader;
import destiny.astrology.chart.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

public class RingPointIcons extends AbstractRing
{
  /** 最外圈的圓 的半徑 */
  private double                   radius;

  /** 圓心 */
  private double                   center;

  @NotNull
  private PointImageResourceReader pointImageResourceReader = new PointImageResourceReaderImpl();

  /** 要繪製的星體 */
  private Set<Point>               shownPoints             = Collections.synchronizedSet(new HashSet<Point>());

  /** 重新排列整理過後的 icon 中心點 */
  private Map<Point, Double>       rearrangedIconCenterMap;

  public RingPointIcons(Horoscope h, double innerFrom, double outerTo, double radius, double center)
  {
    super(h, innerFrom, outerTo);
    this.radius = radius;
    this.center = center;

    //內定繪製行星
    for (Planet planet : Planet.values)
      shownPoints.add(planet);

    //內定繪製北交點
    shownPoints.add(LunarNode.NORTH_MEAN);

    //內定繪製小行星
    for (Asteroid asteroid : Asteroid.values)
      shownPoints.add(asteroid);
  }

  public RingPointIcons(Horoscope h, double innerFrom, double outerTo, double radius, double center, Set<Point> shownPoints)
  {
    super(h, innerFrom, outerTo);
    this.radius = radius;
    this.center = center;
    this.shownPoints = shownPoints;
  }

  @Override
  public Map<Double, BufferedImage> getBufferedImages()
  {
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);

    Map<Double, BufferedImage> result = Collections.synchronizedMap(new HashMap<Double, BufferedImage>());

    //存放「原始」，每顆星體的中心度數
    Map<Point, Double> pointOriginDegMap = Collections.synchronizedMap(new HashMap<Point, Double>());

    //存放每顆行星的 icon , 佔據的度數 , 前者為 Double[0] 為 from , Double[1] 為 to
    Map<Point, Double[]> pointIconRangeMap = Collections.synchronizedMap(new HashMap<Point, Double[]>());

    for (Point point : shownPoints)
    {
      double planetDegree = h.getPositionWithAzimuth(point).getLongitude();
      double 原始第一象限度數 = planetDegree - degDesc;

      BufferedImage iconImg = pointImageResourceReader.getBufferedImage(point);
      RingPointIconRangeUtil util = new RingPointIconRangeUtil(center, radius, getInnerFrom(), getOuterTo(), iconImg, 原始第一象限度數);

      //System.out.println(point + " 位於 " + Utils.getNormalizeDegree(原始第一象限度數) + "\n");
      pointOriginDegMap.put(point, Utils.getNormalizeDegree(原始第一象限度數));

      Double[] iconRange = new Double[2];
      iconRange[0] = util.getRangeFrom();
      iconRange[1] = util.getRangeTo();
      pointIconRangeMap.put(point, iconRange);

      //result.put(Utils.getNormalizeDegree(原始第一象限度數), planetImg);
    }
    rearrangedIconCenterMap = getRearrangedPointPosition(pointIconRangeMap, pointOriginDegMap);
    for (Map.Entry<Point, Double> entry : rearrangedIconCenterMap.entrySet())
    {
      Point point = entry.getKey();
      Double newPos = entry.getValue();
      BufferedImage iconImg = pointImageResourceReader.getBufferedImage(point);
      result.put(newPos, iconImg);
    }
    return result;
  }

  /** 重新排列整理過後的 icon 中心點 */
  public Map<Point, Double> getRearrangedIconCenterMap()
  {
    if (rearrangedIconCenterMap == null)
      getBufferedImages();
    return rearrangedIconCenterMap;
  }

  /** 將行星 icon 所佔的弧角，做 overlap 檢查，並且分出 cluster , 傳回每顆星體重新排列後，位於幾度(第一象限算起) */
  private Map<Point, Double> getRearrangedPointPosition(@NotNull Map<Point, Double[]> pointIconRangeMap, @NotNull Map<Point, Double> pointOriginDegMap)
  {
    List<Set<Point>> clusters = Collections.synchronizedList(new ArrayList<Set<Point>>());

    for (Map.Entry<Point, Double[]> entry : pointIconRangeMap.entrySet())
    {
      Point point = entry.getKey();
      Double[] fromTo = entry.getValue();
      //System.out.println("\n走訪星體：" + point);

      if (clusters.size() == 0)
      {
        //沒有 cluster , 成立一個新的，把自己丟進去
        Set<Point> newCluster = Collections.synchronizedSet(new TreeSet<Point>(new OrientalComparator(h)));
        newCluster.add(point);
        clusters.add(newCluster);
        //System.out.println("空 clusters , 成立新 cluster , 放入 " + point);
      }
      else
      {
        Set<Point> newCluster = Collections.synchronizedSet(new TreeSet<Point>(new OrientalComparator(h)));

        boolean overlapped = false;
        for (Set<Point> cluster : clusters)
        {
          //System.out.println("目前此 cluster 有哪些星體： " + cluster);

          for (Point planetInCluster : cluster)
          {
            if (point != planetInCluster)
            {
              if (isOverlapped(fromTo, pointIconRangeMap.get(planetInCluster)))
              {
                //System.out.println(point + " 與 " + planetInCluster + " icon 重疊");
                cluster.add(point);
                overlapped = true;
                break;
              }
            }
          }
        } //loop Clusters
        if (!overlapped)
        {
          newCluster.add(point);
          clusters.add(newCluster);
          //System.out.println(point + " icon 沒有與任何 cluster 內的星體交錯\n成立新 cluster , 放入 " + point);
        }
      }
    }

    /*
     * System.out.println("================= clustering 初步結果 ==============");
     * for (Set<Point> cluster : clusters) System.out.println(cluster);
     * 
     * System.out.println("================= clustering 重新整理 ==============");
     */

    //檢查每顆行星，看他出現在哪些 cluster 中，如果重複，則把這兩個 cluster 綁在一起
    for (Point point : shownPoints)
    {
      Set<Set<Point>> occurringClusters = getClusters(point, clusters);
      //System.out.println(point + " 出現了 " + occurringClusters.size() + " 次");
      if (occurringClusters.size() > 1)
      {
        clusters.removeAll(occurringClusters);
        Set<Point> combiningNewCluster = Collections.synchronizedSet(new TreeSet<Point>(new OrientalComparator(h)));
        occurringClusters.forEach(combiningNewCluster::addAll);
        clusters.add(combiningNewCluster);
      }
    }

    /*
     * System.out.println("================= clustering 最後結果 ==============");
     * for (Set<Point> cluster : clusters) System.out.println(cluster);
     */

    // 存放重新排列過的星體 icons
    Map<Point, Double> newPointIconRangeMap = Collections.synchronizedMap(new HashMap<Point, Double>());

    //檢查「星體數量大於1」的 cluster, 所佔的範圍
    for (Set<Point> cluster : clusters)
    {
      if (cluster.size() == 1)
      {
        //cluster 裡面只有一顆星
        Point point = cluster.iterator().next();
        //Double[] range = pointIconRangeMap.get(point);
        newPointIconRangeMap.put(point, pointOriginDegMap.get(point));
      }
      else
      {
        //此 cluster 裡面有兩顆星（含）以上
        double clusterRangeCenter = getClusterRangeCenter(cluster, pointIconRangeMap);
        //System.err.println("\n cluster : " + cluster + " 的中心度數是 : " + clusterRangeCenter);

        List<Point> pointList = Collections.synchronizedList(new ArrayList<Point>(cluster));

        // 每個icon之間，最少需間隔多少弧角 , 採用動態調整，如果聚集越多，則 gap 要越大
        double gap = pointList.size() / 2;
        if (gap < 1.5)
          gap = 1.5; // gap 至少要 1.5

        int size = pointList.size();
        if (cluster.size() % 2 == 0)
        {
          //System.out.println("cluster 內的星體數量，為偶數 : " + pointList);
          double sumEast = gap / 2; //往東方，展延的度數總和
          double sumWest = gap / 2; //往西方，展延的度數總和

          for (int i = 0; i < size; i++)
          {
            Point point;
            if (i % 2 == 0)
              point = pointList.get(size / 2 - (i / 2 + 1));
            else
              point = pointList.get(size / 2 + (i - 1) / 2);

            Double[] iconRange = pointIconRangeMap.get(point);
            //System.out.println(point + " 原來在這裡     : " + iconRange[0] + " 到 " + iconRange[1] + " 之間");
            double iconAngle = Horoscope.getAngle(iconRange[0], iconRange[1]);
            double newPointCenter;

            if (i % 2 == 0)
            {
              newPointCenter = Utils.getNormalizeDegree(clusterRangeCenter - sumEast - iconAngle / 2);
              sumEast = sumEast + iconAngle / 2 + gap;
            }
            else
            {
              newPointCenter = Utils.getNormalizeDegree(clusterRangeCenter + sumWest + iconAngle / 2);
              sumWest = sumWest + iconAngle / 2 + gap;
            }
            /*
             * Double[] newIconRange = new Double[2]; newIconRange[0] =
             * Utils.getNormalizeDegree(newPointCenter - iconAngle/2);
             * newIconRange[1] = Utils.getNormalizeDegree(newPointCenter +
             * iconAngle/2);
             */
            //System.out.println("sumEast = " + sumEast + " , sumWest = "+ sumWest);
            newPointIconRangeMap.put(point, newPointCenter);
            //System.out.println(point + " 被放置到新位置 : " + newPointCenter);
          }
        } //偶數
        else
        {
          //System.out.println("cluster 內的星體數量，為奇數" + pointList);
          //先放置中間星體，到 cluster 中心度數
          Point centerPoint = pointList.get((size - 1) / 2);
          Double[] centerIconRange = pointIconRangeMap.get(centerPoint);
          double centerIconAngle = Horoscope.getAngle(centerIconRange[0], centerIconRange[1]);
          double sumEast = centerIconAngle / 2 + gap ; //往東方，展延的度數總和
          double sumWest = centerIconAngle / 2 + gap ; //往西方，展延的度數總和
          /*
           * Double[] newCenterIconRange = new Double[2]; newCenterIconRange[0]
           * = Utils.getNormalizeDegree(clusterRangeCenter - sumEast);
           * newCenterIconRange[1] = Utils.getNormalizeDegree(clusterRangeCenter
           * + sumWest);
           */
          newPointIconRangeMap.put(centerPoint, clusterRangeCenter);
          //System.out.println("排在中間的 " + centerPoint + " 被放置到新位置 : " + clusterRangeCenter);

          for (int i = 0; i < (size - 1); i++)
          {
            Point point;
            if (i % 2 == 0)
            {
              point = pointList.get(size / 2 - (i / 2 + 1));
            }
            else
            {
              point = pointList.get(size / 2 + (i / 2 + 1));
            }
            Double[] iconRange = pointIconRangeMap.get(point);
            //System.out.println(point + " 原來在這裡     : " + pointOriginDegMap.get(point));

            double iconAngle = Horoscope.getAngle(iconRange[0], iconRange[1]);
            double newPointCenter;
            if (i % 2 == 0)
            {
              newPointCenter = Utils.getNormalizeDegree(clusterRangeCenter - sumEast - iconAngle / 2);
              sumEast = sumEast + iconAngle / 2 + gap;
            }
            else
            {
              newPointCenter = Utils.getNormalizeDegree(clusterRangeCenter + sumWest + iconAngle / 2);
              sumWest = sumWest + iconAngle / 2 + gap;
            }
            /*
             * Double[] newIconRange = new Double[2]; newIconRange[0] =
             * Utils.getNormalizeDegree(newPointCenter - iconAngle/2);
             * newIconRange[1] = Utils.getNormalizeDegree(newPointCenter +
             * iconAngle/2);
             */
            newPointIconRangeMap.put(point, newPointCenter);
            //System.out.println(point + " 被放置到新位置 : " + newPointCenter);
          }
        } //奇數
      }
    }
    return newPointIconRangeMap;
  } //clusterRange

  /** 找出此 cluster range 的中心點 */
  private double getClusterRangeCenter(@NotNull Set<Point> cluster, @NotNull Map<Point, Double[]> pointIconRangeMap)
  {
    //range : 找出最東 , 以及最西 的兩點 , 相減即可

    Point firstPoint = cluster.iterator().next();
    double eastest = pointIconRangeMap.get(firstPoint)[0];
    double westest = pointIconRangeMap.get(firstPoint)[1];

    double rangeFrom, rangeTo;
    for (Point point : cluster)
    {
      rangeFrom = pointIconRangeMap.get(point)[0];
      rangeTo = pointIconRangeMap.get(point)[1];

      if (Horoscope.isOriental(rangeFrom, eastest))
        eastest = rangeFrom;

      if (Horoscope.isOccidental(rangeTo, westest))
        westest = rangeTo;
    }
    
    double center;
    if (westest > eastest )
    {
      //System.out.println("westest("+westest+") - eastest("+eastest+") = " + (westest-eastest) + " , < 180");
      center = Utils.getNormalizeDegree((eastest + westest) / 2);
    }
    else
    {
      //System.out.println("eastest("+eastest+") 與 westest("+westest+" 交角： "+Horoscope.getAngle(eastest, westest));
      center = Utils.getNormalizeDegree(eastest + Horoscope.getAngle(eastest, westest)/2);
    }
    //System.out.println("cluster : " + cluster + " 從 " + eastest + " 到 " + westest + " , 中心點是 " + center);
    return center;
  }

  /** 查詢這顆行星，出現在哪些 clusters 中 , 傳回去 */
  private Set<Set<Point>> getClusters(Point point, @NotNull List<Set<Point>> clusters)
  {
    Set<Set<Point>> result = Collections.synchronizedSet(new HashSet<Set<Point>>());
    result.addAll(clusters.stream().filter(cluster -> cluster.contains(point)).collect(Collectors.toList()));
    return result;
  }

  /**
   * 檢查此兩個 range 是否有重疊到 , 一般而言 , range[1] 會大於 range[0] , 但 注意 : 若 range[0] 可能為
   * 359 , range[1] 可能為 1
   */
  private boolean isOverlapped(Double[] range1, Double[] range2)
  {
    if ((Horoscope.isOccidental(range1[1], range2[1]) && Horoscope.isOccidental(range2[1], range1[0])) || (Horoscope
        .isOccidental(range2[1], range1[1]) && Horoscope.isOccidental(range1[1], range2[0])))
      return true;
    return false;
  }

  /** 不畫內圈，傳回 null */
  @Nullable
  @Override
  public Style getInnerRingStyle()
  {
    return null;
  }
}
