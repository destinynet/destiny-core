package destiny.tools.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class FollowUp(val question: String, val rationale: String)
