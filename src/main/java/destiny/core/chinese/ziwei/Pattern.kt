/**
 * Created by smallufo on 2018-07-01.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.ziwei.House.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import destiny.core.chinese.ziwei.Pattern.*
import destiny.core.chinese.ziwei.PatternType.EVIL
import destiny.core.chinese.ziwei.PatternType.GOOD
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.StarUnlucky.*

/** 拱 */
fun Collection<Branch?>.trine(): Branch? {
  return this.takeIf { it.size >= 2 }?.toList()?.let { list ->
    val a = list[0]
    val b = list[1]
    return if (a != null && b != null) {
      when {
        a.getAheadOf(b) == 4 -> a.next(4)
        b.getAheadOf(a) == 4 -> b.next(4)
        else -> null
      }
    } else
      null
  }
}


fun IPlate.拱(branch: Branch = this.mainHouse.branch): Set<Branch> = branch.let { setOf(it.prev(4), it.next(4)) }
fun IPlate.三方(branch: Branch = this.mainHouse.branch) = 拱(branch).plus(branch)
fun IPlate.三方四正(branch: Branch = this.mainHouse.branch): Set<Branch> = 三方(branch).plus(branch.opposite)

fun IPlate.neighbors(branch: Branch = this.mainHouse.branch): Set<Branch> = branch.let { setOf(it.previous, it.next) }


fun IPlate.日月(): List<Branch> = this.getBranches(太陽, 太陰)
fun IPlate.紫府(): List<Branch> = this.getBranches(紫微, 天府)
fun IPlate.昌曲(): List<Branch> = this.getBranches(文昌, 文曲)
fun IPlate.輔弼(): List<Branch> = this.getBranches(左輔, 右弼)
fun IPlate.魁鉞(): List<Branch> = this.getBranches(天魁, 天鉞)

// 六惡星
fun IPlate.羊陀(): List<Branch> = this.getBranches(擎羊, 陀羅)

fun IPlate.火鈴(): List<Branch> = this.getBranches(火星, 鈴星)
fun IPlate.空劫(): List<Branch> = this.getBranches(地空, 地劫)

fun IPlate.三方四正有輔弼(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(輔弼())
fun IPlate.鄰宮有輔弼(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(輔弼())

fun IPlate.三方四正有昌曲(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(昌曲())
fun IPlate.鄰宮有昌曲(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(昌曲())

fun IPlate.三方四正有魁鉞(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(魁鉞())
fun IPlate.鄰宮有魁鉞(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(魁鉞())

fun IPlate.三方四正有祿存(branch: Branch = this.mainHouse.branch) =
  this.三方四正(branch).contains(this.starMap[祿存]?.stemBranch?.branch)

fun IPlate.三方四正有祿權科星(branch: Branch = this.mainHouse.branch): Boolean =
  this.三方四正(branch).flatMap { b ->
    this.getHouseDataOf(b).stars.map { star: ZStar ->
      this.tranFours[star]?.get(FlowType.本命)
    }
  }.any { value: ITransFour.Value? -> setOf(祿, 權, 科).contains(value) }


fun IPlate.化祿入命宮(): Boolean = this.getHouseDataOf(this.mainHouse.branch).stars.map { star ->
  this.tranFours[star]?.get(FlowType.本命)
}.contains(祿)

fun IPlate.化科入命宮(): Boolean = this.getHouseDataOf(this.mainHouse.branch).stars.map { star ->
  this.tranFours[star]?.get(FlowType.本命)
}.contains(科)

enum class GoodCombo {
  輔弼,
  昌曲,
  魁鉞,
  祿存,
  祿權科星
}

enum class EvilCombo {
  空劫,
  火鈴,
  羊陀
}

// =========================== 以下 , 吉格 ===========================
/** 紫微在午宮坐命 */
val p極向離明 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.starMap[紫微]?.stemBranch?.branch?.let { branch ->
      if (branch == 午 && branch == it.mainHouse.branch)
        極向離明
      else
        null
    }
  }
}

/** 安命在寅或申宮，紫微天府同宮。 */
val p紫府同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
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
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {

    val 紫府拱: Branch? = it.紫府().trine().takeIf { branches.contains(it) }

    val goods: Set<GoodCombo>? = 紫府拱?.let { branch ->
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

    return if (紫府拱 != null && goods != null && goods.isNotEmpty()) {
      val house = it.getHouseDataOf(紫府拱).house
      紫府朝垣(house, goods)
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 天府廉貞在戌宮坐命: Boolean = it.mainHouse.branch.let { branch ->
      branch == 戌
        && setOf(branch).containsAll(it.getBranches(天府, 廉貞))
    }

    val goods = mutableSetOf<GoodCombo>().takeIf { 天府廉貞在戌宮坐命 }?.apply {
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
    }?.toSet()

    return if (天府廉貞在戌宮坐命 && goods != null && goods.isNotEmpty())
      天府朝垣(goods)
    else
      null
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
  //  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
  //    val 府相拱: Branch? = listOf(天府, 天相).map { star -> it.starMap[star]?.stemBranch?.branch }.trine()
  //
  //    return 府相拱
  //      ?.takeIf { b -> branches.contains(b) }
  //      ?.let { b ->
  //        val goods = mutableSetOf<GoodCombo>().apply {
  //          if (it.三方四正有輔弼(b))
  //            add(GoodCombo.輔弼)
  //          if (it.三方四正有昌曲(b))
  //            add(GoodCombo.昌曲)
  //          if (it.三方四正有魁鉞(b))
  //            add(GoodCombo.魁鉞)
  //          if (it.三方四正有祿存(b))
  //            add(GoodCombo.祿存)
  //          if (it.三方四正有祿權科星(b))
  //            add(GoodCombo.祿權科星)
  //        }.toSet()
  //
  //        val house = it.getHouseDataOf(b).house
  //        府相朝垣(house , goods)
  //      }
  //  }

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 府相宮位正確 = (it.mainHouse.branch == 寅 || it.mainHouse.branch == 申)
      && it.starMap[天府]?.house == House.官祿
      && it.starMap[天相]?.house == House.財帛

    val goods = mutableSetOf<GoodCombo>().takeIf { 府相宮位正確 }?.apply {
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
    }?.toSet()

    return if (府相宮位正確 && goods != null && goods.isNotEmpty())
      府相朝垣(House.命宮, goods)
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 巨機命同卯或酉: Branch? = it.mainHouse.branch.let {
      if (it == 卯 || it == 酉)
        it
      else
        null
    }?.let { branch ->
      if (it.starMap[巨門]?.stemBranch?.branch == branch
        && it.starMap[天機]?.stemBranch?.branch == branch)
        branch
      else
        null
    }

    val goods: Set<GoodCombo>? = 巨機命同卯或酉?.let { branch ->
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

    //    val 無化忌同宮: Boolean = 巨機命同卯或酉?.let { branch ->
    //      it.getTransFourHouseOf(忌).stemBranch.branch != branch
    //    }?:false

    //println("巨機命同卯或酉 = $巨機命同卯或酉 , 化忌在 : ${it.getTransFourHouseOf(忌).stemBranch.branch}")

    return if (巨機命同卯或酉 != null) {
      巨機同宮(巨機命同卯或酉, goods!!)
    } else
      null
  }
}


/**
 * 天機、天梁二星同時在辰或戌宮守命，為此格。
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
 */
val p善蔭朝綱 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 機梁同時守命於辰或戌: Branch? = it.mainHouse.branch.let {
      if (it == 辰 || it == 戌)
        it
      else
        null
    }?.let { branch ->
      if (
        it.starMap[天機]?.stemBranch?.branch == branch
        && it.starMap[天梁]?.stemBranch?.branch == branch
      )
        branch
      else
        null
    }

    val goods: Set<GoodCombo>? = 機梁同時守命於辰或戌?.let { branch ->
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

    return if (機梁同時守命於辰或戌 != null) {
      善蔭朝綱(goods!!)
    } else
      null
  }
}


/**
 * 於三方四正中有天機、太陰、天同、天梁四星交會。
 *
 * 命宮的對宮、合宮見天機、太陰、天同、天梁等，同宮加會，謂之機月同梁格，主其人智慧超群，為最佳之幕僚、輔佐人才。
 */
val p機月同梁 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 四星: List<Branch> = it.getBranches(天機, 太陰, 天同, 天梁)

    return if (it.三方四正().containsAll(四星))
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 日月入田宅於丑或未: Branch? = it.getHouseDataOf(House.田宅)?.let { houseData: HouseData ->
      houseData.stemBranch.branch.let {
        if (it == 丑 || it == 未)
          it
        else
          null
      }?.let { branch ->
        if (houseData.stars.containsAll(listOf(太陽, 太陰)))
          branch
        else
          null
      }
    }

    val goods: Set<GoodCombo>? = 日月入田宅於丑或未?.let { branch ->
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

    return if (日月入田宅於丑或未 != null) {
      日月照壁(goods!!)
    } else
      null
  }
}

/**
 * (金燦光輝)
 * 太陽在午宮坐命。
 *
 * 白天出生者，謂之金燦光輝格，主富可敵國，或為權貴。
 */
val p日麗中天 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 午
      && it.starMap[太陽]?.stemBranch?.branch == 午
    )
      日麗中天(it.dayNight)
    else
      null
  }

}

/**
 * 命宮在丑或未宮，太陽與太陰在左右鄰宮相夾。有財運，利於事業發展。
 *
 * TODO 命宮坐吉曜，太陽太陰在輔宮夾命宮，謂之日月夾命格，主其人不貴則大富。
 */
val p日月夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (it.日月().containsAll(it.neighbors()))
      日月夾命
    else
      null
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
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
    val 紫微地支: Branch? = it.starMap[紫微]?.stemBranch?.branch?.takeIf { b -> branches.contains(b) }

    val goods: Set<GoodCombo>? = 紫微地支?.let { b ->
      mutableSetOf<GoodCombo>().apply {
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
    }

    return if (goods != null && goods.size >= 3) {
      君臣慶會(it.getHouseDataOf(紫微地支).house, goods)
    } else {
      null
    }
  }
}

