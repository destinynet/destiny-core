package destiny.core

interface IPattern {

  val name: String
    get() = javaClass.simpleName

  val notes: String?
    get() = null

}
