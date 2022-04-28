/**
 * Created by smallufo on 2018-07-23.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.grip
import destiny.core.chinese.trine
import destiny.core.chinese.ziwei.ClassicalPattern.*
import destiny.core.chinese.ziwei.House.*
import destiny.core.chinese.ziwei.IClassicalPattern.PatternType
import destiny.core.chinese.ziwei.IClassicalPattern.PatternType.EVIL
import destiny.core.chinese.ziwei.IClassicalPattern.PatternType.GOOD
import destiny.core.chinese.ziwei.ITransFour.Value.忌
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.StarUnlucky.*
import java.io.Serializable

// =========================== 以下 , 吉格 ===========================
/**
 * 紫微在午宮坐命
 * 又稱 金輿扶駕
 * 三方四正無兇星
 * */
val p極向離明 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.takeIf { it.mainHouse.branch == 午 }
      ?.takeIf { it.starMap[紫微]?.stemBranch?.branch == 午 }
      ?.takeIf { it.三方四正無六惡星() }
      ?.let { 極向離明 }
  }
}

/** 安命在寅或申宮，紫微天府同宮。 */
val p紫府同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch.let { branch ->
      if ((branch == 寅 || branch == 申) &&
        it.starMap[紫微]?.stemBranch == it.starMap[天府]?.stemBranch
      ) 紫府同宮
      else
        null
    }
  }
}

/**
 * 紫微、天府於三方四正照命。
 * 命宮在申，紫微在子，天府在辰，申子辰三合，謂之紫府朝垣格，主其人高官厚爵，福祿昌隆。
 *
 * 紫府朝垣格就是紫微、天府在命宮三方四正合照命宮，且有祿存或者科、權、祿，或者有左輔右弼、文昌文曲、天魁天鉞等吉星會照。
 * */
val p紫府朝垣 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.紫府().trine()
      .takeIf { branches.contains(it) }   // 紫府拱
      ?.let { branch ->
        val goods: Set<GoodCombo> = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        branch to goods
      }
      ?.takeIf { (_, goods) -> goods.isNotEmpty() }
      ?.let { (branch, goods) ->
        val house = it.getHouseDataOf(branch).house
        紫府朝垣(house, goods)
      }
  }
}


/**
 * 天府、廉貞二星在戌宮坐命會祿存、科權祿、左右、昌曲、魁鉞諸吉星，無煞方合此格，有左輔或右弼在命宮方好，甲己年生人最佳，丁年生人次之。
 *
 * 類似 [p極向離明] 的發展，因為當紫微在中宮(午)時，廉貞天府必在戌宮。訂定這個格局的人，其構想是以紫微為帝垣，在戌宮的天府則有如一位大臣，朝拱于帝座。
 *
 * 所以歌裡才會有「乾為君象府為臣」，「輔弼忠臣身報國」的說法。
 * 這個格局。其實也可以說是「紫相朝垣」，因為命宮在戌，紫微在午，天相在寅，寅午兩宮朝向戌垣。故古人認為「天府臨戌有星拱，腰金衣紫。」
 * 從訂定格局的立意來看，顯然「天府朝垣」只能成為「輔弼」，所謂「腰金衣紫」，無非只是屬于大臣的榮譽，究竟不是領袖人材。
 * 根據現代社會結構，廉貞天府在戌宮同守的人，只是一位很好的理財人材。若甲年生人，廉貞化祿，祿存又居于寅宮相會，則其人亦能創業致富，但卻缺少開創力，只能在守成中發展，不擅長開創。若天馬在寅宮，則其人利于外埠經商。
 */
val p天府朝垣 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.mainHouse.branch
      .takeIf { branch ->
        branch == 戌
          && setOf(branch).containsAll(it.getBranches(天府, 廉貞))  // 天府廉貞在戌宮坐命
      }
      ?.let { _ ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼())
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲())
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞())
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存())
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星())
            add(GoodCombo.祿權科星)
        }.toSet()

        goods
      }?.takeIf { goods -> goods.isNotEmpty() }
      ?.let { goods -> 天府朝垣(goods) }
  }
}


/**
 * 命宮在寅或申，天府在 事業宮，天相在財帛宮，謂之府相朝垣格，主其人大富大貴。
 *
 * 命宮三方四正有祿存、科權祿、左右、昌曲、魁鉞加會方合此格，有四煞劫空化忌加會則破格。此格尚主與與親人朋友感情深，人情味濃。
 *
 * NOTE : 不適合用 [PatternMultipleImpl] , 重複率太高
 */
val p府相朝垣 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 寅 || it == 申 }
      ?.takeIf { _ -> it.starMap[天府]?.house == 官祿 }
      ?.takeIf { _ -> it.starMap[天相]?.house == 財帛 }
      ?.let { _ ->
        val goods: Set<GoodCombo> = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼())
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲())
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞())
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存())
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星())
            add(GoodCombo.祿權科星)
        }.toSet()
        goods
      }
      ?.takeIf { it.isNotEmpty() }
      ?.let { goods ->
        府相朝垣(命宮, goods)
      }
  }
}


/**
 * 巨門、天機二星在卯宮或酉宮坐命，且無化忌同宮。
 * 另說： 巨機在酉宮守命不是此格，但總體而言仍不失為好的命局。
 *
 * 天機巨門同宮的情形有兩種，一為在卯宮，一為在酉宮。
 * 古人對此的評價是，「天機與巨門同居卯酉，必退祖而自興」；
 * 「機巨酉上化吉者，縱遇財官也不榮」。
 * 換而言之，即是卯優于酉。原因在于天機屬木，于酉宮受金所制，不如在卯宮得木之旺氣。
 *
 * 巨機同宮格也是紫微斗數中一個比較有名的富貴格局，指的是巨門、天機二星在卯宮坐命，
 * 同時有祿存、科權祿、左右、昌曲等吉星同宮或加會，方成此格。
 * 紫微命盤中有此格局者，有一流的學問，宜從政，主大富大貴，名揚世界。
 * 格局稍次者，經商亦可成為富翁。詩曰：“巨門廟旺遇天機，高節清風世罕稀。學就一朝騰達去，巍巍德業震華夷。”
 *
 * 巨機在酉宮守命不是此格，但總體而言仍不失為好的命局。
 * 古代對于女命這個格局評價比較低，但是當今社會巨機同臨是個旺夫格。
 * 因為巨機同臨時，夫妻官祿線必有太陽、太陰同宮坐守，大利婚姻。
 */
val p巨機同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.mainHouse.branch
      .takeIf { it == 卯 || it == 酉 }
      ?.takeIf { branch -> it.starMap[巨門]?.stemBranch?.branch == branch }
      ?.takeIf { branch -> it.starMap[天機]?.stemBranch?.branch == branch }   // 巨門、天機二星在卯宮或酉宮坐命
      ?.takeIf { branch -> it.getTransFourHouseOf(忌).stemBranch.branch != branch }  // 無化忌同宮
      ?.let { branch ->
        val goods: Set<GoodCombo> = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        巨機同宮(branch, goods)
      }
  }
}


/**
 * (機梁加會)
 * 天機、天梁二星 (同時) 在辰或戌宮守命，為此格。 亦稱「機梁加會」
 *
 * 天機星的五行局木，為益算之星，化氣為「善」，
 * 天梁星屬土，司「壽」，化氣為「蔭」，
 * 天機與天梁同宮時，才符合善蔭朝綱的基本條件，
 * 而唯有在辰宮與戌宮，天機天梁才有同宮的機會
 *
 * 如果機梁在辰，命宮在戌；或機梁在戌，命宮在辰，才符合善蔭朝綱的格局稱謂，而這二種模式的善蔭朝綱何者較優呢？
 * 那必然是天機天梁星在辰，而命宮在戌的組合，
 * 因為在這種盤面裡，太陽、太陰星座落在旺相的宮位。
 * 所謂「機梁善談兵」，可以聯想到，具有此種命格之人，口才必好，而口才好的人其邏輯組織力強，分析能力、策劃、規劃力的縝密度及前瞻性較常人為佳，
 * 也就是為聰明機智之人，這也是具有善蔭朝綱格局之人的通性。
 *
 * 所謂“機梁加會格”是指天機、天梁二星在辰戌宮守命，與祿存、科權祿、左右、昌曲、魁鉞加會。
 * 紫微命盤中有此格局者心地善良，樂善好施，于事奉公守法，于家庭父慈子孝、兄友弟恭，能設身處地為人著想，而且口才極佳，講起話來滔滔不絕。
 * 此人必擅策劃有分析能力，聰明靈敏，能以特殊技藝立足于社會。會吉星多，主大富大貴。
 * 吉星少，從事的工作的軍警、司法等有關。又見煞者，多為宗教教主、邪教創始人、神學家、哲學家、思想家、氣功師。
 */
val p善蔭朝綱 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 辰 || it == 戌 }
      ?.takeIf { branch -> it.starMap[天機]?.stemBranch?.branch == branch }
      ?.takeIf { branch -> it.starMap[天梁]?.stemBranch?.branch == branch } // 機梁同時守命於辰或戌
      ?.let { branch ->
        val goods: Set<GoodCombo> = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        善蔭朝綱(goods)
      }
  }
}


/**
 * 於三方四正中有天機、太陰、天同、天梁四星交會。
 *
 * 命宮的對宮、合宮見天機、太陰、天同、天梁等，同宮加會，謂之機月同梁格，主其人智慧超群，為最佳之幕僚、輔佐人才。
 */
val p機月同梁 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val fourStars: List<Branch> = it.getBranches(天機, 太陰, 天同, 天梁)

    return if (it.三方四正().containsAll(fourStars))
      機月同梁
    else
      null
  }
}


/**
 * 太陽、太陰入田宅宮在丑或未時，謂之日月照壁格，主其人能獲豐盛的祖產、家業。
 *
 * 此格局必須命宮三方四正有吉星并吉化，命主方可富貴，若命宮三方會兇煞多，仍屬貧賤之命，田宅宮之吉并無大用。
 *
 * 此格局最喜與祿存、科權祿、左右、昌曲、魁鉞同宮或加會，富貴更高，
 * 亦喜太陰化祿，化權，化科均佳。
 *
 * 忌太陽、太陰落陷，或有煞忌星會照，命主一生辛苦難免。
 */
val p日月照壁 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.getHouseDataOf(田宅)
      ?.takeIf { houseData -> houseData.stemBranch.branch == 丑 || houseData.stemBranch.branch == 未 }
      ?.takeIf { houseData -> houseData.stars.containsAll(listOf(太陽, 太陰)) }   // 日月入田宅於丑或未
      ?.stemBranch?.branch
      ?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        日月照壁(goods)
      }
  }
}

/**
 * (金燦光輝)
 * 太陽在午宮坐命。
 *
 * 金燦光輝格 : 又名日麗中天格，太陽守命入午宮，與祿存、科權祿、左右、昌曲、魁鉞加會方合此格。喜夏天及白天生人。
 *
 * 白天出生者，謂之金燦光輝格，主富可敵國，或為權貴。
 */
val p日麗中天 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.mainHouse.branch
      .takeIf { branch ->
        branch == 午 && it.starMap[太陽]?.stemBranch?.branch == 午
      }
      ?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        日麗中天(it.dayNight, goods)
      }
  }
}


/**
 * 命宮有紫微星，且於三方四正中有至少有左輔、右弼任何一星加會或同宮，或兩星於兩鄰宮相夾。
 *
 * 命宮有紫微星，得天府、天相、左輔、右弼、文昌、文曲、三台、八座、龍池、鳳閣、恩光、天貴等吉星在三方四正會合，無煞方合此格，加祿存並吉化更佳。
 *
 * (用此法)
 * 「君臣慶會」通常指的是紫微星坐宮和三合宮，坐入三組六吉星（左輔、右弼一組，文昌、文曲一組，天魁、天鉞一組）各一顆以上所形成的格局，
 *
 * 例如紫微在辰宮、文曲在申宮、左輔在辰宮、天魁在子宮、天鉞在申宮，此盤的紫微會入六吉星中的四顆，就形成「君臣慶會」格。
 */

val p君臣慶會 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {

    return it.starMap[紫微]?.stemBranch?.branch
      ?.takeIf { b -> branches.contains(b) }
      ?.let { b ->
        // 紫微地支
        val goods: Set<GoodCombo>? = mutableSetOf<GoodCombo>().apply {
          // 必備條件
          if (it.輔弼().intersect(it.三方(b)).isNotEmpty()) {
            add(GoodCombo.輔弼)
          }
          if (it.昌曲().intersect(it.三方(b)).isNotEmpty()) {
            add(GoodCombo.昌曲)
          }
          if (it.魁鉞().intersect(it.三方(b)).isNotEmpty()) {
            add(GoodCombo.魁鉞)
          }
        }.takeIf { it.size == 3 }?.apply {
          // 附加條件
          if (it.三方四正有祿權科星(b)) {
            add(GoodCombo.祿權科星)
          }
          if (it.三方四正有祿存(b)) {
            add(GoodCombo.祿存)
          }
        }?.toSet()
        b to goods
      }
      ?.takeIf { (_, goods) -> goods != null && goods.size >= 3 }
      ?.let { (branch, goods) ->
        君臣慶會(it.getHouseDataOf(branch).house, goods!!)
      }
  }
}

/**
 * 命宮在丑或未，日月二星坐守。
 */
val p日月同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.mainHouse.branch
      .takeIf { it == 丑 || it == 未 }
      ?.takeIf { branch -> it.starMap[太陽]?.stemBranch?.branch == branch }
      ?.takeIf { branch -> it.starMap[太陰]?.stemBranch?.branch == branch }
      ?.let { 日月同宮 }
  }
}

/**
 * 日月位於三方四正中，且太陽在巳、太陰在酉或太陽在辰、太陰在戌。為本格。
 * 命宮在丑，太陽在巳，太陰在酉，巳酉丑三合， 謂之日月雙明格，主其人財官皆美，金榜高中。
 * 亦有稱「日巳月酉」者。
 *
 * 第一種情況是天梁在丑宮坐守命宮，太陽、太陰分別在巳（官祿宮）、酉（財帛宮）會照命宮，且兩顆主星皆處于廟旺之地，稱為“日月并明”
 * 第二種是命宮在午宮無主星，官祿宮在寅由巨門、太陽坐守，財帛在子有天同、太陰入駐，日月皆處廟旺之地同時會照命宮，
 * 而且有祿存、科權祿、左右、昌曲、魁鉞加會，即為“日月并明”格。
 *
 * 注意其與 [p丹墀桂墀] 之差異 , [p丹墀桂墀] 更加入了 日月 要 廟旺 的條件
 */
