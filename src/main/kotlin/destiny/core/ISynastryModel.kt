package destiny.core

import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.DomainSerializer
import kotlinx.serialization.Contextual
import java.io.Serializable

enum class SynastryGrain {
  BOTH_FULL,              // Both parties have full date and time
  INNER_FULL_OUTER_DATE,  // Inner chart has full data, outer chart only has date
  INNER_DATE_OUTER_FULL,  // Inner chart only has date, outer chart has full data
  BOTH_DATE               // Both parties only have date, no time
}

enum class SynastryAspect {
  LOVE,
  FRIENDSHIP,
  PARTNERSHIP,
  RIVAL,
  PARENT_CHILD,
}

interface ISynastryModel : Serializable {
  val inner: IBirthDataNamePlace
  val outer: IBirthDataNamePlace
  val grain: SynastryGrain
  val domainBdnp: Domain.Bdnp
  val aspect: SynastryAspect
}

@kotlinx.serialization.Serializable
data class SynastryModel(
  @Contextual
  override val inner: IBirthDataNamePlace,
  @Contextual
  override val outer: IBirthDataNamePlace,
  override val grain: SynastryGrain,
  @kotlinx.serialization.Serializable(with = DomainSerializer::class)
  override val domainBdnp: Domain.Bdnp,
  override val aspect: SynastryAspect
) : ISynastryModel

enum class SynastryDomain {
  OVERVIEW,
  EMOTIONAL,
  COMMON,
  FINANCIAL,
  COMMUNICATION,
  PHYSICAL,
  GROWTH,
  SUPPORT,
  STABILITY,
  COLLABORATION,
  INNOVATION,
  RESPECT,
  TENSIONS,
  HIERARCHY,
}


