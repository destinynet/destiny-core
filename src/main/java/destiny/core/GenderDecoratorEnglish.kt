/**
 * @author smallufo
 * Created on 2008/4/2 at 上午 1:28:52
 */
package destiny.core

import destiny.tools.Decorator

class GenderDecoratorEnglish : Decorator<Gender> {
  override fun getOutputString(gender: Gender): String {
    return if (gender === Gender.男) "Male" else "Female"
  }
}