val p日月並明 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == 丑 }
      ?.takeIf { _ -> it.starMap[太陽]?.stemBranch?.branch == 巳 }
      ?.takeIf { _ -> it.starMap[太陰]?.stemBranch?.branch == 酉 }
      ?.let { 日月並明 }
  }
}


/**
 * (日出扶桑)
 *
 * 此格局：太陽、天梁 一定在卯
 * 太陽入命宮在卯時，白天出生者，謂之日照雷門格，主其人榮華富貴。
 *
 *
 * 在紫微斗數命盤中，太陽在卯，是清晨五時到七時的太陽，此時太陽雖然剛剛從地平線升起，但已經是朝氣蓬勃、光芒萬丈，急著想在人間一展長才與抱負。
 * 由于卯宮的太陽身邊還帶有一顆遇難呈祥、化解災厄的天梁星，然而卯宮的太陽實在是太有抱負，同宮的天梁總是得適時的為“沖過頭”的太陽化解各種困難。
 * 因此雖然卯宮的太陽尤其的有志向，能在事業上有所表現，但也多驚險。
 *
 * 天梁星在紫微斗數里是“蔭”星，“蔭”有“蔭庇、保護”之意函，
 * 因此可以引申為，太陽在卯宮（卯時），開始要“努力的”開展它的工作了，但它卻又考慮到萬一把人曬傷或曬昏了怎么辦？
 * 因此它隨時準備著一把傘（天梁的蔭），告訴所有的人都趕緊進來遮“蔭”。
 * 如此我們就可以曉得，太陽、天梁在卯宮入命的人，喜歡以老大自居，喜愛當主導者，也喜歡關心人，待人方式除了強勢之外，還有一點點熱情，只是此熱情有時卻令人受不了罷了。
 *
 * 在紫微斗數中，日照雷門格（又叫日出扶桑格）也是一個非常有名的富貴格局，此格局的特點是紫微命宮在卯，
 *
 * 所謂「日照雷門」的「雷門」指的是「卯宮」，為什麼「卯宮」會叫「雷門」呢？
 * 因為卯在十二地支裡代表農曆的二月，而二月在二十四節氣裡，一交驚蟄便進入二月，同時二月也是打雷下雨、萬物滋長的季節，所以才會稱之為「雷門」。
 *
 * TODO 其中有太陽、天梁坐守；財帛宮在亥，由太陰坐守；官祿宮在未，無主星入駐。另外需有吉星會照，如魁鉞、祿存、化祿、左輔右弼等, 如見空劫、煞忌星則破格。
 *
 * TODO : 另一種類似的 陽梁居卯 , 則針對「非命宮」而言
 */
val p日照雷門 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return if (
      it.mainHouse.branch == 卯
      && it.dayNight == DayNight.DAY
      && it.starMap[太陽]?.stemBranch?.branch == 卯
    ) {
      日照雷門
    } else
      null
  }
}

/**
 * 與 [p日照雷門] 類似， 單純考量 陽梁居卯 + 與文昌、祿存同宮 . (好像沒看到考量 日夜)
 *
 * ==> 綁定 陽梁 在卯宮 , 四星同宮 (很嚴格)
 * 太陽、天梁在卯同宮，若與文昌、祿存同宮，是為“陽梁昌祿”格，主利于考試，
 * 且多能因參加國家考試謀得職業，且因此得以發展，不但具有文藝天才，且終能在辛苦之后，成為國家要員。
 *
 * ==> 另一法，不綁定卯宮。 (較寬鬆) ==> 用此法
 * 以太陽為主。三方四正會入天梁、文昌、化祿（或祿存）。
 * 陽梁昌祿 的格局組成是:太陽、天梁、文昌、祿存（或化祿）在三方四正會之。當然,化祿為上格,祿存次之。
 */
val p陽梁昌祿 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {

    val 太陽地支 = it.starMap[太陽]?.stemBranch?.branch

    val 陽梁昌祿 = 太陽地支?.let { branch ->
      val 梁昌 = it.getBranches(天梁, 文昌)
      // 上格
      val 化祿 = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch
      // 次之
      val 祿存 = it.starMap[祿存]?.stemBranch?.branch

      val 梁昌化祿 = 梁昌.plus(化祿)
      val 梁昌祿存 = 梁昌.plus(祿存)

      it.三方四正(branch).containsAll(梁昌化祿) || it.三方四正(branch).containsAll(梁昌祿存)
    } ?: false

    return if (陽梁昌祿) {
      val house = it.getHouseDataOf(太陽地支!!).house
      陽梁昌祿(house)
    } else
      null
  }
}

/**
 * 本宮在未宮，無主星坐命，且太陽在卯宮、太陰在亥宮。此時日月於三方四正中照命。
 *
 * 命宮在未，太陽在卯，太陰在亥，亥卯未三合，謂之明珠出海格，主其人財官皆美，科第榮恩。
 *
 * 在紫微命盤中，太陰星位于亥宮時能量最強，因為亥時正好是晚上九點至十一點這段時間，此時正是月亮最明亮的時候；
 * 同時太陽位于卯宮時也是處于一種旭日東升、蒸蒸日上的狀態中，因此命盤中有此格局的人容易少年成名。
 */
val p明珠出海 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == 未 }
      ?.takeIf { _ -> it.starMap[太陽]?.stemBranch?.branch == 卯 }
      ?.takeIf { _ -> it.starMap[太陰]?.stemBranch?.branch == 亥 }
      ?.let { 明珠出海 }
  }
}


/**
 * 巨門太陽同時在寅或申宮坐命。
 *
 * 太陽、巨門入命宮在寅時，謂之巨日同宮格，主其人食祿豐譽，口福聞名。
 */
val p巨日同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 寅 || it == 申 }
      ?.takeIf { branch -> setOf(branch).containsAll(it.getBranches(太陽, 巨門)) }
      ?.let { 巨日同宮 }
  }
}

/**
 * 命宮在丑或未，武曲貪狼二星坐守。
 *
 * 貪狼武曲入命宮在丑或未時，謂之貪武同行格，主其人先貧後富，大器晚成，三十歲後發福。
 */
val p貪武同行 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 丑 || it == 未 }
      ?.takeIf { branch -> setOf(branch).containsAll(it.getBranches(武曲, 貪狼)) }
      ?.let { 貪武同行 }
  }
}


/**
 * 武曲坐命在辰或戌宮。
 *
 * TODO 武曲入命宮在辰、戌、丑、未時，若又為辰、戌、丑、未年出生者，謂之將星得地格，主其人英名顯赫，大富大貴。
 */
val p將星得地 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 辰 || it == 戌 }
      ?.takeIf { branch -> it.starMap[武曲]?.stemBranch?.branch == branch }
      ?.let { 將星得地 }
  }
}


/**
 * 七殺入命宮在寅、申、子、午時， TODO 逢諸吉不逢惡曜，謂之七殺廟斗格，主其人一生官祿昌隆。
 *
 * 七殺守命，入子午寅申宮，與祿存、科權祿、左右、昌曲、魁鉞加會為本格。
 * 七殺在寅申宮入廟，在子午宮旺地。
 * 七殺在申、午坐命為“朝斗”，
 *    在寅、子坐命為“仰斗”。
 * 此格帶有殺氣，自己發達而必有另一部份人遭殃，或所以而有不少人死在他的手下。
 */
val p七殺朝斗 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 申 || it == 午 || it == 子 || it == 寅 }
      ?.takeIf { branch -> it.starMap[七殺]?.stemBranch?.branch == branch } // 七殺入命宮在寅申子午
      ?.let { branch ->
        mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
      }
      ?.let { goods -> 七殺朝斗(goods) }
  }
}

/**
 * (雄宿朝元 、 權星朝垣? )
 * 廉貞在申或寅宮守命。
 *
 * 廉貞入命宮在寅或申時， TODO 逢諸吉不逢惡曜，謂之雄宿朝元格，主其人富貴且名揚四海。
 *
 * 廉貞稱為「雄宿」，是由于他具有威武的性質，而卻又下同于破軍的帶有破壞力。所以破軍的「英星」，殺氣大于廉貞的「雄宿」性質。
 * 反映在人世的際遇上，廉貞守命的人便亦比較平和，一生的波折較小。
 *
 *  廉貞在寅申獨坐，對宮必為貪狼，事業為武曲天府在子午，財帛為紫微天相在辰戌。
 */
val p雄宿朝垣 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 寅 || it == 申 }
      ?.takeIf { branch -> it.starMap[廉貞]?.stemBranch?.branch == branch }
      ?.let { 雄宿朝垣 }
  }
}


/**
 * 命宮在子，太陽與化碌在午，謂之對面朝天格，主其人文章蓋世、超群。
 */
val p對面朝天 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 子 }   // 命宮在子
      ?.takeIf { _ -> it.starMap[太陽]?.stemBranch?.branch == 午 }         // 太陽在午
      ?.takeIf { _ -> it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch == 午 } // 化祿在午
      ?.let { 對面朝天 }
  }
}

/**
 * 化科入命宮，化祿入對宮(遷移宮)時，謂之科名會祿格，主其人威權出眾，大官貴。
 *
 * 化科在命宮，化祿在三方四正會照。
 *
 * (更寬鬆)
 * 是指命宮中有化科星坐守，三方四正有化祿或祿存星會照。紫微命盤中有此格局的人才華卓越，有名望，遠近皆榮顯，風云際會，開展運程，財名皆足。步入社會發展，可獲擢昇。多主大限行吉之地平步青云，
 * TODO 惟財官二宮亦并有魁鉞守照方作此斷。加煞忌、辛勞更甚、以破格論。
 */
val p科名會祿 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 三方四正有化祿: Boolean = it.三方四正().contains(it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch)

    return if (it.化科入命宮() && (三方四正有化祿 || it.三方四正有祿存())) {
      科名會祿
    } else
      null
  }
}

/**
 * 甲第登科格 : 化科在命宮，化權在三方四正會照，主聰明，有學歷，入社會時可飛黃騰達之跡象。
 * 因為以上定義已經包含了 [p科權逢迎] , 故在此，只實作「三方」 , 不考慮 對宮
 *
 * 「甲第登科格」一般包含在 [p三奇嘉會] 格裡，所不同者，不涉及化祿之定位，所以也不須去計較化忌的落點位置。
 */
val p甲第登科 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return if (
      it.化科入命宮() &&
      it.三方().contains(it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch)
    )
      甲第登科
    else
      null
  }
}

/**
 * 化科入命宮，化權入遷移宮(對宮)時，謂之科權逢迎格，主其人科甲及第，金榜高中。
 */
val p科權逢迎 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it
      .takeIf { it.化科入命宮() }
      ?.takeIf { it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch == it.mainHouse.branch.opposite } // 化權入遷移宮
      ?.let { 科權逢迎 }
  }
}

/**
 * 祿存、化碌入命宮時，謂之祿合鴛鴦格，主其人一生富貴無窮。
 * NOTE : 祿存 沒有化祿的機會。 因此，應該判讀為： 祿存 在命宮，且，命宮有星 化祿
 *
 * 祿存、化祿同宮守命；或者兩星一個在命宮，一個在遷移，相互守照，命宮三方四正有吉星會照，無煞忌星衝破。
 *
 * 雙祿交流格 : TODO 祿存和化祿俱在三方四正中。有財源，在事業上有成富的機運。又稱為祿合鴛鴦格。
 */
val p祿合鴛鴦 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 化祿入對宮 = it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch == it.mainHouse.branch.opposite
    val 祿存入命宮 = (it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch)
    val 祿存在對宮 = (it.mainHouse.branch.next(6) == it.starMap[祿存]?.stemBranch?.branch)

    return if (
      (it.化祿入命宮() && 祿存入命宮) ||
      ((祿存入命宮 && 化祿入對宮) || (祿存在對宮 && it.化祿入命宮()))
    ) 祿合鴛鴦
    else
      null
  }
}

/**
 * (嚴格)
 * 化碌入財帛宮，祿存入官祿宮，三合命宮，謂之雙祿朝垣格，主其人終身富貴無窮。
 *
 * (比較寬鬆)
 * 命宮無祿星坐守，但祿存與化祿分別位于命宮的三合宮，一守官祿一守財帛，是為最佳結構。
 *
 * (更寬鬆 , 考量三方四正) ==> 用此法
 * 所謂雙祿朝垣格，是指命宮有吉星坐守，命宮三方四正有祿存和化祿加會，無煞忌星沖照，即為此格。
 * [p雙祿朝垣] 講的是「化祿、祿存」拱「命宮」 .
 * [p祿合鴛鴦] 則是「化祿、祿存」一在「命」，一在「對宮（遷移）」
 */

val p雙祿朝垣 = object : PatternMultipleImpl() {

  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    val 化祿宮位: Branch = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch
    val 祿存宮位: Branch? = it.starMap[祿存]?.stemBranch?.branch

    val 被拱宮位: Branch? = setOf(化祿宮位, 祿存宮位).trine()?.takeIf { branches.contains(it) }

    return 被拱宮位?.let { b ->
      val house = it.getHouseDataOf(b).house
      雙祿朝垣(house)
    }
  }
}

/**
 * 化權、化碌、化科在命宮的三方四正，謂之三奇加會格，主其文才蓋世，出將入相。
 * (一定是：命宮、官祿、財帛)
 */
val p三奇嘉會 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val good3: Set<ITransFour.Value> = setOf(ITransFour.Value.祿, ITransFour.Value.權, ITransFour.Value.科)

    return it.三方四正().all { branch ->
      val a: Set<ITransFour.Value?> = it.getHouseDataOf(branch).stars.map { star ->
        it.transFours[star]?.get(FlowType.MAIN)
      }.toSet()
      a.any { value: ITransFour.Value? -> good3.contains(value) }
    }.let {
      if (it)
        三奇嘉會
      else
        null
    }
  }
}


