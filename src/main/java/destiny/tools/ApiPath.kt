/**
 * Created by smallufo on 2020-11-20.
 */
package destiny.tools

object ApiPath {

  object wicket {

    object api {
      const val page = "/api/page"
      const val pages = "/api/pages"
    }

    /** 風水羅盤 */
    object fengshui {

      /** 24山 大羅盤 */
      const val compassPage = "/fengshui/compass/mnt24Map"

      /** 九星 */
      const val nineStarPage = "/sanyuan/NineStar"
    }

    /** 籤詩 */
    object oracles {

      /** 六十甲子籤詩 */
      const val sixtyClause = "/oracles/SixtyClause"

      /** 觀音靈籤 */
      const val guanyinClause = "/oracles/GuanyinClause"
    }

    /** 易經 */
    object iching {
      const val hexagramPage = "/iching/Hexagram"
      const val commentsPage = "/iching/Comments"
    }

  }
}