/**
 * 命宮在丑或未，日月二星坐守。
 */
val p日月同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let { branch ->
      if (branch == 丑 || branch == 未)
        branch
      else
        null
    }?.let { branch ->
      it.starMap[太陽]?.stemBranch?.branch == branch
        && it.starMap[太陰]?.stemBranch?.branch == branch
    }?.let {
      if (it)
        日月同宮
      else
        null
    }
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

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 丑
      && it.starMap[太陽]?.stemBranch?.branch == 巳
      && it.starMap[太陰]?.stemBranch?.branch == 酉
    )
      日月並明
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 卯
      && it.dayNight == DayNight.DAY
      && it.starMap[太陽]?.stemBranch?.branch == 卯
    ) {
      println("日照雷門 , 太陽卯， 天梁 = ${it.starMap[天梁]?.stemBranch?.branch}")
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
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {

    val 太陽地支 = it.starMap[太陽]?.stemBranch?.branch

    val 陽梁昌祿 = 太陽地支?.let { branch ->
      val 梁昌 = it.getBranches(天梁, 文昌)
      // 上格
      val 化祿 = it.getTransFourHouseOf(祿).stemBranch.branch
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 未
      && it.starMap[太陽]?.stemBranch?.branch == 卯
      && it.starMap[太陰]?.stemBranch?.branch == 亥
    )
      明珠出海
    else
      null
  }
}


/**
 * 巨門太陽同時在寅或申宮坐命。
 *
 * 太陽、巨門入命宮在寅時，謂之巨日同宮格，主其人食祿豐譽，口福聞名。
 */
val p巨日同宮 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let {
      if (it == 寅 || it == 申)
        it
      else
        null
    }?.let { branch ->
      val branches = it.getBranches(太陽, 巨門)

      setOf(branch).containsAll(branches)
    }?.let {
      if (it)
        巨日同宮
      else
        null
    }
  }
}

/**
 * 命宮在丑或未，武曲貪狼二星坐守。
 *
 * 貪狼武曲入命宮在丑或未時，謂之貪武同行格，主其人先貧後富，大器晚成，三十歲後發福。
 */
