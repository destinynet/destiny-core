/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.IPoints
import destiny.core.Point
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.tools.serializers.StarDoctorSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

/**
 * 博士12神煞 , 丙級星
 *
 * 博士永遠跟著祿存走，也就是說 [StarLucky.祿存] 的旁邊一定有博士，然後分別排列出力士、青龍、小耗……等星。
 * 陽男陰女順排，陰男陽女逆排。
 *
 * 亦即，這是 (年干,性別) -> 地支
 * 每次 function 都要 call [StarLucky.fun祿存]
 *
 */
@Serializable(with = StarDoctorSerializer::class)
sealed class StarDoctor(nameKey: String) : ZStar(nameKey, ZStar::class.java.name, Type.博士) {
  object 博士 : StarDoctor("博士")
  object 力士 : StarDoctor("力士")
  object 青龍 : StarDoctor("青龍")
  object 小耗 : StarDoctor("小耗") // 又叫「地耗」
  object 將軍 : StarDoctor("將軍")
  object 奏書 : StarDoctor("奏書")
  object 飛廉 : StarDoctor("飛廉")
  object 喜神 : StarDoctor("喜神")
  object 病符 : StarDoctor("病符")
  object 大耗 : StarDoctor("大耗") // 又叫「天耗」
  object 伏兵 : StarDoctor("伏兵")
  object 官府 : StarDoctor("官府")

  override fun compareTo(other: ZStar): Int {
    return if (other is StarDoctor) {
      values.indexOf(this) - values.indexOf(other)
    } else {
      super.compareTo(other)
    }
  }

  companion object : IPoints<StarDoctor> {

    override val type: KClass<out Point> = StarDoctor::class

    override val values by lazy { arrayOf(博士, 力士, 青龍, 小耗, 將軍, 奏書, 飛廉, 喜神, 病符, 大耗, 伏兵, 官府) }

    override fun fromString(value: String, locale: Locale): StarDoctor? {
      return values.firstOrNull {
        it.nameKey == value
      }
    }

    // 年干、性別、步數
    private val branchGender2Branch = { tuple3: Triple<Stem, Gender, Int> ->
      val yearStem = tuple3.first
      // 祿存地支
      val branch = StarLucky.fun祿存.invoke(yearStem)
      val gender = tuple3.second
      val steps = tuple3.third

      if (yearStem.booleanValue && gender === Gender.M || !yearStem.booleanValue && gender === Gender.F) {
        // 陽男 陰女 順行
        branch.next(steps - 1)
      } else {
        // 陰男 陽女 逆行
        branch.prev(steps - 1)
      }
    }

    // 年干星系
    private val fun博士 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 1)) }
    private val fun力士 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 2)) }
    private val fun青龍 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 3)) }
    private val fun小耗 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 4)) }
    private val fun將軍 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 5)) }
    private val fun奏書 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 6)) }
    private val fun飛廉 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 7)) }
    private val fun喜神 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 8)) }
    private val fun病符 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 9)) }
    private val fun大耗 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 10)) }
    private val fun伏兵 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 11)) }
    private val fun官府 = { stem: Stem, gender: Gender -> branchGender2Branch.invoke(Triple(stem, gender, 12)) }

    val starFuncMap: Map<StarDoctor, Function2<Stem, Gender, Branch>> by lazy {
      mapOf(
        博士 to fun博士,
        力士 to fun力士,
        青龍 to fun青龍,
        小耗 to fun小耗,
        將軍 to fun將軍,
        奏書 to fun奏書,
        飛廉 to fun飛廉,
        喜神 to fun喜神,
        病符 to fun病符,
        大耗 to fun大耗,
        伏兵 to fun伏兵,
        官府 to fun官府
           )
    }
  }
}