/**
 * 命宮或三方有祿存、天馬或為化祿、天馬。主奔波勞碌而招財。
 *
 * 目前用此法 =>
 * 祿馬交馳最好（標準）的組合是祿和馬應分處對宮，祿存或化祿在對宮，天馬在本宮，這點是必須清晰認識的。
 * 因為祿馬交馳的本義是本宮的天馬把對宮的祿運輸到本宮，
 * 例如天馬在命，而祿在遷移，因為馬是來來回回的，所以這例子中命宮最能享祿馬交馳的好處（因為天馬在命宮），當然遷移宮也能受益。
 *
 * TODO 天馬、化碌、祿存在命宮或財帛宮的三方四正，謂之祿馬交馳格，主其人富貴、榮顯
 * 若祿跟馬同在本宮或祿馬同在對宮，祿馬交馳的力量就會大減。另外，若天馬同宮有截空，空亡，當然亦是減力的，祿馬交馳也不喜歡疊祿。
 */
val p祿馬交馳 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 天馬在命: Boolean = it.mainHouse.branch == it.starMap[天馬]?.stemBranch?.branch
    val 天馬在遷: Boolean = it.mainHouse.branch.opposite == it.starMap[天馬]?.stemBranch?.branch

    val 祿存在命 = it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch
    val 祿存在遷 = it.mainHouse.branch.opposite == it.starMap[祿存]?.stemBranch?.branch

    val 化祿入遷移: Boolean = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch == it.mainHouse.branch.opposite

    return if (
      (天馬在命 && (祿存在遷 || 化祿入遷移))
      ||
      (天馬在遷 && (祿存在命 || it.化祿入命宮()))
    )
      祿馬交馳
    else
      null
  }
}

/**
 * 太陰在亥宮守命，為本格。
 *
 * 太陰入命在亥宮時，夜晚出生者，謂之月朗天門格，主其人出相入將，非貴則富。
 */
val p月朗天門 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 亥 }
      ?.takeIf { _ -> it.starMap[太陰]?.stemBranch?.branch == 亥 }
      ?.let { _ -> 月朗天門(it.dayNight) }
  }
}

/**
 * 太陰、天同星在子宮坐命。又名“水澄桂萼”格
 *
 * 日月滄海格：太陰入命宮在子時，夜晚出生者，謂之日月滄海格，主其人富貴、清高、忠良。
 * 一輪明月高掛夜空，水中倒映著月亮，安靜祥和，美不勝收！因此此格生人，男的多儒雅帥哥，英俊瀟灑，女的多婀娜多姿，容貌秀麗，而且委婉從容，落落大方，這個格局是最能出俊男美女的。
 */
val p月生滄海 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 子 }
      ?.takeIf { _ -> it.starMap[太陰]?.stemBranch?.branch == 子 }
      ?.takeIf { _ -> it.starMap[天同]?.stemBranch?.branch == 子 }
      ?.let { _ -> 月生滄海(it.dayNight) }
  }
}

/**
 * 巨門在子或午宮坐命。
 * (對宮一定是天機？)
 *
 * TODO : 巨門入命宮在子或午時，又逢化權、化碌同宮，謂之石中隱玉格，主其人大富大貴，福祿豐隆。
 *
 * TODO : 所謂“石中隱玉格”，是指巨門在子宮或午宮坐命，三方四正有昌曲、魁鉞、左右、四化等吉星會照，無煞星沖破，即成格局。
 */
val p石中隱玉 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 子 || it == 午 }
      ?.takeIf { branch -> it.starMap[巨門]?.stemBranch?.branch == branch } // 巨門入命於子或午
      ?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        石中隱玉(goods)
      }
  }
}

/**
 * 天梁守命，入午宮。
 *
 * 天梁入命宮在午時， 逢諸吉不逢惡曜，謂之壽星入廟格，主其人官貴清高，福壽綿長。
 *
 * 壽星入廟格也是一個比較典型富貴格局，此格局的特點是
 * 天梁星在午宮坐命，且與祿存、科權祿、左右、昌曲、魁鉞會照。
 * 紫微命盤中有此格局的人性格沉穩，心地善良，正直無私，學識優秀，具有處理難題、統御眾人之才，
 * 且其人健康佳，壽命長，一生名大於利，會吉星眾，主大貴。
 * 缺點是性格過于耿直，缺乏變通，有些不通人情事故。
 * 有詩曰：命遇離明拱壽星，一生榮華沐深恩。飛騰鴻鴣青霄近，氣象堂堂侍帝廷。
 */
val p壽星入廟 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 午 }
      ?.takeIf { _ -> it.starMap[天梁]?.stemBranch?.branch == 午 } // 天梁入命宮在午
      ?.let { _ ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼())
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲())
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞())
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存())
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星())
            add(GoodCombo.祿權科星)
        }.toSet()
        壽星入廟(goods)
      }
  }
}

/**
 * 破軍守命居子或午宮。
 *
 * 破軍入命宮在子或午時，不逢惡耀等等，謂之英星入廟格，主其人高官厚祿，大貴顯榮。
 *
 * 所謂“英星入廟格”是指破軍星在子宮或者是午宮入廟坐守命宮，三方四正遇祿存、科權祿、左右、魁鉞等吉星會照，無煞星沖破，為此格。
 *
 * 紫微命盤中有“英星入廟”格局時，財帛宮必有七殺星坐守，而官祿宮則有貪狼星坐陣，
 * 為典型的殺、破狼結構。
 *
 * 只是這個格局因為貪狼星落入天羅地網宮位，其負面的性格受到非常大的制約，不致大事彰顯貪狼星的漫無天際，尚能遵循禮法、司法等等的規范。
 * 而七殺星也能在“體制”之內作最大能量的沖刺作為，這便是“英星入廟”格局的實質精神所在。
 */
val p英星入廟 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 子 || it == 午 }
      ?.takeIf { branch -> it.starMap[破軍]?.stemBranch?.branch == branch } // 破軍守命居子或午
      ?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        英星入廟(goods)
      }
  }
}

/**
 * 文昌、文曲兩星在丑或未宮守命。
 *
 * 文昌、文曲入命宮，或夾命宮，或三合命宮，謂之文桂文華格，主其人多學而廣，非富則貴。
 *
 * 若逢 破軍，且在 寅 or 卯 , 則為 [p眾水朝東]
 * */
val p文桂文華 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 丑 || it == 未 }
      ?.let { branch ->
        when {
          setOf(branch).containsAll(it.昌曲()) -> Route.入
          it.昌曲().grip() == branch -> Route.夾
          it.拱(branch) == it.昌曲().toSet() -> Route.拱
          else -> null
        }
      }
      ?.let { route -> 文桂文華(route) }
  }
}

/**
 * 類似 [p文桂文華] , 但是有更嚴格的條件 (旺廟...)
 *
 * 文曲（或文昌）與天梁旺地守命，三方有祿存、科權祿、左右、魁鉞加會為本格。宜從政，遇吉星多者，主大貴。
 * 紫微命盤中有此格局的人為人剛正不阿，口才出眾，能言善辯，特別適合在監察、紀律等行政部門工作，一生貴大于富。
 *
 * 在紫微斗數中，“文梁振紀格”是一個比較有名的清貴格局，
 * 即文曲（或文昌）、天梁一起坐守命宮，二星皆入廟旺之地，且命宮三方四正有祿存、科權祿、左右、魁鉞等吉星加會，即成格。
 */
val p文梁振紀 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 命宮有昌或曲且處廟旺 = it.getHouseDataOf(it.mainHouse.branch).stars
      .filter { star -> listOf(文昌, 文曲).contains(star) }
      .mapNotNull { star -> it.starStrengthMap[star] }
      .any { value -> value <= 2 }

    val 天梁在命且廟旺 = it.starMap[天梁]?.stemBranch?.branch?.takeIf { branch ->
      // 天梁在命
      branch == it.mainHouse.branch
    }?.let { _ ->
      it.starStrengthMap[天梁]?.let { value -> value <= 2 }
    } ?: false

    return if (命宮有昌或曲且處廟旺 && 天梁在命且廟旺) {
      val goods = mutableSetOf<GoodCombo>().apply {
        if (it.三方四正有昌曲())
          add(GoodCombo.昌曲)
        if (it.三方四正有魁鉞())
          add(GoodCombo.魁鉞)
        if (it.三方四正有祿存())
          add(GoodCombo.祿存)
        if (it.三方四正有祿權科星())
          add(GoodCombo.祿權科星)
      }.toSet()
      文梁振紀(goods)
    } else
      null
  }
}

/**
 * 天魁、天鉞入命宮，或夾命宮，或三合命宮，謂之魁鉞拱命格，主其人文章蓋世，高官厚祿，逢凶化吉，大富大貴。
 */
val p魁鉞拱命 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return when {
      setOf(it.mainHouse.branch).containsAll(it.魁鉞()) -> Route.入
      it.魁鉞().toSet() == it.neighbors() -> Route.夾
      it.魁鉞().trine() == it.mainHouse.branch -> Route.拱
      else -> null
    }?.let { route -> 魁鉞拱命(route) }
  }
}

/**
 * 命宮入丑或未宮，左輔右弼同宮，為本格。
 * 左輔、右弼入命宮 TODO 或三合命宮，鄰夾命宮，謂之左右同宮格，主其人終生福厚、豐隆。
 *
 * “左右同宮格”，就是說左輔、右弼星在丑未入命身宮，且與吉星同宮和加會，三方四正無煞星侵擾，放成此格。
 * 紫微命盤中有此格局者，其人必為端莊高士，性喜助人，富計劃、企劃能力，凡事可解兇，圓滿達成，加會為吉，主富貴，但多是居於輔佐他人的位置。
 * 若叁方四正多兇少吉，仍屬普通之人。
 * */
val p左右同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.mainHouse.branch
      .takeIf { it == 丑 || it == 未 }
      ?.takeIf { branch -> setOf(branch).containsAll(it.輔弼()) } // 輔弼入命於丑或未
      ?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        左右同宮(goods)
      }
  }
}

/**
 * A : 三方四正會太陽、太陰，且日月均在廟旺之宮位。
 *
 * 當太陽、太陰在廟旺之地會照命宮時，稱之為丹墀桂墀格，又稱為 [日月並明]。
 * 這個格局因為太陽、太陰皆處廟旺之地，能量充足，主星得力，因此主富貴。
 *
 * 當太陽入廟，處于旺位時便稱其為“丹墀”；
 * 當太陰入廟，處于旺位時稱之為“桂墀”。
 *
 *
 * B : 而中有當太陽、太陰一在辰宮，一在戌宮守命對照時，才稱之為丹墀桂墀格。
 * 這個格局因為太陽、太陰皆處廟旺之地，能量充足，主星得力，因此主富貴。
 */
val p丹墀桂墀 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it
      .takeIf { it.starStrengthMap[太陽]?.let { strength -> strength <= 2 } ?: false }  // 日旺廟
      ?.takeIf { it.starStrengthMap[太陰]?.let { strength -> strength <= 2 } ?: false } // 月旺廟
      ?.takeIf {
        val 三方四正有日月: Boolean = it.三方四正().containsAll(it.日月())
        val 日月分散在辰戌且其中一個守命宮 = setOf(辰, 戌) == it.日月().toSet() && setOf(辰,
                                                                      戌).contains(it.mainHouse.branch)
        三方四正有日月 || 日月分散在辰戌且其中一個守命宮
      }
      ?.let { 丹墀桂墀 }
  }
}

/**
 * 化科在命宮，化權在三方朝是。
 * 此格聰明過人，必考入高等學府，且主其人文章冠世，或在學術、科技上有創新和發明。又宜從任管理之職，或在政治上作投機。
 * 古人以化科為文墨之星，若正曜化科守命，雖會惡煞亦不失為「文章秀士，群英師範」，所以一旦與化權相會，便認為可因考試得科名而晉身廊廟。
 */
val p甲第登庸 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 拱有化權 = it.拱().contains(it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch)

    return if (it.化科入命宮() && 拱有化權)
      甲第登庸
    else
      null
  }
}

/**
 * 是一個好壞參半的格局
 * 巨門在辰宮坐命，辛年生人；天同在戌坐命，丁年生人，即為此格。
 */
val p化星返貴 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 辛年巨門在辰坐命: Boolean = it.mainHouse.branch
      .takeIf { it == 辰 }
      ?.takeIf { branch -> it.starMap[巨門]?.stemBranch?.branch == branch }
      ?.takeIf { _ -> it.year.stem == Stem.辛 }
      ?.let { _ -> true } ?: false

    val 丁年天同在戌坐命 = it.mainHouse.branch
      .takeIf { it == 戌 }
      ?.takeIf { branch -> it.starMap[天同]?.stemBranch?.branch == branch }
      ?.takeIf { _ -> it.year.stem == Stem.丁 }
      ?.let { _ -> true } ?: false

    return if (辛年巨門在辰坐命 || 丁年天同在戌坐命)
      化星返貴
    else
      null
  }
}


/**
 * 天魁、天鉞落入命盤三方四正，有學識，能取得高學歷，為人端莊，一生多助人，亦多得眾人相助，尤其能逢兇化吉，遇難呈祥，格局略低於坐貴向貴格。
 *
 * 與 [p坐貴向貴] 類似 , 有人認為是一樣的 . 這裡，把「拱」放入 [p天乙拱命] , 「沖」放入 [p坐貴向貴]
 *
 */
val p天乙拱命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == it.魁鉞().trine() }
      ?.let { 天乙拱命 }
  }
}

/**
 * 類似 [p天乙拱命] , 但 [p坐貴向貴] 只探討 命宮、對宮 (or 身宮)
 *
 * 在紫微斗數中，天魁星就是天乙貴人，另外一個是玉堂貴人就是天鉞星，天鉞星和天魁星是一對，因此這個格局也叫做「坐貴向貴」
 *
 * 此格局有如下兩種情況：
 * 1.天魁、天鉞一在命宮，一在身宮；
 * 2.或天魁、天鉞一在命宮，一在遷移宮(對宮)，身宮守遷移更佳。這兩種格局均須命宮主星廟旺，三方四正有吉星加會，方才成格。
 *
 * TODO 須命宮主星廟旺，三方四正有吉星加會，主有學識，高學歷，端莊，一生多助人，亦多得眾人相助，能逢凶化吉，遇難呈祥，富貴。 若命陷弱，縱有魁鉞，雖能得人助，終究普通無成就。
 */
