/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import destiny.core.chinese.Branch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StrengthImpl implements IStrength , Serializable {

  private static Table<ZStar, Branch, Integer> sortedTable = TreeBasedTable.create();
  static {
    sortedTable.putAll(IStrength.commonTable);
  }

  public Map<Branch, Integer> getMapOf(ZStar star) {
    return sortedTable.rowMap().getOrDefault(star , new HashMap<>());
  }

  public Optional<Integer> getStrengthOf(ZStar star , Branch branch) {
    return Optional.ofNullable(sortedTable.get(star , branch));
  }
}
