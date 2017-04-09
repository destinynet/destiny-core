/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

/**
 * 十二宮位 (遷移 or 相貌)
 */
public enum House {
  命宮("命宮"),
  兄弟("兄弟"),
  夫妻("夫妻"),

  子女("子女"),
  財帛("財帛"),
  疾厄("疾厄"),

  遷移("遷移"), // 太乙派，沒有遷移宮
  交友("交友"),
  官祿("官祿"),

  田宅("田宅"),
  福德("福德"),
  父母("父母"),

  相貌("相貌"), // 太乙派使用

  ;

  private final String name;

  House(String name) {this.name = name;}

  public String getName() {
    return name;
  }

  public House next(int n , HouseSeqIF seq) {
    return seq.next(this , n);
  }

  public House prev(int n , HouseSeqIF seq) {
    return seq.prev(this , n);
  }

}