val p坐貴向貴 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 命宮身宮 = setOf(it.mainHouse.branch, it.bodyHouse.branch)
    val 命宮對宮 = setOf(it.mainHouse.branch, it.mainHouse.branch.opposite)

    return it.魁鉞()
      .takeIf { 魁鉞 -> 魁鉞.containsAll(命宮身宮) || 魁鉞.containsAll(命宮對宮) }
      ?.let { 坐貴向貴 }
  }
}

/**
 * 廉貞坐命，官祿宮為武曲來會，三方四正再會文昌或文曲。主個人可獲功績名望。
 *
 * (比較嚴格, 綁定命宮為寅、申)
 * 所謂廉貞文武格是指廉貞星在寅、申宮坐守命宮，三方四正有武曲、文昌、文曲星拱照，即為此格。
 * 古人對這個格局的評價非常高，有「命中文武喜朝垣，入廟平生福氣全，純粹文能高折桂，戰征武定鎮三邊」之說。也就是說紫微命盤中有此格局的人文武雙全，很適合做大將軍。
 */
val p廉貞文武 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse
      .takeIf { sb -> it.starMap[廉貞]?.stemBranch?.branch == sb.branch }  // 廉貞坐命
      ?.takeIf { _ -> it.starMap[武曲]?.house == 官祿 }          // 武曲官祿
      ?.takeIf { _ -> it.昌曲().intersect(it.三方四正()).isNotEmpty() } // 三方四正有昌或曲
      ?.let { 廉貞文武 }
  }
}

/**
 * 下列任何一顆星入其正位，
 * 武曲正位在財帛宮、
 * 廉貞正位在官祿宮、
 * 天同正位在福德宮、
 * 太陰正位在田宅宮、
 * 天梁正位在父母宮。
 */
val p星臨正位 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val matched = mutableSetOf<ZStar>().apply {
      if (it.starMap[武曲]?.house == 財帛)
        add(武曲)
      if (it.starMap[廉貞]?.house == 財帛)
        add(廉貞)
      if (it.starMap[天同]?.house == 福德)
        add(天同)
      if (it.starMap[太陰]?.house == 田宅)
        add(太陰)
      if (it.starMap[天梁]?.house == 父母)
        add(天梁)
    }.toSet()

    return if (matched.isNotEmpty())
      星臨正位(matched)
    else
      null
  }
}

/**
 * 所謂“輔拱文星格”是指文昌星坐命宮，三方四正遇左輔星拱照，或者是左輔、右弼星夾命宮，即為此格。
 * 喜文曲、天魁、天鋮、化科星同宮或會照，富貴皆有；
 * 遇擎羊、火星、鈴星、地空、地劫、截路、空亡等兇星同宮或會照，則破格。
 */
val p輔拱文星 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.starMap[文昌]?.stemBranch?.branch
      ?.takeIf { branch -> it.三方四正(branch).contains(it.starMap[左輔]?.stemBranch?.branch) }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        輔拱文星(house)
      }
  }
}

/**
 * (貪火相逢)
 *
 * 貪狼守命，遇火星在命或三方拱照為此格。火星與貪狼同守命垣為佳，三合次之，
 * 若貪狼居于辰戌丑未，與祿存、科權祿、左右、魁鉞加會，則為極美之格，主大富大貴。
 * 其人或以武職立功，把握國家軍警大權，或經商暴發，財運亨通。喜與鈴星加會。
 */
val p三合火貪 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.starMap[貪狼]?.stemBranch?.branch
      ?.takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch -> it.三方(branch).contains(it.starMap[火星]?.stemBranch?.branch) }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        三合火貪(house)
      }
  }
}

/**
 * 類似 [p三合火貪] , ㄧ樣以 [貪狼] 為主 , 但以 [鈴星] 為輔
 */
val p三合鈴貪 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.starMap[貪狼]?.stemBranch?.branch
      ?.takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch -> it.三方(branch).contains(it.starMap[鈴星]?.stemBranch?.branch) }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        三合鈴貪(house)
      }
  }
}

/**
 * 說法A : (狹義的 [權祿巡逢] 必須由權與祿同入本命宮方算)
 * 即 化祿或祿存 與 化權 同守命宮，唯需無煞有吉者，遇煞則成虛譽之隆，虛有其表而已。
 *
 * 說法B : (廣義)
 * 化祿、化權在命宮三方四正會照
 *
 * 古歌云：「命逢權祿實堪誇，千載功名富貴家，單見也應身富厚，平生穩步好生涯」。
 * 具此命格者，有專業能力兼有經商智謀，若朝專業方向研發創新，可有大成就。
 */
val p權祿巡逢 = object : PatternMultipleImpl() {

  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    // 說法A
    val 化權與祿存或化祿同宮: Branch? = it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch
      .takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch ->
        it.starMap[祿存]?.stemBranch?.branch == branch
          || it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch == branch
      }

    // 說法B : 化祿、化權 拱 某宮
    val 化祿化權拱某宮: Branch? =
      listOf(it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch,
             it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch).trine()

    return listOf(化權與祿存或化祿同宮, 化祿化權拱某宮)
      .firstOrNull { it != null }
      ?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        val house = it.getHouseDataOf(branch).house
        權祿巡逢(house, goods)
      }
  }
}

/**
 * 化祿、化權、化科這三化曜其中有兩個或者三個居命宮兩側，即在鄰宮來夾命。
 */
val p科權祿夾 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return listOf(
      it.getTransFourHouseOf(ITransFour.Value.科).stemBranch.branch,
      it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch,
      it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch)
      .takeIf { 科權祿 -> 科權祿.containsAll(it.neighbors()) }
      ?.let { 科權祿夾 }
  }
}

/**
 * 坐命於旺宮，文昌、文曲於三方四正會入，稱為『文星拱命格』，
 * 書云『文昌文曲最榮華，值此需生富貴家，更得三方祥曜拱，卻如錦上又添花』
 */
val p文星拱命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.昌曲().trine()
      ?.takeIf { b -> it.mainHouse.branch == b }
      ?.let { 文星拱命 }
  }
}

/**
 * 財祿夾馬
 *
 * 財祿夾馬格，是指命宮在巳宮或亥宮，有天馬星坐守，
 * 命宮兩邊分別有武曲和祿存(或化祿)相夾，命宮三方四正有吉星會照，即為此格。
 *
 * 在紫微斗數中，武曲為財星，祿存或化祿亦主財，紫微命盤中有雙財星夾命，其人一生錢財不愁，假如命宮中又有天馬星坐守，則財源更廣，錢水更活。
 */
val p財祿夾馬 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {

    val 武曲branch = it.starMap[武曲]?.stemBranch?.branch
    val 祿存branch = it.starMap[祿存]?.stemBranch?.branch
    val 化祿branch = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch

    return it.starMap[天馬]?.stemBranch?.branch
      ?.takeIf { branches.contains(it) }
      ?.takeIf { 天馬branch ->
        val 武曲祿存夾天馬 = listOf(武曲branch, 祿存branch).grip() == 天馬branch
        val 武曲化祿夾天馬 = listOf(武曲branch, 化祿branch).grip() == 天馬branch
        (武曲祿存夾天馬 || 武曲化祿夾天馬)
      }?.let { branch ->
        val goods = mutableSetOf<GoodCombo>().apply {
          if (it.三方四正有輔弼(branch))
            add(GoodCombo.輔弼)
          if (it.三方四正有昌曲(branch))
            add(GoodCombo.昌曲)
          if (it.三方四正有魁鉞(branch))
            add(GoodCombo.魁鉞)
          if (it.三方四正有祿存(branch))
            add(GoodCombo.祿存)
          if (it.三方四正有祿權科星(branch))
            add(GoodCombo.祿權科星)
        }.toSet()
        val house = it.getHouseDataOf(branch).house
        財祿夾馬(house, goods)
      }
  }
}


/**
 * 天相星被稱為印星，掌管印鑒、玉璽之類的物事，一般被世人視為權力的象征。而在紫微斗數命盤中，一般的正曜都重視三方四正的組合，夾宮的情況，除了個別情形外，一般都較不重要。
 * 但是對于天相星來說，夾宮的星系性質，往往比三方四正還重要，這是天相的特點。
 *
 * 說法A (最寬鬆 , 選用此法) : (化祿,天梁).夾 == 天相
 * 在天相星系組合中，有一個很吉利的格局，稱為“財蔭夾印”格，
 * 凡天相被化祿和天梁在左右鄰宮相夾，便合此格。因為在紫微斗數中，祿曜為財，天梁有庇蔭的性質，化氣為蔭，故稱為“財蔭夾印”格。
 *
 * 說法B : (巨門化祿,天梁).夾 = 天相
 * 最正宗“財蔭夾印”格是巨門化祿，與天梁一起夾天相。
 * 因為按照安星訣的說法，天梁必在天相的前一宮，而巨門必在天相的后一宮。
 *
 * 說法C : (天機or太陽or天同 化祿,天梁化權).夾 = 天相
 * 另外，天機化祿和天梁化權夾天相，亦為大吉，不單是財蔭夾，而且是祿權夾。
 * 太陽化祿和天梁夾宮亦佳，更次之的，是天同化祿天梁夾，財氣較弱，助力也較遜色。祿存和天梁夾宮，雖然也有財蔭夾的性質，但由于必有擎羊同度，擎羊化氣為刑，不利天相，故為破格。
 */
val p財蔭夾印 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    val 化祿地支 = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch
    val 天梁地支 = it.starMap[天梁]?.stemBranch?.branch

    return listOf(化祿地支, 天梁地支).grip()
      ?.takeIf { branch ->
        it.starMap[天相]?.stemBranch?.branch == branch
      }?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        財蔭夾印(house)
      }
  }
}

/**
 * 擎羊坐命於丑辰未戌四墓之地。
 *
 * 羊刃本為刑伐之宿，但在辰戌丑未四墓之地，其凶性反為所製，保留其威強與衝勁，使之成為吉星反應。
 * 古典出處
 * 賦云：「擎羊入廟，富貴聲揚」。 賦又云：「擎羊火星，權威出眾」。 意為羊刃入廟格，若加火星，權威出眾，領導有方，有統御之方。
 * 雖說羊刃在四墓地，兇性被制，然其它凶星發作時，羊刃亦為災，刑傷難免。
 * 古賦云：「巨火擎羊，防遭縊死」。 此謂巨門、火星、羊刃(或陀羅)坐守身、命，大限逢之，太歲又兇，謹防縊死或投河。
 *
 */
val p擎羊入廟 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return listOf(辰, 戌, 丑, 未).firstOrNull { branch ->
      branches.contains(branch) &&
        it.starMap[擎羊]?.stemBranch?.branch == branch
    }?.let { branch ->
      val house = it.getHouseDataOf(branch).house
      擎羊入廟(house)
    }
  }
}

/**
 * 說法A : 只考量同宮 (這裡用此法)
 * 祿存(或化祿)、天馬又同時與天相星同宮，便叫祿馬配印格。
 *
 * 說法B : (考量三方四正 , 更寬鬆 )
 * 天相、祿存（或化祿）、天馬這三顆星在三方四正相會，且未遇煞星者，是為成格。
 */
val p祿馬配印 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.getBranches(天馬, 天相)
      .takeIf { list -> list.size == 1 } // 天馬 天相 同宮
      ?.let { list -> list[0] }
      ?.takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch ->
        branch == it.starMap[祿存]?.stemBranch?.branch
          || branch == it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch
      }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        祿馬配印(house)
      }
  }
}

/**
 * 命宮在寅或申宮，遇紫微與天府來夾。
 */
val p紫府夾命 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 寅 || it == 申 }    // 命在寅或申
      ?.takeIf { branch -> branch == it.紫府().grip() } // 紫府有夾命
      ?.let { 紫府夾命 }
  }
}


/**
 * 命宮在丑或未宮，太陽與太陰在左右鄰宮相夾。有財運，利於事業發展。
 * 天府或者是武曲、貪狼在丑、未宮駐守命宮，兩邊有太陽、太陰相夾，命宮及三方四正有吉星廟旺會照，又無煞星侵擾，命主既有權又有錢，一生富貴。
 *
 * TODO 命宮坐吉曜，太陽太陰在輔宮夾命宮，謂之日月夾命格，主其人不貴則大富。
 */
val p日月夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.日月()
      .takeIf { 日月 -> it.mainHouse.branch == 日月.grip() }
      ?.let { 日月夾命 }
  }
}

/**
 * (文星暗拱)
 * 命宮在丑或未宮，文昌與文曲在左右鄰宮相夾。
 *
 * 命宮無煞星坐守，而兩旁有文昌、文曲來夾，謂之「昌曲夾命格」，亦稱「文星暗拱格」。
 * 僅如下四種情況才會出現：
 * 1、寅時生人，立命在未。
 * 2、申時生人立命在丑。
 * 3、辰時生人立命在未。
 * 4、戌時生人立命在丑。
 */
val p昌曲夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == 丑 || branch == 未 }  // 命宮在丑或未宮
      ?.takeIf { branch -> it.昌曲().grip() == branch } // 昌曲夾命
      ?.let { 昌曲夾命 }
  }
}

/**
 * 命宮在丑或未宮，左輔與右弼在左右鄰宮相夾。
 *
 * 「左右夾命」看的則是「人生的輔助力」，但是還要再研究「輔助來源」的品質，才能論好壞。
 * 而且，如果因為命盤結構中的某些原因造成「事業容易失敗」的話，即使化祿化權在命宮，左右夾命反而還會是拖垮整個家族經濟的最後一跟稻草。
 */
val p左右夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.輔弼()
      .takeIf { 左右 -> 左右.grip() == it.mainHouse.branch }
      ?.let { 左右夾命 }
  }
}

