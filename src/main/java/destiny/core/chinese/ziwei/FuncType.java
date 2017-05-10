/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

public enum FuncType {
   MAIN_STAR       // 主星 , 可能要依據閏月調整
  ,SET_DAY_NUM     // (局數,生日)
  ,HOUR_BRANCH
  ,MONTH                    // (月數 or 月支)
  ,YEAR                     // (年干支)
  ,YEAR_STEM                // 年的天干 (可能來自陰曆、或節氣)
  ,YEAR_BRANCH              // 年的地支 (可能來自陰曆、或節氣）
  ,YEAR_BRANCH_HOUR_BRANCH  // (年支,時支）
  ,MONTH_DAY_NUM            // (月數,日數)
  ,DAY_NUM_HOUR_BRANCH      // (時支,日數)
  ,YEAR_BRANCH_MONTH_NUM_HOUR_BRANCH // (年支,月數,時支)
  ,YEAR_STEM_GENDER         // (年干,性別)
  ,HOUSE_DEP                // 與宮位相關
  ,FIVE_GENDER_YINYANG      // (五行,性別,陰陽)
  ;

  FuncType() {}
}