val p貪武同行 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let {
      if (it == 丑 || it == 未)
        it
      else
        null
    }?.let { branch ->
      val branches: Set<Branch> = it.getBranches(武曲, 貪狼).toSet()

      setOf(branch).containsAll(branches)
    }?.let {
      if (it)
        貪武同行
      else
        null
    }
  }
}


/**
 * 武曲坐命在辰或戌宮。
 *
 * TODO 武曲入命宮在辰、戌、丑、未時，若又為辰、戌、丑、未年出生者，謂之將星得地格，主其人英名顯赫，大富大貴。
 */
val p將星得地 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let {
      if (it == 辰 || it == 戌)
        it
      else
        null
    }?.let { branch ->
      it.starMap[武曲]?.stemBranch?.branch == branch
    }?.let {
      if (it)
        將星得地
      else
        null
    }
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 七殺入命宮在寅申子午: Branch? = it.mainHouse.branch.let {
      if (it == 申 || it == 午 || it == 子 || it == 寅)
        it
      else
        null
    }?.let { branch ->
      if (it.starMap[七殺]?.stemBranch?.branch == branch)
        branch
      else
        null
    }

    val goods: Set<GoodCombo>? = 七殺入命宮在寅申子午?.let { branch ->
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

    return if (七殺入命宮在寅申子午 != null) {
      七殺朝斗(goods!!)
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let {
      if (it == 寅 || it == 申)
        it
      else
        null
    }?.let { branch ->
      it.starMap[廉貞]?.stemBranch?.branch == branch
    }?.let {
      if (it)
        雄宿朝垣
      else
        null
    }
  }
}


/**
 * 命宮在子，太陽與化碌在午，謂之對面朝天格，主其人文章蓋世、超群。
 */
val p對面朝天 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 命宮在子 = (it.mainHouse.branch == 子)
    val 太陽在午 = (it.starMap[太陽]?.stemBranch?.branch == 午)
    val 化祿在午 = (it.getTransFourHouseOf(祿)).stemBranch.branch == 午

    return if (命宮在子 && 太陽在午 && 化祿在午)
      對面朝天
    else
      null
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

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    //val 化祿入對宮 = it.getTransFourHouseOf(祿).stemBranch.branch == it.mainHouse.branch.opposite

    val 三方四正有化祿: Boolean = it.三方四正().contains(it.getTransFourHouseOf(祿).stemBranch.branch)

    return if (it.化科入命宮() && (三方四正有化祿 || it.三方四正有祿存())) {
      科名會祿
    } else
      null
  }
}

/**
 * 化科入命宮，化權入遷移宮(對宮)時，謂之科權逢迎格，主其人科甲及第，金榜高中。
 */
val p科權逢迎 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 化權入遷移宮 = it.getTransFourHouseOf(權).house == House.遷移

    return if (it.化科入命宮() && 化權入遷移宮)
      科權逢迎
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 化祿入對宮 = it.getTransFourHouseOf(權).stemBranch.branch == it.mainHouse.branch.opposite
    val 祿存入命宮 = (it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch)
    val 祿存在對宮 = (it.mainHouse.branch.next(6) == it.starMap[祿存]?.stemBranch?.branch)

    return if (
      (it.化祿入命宮() && 祿存入命宮) || ((祿存入命宮 && 化祿入對宮) || (祿存在對宮 && it.化祿入命宮()))
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

  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
    val 化祿宮位: Branch = it.getTransFourHouseOf(祿).stemBranch.branch
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
val p三奇加會 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val good3: Set<ITransFour.Value> = setOf(祿, 權, 科)

    return it.三方四正().all { branch ->
      val a: Set<ITransFour.Value?> = it.getHouseDataOf(branch).stars.map { star ->
        it.tranFours[star]?.get(FlowType.本命)
      }.toSet()
      a.any { value: ITransFour.Value? -> good3.contains(value) }
    }.let {
      if (it)
        三奇加會
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 天馬在命: Boolean = it.mainHouse.branch == it.starMap[天馬]?.stemBranch?.branch
    val 天馬在遷: Boolean = it.mainHouse.branch.opposite == it.starMap[天馬]?.stemBranch?.branch

    val 祿存在命 = it.mainHouse.branch == it.starMap[祿存]?.stemBranch?.branch
    val 祿存在遷 = it.mainHouse.branch.opposite == it.starMap[祿存]?.stemBranch?.branch

    val 化祿入遷移: Boolean = it.getTransFourHouseOf(祿).stemBranch.branch == it.mainHouse.branch.opposite

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

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 亥
      && it.starMap[太陰]?.stemBranch?.branch == 亥
    )
      月朗天門(it.dayNight)
    else
      null
  }
}

/**
 * 太陰、天同星在子宮坐命。
 *
 * 日月滄海格：太陰入命宮在子時，夜晚出生者，謂之日月滄海格，主其人富貴、清高、忠良。
 */
