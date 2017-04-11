/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

/**
 * 乙級星有總共有32顆
 */
public class MinorStar extends ZStar {

  public final static MinorStar 天官 = new MinorStar("天官");
  public final static MinorStar 天福 = new MinorStar("天福");
  public final static MinorStar 天廚 = new MinorStar("天廚");
  public final static MinorStar 天刑 = new MinorStar("天刑");
  public final static MinorStar 天姚 = new MinorStar("天姚");
  public final static MinorStar 解神 = new MinorStar("解神");
  public final static MinorStar 天巫 = new MinorStar("天巫");
  public final static MinorStar 天月 = new MinorStar("天月");
  public final static MinorStar 陰煞 = new MinorStar("陰煞");
  public final static MinorStar 台輔 = new MinorStar("台輔");
  public final static MinorStar 封誥 = new MinorStar("封誥");
  public final static MinorStar 天空 = new MinorStar("天空");
  public final static MinorStar 天哭 = new MinorStar("天哭");
  public final static MinorStar 天虛 = new MinorStar("天虛");
  public final static MinorStar 龍池 = new MinorStar("龍池");
  public final static MinorStar 鳳閣 = new MinorStar("鳳閣");
  public final static MinorStar 紅鸞 = new MinorStar("紅鸞");
  public final static MinorStar 天喜 = new MinorStar("天喜");
  public final static MinorStar 孤辰 = new MinorStar("孤辰");
  public final static MinorStar 寡宿 = new MinorStar("寡宿");
  public final static MinorStar 蜚廉 = new MinorStar("蜚廉");
  public final static MinorStar 破碎 = new MinorStar("破碎");
  public final static MinorStar 華蓋 = new MinorStar("華蓋");
  public final static MinorStar 咸池 = new MinorStar("咸池");
  public final static MinorStar 天德 = new MinorStar("天德");
  public final static MinorStar 月德 = new MinorStar("月德");
  public final static MinorStar 天才 = new MinorStar("天才");
  public final static MinorStar 天壽 = new MinorStar("天壽");
  public final static MinorStar 三台 = new MinorStar("三台");
  public final static MinorStar 八座 = new MinorStar("八座");
  public final static MinorStar 恩光 = new MinorStar("恩光");
  public final static MinorStar 天貴 = new MinorStar("天貴");

  public final static MinorStar[] values = {天官, 天福, 天廚, 天刑, 天姚, 解神, 天巫, 天月, 陰煞, 台輔, 封誥, 天空, 天哭, 天虛, 龍池, 鳳閣, 紅鸞, 天喜, 孤辰, 寡宿, 蜚廉, 破碎, 華蓋, 咸池, 天德, 月德, 天才, 天壽, 三台, 八座, 恩光, 天貴};

  public MinorStar(String nameKey) {
    super(nameKey, ZStar.class.getName());
  }
}
