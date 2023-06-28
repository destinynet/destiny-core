/**
 * Created by smallufo on 2018-02-04.
 */
package destiny.core.chinese

import destiny.core.Descriptive

/** 羊刃 */
interface IYangBlade : Descriptive {

  fun getYangBlade(stem: Stem): Branch
}