val p月生滄海 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 子
      && it.starMap[太陰]?.stemBranch?.branch == 子
      && it.starMap[天同]?.stemBranch?.branch == 子
    )
      月生滄海(it.dayNight)
    else
      null
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

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 巨門入命於子或午: Branch? = it.mainHouse.branch.let {
      if (it == 子 || it == 午)
        it
      else
        null
    }?.let { branch ->
      if (it.starMap[巨門]?.stemBranch?.branch == branch)
        branch
      else
        null
    }

    val goods: Set<GoodCombo>? = 巨門入命於子或午?.let { branch ->
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

    return if (巨門入命於子或午 != null && goods != null) {
      石中隱玉(goods)
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 天梁入命宮在午: Boolean = (
      it.mainHouse.branch == 午
        && it.starMap[天梁]?.stemBranch?.branch == 午
      )

    val goods: Set<GoodCombo>? = mutableSetOf<GoodCombo>().takeIf { 天梁入命宮在午 }?.apply {
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
    }?.toSet()

    return if (天梁入命宮在午) {
      壽星入廟(goods!!)
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 破軍守命居子或午: Branch? = it.mainHouse.branch.let {
      if (it == 子 || it == 午)
        it
      else
        null
    }?.let { branch ->
      if (it.starMap[破軍]?.stemBranch?.branch == branch)
        branch
      else
        null
    }

    val goods: Set<GoodCombo>? = 破軍守命居子或午?.let { branch ->
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

    return if (破軍守命居子或午 != null) {
      英星入廟(goods!!)
    } else
      null
  }
}

/**
 * 天機、天梁入命宮在辰或戌宮，謂之機梁加會格，主其人富貴、仁慈、善良。
 *
 * 所謂“機梁加會格”是指天機、天梁二星在辰戌宮守命，與祿存、科權祿、左右、昌曲、魁鉞加會。
 * 紫微命盤中有此格局者心地善良，樂善好施，于事奉公守法，于家庭父慈子孝、兄友弟恭，能設身處地為人著想，而且口才極佳，講起話來滔滔不絕。
 * 此人必擅策劃有分析能力，聰明靈敏，能以特殊技藝立足于社會。會吉星多，主大富大貴。
 * 吉星少，從事的工作的軍警、司法等有關。又見煞者，多為宗教教主、邪教創始人、神學家、哲學家、思想家、氣功師。
 */
val p機梁加會 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 天機天梁入命宮在辰或戌宮: Branch? =
      it.mainHouse.branch.let {
        if (it == 辰 || it == 戌)
          it
        else
          null
      }?.let { branch ->
        if (setOf(branch).containsAll(it.getBranches(天機, 天梁)))
          branch
        else
          null
      }

    val goods: Set<GoodCombo>? = 天機天梁入命宮在辰或戌宮?.let { branch ->
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

    return if (天機天梁入命宮在辰或戌宮 != null) {
      機梁加會(goods!!)
    } else
      null
  }
}

/**
 * 文昌、文曲兩星在丑或未宮守命。
 *
 * TODO 文昌、文曲入命宮，或夾命宮，或三合命宮，謂之文桂文華格，主其人多學而廣，非富則貴。
 * */
val p文桂文華 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let {
      if (it == 丑 || it == 未)
        it
      else
        null
    }?.let { branch ->
      setOf(branch).containsAll(it.昌曲())
    }?.let {
      if (it)
        文桂文華
      else
        null
    }
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 命宮有昌或曲且處廟旺 = it.getHouseDataOf(it.mainHouse.branch).stars
      .filter { star -> listOf(文昌, 文曲).contains(star) }
      .map { star -> it.starStrengthMap[star] }
      .filter { value -> value != null }
      .map { value -> value!! }
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

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 魁鉞入命: Boolean = setOf(it.mainHouse.branch).containsAll(it.魁鉞())
    val 魁鉞夾命: Boolean = it.魁鉞() == it.neighbors()
    val 三合命宮: Boolean = it.魁鉞().containsAll(it.拱())

    return if (魁鉞入命 || 魁鉞夾命 || 三合命宮)
      魁鉞拱命
    else
      null
  }
}

/**
 * 命宮在寅或申宮，遇紫微與天府來夾。
 */
val p紫府夾命 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 紫府有夾命 = it.紫府().containsAll(it.neighbors())
    val 命在寅或申 = it.mainHouse.branch.let { (it == 寅 || it == 申) }

    return if (紫府有夾命 && 命在寅或申) {
      紫府夾命
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 輔弼入命於丑或未: Branch? = it.mainHouse.branch.let {
      if (it == 丑 || it == 未)
        it
      else
        null
    }?.let { branch ->
      if (setOf(branch).containsAll(it.輔弼()))
        branch
      else
        null
    }

    val goods: Set<GoodCombo>? = 輔弼入命於丑或未?.let { branch ->
      mutableSetOf<GoodCombo>().apply {
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

    return if (輔弼入命於丑或未 != null) {
      左右同宮(goods!!)
    } else
      null
  }
}

/**
 * A : 三方四正會太陽、太陰，且日月均在廟旺之宮位。
 *
 * 當太陽、太陰在廟旺之地會照命宮時，稱之為丹墀桂墀格，又稱為 [fun日月並明]。這個格局因為太陽、太陰皆處廟旺之地，能量充足，主星得力，因此主富貴。
 *
 * 當太陽入廟，處于旺位時便稱其為“丹墀”；
 * 當太陰入廟，處于旺位時稱之為“桂墀”。
 *
 *
 * B : 而中有當太陽、太陰一在辰宮，一在戌宮守命對照時，才稱之為丹墀桂墀格。
 * 這個格局因為太陽、太陰皆處廟旺之地，能量充足，主星得力，因此主富貴。
 */
val p丹墀桂墀 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 三方四正有日月: Boolean = it.三方四正().containsAll(it.日月())

    val 日旺廟 = it.starStrengthMap[太陽]?.let { it <= 2 } ?: false
    val 月旺廟 = it.starStrengthMap[太陰]?.let { it <= 2 } ?: false

    // B
    val 日月分散在辰戌 = setOf(辰, 戌) == it.日月().toSet()

    return if (日旺廟 && 月旺廟 && (三方四正有日月 || 日月分散在辰戌))
      丹墀桂墀
    else
      null
  }
}

/**
 * 化科在命宮，化權在三方朝是。
 * 此格聰明過人，必考入高等學府，且主其人文章冠世，或在學術、科技上有創新和發明。又宜從任管理之職，或在政治上作投機。
 * 古人以化科為文墨之星，若正曜化科守命，雖會惡煞亦不失為「文章秀士，群英師範」，所以一旦與化權相會，便認為可因考試得科名而晉身廊廟。
 */