/**
 * 所謂雙祿夾命格就是在紫微命盤中，命宮被祿存和化祿相夾。經云：“化祿及祿存夾身命，主富貴”。
 *
 * 以雙祿夾命而言, 基本上被祿存及化祿夾輔的宮位必然會有擎羊或陀羅其中一煞,
 * 但經由雙祿夾輔, 得以制煞為用. 甚至連凶格被雙祿夾輔, 都能轉危為安
 */
val p雙祿夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return listOf(
      it.starMap[祿存]?.stemBranch?.branch,
      it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch)
      .grip()
      ?.takeIf { branch ->
        branch == it.mainHouse.branch
      }?.let { 雙祿夾命 }
  }
}

/**
 * 命宮無主星，羊、陀、火、鈴其一顆入命，且居廟旺之地。
 * 此格局的人，性格剛烈，具英雄氣慨，一生運勢起伏不穩，九死一生，驚險百出。若從事武職，危險性職業或是做冒險投機生意，很容易成功。
 */
val p權煞化祿 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.getHouseDataOf(it.mainHouse.branch).stars
      .takeIf { _ -> it.getMainStarsIn(it.mainHouse.branch).isEmpty() } // 命宮無主星
      ?.intersect(listOf(擎羊, 陀羅, 火星, 鈴星))
      ?.firstOrNull()
      ?.takeIf { star ->
        it.starStrengthMap[star]?.let { value -> value <= 2 } ?: false
      }?.let { 權煞化祿 }
  }
}

/**
 * 命宮中有主星與祿存星坐守，三方四正有文昌、文曲星拱照，無煞星沖破。
 *
 * 所謂祿文拱命格，是指命宮中有祿存星坐守，命宮三方四正有文昌、文曲星拱照，無煞星沖破，即此格。
 * 紫微命盤中有此格局者主富貴，能因文而進財，在才藝方面表現卓越。
 * 若無煞湊，必為富有的知名人士，因此有“祿文拱命，富而且貴”之說。
 *
 * 不過，此格局必須命宮中有主星坐守，假如再有魁鉞、輔弼加臨，則格局會更高。
 * 最忌有煞忌星沖照，視為破格，如命宮無主星，則不成格。
 */
val p祿文拱命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.starMap[祿存]?.stemBranch?.branch
      ?.takeIf { branch -> branch == it.mainHouse.branch } // 祿存坐命
      ?.takeIf { _ -> it.getMainStarsIn(it.mainHouse.branch).isNotEmpty() } // 命宮有主星
      ?.takeIf { _ -> it.三方四正有昌曲() }
      ?.let { 祿文拱命 }
  }
}

/**
 * 化祿或祿存其一坐命，另一個位於命宮六合之宮位。命中常有意外之財，主錦上添花。
 * 六合包括：子丑，寅亥，卯戌，辰酉，巳申，午未。
 */
val p明祿暗祿 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 祿存branch = it.starMap[祿存]?.stemBranch?.branch
    val 化祿branch = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch

    val 祿存及化祿 = setOf(祿存branch, 化祿branch)

    return 祿存及化祿
      .takeIf { it.size == 2 }
      ?.firstOrNull { b -> it.mainHouse.branch == b } // 其中一顆在命宮
      ?.takeIf { b1: Branch ->
        // 取出另一顆
        val b2: Branch = 祿存及化祿.minus(b1).first()!!
        // 兩星六合
        b1.combined == b2
      }?.let { 明祿暗祿 }
  }
}

/**
 * 天機與太陰同時座於命宮.此格局只會出現在寅宮or申宮.
 */
val p水木清華 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return if (setOf(it.mainHouse.branch).containsAll(it.getBranches(天機, 太陰)))
      水木清華
    else
      null
  }
}

/**
 * [p極向離明] 是 「金輿」 , 這是「金鑾」
 *
 * 紫微守命宮，左輔右弼同宮得日月合臨，乃極貴之命。
 * 五星同宮！？
 *
 * 類似 [p左右同宮] , 但更增加 [紫微]  的條件
 * 日月合臨 ? 真有此盤！？ 暫且先不做此條件
 */
val p金鑾扶駕 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.starMap[紫微]?.stemBranch?.branch
      ?.takeIf { b -> branches.contains(b) }
      ?.takeIf { b -> setOf(b).containsAll(it.輔弼()) }
      ?.takeIf { b -> it.三方四正(b).containsAll(it.日月()) } // 不太可能五星同宮, 因此，對於日月，放寬到「三方四正」
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        金鑾扶駕(house)
      }
  }
}

/**
 * 命坐巳或亥宮，福德宮同見文昌文曲。
 * 福德有昌曲的人喜歡閱讀，重精神享受，喜思考，善理解。
 */
val p玉袖添香 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 巳 || it == 亥 }
      .takeIf { _ -> it.getHouseDataOf(福德)?.stars?.containsAll(listOf(文昌, 文曲)) ?: false }
      ?.let { 玉袖添香 }
  }
}

/**
 * 「七殺、破軍、貪狼」這三顆星辰的相對位置是固定的，只要其中一顆在「命宮」，
 * 其他兩個一定會分別在「財帛宮」與「官祿宮」。簡單說，這三顆星是「一組」。
 */
val p殺破狼格 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch.trinities
      .takeIf { set -> set == it.getBranches(七殺, 破軍, 貪狼).toSet() }
      ?.let { 殺破狼格 }
  }
}

/**
 * 太陰或天同在命宮入廟，顯得享福而缺乏開創力，若能化忌而不會其它煞星、則有激發作用，雖會有一時失敗，終有偉大成就。
 */
val p廟星變景 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return setOf(太陰, 天同)
      .firstOrNull { star -> it.starMap[star]?.stemBranch?.branch == it.mainHouse.branch } // 有其中一顆星在命宮
      ?.takeIf { star -> it.starStrengthMap[star] == 1 } // 入廟
      ?.takeIf { star -> 忌 == it.getTransFourValue(star) }
      ?.let { 廟星變景 }
  }
}

/**
 * 化忌在四墓之地(辰戌丑未)入命。一生辛勞開創，中年後能立大事業。
 */
val p辛勞開創 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> setOf(辰, 戌, 丑, 未).contains(branch) } // 命宮在 辰, 戌, 丑, 未 之一
      ?.takeIf { branch -> branch == it.getTransFourHouseOf(忌).stemBranch.branch }  // 命宮有化忌星
      ?.let { 辛勞開創 }
  }
}

/**
 * 天同、天梁、祿星(或化祿)坐命寅或申宮，武曲天相左右鄰宮夾命，主富貴雙全，但偏向貴格。
 */
val p財印天祿 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == 寅 || branch == 申 }
      ?.takeIf { branch -> it.neighbors(branch) == it.getBranches(武曲, 天相).toSet() }  // 武曲天相左右鄰宮夾命
      ?.takeIf { branch ->

        val 同梁祿星: Set<ZStar> = setOf(天同, 天梁, 祿存)
        val 天同天梁祿存在命宮: Boolean = it.getHouseDataOf(branch).stars.containsAll(同梁祿星)

        val 天同天梁化祿地支: List<Branch> = it.getBranches(天同, 天梁).plus(it.getTransFourHouseOf(
          ITransFour.Value.祿).stemBranch.branch)
        val 天同天梁化祿在命宮: Boolean = setOf(branch).containsAll(天同天梁化祿地支)

        天同天梁祿存在命宮 || 天同天梁化祿在命宮
      }
      ?.let { 財印天祿 }
  }
}

/**
 * 太陰、文曲入廟旺同坐夫妻宫。男招貴妻，有才華；女生貴子，得富貴。
 *
 * 廟旺太陰、文曲星同坐夫妻宮，再逢遇吉星，為「蟾宮折桂格」，男招貴妻，有才華，會做人，內外兼顧；女生貴子，丈夫文質彬彬，得富貴。
 */
val p蟾宮折桂 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return listOf(太陰, 文曲)
      .takeIf { 太陰文曲 -> it.getHouseDataOf(夫妻)?.stars?.containsAll(太陰文曲) ?: false }  // 共入夫妻宮
      ?.takeIf { 太陰文曲 -> 太陰文曲.all { star -> it.starStrengthMap[star]?.let { value -> value <= 2 } ?: false } }  // 共同旺廟
      ?.let { 蟾宮折桂 }
  }
}

// =========================== 以下 , 惡格 ===========================

/**
 * 擎羊坐命午宮，為本格。
 *
 * 因為馬頭帶劍最強 當遇到凶星會集 同樣的它的凶性是最惡的 所以全書註解提到了午凶卯次之子酉又次之
 *
 *  另外又有兩個格局，亦稱為「馬頭帶劍」：
 * 一是貪狼與擎羊在午宮守命。特別是戊年生人，貪狼化祿，更吉。即或不然，為丙年生人，天同於未宮化祿，與巳宮的祿存夾命，亦為美格。
 * 一是天同與擎羊在午宮守命，亦以丙戊年生人為合格。
 *
 * 擎羊為古人視為剛強好勇之星，於午宮雖然落陷，但午宮的火可以制擎羊之金，反成器用，於是橫暴之力任為威權，因此有「威鎮邊疆」之說。
 *
 */
val p馬頭帶劍 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == 午 } // 命在午
      ?.takeIf { _ -> it.starMap[擎羊]?.stemBranch?.branch == 午 } // 擎羊在午
      ?.let { _ ->
        val evils: Set<EvilCombo> = mutableListOf<EvilCombo>().apply {
          if (it.三方四正().contains(it.starMap[陀羅]?.stemBranch?.branch))
            add(EvilCombo.羊陀)
          if (it.三方四正().containsAll(it.火鈴()))
            add(EvilCombo.火鈴)
          if (it.三方四正().containsAll(it.空劫()))
            add(EvilCombo.空劫)
        }.toSet()
        馬頭帶劍(evils)
      }
  }
}

/**
 * 紫微、貪狼同在卯或酉坐命。
 *
 * 紫微貪狼在卯酉坐命，又遇煞星。紫貪卯酉，并非個個都是貧賤之命或僧人羽士。
 * 若遇紫微化權、化科，貪狼化祿、化權，或祿存在命宮，或加會火星、鈴星、左輔、右弼、文昌、文曲等以上情形，均不能以貧賤定之，反主富貴有成（女命不宜見昌曲）。
 *
 * 若無上述任何一個星曜會合，而命宮三方見擎羊、地劫、天空、旬空、截空、化忌、天哭、天虛、孤辰、寡宿等星宿，必一生無成，貧窮孤單，名利俱無，宜出家為僧道，空亡若是在命宮，其人與宗教之緣份愈強。
 * 古詩云：“極居卯酉遇劫空，十人之命九為僧”。又云：“借問此身何處去，衲衣削發居空門”。
 */
val p極居卯酉 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.mainHouse.branch
      .takeIf { it == 卯 || it == 酉 }
      ?.takeIf { branch -> setOf(branch).containsAll(it.getBranches(紫微, 貪狼)) }  // 紫微、貪狼同在卯或酉
      ?.let { branch ->
        val evils: Set<EvilCombo> = mutableListOf<EvilCombo>().apply {
          if (it.三方四正(branch).containsAll(it.羊陀()))
            add(EvilCombo.羊陀)
          if (it.三方四正(branch).containsAll(it.火鈴()))
            add(EvilCombo.火鈴)
          if (it.三方四正(branch).containsAll(it.空劫()))
            add(EvilCombo.空劫)
        }.toSet()

        極居卯酉(evils)
      }
  }
}

/**
 * 命宮裡無任何十四顆主星坐命。
 */
val p命無正曜 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.takeIf { it.getMainStarsIn(it.mainHouse.branch).isEmpty() }
      ?.let { 命無正曜 }
  }
}

/**
 * 在寅宮，貪狼坐命，遇陀羅同宮。
 */
val p風流綵杖 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.takeIf { it.mainHouse.branch == 寅 }
      ?.takeIf { it.starMap[貪狼]?.stemBranch?.branch == 寅 }
      ?.takeIf { it.starMap[陀羅]?.stemBranch?.branch == 寅 }
      ?.let { 風流綵杖 }
  }
}

/**
 * 巨門、天機同在酉宮坐命，有化忌同宮。性質為奔波飄蕩。不利於感情、 事業。
 */
val p巨機化酉 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { _ -> it.化忌入命宮() }
      ?.takeIf { _ -> it.mainHouse.branch == 酉 }
      ?.takeIf { _ -> it.starMap[巨門]?.stemBranch?.branch == 酉 }
      ?.takeIf { _ -> it.starMap[天機]?.stemBranch?.branch == 酉 }
      ?.let { 巨機化酉 }
  }
}

/**
 * 太陽在戌宮坐命，此時太陰在辰宮；
 * 或太陰在辰宮坐命，太陽在戌宮。
 * 取其日在戌時，月在辰時， 兩星光芒皆弱不旺。勞碌命，求人不如勞己。無閒享清福。
 */
val p日月反背 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 太陽在戌宮坐命 = (it.mainHouse.branch == 戌 && it.starMap[太陽]?.stemBranch?.branch == 戌)
    val 太陰在辰宮坐命 = (it.mainHouse.branch == 辰 && it.starMap[太陰]?.stemBranch?.branch == 辰)
    return if (太陽在戌宮坐命 || 太陰在辰宮坐命)
      日月反背
    else
      null
  }
}

/**
 * 太陽、太陰同入疾厄宮，有身體障礙。但從事醫生、星相命卜之業，反主吉。
 */
val p日月疾厄 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.getHouseDataOf(疾厄)?.stars
      ?.takeIf { stars -> stars.containsAll(listOf(太陽, 太陰)) }
      ?.let { 日月疾厄 }
  }
}

/**
 * 說法A :
 * 天梁在巳亥寅申宮坐命，與天馬同宮。
 * 天馬只會出現於四馬地(巳亥寅申。 此格表示勞而無獲之象。若顯現在感情生活上，對婚姻生活帶來不利影響。
 *
 * 說法B : (又分 天馬 是否要與 命宮 同宮 的區別)
 * 天梁在巳亥宮坐命，與四煞空劫忌星同宮加會，不見吉星，命和遷移有天馬，即為此格。
 * 天梁在四馬地守命，若無命馬同宮，僅只寅宮無飄蕩性質；若逢命馬，皆主風流、飄蕩。
 *
 * 為社會底層人物，一生飄蕩，身在異鄉，并常為他人之事而奔波，無事閑忙，名利皆虛，貧賤之命。
 */
