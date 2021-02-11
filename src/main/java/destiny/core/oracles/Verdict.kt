package destiny.core.oracles

import java.io.Serializable

data class Verdict(val domain: String, val result: String) : Serializable