val p甲第登庸 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 拱有化權 = it.拱().any { branch ->
      it.getTransFourHouseOf(權).stemBranch.branch == branch
    }

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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 巨門在辰坐命 = it.mainHouse.branch.let {
      if (it == 辰)
        it
      else
        null
    }?.let { branch ->
      it.starMap[巨門]?.stemBranch?.branch == branch
    } ?: false

    val 天同在戌坐命 = it.mainHouse.branch.let {
      if (it == 戌)
        it
      else
        null
    }?.let { branch ->
      it.starMap[天同]?.stemBranch?.branch == branch
    } ?: false

    return if (
      (巨門在辰坐命 && it.year.stem == Stem.辛)
      || (天同在戌坐命 && it.year.stem == Stem.丁)
    )
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    return if (it.魁鉞().trine() == it.mainHouse.branch) {
      天乙拱命
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 命宮身宮 = setOf(it.mainHouse.branch, it.bodyHouse.branch)
    val 命宮對宮 = setOf(it.mainHouse.branch, it.mainHouse.branch.opposite)

    return it.魁鉞().let { 魁鉞 ->
      魁鉞.containsAll(命宮身宮) || 魁鉞.containsAll(命宮對宮)
    }.let {
      if (it)
        坐貴向貴
      else
        null
    }
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 廉貞坐命 = it.starMap[廉貞]?.stemBranch?.branch == it.mainHouse.branch
    val 武曲官祿 = it.starMap[武曲]?.house == House.官祿

    val 三方四正有昌或曲 = it.昌曲().intersect(it.三方四正()).isNotEmpty()

    return if (廉貞坐命 && 武曲官祿 && 三方四正有昌或曲)
      廉貞文武
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
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
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
    return it.starMap[文昌]?.stemBranch?.branch?.let { branch ->
      if (it.拱(branch).contains(it.starMap[左輔]?.stemBranch?.branch))
        輔拱文星
      else
        null
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
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
    return it.starMap[貪狼]?.stemBranch?.branch?.let { branch ->
      if (it.三方(branch).contains(it.starMap[火星]?.stemBranch?.branch)) {
        val house = it.getHouseDataOf(branch).house
        三合火貪(house)
      } else
        null
    }
  }
}

/**
 * 類似 [p三合火貪] , ㄧ樣以 [貪狼] 為主 , 但以 [鈴星] 為輔
 */
val p三合鈴貪 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
    return it.starMap[貪狼]?.stemBranch?.branch?.let { branch ->
      if (it.三方(branch).contains(it.starMap[鈴星]?.stemBranch?.branch)) {
        val house = it.getHouseDataOf(branch).house
        三合火貪(house)
      } else
        null
    }
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
 * 擎羊為古人視為剛強好勇之星，於午宮雖然落陷，但午宮的火可以制擎羊之金，反成器用，於是橫暴之力任為威權，因此有「威鎮邊疆」之說。相傳漢光武劉秀的命，即入此格局。
 *
 */
val p馬頭帶劍 = object : PatternSingleImpl() {

  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 擎羊居命在午 = (it.mainHouse.branch == 午 && it.starMap[擎羊]?.stemBranch?.branch == 午)

    val evils: Set<EvilCombo>? = mutableListOf<EvilCombo>().takeIf { 擎羊居命在午 }?.apply {
      if (it.三方四正().contains(it.starMap[陀羅]?.stemBranch?.branch))
        add(EvilCombo.羊陀)
      if (it.三方四正().containsAll(it.火鈴()))
        add(EvilCombo.火鈴)
      if (it.三方四正().containsAll(it.空劫()))
        add(EvilCombo.空劫)
    }?.toSet()

    return if (擎羊居命在午) {
      馬頭帶劍(evils!!)
    } else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    return it.mainHouse.branch.let {
      if (it == 卯 || it == 酉)
        it
      else
        null
    }?.takeIf { branch ->
      setOf(branch).containsAll(it.getBranches(紫微, 貪狼))
    }?.let { branch ->
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return StarMain.values.map { star ->
      it.starMap[star]?.stemBranch?.branch
    }.toSet().contains(it.mainHouse.branch).let {
      if (!it)
        命無正曜
      else
        null
    }
  }
}

/**
 * 擎羊、陀羅於左右鄰宮夾命。
 * 特点：羊陀必然夹禄存。如果羊陀夹命，那么禄存一定在命宫坐守；所以该格重点看禄存星。
 * 注意：该格与羊陀夹忌格只一字之差。本格无忌冲破禄存，所以相对好一些。但同样是凶格。
 */
val p羊陀夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (it.羊陀().containsAll(it.neighbors()))
      羊陀夾命
    else
      null
  }
}

/**
 * 火星、鈴星在左右鄰宮相夾命宮，即為此格。
 *
 * TODO : 若為火鈴夾貪格情況，就不 為火鈴夾命格。
 * 若是貪狼守命宮，得火鈴夾命反為大吉之格，要是貪狼化祿的話，則是大富大貴之命。
 */
val p火鈴夾命 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (it.火鈴().containsAll(it.neighbors()))
      火鈴夾命
    else
      null
  }
}

/**
 * 化忌坐命，擎羊、陀羅於兩鄰宮相夾。
 *
 * 祿存在命宮，則必為羊陀所夾。若有化忌星同宮，羊陀凶性得以充分發揮。雖有祿存守命，亦不為美。
 */
val p羊陀夾忌 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
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
 * 在寅宮，貪狼坐命，遇陀羅同宮。
 */
val p風流綵杖 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.mainHouse.branch == 寅
      && it.starMap[貪狼]?.stemBranch?.branch == 寅
      && it.starMap[陀羅]?.stemBranch?.branch == 寅
    )
      風流綵杖
    else
      null
  }
}

/**
 * 巨門、天機同在酉宮坐命，有化忌同宮。性質為奔波飄蕩。不利於感情、 事業。
 */
val p巨機化酉 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 化忌入命宮 = it.getTransFourHouseOf(忌).stemBranch.branch == it.mainHouse.branch

    return if (
      化忌入命宮
      && it.mainHouse.branch == 酉
      && it.starMap[巨門]?.stemBranch?.branch == 酉
      && it.starMap[天機]?.stemBranch?.branch == 酉
    )
      巨機化酉
    else
      null
  }
}

/**
 * 太陽在戌宮坐命，此時太陰在辰宮；
 * 或太陰在辰宮坐命，太陽在戌宮。
 * 取其日在戌時，月在辰時， 兩星光芒皆弱不旺。勞碌命，求人不如勞己。無閒享清福。
 */
val p日月反背 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 太陽在戌宮坐命 = (it.mainHouse.branch == 戌 && it.starMap[太陽]?.stemBranch?.branch == 戌)
    val 太陰在辰宮坐命 = (it.mainHouse.branch == 辰 && it.starMap[太陰]?.stemBranch?.branch == 辰)
    return if (太陽在戌宮坐命 || 太陰在辰宮坐命)
      日月反背
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return it.mainHouse.branch.let { branch ->
      if (branch == 丑 || branch == 未)
        branch
      else
        null
    }?.let { branch ->
      setOf(branch).containsAll(it.getBranches(廉貞, 七殺))
    }?.let {
      if (it)
        貞殺同宮
      else
        null
    }
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
 * ②廉貞七殺在丑未宮守命； ==> 此即 [fun貞殺同宮]
 * ③七殺在卯酉守命，三合有廉貞。
 * 以上諸等，命宮三方四正無吉星加會，反而加會羊陀火鈴天刑化忌劫空等諸多惡曜，便爲殺拱廉貞之格。
 */
