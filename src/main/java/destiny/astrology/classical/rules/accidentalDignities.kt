/**
 * Created by kevin.huang on 2019-09-26.
 */
package destiny.astrology.classical.rules

abstract class AccidentalDignity(override val name: String) : IClassicalPattern {
  override val type: RuleType = RuleType.ESSENTIAL
}

val ruler = object : AccidentalDignity(name = "Ruler") {

  override val notes: String?
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}