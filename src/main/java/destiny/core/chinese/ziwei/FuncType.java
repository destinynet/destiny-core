/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

public enum FuncType {
  SET_DAY_NUM   // (局數,生日)
  ,HOUR_BRANCH
  ,MONTH                    // (月數 or 月支)
  ,YEAR_STEM
  ,YEAR_BRANCH
  ,YEAR_BRANCH_HOUR_BRANCH  // (年支,時支）
  ,MONTH_DAY_NUM            // (月數,日數)
  ,DAY_NUM_HOUR_BRANCH      // (時支,日數)
  ,YEAR_BRANCH_MONTH_NUM_HOUR_BRANCH // (年支,月數,時支)
  ,YEAR_STEM_GENDER         // (年干,性別)
  ,HOUSE_DEP
  ;

  FuncType() {}
}
