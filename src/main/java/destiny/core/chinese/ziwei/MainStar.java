/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei;

/**
 * 14 顆主星
 * */
public final class MainStar extends ZStar {

  public final static MainStar 紫微 = new MainStar("紫微");
  public final static MainStar 天機 = new MainStar("天機");
  public final static MainStar 太陽 = new MainStar("太陽");
  public final static MainStar 武曲 = new MainStar("武曲");
  public final static MainStar 天同 = new MainStar("天同");
  public final static MainStar 廉貞 = new MainStar("廉貞");

  public final static MainStar 天府 = new MainStar("天府");
  public final static MainStar 太陰 = new MainStar("太陰");
  public final static MainStar 貪狼 = new MainStar("貪狼");
  public final static MainStar 巨門 = new MainStar("巨門");
  public final static MainStar 天相 = new MainStar("天相");
  public final static MainStar 天梁 = new MainStar("天梁");
  public final static MainStar 七殺 = new MainStar("七殺");
  public final static MainStar 破軍 = new MainStar("破軍");

  public final static MainStar[] values = {紫微 , 天機 , 太陽 , 武曲 , 天同 , 廉貞 , 天府 , 太陰 , 貪狼 , 巨門 , 天相 , 天梁 , 七殺 , 破軍};

  public MainStar(String nameKey) {
    // resource key 存放於 destiny.core.chinese.ziwei.ZStar.properties 當中
    super(nameKey , ZStar.class.getName() , nameKey+"_ABBR");
  }
}