val p梁馬飄蕩 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 說法A_天梁在寅巳申亥坐命與天馬同宮 = it.mainHouse.branch.let { branch ->
      if (listOf(寅, 巳, 申, 亥).contains(branch))
        branch
      else
        null
    }?.let { branch ->
      it.starMap[天梁]?.stemBranch?.branch == branch
        && it.starMap[天馬]?.stemBranch?.branch == branch
    } ?: false

    val 說法B_天梁在四馬守命_命宮有天馬 = it.mainHouse.branch.let { branch ->
      if (listOf(寅, 巳, 申, 亥).contains(branch))
        branch
      else
        null
    }?.let { branch ->
      it.starMap[天梁]?.stemBranch?.branch == branch
        && it.starMap[天馬]?.stemBranch?.branch == branch
    } ?: false

    return if (說法B_天梁在四馬守命_命宮有天馬) {
      梁馬飄蕩
    } else
      null
  }
}

/**
 * 廉貞、七殺同在丑或未宮守命。
 * 此格人應注意法律方面的問題。
 */
val p貞殺同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 丑 || it == 未 }
      ?.takeIf { branch -> setOf(branch).containsAll(it.getBranches(廉貞, 七殺)) } // 貞殺同宮
      ?.let { 貞殺同宮 }
  }
}

/**
 * 「殺拱廉貞」──即廉貞守命，七殺合照 。古歌云：「貞逢七殺實堪傷，十載淹留有災殃，運至經求多不遂，錢財勝似雪澆湯。」
 *
 * 這個格局非常之有名，但照王亭之所知，其格局結構僅有兩種，即
 *
 * 廉貞貪狼于巳宮守命。武曲七殺于酉宮來會；
 * 或
 * 廉貞貪狼于亥宮守命，武曲七殺于卯宮來會。亦即是廉貞、貪狼、武曲、七殺四顆星曜的星系組合。
 *
 * 另一說 :
 * ①廉貞、七殺二星分守身宮和命宮；
 * ②廉貞七殺在丑未宮守命； ==> 此即 [貞殺同宮]
 * ③七殺在卯酉守命，三合有廉貞。
 * 以上諸等，命宮三方四正無吉星加會，反而加會羊陀火鈴天刑化忌劫空等諸多惡曜，便爲殺拱廉貞之格。
 */
val p殺拱廉貞 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 廉貞貪狼在巳宮守命: Boolean = it.mainHouse.branch.let { branch ->
      branch == 巳
        && setOf(branch).containsAll(it.getBranches(廉貞, 貪狼))
    }

    val 武曲七殺于酉宮來會: Boolean = setOf(酉).containsAll(it.getBranches(武曲, 七殺))

    val 廉貞貪狼在亥宮守命: Boolean = it.mainHouse.branch.let { branch ->
      branch == 亥
        && setOf(branch).containsAll(it.getBranches(廉貞, 貪狼))
    }

    val 武曲七殺于卯宮來會: Boolean = setOf(卯).containsAll(it.getBranches(武曲, 七殺))

    return if (
      (廉貞貪狼在巳宮守命 && 武曲七殺于酉宮來會) || (廉貞貪狼在亥宮守命 && 武曲七殺于卯宮來會)
    )
      殺拱廉貞
    else
      null
  }
}


/**
 * 巨門守命，且在三方四正中，與羊陀火鈴四煞同時有會照或同宮關係。
 * 此格局應防意外之災或為不得已苦衷流落四方。
 */
val p巨逢四煞 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.starMap[巨門]?.stemBranch?.branch
      ?.takeIf { branches.contains(it) }
      ?.takeIf { 巨門地支 -> it.三方四正(巨門地支).containsAll(it.getBranches(擎羊, 陀羅, 火星, 鈴星)) } // 三方四正包含全部四凶星
      ?.let { 巨門地支 ->
        val house = it.getHouseDataOf(巨門地支).house
        巨逢四煞(house)
      }
  }
}

/**
 * 地劫、地空二星或其中之一星守命。
 * 有精神上孤獨，錢不易留住之跡象。
 */
val p命裡逢空 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.空劫()
      .takeIf { 空劫 -> 空劫.contains(it.mainHouse.branch) }
      ?.let { 命裡逢空 }
  }
}

/**
 * 文昌或文曲守命，遇空劫 或火鈴或羊陀對星來夾。有懷才不遇跡象。
 */
val p文星遇夾 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> it.昌曲().contains(branch) } // 命宮有文星
      ?.let { _ ->
        mutableSetOf<EvilCombo>().apply {
          p空劫夾命.getSingle(it, pContext)?.also { add(EvilCombo.空劫) }
          p火鈴夾命.getSingle(it, pContext)?.also { add(EvilCombo.火鈴) }
          p羊陀夾命.getSingle(it, pContext)?.also { add(EvilCombo.羊陀) }
        }.toSet()
      }
      ?.takeIf { evils -> evils.isNotEmpty() }
      ?.let { evils -> 文星遇夾(evils) }
  }
}

/**
 * 天馬遇地劫、地空同宮或三方沖照。奔波，空忙一場。
 *
 * 命馬遇空亡、截路、天空、四煞、化忌同宮或三方沖照，主奔波 ，白忙白勞。賦云：「馬遇空亡，終身奔走。」又云： 「天馬逢空亡死絕，災病悔吝。」
 * 命宮天馬，與劫空同宮，對宮有祿存，或祿坐空亡，又逢空劫煞星。生逢此格，一生總是變來變去，東奔西走，辛苦勞碌一場空，爲空虛之兆，貧賤之命。經云：“馬遇空亡，終身奔走”。
 *
 * 空亡用日空
 */
val p馬落空亡 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> branch == it.starMap[天馬]?.stemBranch?.branch } // 命宮天馬
      ?.takeIf { _ -> it.三方四正().intersect(it.空劫()).isNotEmpty() } // 三方四正有地空或地劫
      ?.let { 馬落空亡 }
  }
}


/**
 * 祿存、化祿同時坐命，遇地劫、地空同宮。
 * 此即 [p祿合鴛鴦] 格
 *
 * 祿存、化祿同時坐命，本為 雙祿交流格。但若遇地空、地劫同宮，此時雙祿為被衝破情形，稱為兩重華蓋。
 * 華蓋表示有宗教緣分。皈依宗教，反可享主清福。但因雙祿被衝破，較不易累積錢財。
 */
val p兩重華蓋 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return p祿合鴛鴦.getSingle(it, pContext)
      ?.takeIf { _ -> setOf(it.mainHouse.branch).containsAll(it.空劫()) } // 空劫入命
      ?.let { 兩重華蓋 }
  }
}


/**
 * 祿存或化祿坐命，在三方四正中，有被地劫、地空衝破。
 * 吉處藏凶之象，應居安思危。
 */
val p祿逢衝破 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch ->
        val 化祿入命宮 = branch == it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch
        val 祿存坐命 = branch == it.starMap[祿存]?.stemBranch?.branch
        化祿入命宮 || 祿存坐命
      }
      ?.takeIf { _ -> it.三方四正().containsAll(it.空劫()) }
      ?.let { 祿逢衝破 }
  }
}

/**
 * 貪狼坐命在子宮。
 * 廉貞、貪狼坐命於亥宮，遇陀羅同宮。
 *
 * 無論男女，多風流，感情債不斷。
 *
 * 泛水桃花的極致是貪狼居子宮遇擎羊同宮
 */
val p泛水桃花 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 貪狼坐命在子宮 = it.mainHouse.branch == 子
      && it.starMap[貪狼]?.stemBranch?.branch == 子

    val 貪狼坐命在亥宮 = it.mainHouse.branch == 亥
      && it.starMap[貪狼]?.stemBranch?.branch == 亥
    val 廉貞在亥 = it.starMap[廉貞]?.stemBranch?.branch == 亥
    val 陀羅在亥 = it.starMap[陀羅]?.stemBranch?.branch == 亥

    return if (
      貪狼坐命在子宮 || (貪狼坐命在亥宮 && 廉貞在亥 && 陀羅在亥)
    )
      泛水桃花
    else
      null
  }
}

/**
 * 天梁在巳、亥、申爲陷地，
 * 太陰在卯、辰、巳、午、未爲陷地。
 * ①天梁在陷地守命與太陰加會；
 * ②太陰在陷地守命與天梁加會。
 * 以上二者，命宮及三方無祿存、科權祿、左右、昌曲、魁鉞等吉星同宮和加會，
 * 反而會有羊陀火鈴劫空刑姚化忌大耗等凶星，爲天梁拱月格。
 *
 * 人命逢此，窮困而事業無成，不聚財，飄流在外，不務正業，成事不足，敗事有餘。男命浪蕩，好酒色嫖賭，女命多淫，私通內亂
 *
 * 王亭之的意見。所謂「天梁拱月」，乃是天梁居巳亥，太陰居丑未，或大陰居寅申，天梁居子午的格局
 */
val p天梁拱月 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> listOf(巳, 亥, 申).contains(branch) }
      ?.takeIf { branch -> it.starMap[天梁]?.stemBranch?.branch == branch } // 天梁陷地守命
      ?.takeIf { _ -> listOf(卯, 辰, 巳, 午).contains(it.getBranches(太陰).first()) }  // 太陰陷地
      ?.let { 天梁拱月 }
  }
}


/**
 * 武曲(金)、廉貞(火)分別守身宮與命宮，金火相剋！
 * 如周武王和奸臣費仲，仇人相見，分外眼紅，必兇禍百出，終身不得安寧。
 *
 * 武曲為財星屬金，廉貞為囚星屬火。二星一守命宮，一守身宮，乃火金相克，如仇人相見，分外眼紅，必兇禍百出，終身不得安寧，
 * 二星有一化忌加煞，定遭暴病、險厄。
 * TODO 若是命宮三方四正臨廟旺，加會星并得吉化，則不作此論。
 */
val p財與囚仇 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return setOf(it.mainHouse.branch, it.bodyHouse.branch)
      .takeIf { set -> set == it.getBranches(武曲, 廉貞).toSet() }
      ?.let { 財與囚仇 }
  }
}


/**
 * 廉貞(火)在亥逢化忌(水)，是火入泉鄉，主大凶。
 */
val p火入泉鄉 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it
      .takeIf { it.starMap[廉貞]?.stemBranch?.branch == 亥 }
      ?.takeIf { it.getTransFourValue(廉貞) == 忌 }
      ?.let { 火入泉鄉 }
  }
}


/**
 * 祿逢兩煞
 *
 * 祿存守命宮，或化祿守命宮，
 * 但命宮亦有旬空、截空、天空、地劫，
 * 同是三方會有羊陀火鈴大耗諸惡，即為祿逢兩煞之格。
 *
 * 主其人虛有其表，好看而已，終究不能有所作為，縱有一時之財利也很快陷于困窮。
 */
val p祿逢兩煞 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    val 祿存守命宮: Boolean = it.getHouseDataOf(it.mainHouse.branch).stars.contains(祿存)
    val 化祿守命宮: Boolean = it.getTransFourHouseOf(ITransFour.Value.祿).stemBranch.branch == it.mainHouse.branch

    val 命宮有空劫: Boolean = it.空劫().contains(it.mainHouse.branch)
    val 三方四正有惡煞 = it.三方四正().intersect(it.羊陀().plus(it.火鈴())).isNotEmpty()

    return if ((祿存守命宮 || 化祿守命宮) && 命宮有空劫 && 三方四正有惡煞)
      祿逢兩煞
    else
      null
  }
}

/**
 * 六吉星盡落入閒宮，而三方四正盡是貪狼、天刑、四煞、空劫忌諸惡曜交會。
 *
 * 而在紫微斗數中「君子」即指吉星，「在野」指眾星入廟可惜盡落入閒宮
 * （男命吉星落入父母、兄弟、疾厄、奴僕、夫妻、子女），
 * 而命宮、財帛宮、官祿宮、遷移宮盡是貪狼、天刑、羊陀火鈴四煞、地劫地空諸惡曜交會，且星辰居陷地，即爲君子在野，奸臣當朝之兆。
 * 人命得此，乃貧賤夭折之命無疑。在現代生活中，則代表生活艱辛，有志難伸，充滿挑戰與磨難。一生較無貴人相輔相成，只靠自己一步一腳印，奮發圖強。
 */
val p君子在野 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 閒宮: List<Branch> = listOf(父母, 兄弟, 疾厄, 交友, 夫妻, 子女)
      .mapNotNull { h -> it.getHouseDataOf(h)?.stemBranch?.branch }

    return 閒宮.containsAll(it.getBranches(*StarLucky.values)).takeIf { value -> value }?.let { _ ->
      val evils = mutableSetOf<EvilCombo>().apply {
        if (it.三方四正().containsAll(it.羊陀()))
          add(EvilCombo.空劫)
        if (it.三方四正().containsAll(it.火鈴()))
          add(EvilCombo.火鈴)
        if (it.三方四正().containsAll(it.羊陀()))
          add(EvilCombo.羊陀)
      }.toSet()
      君子在野(evils)
    }
  }
}

/**
 * 化忌坐命，擎羊、陀羅於兩鄰宮相夾。
 *
 * 祿存在命宮，則必為羊陀所夾。若有化忌星同宮，羊陀凶性得以充分發揮。雖有祿存守命，亦不為美。
 */
val p羊陀夾忌 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    val 化忌宮位 = it.getTransFourHouseOf(忌)
    return 化忌宮位
      .takeIf { branches.contains(it.stemBranch.branch) }
      ?.takeIf { houseData -> it.羊陀().containsAll(it.neighbors(houseData.stemBranch.branch)) }
      ?.let {
        羊陀夾忌(化忌宮位.house)
      }
  }
}

/**
 * 破軍或七殺入命宮，火星、鈴星在左右鄰宮相夾。
 */
