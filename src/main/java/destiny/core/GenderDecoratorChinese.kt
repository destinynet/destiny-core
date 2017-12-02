/**
 * @author smallufo
 * Created on 2008/4/2 at 上午 1:27:20
 */
package destiny.core

import destiny.tools.Decorator

class GenderDecoratorChinese : Decorator<Gender> {
  override fun getOutputString(gender: Gender): String {
    return if (gender === Gender.男) "男" else "女"
  }
}