val p殺拱廉貞 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 說法A_廉貞天相擎羊入命宮於子或午: House? = it.mainHouse.branch.let {
      if (it == 子 || it == 午)
        it
      else
        null
    }?.let { branch ->
      if (setOf(branch).containsAll(it.getBranches(廉貞, 天相, 擎羊)))
        House.命宮
      else
        null
    }

    val 說法B_廉貞天相擎羊同宮: House? = it.getBranches(廉貞, 天相, 擎羊).let { branches ->
      if (branches.size == 1) // 同宮
        branches.first()
      else
        null
    }?.let { branch ->
      it.getHouseDataOf(branch).house
    }

    return when {
      說法A_廉貞天相擎羊入命宮於子或午 != null -> 刑囚夾印(House.命宮)
      說法B_廉貞天相擎羊同宮 != null -> 刑囚夾印(說法B_廉貞天相擎羊同宮)
      else -> null
    }
  }
}

/**
 * 巨門守命，且在三方四正中，與羊陀火鈴四煞同時有會照或同宮關係。
 * 此格局應防意外之災或為不得已苦衷流落四方。
 */
val p巨逢四煞 = object : PatternMultipleImpl() {
  override fun getMultiple(it: IPlate, branches: Set<Branch>, pContext: IPatternContext): Pattern? {
    val 巨門地支: Branch? = it.starMap[巨門]?.stemBranch?.branch?.takeIf { branches.contains(it) }

    val 羊陀火鈴 = it.getBranches(擎羊, 陀羅, 火星, 鈴星)

    val 三方四正包含四凶星: Boolean? = 巨門地支?.let { b -> it.三方四正(b).containsAll(羊陀火鈴) }

    return 三方四正包含四凶星?.let { value ->
      if (value) {
        val house = it.getHouseDataOf(巨門地支).house
        巨逢四煞(house)
      } else
        null
    }
  }
}

/**
 * 地劫、地空二星或其中之一星守命。
 * 有精神上孤獨，錢不易留住之跡象。
 */
val p命裡逢空 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.空劫().contains(it.mainHouse.branch)
    )
      命裡逢空
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (
      it.空劫().containsAll(it.neighbors())
    )
      空劫夾命
    else
      null
  }
}

/**
 * 文昌或文曲守命，遇空劫 或火鈴或羊陀對星來夾。有懷才不遇跡象。
 */
val p文星遇夾 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 命宮有文星: Boolean = it.昌曲().contains(it.mainHouse.branch)

    val evils = mutableSetOf<EvilCombo>().takeIf { 命宮有文星 }?.apply {
      p空劫夾命.getSingle(it, pContext)?.also { add(EvilCombo.空劫) }
      p火鈴夾命.getSingle(it, pContext)?.also { add(EvilCombo.火鈴) }
      p羊陀夾命.getSingle(it, pContext)?.also { add(EvilCombo.羊陀) }
    }?.toSet()

    return if (命宮有文星 && evils != null && evils.isNotEmpty())
      文星遇夾(evils)
    else
      null
  }
}

/**
 * 天相受化忌和天梁於左右鄰宮相夾；
 * 或天相受化忌和擎羊於左右鄰宮相夾。
 */
val p刑忌夾印 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

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
 * 天馬遇地劫、地空同宮或三方沖照。奔波，空忙一場。
 *
 * 命馬遇空亡、截路、天空、四煞、化忌同宮或三方沖照，主奔波 ，白忙白勞。賦云：「馬遇空亡，終身奔走。」又云： 「天馬逢空亡死絕，災病悔吝。」
 * 命宮天馬，與劫空同宮，對宮有祿存，或祿坐空亡，又逢空劫煞星。生逢此格，一生總是變來變去，東奔西走，辛苦勞碌一場空，爲空虛之兆，貧賤之命。經云：“馬遇空亡，終身奔走”。
 *
 * 空亡用日空
 */
val p馬落空亡 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 命宮天馬: Boolean = it.mainHouse.branch == it.starMap[天馬]?.stemBranch?.branch

    val 三方四正有地空或地劫 = it.三方四正().intersect(it.空劫()).isNotEmpty()
    //val 空或劫入命: Boolean = it.空劫().contains(it.mainHouse.branch) //setOf(it.mainHouse.branch).containsAll(it.空劫())

    val 對宮祿存 = true //it.mainHouse.branch.opposite == it.starMap[祿存]?.stemBranch?.branch

    return if (命宮天馬 && 三方四正有地空或地劫 && 對宮祿存)
      馬落空亡
    else
      null
  }
}


/**
 * 祿存、化祿同時坐命，遇地劫、地空同宮。
 * 此即 [fun祿合鴛鴦] 格
 *
 * 祿存、化祿同時坐命，本為 雙祿交流格。但若遇地空、地劫同宮，此時雙祿為被衝破情形，稱為兩重華蓋。
 * 華蓋表示有宗教緣分。皈依宗教，反可享主清福。但因雙祿被衝破，較不易累積錢財。
 */
val p兩重華蓋 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 祿合鴛鴦 = p祿合鴛鴦.getSingle(it, pContext) != null
    val 空劫入命 = setOf(it.mainHouse.branch).containsAll(it.空劫())

    return if (祿合鴛鴦 && 空劫入命)
      兩重華蓋
    else
      null
  }
}


/**
 * 祿存或化祿坐命，在三方四正中，有被地劫、地空衝破。
 * 吉處藏凶之象，應居安思危。
 */
