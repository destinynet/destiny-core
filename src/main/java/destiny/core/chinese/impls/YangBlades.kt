package destiny.core.chinese.impls

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.core.chinese.Characters
import destiny.core.chinese.IYangBlade
import destiny.core.chinese.Stem
import java.util.*

/** 羊刃 : 「祿」  的下一位 , 陰干 的羊刃，會落在辰戌丑未 四庫中。 參考 https://imgur.com/bZZQRIw */
class YangBladeNextBlissImpl : IYangBlade, Descriptive {
  override fun getYangBlade(stem: Stem): Branch {
    return Characters.getBliss(stem).next(1)
  }

  override fun getTitle(locale: Locale): String {
    return "「祿」的下一位"
  }

  override fun getDescription(locale: Locale): String {
    return "陰干 的羊刃，會落在辰戌丑未 四庫中。"
  }
}

/** 羊刃 : 劫財 算法 , 陰干 的羊刃，會落在 寅巳申亥 四驛馬中。 參考 https://imgur.com/bZZQRIw */
class YangBladeRobCashImpl : IYangBlade, Descriptive {
  override fun getYangBlade(stem: Stem): Branch {
    return when (stem) {
      Stem.甲 -> Branch.卯      // 甲的帝旺在卯，卯中藏乙木，乙是甲的劫財，故卯是甲的羊刃。
      Stem.乙 -> Branch.寅      // 乙的帝旺在寅，寅藏甲丙戊，甲是乙的劫財，故寅是乙的羊刃。
      Stem.丙, Stem.戊 -> Branch.午 // 丙戊帝旺在午，午中藏丁己，丁是丙的劫財，己是戊的劫財，故午是丙戊的羊刃。
      Stem.丁, Stem.己 -> Branch.巳 // 丁己帝旺在巳，巳藏丙戊庚，丙是丁的劫財，戊是己的劫財，故巳是丁己的羊刃。
      Stem.庚 -> Branch.酉      // 庚的帝旺在酉，酉獨藏辛金，辛是庚的劫財，故酉是庚的羊刃。
      Stem.辛 -> Branch.申      // 辛的帝旺在申，申藏庚壬戊，庚是辛的劫財，故申是辛的羊刃。
      Stem.壬 -> Branch.子      // 壬的帝旺在子，子藏單癸水，癸是壬的劫財，故子是壬的羊刃。
      Stem.癸 -> Branch.亥      // 癸的帝旺在亥，亥中藏壬甲，壬是子的劫財，故亥是癸的羊刃。
    }
  }

  override fun getTitle(locale: Locale): String {
    return "劫財"
  }

  override fun getDescription(locale: Locale): String {
    return "陰干 的羊刃，會落在 寅巳申亥 四驛馬中。"
  }
}