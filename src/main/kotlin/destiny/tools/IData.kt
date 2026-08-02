/**
 * Created by smallufo on 2025-02-13.
 */
package destiny.tools

import destiny.core.Gender


interface IData {
  val id: JSerializable
  val gender: Gender?
}
