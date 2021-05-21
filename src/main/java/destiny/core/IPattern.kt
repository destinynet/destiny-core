package destiny.core

import java.io.Serializable

interface IPattern : Serializable {

  val name: String
    get() = javaClass.simpleName

  val notes: String?
    get() = null

}
