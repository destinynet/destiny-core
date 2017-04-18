/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei;

/**
 * 14 顆主星
 * */
public final class StarMain extends ZStar {

  public final static StarMain 紫微 = new StarMain("紫微");
  public final static StarMain 天機 = new StarMain("天機");
  public final static StarMain 太陽 = new StarMain("太陽");
  public final static StarMain 武曲 = new StarMain("武曲");
  public final static StarMain 天同 = new StarMain("天同");
  public final static StarMain 廉貞 = new StarMain("廉貞");

  public final static StarMain 天府 = new StarMain("天府");
  public final static StarMain 太陰 = new StarMain("太陰");
  public final static StarMain 貪狼 = new StarMain("貪狼");
  public final static StarMain 巨門 = new StarMain("巨門");
  public final static StarMain 天相 = new StarMain("天相");
  public final static StarMain 天梁 = new StarMain("天梁");
  public final static StarMain 七殺 = new StarMain("七殺");
  public final static StarMain 破軍 = new StarMain("破軍");

  public final static StarMain[] values = {紫微 , 天機 , 太陽 , 武曲 , 天同 , 廉貞 , 天府 , 太陰 , 貪狼 , 巨門 , 天相 , 天梁 , 七殺 , 破軍};

  public StarMain(String nameKey) {
    // resource key 存放於 destiny.core.chinese.ziwei.ZStar.properties 當中
    super(nameKey , ZStar.class.getName() , nameKey+"_ABBR", Type.主星);
  }
}
