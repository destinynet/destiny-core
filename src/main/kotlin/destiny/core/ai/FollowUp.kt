package destiny.core.ai

import kotlinx.serialization.Serializable

@Serializable
data class FollowUp(val question: String, val rationale: String)
