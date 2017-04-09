/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static destiny.core.chinese.Branch.寅;

/** 紫微斗數 */
public interface ZiweiIF {

  static Logger logger = LoggerFactory.getLogger(ZiweiIF.class);

  /**
   * 命宮
   *
   * 假設我的出生月日是農曆 7/6 巳時，順數生月，那就是從寅宮開始順時針走七步 , 因為是農曆七月，所以經由 順數生月，所以我們找到了申宮
   *
   * 找到申宮之後再 逆數生時 找到了卯宮，所以卯就是你的命宮
   * */
  default Branch getFirstHouse(int month , Branch hour) {
    return 寅.next(month-1).prev(hour.getIndex());
  }

  /**
   * 從命宮開始，逆時針，飛佈 兄弟、夫妻...
   */
  default Branch getHouse(int month , Branch hour , House house , HouseSeqIF seq) {
    // 命宮 的地支
    Branch branchOfFirstHouse = getFirstHouse(month , hour);
    int steps = seq.getAheadOf(house , House.命宮);
    return branchOfFirstHouse.prev(steps);
  }

  void calculate(Gender gender , LocalDateTime time , Location loc);
}