val p祿逢衝破 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 化祿入命宮: Boolean = it.getTransFourHouseOf(祿).stemBranch.branch == it.mainHouse.branch

    val 祿存坐命: Boolean = it.starMap[祿存]?.stemBranch?.branch == it.mainHouse.branch

    return if (
      (化祿入命宮 || 祿存坐命) && it.三方四正().containsAll(it.空劫())
    )
      祿逢衝破
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
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
 * 人命逢此，窮困而事業無成，不聚財，飄流在外，不務正業，成事不足，敗事有餘。男命浪蕩，好酒色嫖賭，女命多淫，私通內亂，故古詩云：
 *
 * 王亭之的意見。所謂「天梁拱月」，乃是天梁居巳亥，太陰居丑未，或大陰居寅申，天梁居子午的格局
 */
val p天梁拱月 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 天梁陷地守命: Boolean = it.mainHouse.branch.let { branch ->
      if (listOf(巳, 亥, 申).contains(branch))
        branch
      else
        null
    }?.let { branch ->
      it.starMap[天梁]?.stemBranch?.branch == branch
    } ?: false

    val 太陰陷地: Boolean = it.starMap[太陰]?.stemBranch?.branch?.let { branch ->
      listOf(卯, 辰, 巳, 午).contains(branch)
    } ?: false

    return if (天梁陷地守命 && 太陰陷地)
      天梁拱月
    else
      null
  }
}


/**
 * 武曲(金)、廉貞(火)分別守身宮與命宮，金火相剋！
 * 如周武王和奸臣費仲，仇人相見，分外眼紅，必兇禍百出，終身不得安寧。
 *
 * 武曲為財星屬金，廉貞為囚星屬火。二星一守命宮，一守身宮，乃火金相克，如仇人相見，分外眼紅，必兇禍百出，終身不得安寧，
 * 二星有一化忌加煞，定遭暴病、險厄。
 * 若是命宮三方四正臨廟旺，加會星并得吉化，則不作此論。
 */
val p財與囚仇 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 武曲廉貞: Set<Branch> = it.getBranches(武曲, 廉貞).toSet()

    val 命身: Set<Branch> = setOf(it.mainHouse.branch, it.bodyHouse.branch)

    return if (武曲廉貞 == 命身)
      財與囚仇
    else
      null
  }
}


/**
 * 廉貞(火)在亥逢化忌(水)，是火入泉鄉，主大凶。
 */
