package destiny.core.oracles

import destiny.tools.JSerializable

data class Verdict(val domain: String, val result: String) : JSerializable
