/**
 * Created by smallufo on 2025-02-13.
 */
package destiny.tools

import destiny.core.Gender
import java.io.Serializable


interface IData {
  val id: Serializable
  val gender: Gender?
}
