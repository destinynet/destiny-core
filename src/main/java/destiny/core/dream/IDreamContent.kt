package destiny.core.dream


interface IDreamContent<T> {

  val title: String

  val dream: IDream

  val provider: String

  val providerKey: String

  val content: T
}

