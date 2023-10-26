package destiny.tools.model

interface ISessionRawExtractor {

  fun extract(rawJson: String): Pair<Domain, Any>?
}