val p火鈴夾忌 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.火鈴().grip() // 火鈴夾
      ?.takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch -> it.getBranches(破軍, 七殺).contains(branch) }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        火鈴夾忌(house)
      }
  }
}

/**
 * 擎羊、陀羅於左右鄰宮夾命。
 * 特点：羊陀必然夹禄存。如果羊陀夹命，那么禄存一定在命宫坐守；所以该格重点看禄存星。
 * 注意：该格与羊陀夹忌格只一字之差。本格无忌冲破禄存，所以相对好一些。但同样是凶格。
 */
val p羊陀夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.羊陀()
      .takeIf { 羊陀 -> it.mainHouse.branch == 羊陀.grip() }
      ?.let { 羊陀夾命 }
  }
}


/**
 * 火星、鈴星在左右鄰宮相夾命宮，即為此格。
 *
 * TODO : 若為火鈴夾貪格情況，就不 為火鈴夾命格。
 * 若是貪狼守命宮，得火鈴夾命反為大吉之格，要是貪狼化祿的話，則是大富大貴之命。
 */
val p火鈴夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.火鈴()
      .takeIf { 火鈴 -> it.mainHouse.branch == 火鈴.grip() }
      ?.let { 火鈴夾命 }
  }
}


/**
 * 地劫、地空二星在左右鄰宮夾命。
 * 有精神上孤獨，錢不易留住之跡象。
 *
 * 地劫天空二星在鄰宮夾命。此格唯有安命子宮和巳宮，遇劫空來夾。如同父母兄弟無助，其性質與劫空同在命宮相似。
 * 若是命宮無正曜，或星辰落陷，遇劫空夾，主惡兆。
 */
val p空劫夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.空劫()
      .takeIf { 空劫 -> it.mainHouse.branch == 空劫.grip() }
      ?.let { 空劫夾命 }
  }
}


/**
 * (刑囚會印)
 * 刑囚夾印，刑仗唯司
 *
 * 說法A :
 * 廉貞、天相在子或午宮坐命有擎羊同宮。
 * 天相為印，廉貞為囚，擎羊 化氣為刑。應注意法律訴訟問題。
 * 廉貞、天相、擎羊 其實無法「夾」，故，「刑囚會印」比較貼切
 *
 * 以丙年生人廉貞在午化忌最兇
 *
 * 說法B :
 * 廉貞天相與天刑同宮 (並未指定命宮)
 *
 * 「刑囚夾印」在紫微斗數裡是一種破敗格局，主是非官訟，其中
 *    「刑」代表「羊刃」，乃六煞星之首；
 *    「囚」指的是廉貞星，廉貞星乃次桃花星；
 *    「印」指的是天相星，是一顆宰相星，代表參謀作業，化氣為蔭。
 */
val p刑囚夾印 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    val 說法A_廉貞天相擎羊入命宮於子或午: House? = it.mainHouse.branch
      .takeIf { it == 子 || it == 午 }
      ?.takeIf { branch -> setOf(branch).containsAll(it.getBranches(廉貞, 天相, 擎羊)) }
      ?.let { 命宮 }

    val 說法B_廉貞天相擎羊同宮 = it.getBranches(廉貞, 天相, 擎羊)
      .takeIf { branches -> branches.size == 1 }  // 同宮
      ?.let { list -> list[0] }
      ?.let { branch -> it.getHouseDataOf(branch).house }

    return when {
      說法A_廉貞天相擎羊入命宮於子或午 != null -> 刑囚夾印(命宮)
      說法B_廉貞天相擎羊同宮 != null -> 刑囚夾印(說法B_廉貞天相擎羊同宮)
      else -> null
    }
  }
}

/**
 * 天相受化忌和天梁於左右鄰宮相夾 (不是正宗) ；
 * 或天相受化忌和擎羊於左右鄰宮相夾。
 */
val p刑忌夾印 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {

    return it.starMap[天相]?.stemBranch?.branch?.let { 天相branch ->
      val neighbors = it.neighbors(天相branch)

      val 化忌的鄰宮: Branch? =
        neighbors.firstOrNull { 鄰宮 ->
          it.getTransFourHouseOf(忌).stemBranch.branch == 鄰宮
        }

      val 另宮: Branch? = 化忌的鄰宮?.let { branch -> neighbors.minus(branch).first() }

      另宮?.let { 另宮branch ->
        val 另宮有天梁: Boolean = it.starMap[天梁]?.stemBranch?.branch == 另宮branch
        val 另宮有擎羊: Boolean = it.starMap[擎羊]?.stemBranch?.branch == 另宮branch
        if (另宮有天梁 || 另宮有擎羊) {
          val house = it.getHouseDataOf(天相branch).house
          刑忌夾印(house)
        } else
          null
      }
    }
  }
}


/**
 * 祿存天馬與地空或地劫同宮。似有展望之機，卻又乏力邁步。
 */
val p祿衰馬困 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.getBranches(祿存, 天馬)
      .takeIf { it.size == 1 } // 祿存天馬 同宮
      ?.let { list -> list[0] }
      ?.takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch -> it.getBranches(地空, 地劫).contains(branch) }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        祿衰馬困(house)
      }
  }
}

/**
 * 命坐文星，卻逢四煞 (鈴星、火星、擎羊、陀羅) 、忌星、空劫。苟有文才亦不見用，空懷大志功名不成。
 */
val p名不利達 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.getHouseDataOf(it.mainHouse.branch).stars.intersect(setOf(文昌, 文曲))
      .takeIf { stars -> stars.isNotEmpty() }  // 命宮 有 文昌 或 文曲
      ?.takeIf { _ ->
        val 命宮有四煞 = it.火鈴().plus(it.羊陀()).contains(it.mainHouse.branch)
        val 命宮有空劫 = it.空劫().contains(it.mainHouse.branch)
        val 命宮有化忌 = it.化忌入命宮()
        命宮有四煞 && 命宮有空劫 && 命宮有化忌
      }?.let { 名不利達 }
  }
}

/**
 * (三方煞聚)
 * 命宮的三方四正，皆有煞星(四煞空劫忌、殺破狼廉)，做事每每碰壁。
 * 若加會吉星如魁鉞、輔弼、昌曲，雖可解救，但一生中仍難免辛勞。
 *
 * 另有一種： (額外考慮 [忌] )
 * 三方忌煞格：某宫的三方四正宫均有煞星并化忌星时，即是。与三方煞聚凶格的区别是：煞聚格无化忌星，忌煞格有忌星。
 */
val p三方並凶 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.三方四正().takeIf { branches ->
      val 有六煞: Boolean = it.getBranches(*StarUnlucky.values).intersect(branches).isNotEmpty()
      val 有化忌 = branches.contains(it.getTransFourHouseOf(忌).stemBranch.branch)
      有六煞 && 有化忌
    }
      ?.let { 三方並凶 }
  }
}

/**
 * 化忌、化權、化科同時會入某宮，該宮便形成「三奇沖剋 」，三奇沖剋 ，一片凄慘，必有災難之事。
 */
val p三奇沖剋 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.getTransFourHouseOf(忌).stemBranch.branch
      .takeIf { branches.contains(it) }
      ?.takeIf { branch -> branch == it.getTransFourHouseOf(ITransFour.Value.權).stemBranch.branch }
      ?.takeIf { branch -> branch == it.getTransFourHouseOf(ITransFour.Value.科).stemBranch.branch }
      ?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        三奇沖剋(house)
      }
  }
}

/**
 * 天機星在巳、亥宮獨坐，對宮為太陰星。
 *
 * 天機星在巳、亥宮獨坐，對宮為太陰星，財帛同巨在丑未，官祿在卯酉空宮借陽梁。
 *
 * 天機星在巳、亥宮都是獨坐，對宮為太陰星。巳、亥二宮的天機星皆為平宮，力量不強，因此很容易受到對宮太陰的影響。
 * 尤其是天機星在巳宮時，對宮的太陰星為入廟最強的宮位，而且是“月朗天門格”。
 * 在紫微命盤中，有月朗天門格的人聰明異常、喜歡研究學問、文筆又好、為人謙恭有忍讓之心、人緣佳，可因學術而成名，甚宜學者、專家。
 * 而天機星在亥宮時，由於此時對宮的太陰為落陷，故無法受到太陰好的影響，因此就不如巳宮吉祥。
 *
 * 但天機星在巳宮時，由於受到對宮亥宮太陰星入廟的影響，男命大多屬於溫柔多情，并且能娶到一位端正賢淑的好妻子。
 * 但一生中將時有艷遇，應注意把持住使不逾己，才不致毀掉辛苦建立的美滿家庭。
 */
val p天機巳亥 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.starMap[天機]?.stemBranch?.branch
      ?.takeIf { branch -> branch == 巳 || branch == 亥 }
      ?.takeIf { branch -> it.starMap[太陰]?.stemBranch?.branch == branch.opposite } // 太陰 在 對宮
      ?.let { 天機巳亥 }
  }
}

/**
 * 太陰、天同在午宮坐命，本屬落陷，
 * 又有火星、鈴星、地劫、天空、天刑等惡星同宮，三方又加會兇星，為此格。
 *
 * 此格之人生性怯弱，身體多病，或一生貧賤愁苦，再遇四煞沖會，多主心狠手毒，無情無義。女命受感情打擊或作人情婦，難成為正宮。
 */
val p月同遇煞 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.getHouseDataOf(it.mainHouse.branch).stars
      .takeIf { stars -> stars.containsAll(listOf(太陰, 天同)) }   // 太陰 , 天同 都在命宮
      ?.takeIf { stars ->
        listOf(太陰, 天同).map { star ->
          it.getTransFourValue(star)
        }.contains(忌)     // 太陰 , 天同 其中一個化忌
      }
      ?.takeIf { stars ->
        stars.intersect(StarUnlucky.values.toSet()).isNotEmpty()   // 惡星同宮 (暫不考量拱、對宮）
      }
      ?.let { 月同遇煞 }
  }
}

/**
 * 文曲星獨坐戌宮，再三合會照太陽、巨門二星。
 * (應該要坐命宮)
 * 此格之人命纏文曲主桃花主糾纏不清，桃花不停困擾不斷，容易在感情上迷失自我而導致失敗。
 */
val p桃花滾浪 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.starMap[文曲]?.stemBranch?.branch
      ?.takeIf { branch -> branch == it.mainHouse.branch }  // 文曲 在命宮
      ?.takeIf { branch -> branch == 戌 }  // 文曲 在戌
      ?.takeIf { _ -> it.getMainStarsIn(戌).minus(文曲).isEmpty() } // 戌宮 沒有其他主星
      ?.takeIf { branch -> it.拱(branch).containsAll(it.getBranches(太陽, 巨門)) }  // 拱的宮位，有 太陽 and 巨門
      ?.let { 桃花滾浪 }
  }
}

/**
 * 天梁星在巳、亥二宫落陷守命。見羊陀多感情痛苦，煞曜同度一生浮動漂泊，如加吉星則穩定度大大增加。會化祿，人生經歷重大挫折後興家。
 *
 * 當天梁坐命巳或亥宮時，天同星必在對宮照命，所以這個格局的主角為天梁星，所以請勿將天同星坐命巳、亥宮之人歸為同類，為避免誤解
 * 古人說「梁同巳亥，男浪盪女多淫」
 *
 * 天梁在巳亥，已帶「浮蕩」的性質，若再遇天馬， 則一生漂泊，到處流連，
 * 古云：「天梁弱陷遇天馬，主飄蕩」其人喜浪跡天涯，如旅遊或到處換工作，無法久居一處。
 * 所以適合變動性大的工作，可減少漂泊性質，如導遊、空勤人員或是遠洋船員。
 */
val p梁同巳亥 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { it == 巳 || it == 亥 }
      ?.takeIf { branch -> branch == it.starMap[天梁]?.stemBranch?.branch } // 天梁 在 巳 or 亥
      ?.let { _ -> 梁同巳亥 }
  }
}

/**
 * 化科星在命宫，三方遇羊陀火鈴空劫。
 * (四正?)
 */
val p科星逢破 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    return it.getTransFourHouseOf(ITransFour.Value.科).stemBranch.branch
      .takeIf { branch -> branches.contains(branch) }
      ?.takeIf { branch ->
        val 三方四正有擎羊或陀羅 = it.三方四正(branch).intersect(it.getBranches(擎羊, 陀羅)).isNotEmpty()
        val 三方四正有火星或鈴星 = it.三方四正(branch).intersect(it.getBranches(火星, 鈴星)).isNotEmpty()
        val 三方四正有地空或地劫 = it.三方四正(branch).intersect(it.getBranches(地空, 地劫)).isNotEmpty()
        三方四正有擎羊或陀羅 && 三方四正有火星或鈴星 && 三方四正有地空或地劫
      }?.let { branch ->
        val house = it.getHouseDataOf(branch).house
        科星逢破(house)
      }
  }
}

/**
 * 廉貞化忌，擎羊同度，更見鈴星，主意外災難。
 *
 * 廉貞與擎羊同度，如再見鈴星主凶厄之災，這是古書所記載，
 * 但如果同宮內再有天刑星時，其之為禍才大（其名為：刑囚會鈴），而其是克應在廉貞化忌之大限或流年。
 */
val p刑囚會鈴 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: ZPatternContext): ZPattern? {
    val 廉貞化忌 = it.getTransFourValue(廉貞) == 忌

    val 三星同宮: Branch? = it.getBranches(廉貞, 擎羊, 鈴星)
      .takeIf { list -> list.size == 1 } // 三星 同宮
      ?.let { list -> list[0] }
      ?.takeIf { branches.contains(it) }

    return if (廉貞化忌 && 三星同宮 != null) {
      val house = it.getHouseDataOf(三星同宮).house
      刑囚會鈴(house)
    } else
      null
  }
}

/**
 * 辰戌宮安命，會齊鈴星、陀羅、文昌、武曲四曜。一般主水厄或者意外災害。
 * (三方四正？）
 */
val p鈴昌陀武 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> it.三方四正(branch).containsAll(it.getBranches(鈴星, 文昌, 陀羅, 武曲)) }
      ?.let { 鈴昌陀武 }
  }
}

