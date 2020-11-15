package destiny.oracles

import java.io.Serializable

data class Verdict(val domain: String, val result: String) : Serializable