val p火入泉鄉 = object : PatternSingleImpl() {
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    return if (it.starMap[廉貞]?.stemBranch?.branch == 亥)
      火入泉鄉
    else
      null
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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {
    val 祿存守命宮: Boolean = it.getHouseDataOf(it.mainHouse.branch).stars.contains(祿存)
    val 化祿守命宮: Boolean = it.getTransFourHouseOf(祿).stemBranch.branch == it.mainHouse.branch

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
  override fun getSingle(it: IPlate, pContext: IPatternContext): Pattern? {

    val 閒宮: List<Branch> = listOf(父母, 兄弟, 疾厄, 交友, 夫妻, 子女)
      .map { h -> it.getHouseDataOf(h)?.stemBranch?.branch }
      .filter { b -> b != null }
      .map { b -> b!! }

    //val 六吉星盡落入閒宮: Boolean = 閒宮.containsAll(it.getBranches(*StarLucky.values))

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

enum class PatternType {
  GOOD, EVIL
}


/**
 * 參考資料
 * https://goo.gl/hDUun2 ( http://vioheart.pixnet.net )
 * http://www.ai5429.com/c/clock108/
 */
sealed class Pattern(val name: String, val type: PatternType, val notes: String? = null) {
  object 極向離明 : Pattern("極向離明", GOOD)
  object 紫府同宮 : Pattern("紫府同宮", GOOD)
  class 紫府朝垣(house: House, goods: Set<GoodCombo>) :
    Pattern("紫府朝垣", GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  class 天府朝垣(goods: Set<GoodCombo>) : Pattern("天府朝垣", GOOD, goods.joinToString(","))
  class 府相朝垣(house: House, goods: Set<GoodCombo>) :
    Pattern("府相朝垣", GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  class 巨機同宮(branch: Branch, goods: Set<GoodCombo>) :
    Pattern("巨機同宮", GOOD, "[" + branch.toString() + "]" + goods.joinToString(","))

  class 善蔭朝綱(goods: Set<GoodCombo>) : Pattern("善蔭朝綱", GOOD, goods.joinToString(","))
  object 機月同梁 : Pattern("機月同梁", GOOD)
  class 日月照壁(goods: Set<GoodCombo>) : Pattern("日月照壁", GOOD, goods.joinToString(","))
  class 日麗中天(dayNight: DayNight) : Pattern("日麗中天", GOOD, dayNight.toString())
  object 日月夾命 : Pattern("日月夾命", GOOD)
  class 君臣慶會(house: House, goods: Set<GoodCombo>) :
    Pattern("君臣慶會", GOOD, "[" + house.toString() + "]" + goods.joinToString(","))

  object 日月同宮 : Pattern("日月同宮", GOOD)
  object 日月並明 : Pattern("日月並明", GOOD)
  object 日照雷門 : Pattern("日照雷門", GOOD)
  class 陽梁昌祿(house: House) : Pattern("陽梁昌祿", GOOD, "[" + house.toString() + "]")
  object 明珠出海 : Pattern("明珠出海", GOOD)
  object 巨日同宮 : Pattern("巨日同宮", GOOD)
  object 貪武同行 : Pattern("貪武同行", GOOD)
  object 將星得地 : Pattern("將星得地", GOOD)
  class 七殺朝斗(goods: Set<GoodCombo>) : Pattern("七殺朝斗", GOOD, goods.joinToString(","))
  object 雄宿朝垣 : Pattern("雄宿朝垣", GOOD)
  object 對面朝天 : Pattern("對面朝天", GOOD)
  object 科名會祿 : Pattern("科名會祿", GOOD)
  object 科權逢迎 : Pattern("科權逢迎", GOOD)
  object 祿合鴛鴦 : Pattern("祿合鴛鴦", GOOD)
  class 雙祿朝垣(house: House) : Pattern("雙祿朝垣", GOOD, "[" + house.toString() + "]")
  object 三奇加會 : Pattern("三奇加會", GOOD)
  object 祿馬交馳 : Pattern("祿馬交馳", GOOD)
  class 月朗天門(dayNight: DayNight) : Pattern("月朗天門", GOOD, dayNight.toString())
  class 月生滄海(dayNight: DayNight) : Pattern("月生滄海", GOOD, dayNight.toString())
  class 石中隱玉(goods: Set<GoodCombo>) : Pattern("石中隱玉", GOOD, goods.joinToString(","))
  class 壽星入廟(goods: Set<GoodCombo>) : Pattern("壽星入廟", GOOD, goods.joinToString(","))
  class 英星入廟(goods: Set<GoodCombo>) : Pattern("英星入廟", GOOD, goods.joinToString(","))
  class 機梁加會(goods: Set<GoodCombo>) : Pattern("機梁加會", GOOD, goods.joinToString(","))
  object 文桂文華 : Pattern("文桂文華", GOOD)
  class 文梁振紀(goods: Set<GoodCombo>) : Pattern("文梁振紀", GOOD, goods.joinToString(","))
  object 魁鉞拱命 : Pattern("魁鉞拱命", GOOD)
  object 紫府夾命 : Pattern("紫府夾命", GOOD)
  class 左右同宮(goods: Set<GoodCombo>) : Pattern("左右同宮", GOOD, goods.joinToString(","))
  object 丹墀桂墀 : Pattern("丹墀桂墀", GOOD)
  object 甲第登庸 : Pattern("甲第登庸", GOOD)
  object 化星返貴 : Pattern("化星返貴", GOOD)
  object 天乙拱命 : Pattern("天乙拱命", GOOD)
  object 坐貴向貴 : Pattern("坐貴向貴", GOOD)
  object 廉貞文武 : Pattern("廉貞文武", GOOD)
  class 星臨正位(stars: Set<ZStar>) : Pattern("星臨正位", GOOD, stars.joinToString(","))
  object 輔拱文星 : Pattern("輔拱文星", GOOD)
  class 三合火貪(house: House) : Pattern("三合火貪", GOOD, "[" + house.toString() + "]")
  class 三合鈴貪(house: House) : Pattern("三合鈴貪", GOOD, "[" + house.toString() + "]")

  // =========================== 以下 , 惡格 ===========================

  class 馬頭帶劍(evils: Set<EvilCombo>) : Pattern("馬頭帶劍", EVIL, evils.joinToString(","))
  class 極居卯酉(evils: Set<EvilCombo>) : Pattern("極居卯酉", EVIL, evils.joinToString(","))
  object 命無正曜 : Pattern("命無正曜", EVIL)
  object 羊陀夾命 : Pattern("羊陀夾命", EVIL)
  class 羊陀夾忌(house: House) : Pattern("羊陀夾忌", EVIL, house.toString())
  object 火鈴夾命 : Pattern("火鈴夾命", EVIL)
  object 風流綵杖 : Pattern("風流綵杖", EVIL)
  object 巨機化酉 : Pattern("巨機化酉", EVIL)
  object 日月反背 : Pattern("日月反背", EVIL)
  object 梁馬飄蕩 : Pattern("梁馬飄蕩", EVIL)
  object 貞殺同宮 : Pattern("貞殺同宮", EVIL)
  object 殺拱廉貞 : Pattern("殺拱廉貞", EVIL)
  class 刑囚夾印(house: House) : Pattern("刑囚夾印", EVIL, "[" + house.toString() + "]")
  class 巨逢四煞(house: House) : Pattern("巨逢四煞", EVIL, "[" + house.toString() + "]")
  object 命裡逢空 : Pattern("命裡逢空", EVIL)
  object 空劫夾命 : Pattern("空劫夾命", EVIL)
  class 文星遇夾(evils: Set<EvilCombo>) : Pattern("文星遇夾", EVIL, evils.joinToString(","))
  class 刑忌夾印(house: House) : Pattern("刑忌夾印", EVIL, "[" + house.toString() + "]")
  object 馬落空亡 : Pattern("馬落空亡", EVIL)
  object 兩重華蓋 : Pattern("兩重華蓋", EVIL)
  object 祿逢衝破 : Pattern("祿逢衝破", EVIL)
  object 泛水桃花 : Pattern("泛水桃花", EVIL)
  object 天梁拱月 : Pattern("天梁拱月", EVIL)
  object 財與囚仇 : Pattern("財與囚仇", EVIL)
  object 火入泉鄉 : Pattern("火入泉鄉", EVIL)
  object 祿逢兩煞 : Pattern("祿逢兩煞", EVIL)
  class 君子在野(evils: Set<EvilCombo>) : Pattern("君子在野", EVIL, evils.joinToString(","))

  companion object {

    fun values(): List<IPattern> = listOf(
      p極向離明, p紫府同宮, p紫府朝垣, p天府朝垣, p府相朝垣, p巨機同宮, p善蔭朝綱, p機月同梁, p日月照壁, p日麗中天,
      p日月夾命, p君臣慶會, p日月同宮, p日月並明, p日照雷門, p陽梁昌祿, p明珠出海, p巨日同宮, p貪武同行, p將星得地,
      p七殺朝斗, p雄宿朝垣, p對面朝天, p科名會祿, p科權逢迎, p祿合鴛鴦, p雙祿朝垣, p三奇加會, p祿馬交馳, p月朗天門,
      p月生滄海, p石中隱玉, p壽星入廟, p英星入廟, p機梁加會, p文桂文華, p文梁振紀, p魁鉞拱命, p紫府夾命, p左右同宮,
      p丹墀桂墀, p甲第登庸, p化星返貴, p天乙拱命, p坐貴向貴, p廉貞文武, p星臨正位, p輔拱文星, p三合火貪, p三合鈴貪,

      p馬頭帶劍, p極居卯酉, p命無正曜, p羊陀夾命, p火鈴夾命, p羊陀夾忌, p風流綵杖, p巨機化酉, p日月反背, p梁馬飄蕩,
      p貞殺同宮, p殺拱廉貞, p刑囚夾印, p巨逢四煞, p命裡逢空, p空劫夾命, p文星遇夾, p刑忌夾印, p馬落空亡, p兩重華蓋,
      p祿逢衝破, p泛水桃花, p天梁拱月, p財與囚仇, p火入泉鄉, p祿逢兩煞, p君子在野
                                         )
  }
}