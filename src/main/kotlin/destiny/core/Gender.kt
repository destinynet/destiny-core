package destiny.core

import destiny.tools.serializers.GenderSerializer
import kotlinx.serialization.Serializable

/** 性別  */
@Serializable(with = GenderSerializer::class)
enum class Gender(val male: Boolean) {

  男(true),
  女(false);

}
