package destiny.core

import destiny.core.astrology.BirthDataGrain
import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.DomainSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable as KSerializable
import destiny.tools.JSerializable

enum class SynastryGrain {
  BOTH_FULL,              // Both parties have full date and time
  INNER_FULL_OUTER_DATE,  // Inner chart has full data, outer chart only has date
  INNER_DATE_OUTER_FULL,  // Inner chart only has date, outer chart has full data
  BOTH_DATE               // Both parties only have date, no time
}

/** 內盤自身的時刻精度 */
val SynastryGrain.innerGrain: BirthDataGrain
  get() = when (this) {
    SynastryGrain.BOTH_FULL, SynastryGrain.INNER_FULL_OUTER_DATE -> BirthDataGrain.MINUTE
    SynastryGrain.INNER_DATE_OUTER_FULL, SynastryGrain.BOTH_DATE -> BirthDataGrain.DAY
  }

/** 外盤自身的時刻精度 */
val SynastryGrain.outerGrain: BirthDataGrain
  get() = when (this) {
    SynastryGrain.BOTH_FULL, SynastryGrain.INNER_DATE_OUTER_FULL -> BirthDataGrain.MINUTE
    SynastryGrain.INNER_FULL_OUTER_DATE, SynastryGrain.BOTH_DATE -> BirthDataGrain.DAY
  }

enum class SynastryRelationship {
  LOVE,
  FRIENDSHIP,
  PARTNERSHIP,
  RIVAL,
  PARENT_CHILD,
}

interface ISynastryModel : JSerializable {
  val inner: IBirthDataNamePlace
  val outer: IBirthDataNamePlace
  val grain: SynastryGrain
  val domainBdnp: Domain.Bdnp
  val relationship: SynastryRelationship
}

@KSerializable
data class SynastryModel(
  @Contextual
  override val inner: IBirthDataNamePlace,
  @Contextual
  override val outer: IBirthDataNamePlace,
  override val grain: SynastryGrain,
  @KSerializable(with = DomainSerializer::class)
  override val domainBdnp: Domain.Bdnp,
  override val relationship: SynastryRelationship
) : ISynastryModel

enum class SynastryDomain {
  OVERVIEW,
  EMOTIONAL,
  COMMON,
  FINANCIAL,
  COMMUNICATION,
  INTIMACY,
  GROWTH,
  SUPPORT,
  STABILITY,
  COLLABORATION,
  INNOVATION,
  RESPECT,
  TENSIONS,
  HIERARCHY,
}