/**
 * 廉貞七殺，有擎羊同度，會鈴星，不利軍職武職人員，主殉職。
 */
val p廉殺羊鈴 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.mainHouse.branch
      .takeIf { branch -> it.三方四正(branch).containsAll(it.getBranches(廉貞, 七殺, 擎羊, 鈴星)) }
      ?.let { 廉殺羊鈴 }
  }
}

/**
 * 破軍守命垣，坐於陷地， TODO 眾煞聚會。
 *
 * 一生孤贫格,破军陷地守命（卯酉宫、巳亥宫），命宫、对宫、三合宫没有任何一颗吉星加会，即为一生孤贫之格。
 */
val p一生孤貧 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.starMap[破軍]?.stemBranch?.branch
      ?.takeIf { branch -> branch == it.mainHouse.branch } // 破軍守命
      ?.takeIf { _ -> 7 == it.starStrengthMap[破軍] }  // 破軍 陷
      ?.takeIf { branch -> it.三方四正(branch).intersect(it.getBranches(*StarLucky.values)).isEmpty() } // 三方四正 沒有吉星
      ?.let { 一生孤貧 }
  }
}

/**
 * 1、名詞解釋：魁鉞入命身，逢眾多凶煞同宮、加會。
 * 2、成功條件：三方四正無吉星解救，而遇煞星忌星。
 * 3、作用：難於顯貴，富貴艱難。
 * 4、遇火鈴，性格急躁，不易聚財；遇羊陀，富貴不長；遇空劫，空成空敗，歡喜一場。
 */
val p魁鉞凶冲 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return setOf(it.mainHouse.branch, it.bodyHouse.branch)
      .takeIf { set -> set == it.getBranches(天魁, 天鉞).toSet() }
      ?.takeIf { _ -> it.三方四正().intersect(it.getBranches(*StarLucky.values)).isEmpty() } // 三方四正无吉星
      ?.let { _ ->
        val evils = mutableSetOf<EvilCombo>().apply {
          if (it.三方四正().containsAll(it.羊陀()))
            add(EvilCombo.空劫)
          if (it.三方四正().containsAll(it.火鈴()))
            add(EvilCombo.火鈴)
          if (it.三方四正().containsAll(it.羊陀()))
            add(EvilCombo.羊陀)
        }
        魁鉞凶冲(evils)
      }
  }
}

/**
 * 『眾水朝東』-破軍與文昌或文曲在寅卯兩地，不但刑剋而且財不聚
 */
val p眾水朝東 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: ZPatternContext): ZPattern? {
    return it.getBranches(文昌, 文曲, 破軍)
      .takeIf { list -> list.size == 1 } // 三星同宮
      ?.let { list -> list[0] }
      ?.takeIf { it == 寅 || it == 卯 }
      ?.let { 眾水朝東 }
  }
}

// =========================== 以上 , 惡格 ===========================


/**
 * 經典 pattern , 具備 「吉格」「惡格」 的分別
 */
interface IClassicalPattern : ZPattern {

  enum class PatternType {
    GOOD, EVIL
  }

  val type: PatternType
}


/**
 * ClassicalPattern
 * 經典 Pattern , 名稱固定為四字
 * 參考資料
 * https://goo.gl/hDUun2 ( http://vioheart.pixnet.net )
 * http://www.ai5429.com/c/clock108/
 */
sealed class ClassicalPattern(override val type: PatternType,
                              override val notes: String? = null) : IClassicalPattern, Serializable {
  object 極向離明 : ClassicalPattern(GOOD)
  object 紫府同宮 : ClassicalPattern(GOOD)
  class 紫府朝垣(house: House, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  class 天府朝垣(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  class 府相朝垣(house: House, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  class 巨機同宮(branch: Branch, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + branch.toString() + "]" + goods.joinToString(","))

  class 善蔭朝綱(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  object 機月同梁 : ClassicalPattern(GOOD)
  class 日月照壁(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  class 日麗中天(dayNight: DayNight, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + dayNight.toString() + "]" + goods.joinToString(","))


  class 君臣慶會(house: House, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  object 日月同宮 : ClassicalPattern(GOOD)
  object 日月並明 : ClassicalPattern(GOOD)
  object 日照雷門 : ClassicalPattern(GOOD)
  class 陽梁昌祿(house: House) : ClassicalPattern(GOOD, "[$house]")
  object 明珠出海 : ClassicalPattern(GOOD)
  object 巨日同宮 : ClassicalPattern(GOOD)
  object 貪武同行 : ClassicalPattern(GOOD)
  object 將星得地 : ClassicalPattern(GOOD)
  class 七殺朝斗(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  object 雄宿朝垣 : ClassicalPattern(GOOD)
  object 對面朝天 : ClassicalPattern(GOOD)
  object 科名會祿 : ClassicalPattern(GOOD)
  object 甲第登科 : ClassicalPattern(GOOD)
  object 科權逢迎 : ClassicalPattern(GOOD)
  object 祿合鴛鴦 : ClassicalPattern(GOOD)
  class 雙祿朝垣(house: House) : ClassicalPattern(GOOD, "[$house]")
  object 三奇嘉會 : ClassicalPattern(GOOD)
  object 祿馬交馳 : ClassicalPattern(GOOD)
  class 月朗天門(dayNight: DayNight) : ClassicalPattern(GOOD, dayNight.toString())
  class 月生滄海(dayNight: DayNight) : ClassicalPattern(GOOD, dayNight.toString())
  class 石中隱玉(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  class 壽星入廟(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  class 英星入廟(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  //class 機梁加會(goods: Set<GoodCombo>) : Pattern(GOOD, goods.joinToString(","))
  class 文桂文華(route: Route) : ClassicalPattern(GOOD, route.toString())

  class 文梁振紀(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  class 魁鉞拱命(route: Route) : ClassicalPattern(GOOD, route.toString())
  class 左右同宮(goods: Set<GoodCombo>) : ClassicalPattern(GOOD, goods.joinToString(","))
  object 丹墀桂墀 : ClassicalPattern(GOOD)
  object 甲第登庸 : ClassicalPattern(GOOD)
  object 化星返貴 : ClassicalPattern(GOOD)
  object 天乙拱命 : ClassicalPattern(GOOD)
  object 坐貴向貴 : ClassicalPattern(GOOD)
  object 廉貞文武 : ClassicalPattern(GOOD)
  class 星臨正位(stars: Set<ZStar>) : ClassicalPattern(GOOD, stars.joinToString(","))
  class 輔拱文星(house: House) : ClassicalPattern(GOOD, "[$house]")
  class 三合火貪(house: House) : ClassicalPattern(GOOD, "[$house]")
  class 三合鈴貪(house: House) : ClassicalPattern(GOOD, "[$house]")
  class 權祿巡逢(house: House, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  object 科權祿夾 : ClassicalPattern(GOOD)
  object 文星拱命 : ClassicalPattern(GOOD)
  class 財祿夾馬(house: House, goods: Set<GoodCombo>) :
    ClassicalPattern(GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  class 財蔭夾印(house: House) : ClassicalPattern(GOOD, "[$house]")
  class 擎羊入廟(house: House) : ClassicalPattern(GOOD, "[$house]")
  class 祿馬配印(house: House) : ClassicalPattern(GOOD, "[$house]")
  object 紫府夾命 : ClassicalPattern(GOOD)
  object 日月夾命 : ClassicalPattern(GOOD)
  object 昌曲夾命 : ClassicalPattern(GOOD)
  object 左右夾命 : ClassicalPattern(GOOD)
  object 雙祿夾命 : ClassicalPattern(GOOD)
  object 權煞化祿 : ClassicalPattern(GOOD)
  object 祿文拱命 : ClassicalPattern(GOOD)
  object 明祿暗祿 : ClassicalPattern(GOOD)
  object 水木清華 : ClassicalPattern(GOOD)
  class 金鑾扶駕(house: House) : ClassicalPattern(GOOD, "[$house]")
  object 玉袖添香 : ClassicalPattern(GOOD)
  object 殺破狼格 : ClassicalPattern(GOOD)
  object 廟星變景 : ClassicalPattern(GOOD)
  object 辛勞開創 : ClassicalPattern(GOOD)
  object 財印天祿 : ClassicalPattern(GOOD)
  object 蟾宮折桂 : ClassicalPattern(GOOD)

  // =========================== 以下 , 惡格 ===========================

  class 馬頭帶劍(evils: Set<EvilCombo>) : ClassicalPattern(EVIL, evils.joinToString(","))
  class 極居卯酉(evils: Set<EvilCombo>) : ClassicalPattern(EVIL, evils.joinToString(","))
  object 命無正曜 : ClassicalPattern(EVIL)
  object 風流綵杖 : ClassicalPattern(EVIL)
  object 巨機化酉 : ClassicalPattern(EVIL)
  object 日月反背 : ClassicalPattern(EVIL)
  object 日月疾厄 : ClassicalPattern(EVIL)
  object 梁馬飄蕩 : ClassicalPattern(EVIL)
  object 貞殺同宮 : ClassicalPattern(EVIL)
  object 殺拱廉貞 : ClassicalPattern(EVIL)
  class 巨逢四煞(house: House) : ClassicalPattern(EVIL, "[$house]")
  object 命裡逢空 : ClassicalPattern(EVIL)
  class 文星遇夾(evils: Set<EvilCombo>) : ClassicalPattern(EVIL, evils.joinToString(","))
  object 馬落空亡 : ClassicalPattern(EVIL)
  object 兩重華蓋 : ClassicalPattern(EVIL)
  object 祿逢衝破 : ClassicalPattern(EVIL)
  object 泛水桃花 : ClassicalPattern(EVIL)
  object 天梁拱月 : ClassicalPattern(EVIL)
  object 財與囚仇 : ClassicalPattern(EVIL)
  object 火入泉鄉 : ClassicalPattern(EVIL)
  object 祿逢兩煞 : ClassicalPattern(EVIL)
  class 君子在野(evils: Set<EvilCombo>) : ClassicalPattern(EVIL, evils.joinToString(","))
  class 羊陀夾忌(house: House) : ClassicalPattern(EVIL, house.toString())
  class 火鈴夾忌(house: House) : ClassicalPattern(EVIL, house.toString())
  object 羊陀夾命 : ClassicalPattern(EVIL)
  object 火鈴夾命 : ClassicalPattern(EVIL)
  object 空劫夾命 : ClassicalPattern(EVIL)
  class 刑囚夾印(house: House) : ClassicalPattern(EVIL, "[$house]")
  class 刑忌夾印(house: House) : ClassicalPattern(EVIL, "[$house]")
  class 祿衰馬困(house: House) : ClassicalPattern(EVIL, "[$house]")
  object 名不利達 : ClassicalPattern(EVIL)
  object 三方並凶 : ClassicalPattern(EVIL)
  class 三奇沖剋(house: House) : ClassicalPattern(EVIL, "[$house]")
  object 天機巳亥 : ClassicalPattern(EVIL)
  object 月同遇煞 : ClassicalPattern(EVIL)
  object 桃花滾浪 : ClassicalPattern(EVIL)
  object 梁同巳亥 : ClassicalPattern(EVIL)
  class 科星逢破(house: House) : ClassicalPattern(EVIL, "[$house]")
  class 刑囚會鈴(house: House) : ClassicalPattern(EVIL, "[$house]")
  object 鈴昌陀武 : ClassicalPattern(EVIL)
  object 廉殺羊鈴 : ClassicalPattern(EVIL)
  object 一生孤貧 : ClassicalPattern(EVIL)
  class 魁鉞凶冲(evils: Set<EvilCombo>) : ClassicalPattern(EVIL, evils.joinToString(","))
  object 眾水朝東 : ClassicalPattern(EVIL)
  // TODO : 在野孤君 , 無道之君

  companion object {

    fun values(): List<IPatternFactory> = listOf(
      p極向離明, p紫府同宮, p紫府朝垣, p天府朝垣, p府相朝垣, p巨機同宮, p善蔭朝綱, p機月同梁, p日月照壁, p日麗中天,
      p君臣慶會, p日月同宮, p日月並明, p日照雷門, p陽梁昌祿, p明珠出海, p巨日同宮, p貪武同行, p將星得地, p七殺朝斗,
      p雄宿朝垣, p對面朝天, p科名會祿, p甲第登科, p科權逢迎, p祿合鴛鴦, p雙祿朝垣, p三奇嘉會, p祿馬交馳, p月朗天門,
      p月生滄海, p石中隱玉, p壽星入廟, p英星入廟, p文桂文華, p文梁振紀, p魁鉞拱命, p左右同宮, p丹墀桂墀, p甲第登庸,
      p化星返貴, p天乙拱命, p坐貴向貴, p廉貞文武, p星臨正位, p輔拱文星, p三合火貪, p三合鈴貪, p權祿巡逢, p科權祿夾,
      p文星拱命, p財祿夾馬, p財蔭夾印, p擎羊入廟, p祿馬配印, p紫府夾命, p日月夾命, p昌曲夾命, p左右夾命, p雙祿夾命,
      p權煞化祿, p祿文拱命, p明祿暗祿, p水木清華, p金鑾扶駕, p玉袖添香, p殺破狼格, p廟星變景, p辛勞開創, p財印天祿,
      p蟾宮折桂,

      p馬頭帶劍, p極居卯酉, p命無正曜, p風流綵杖, p巨機化酉, p日月反背, p日月疾厄, p梁馬飄蕩, p貞殺同宮, p殺拱廉貞,
      p巨逢四煞, p命裡逢空, p文星遇夾, p馬落空亡, p兩重華蓋, p祿逢衝破, p泛水桃花, p天梁拱月, p財與囚仇, p火入泉鄉,
      p祿逢兩煞, p君子在野, p羊陀夾忌, p火鈴夾忌, p羊陀夾命, p火鈴夾命, p空劫夾命, p刑囚夾印, p刑忌夾印, p祿衰馬困,
      p名不利達, p三方並凶, p三奇沖剋, p天機巳亥, p月同遇煞, p桃花滾浪, p梁同巳亥, p科星逢破, p刑囚會鈴, p鈴昌陀武,
      p廉殺羊鈴, p一生孤貧, p魁鉞凶冲, p眾水朝東
                                                )
  }
}
