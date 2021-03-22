package destiny.tools.converters

import destiny.core.astrology.IDayNight
import destiny.core.calendar.eightwords.IHour
import destiny.core.calendar.eightwords.IMidnight
import destiny.core.calendar.eightwords.IRisingSign
import destiny.core.chinese.IClockwise
import destiny.core.chinese.IMonthlyHexagram
import destiny.core.chinese.ITianyi
import destiny.core.chinese.IYangBlade
import destiny.core.chinese.liuren.IGeneralSeq
import destiny.core.chinese.liuren.IGeneralStemBranch
import destiny.core.chinese.onePalm.IPositive
import destiny.core.chinese.ziwei.*
import destiny.core.iching.divine.IHiddenEnergy
import destiny.core.iching.divine.ISettingsOfStemBranch

/**
 * Created by smallufo on 2020-03-11.
 */
object Domains {

  /** 子正 的實作 [IMidnight]  */
  const val KEY_MIDNIGHT = "mn"

  /** 命宮（上升星座）的實作 [IRisingSign] */
  const val KEY_RISING_SIGN = "rs"

  /** 晝夜區分 [IDayNight] */
  const val KEY_DAY_NIGHT = "dnd"

  /** 時辰切割 [IHour] */
  const val KEY_HOUR = "hour"


  /** 易卦 */
  object Divine {

    /** 納甲設定 [ISettingsOfStemBranch] */
    const val KEY_DIVINE_HEXSETTINGS = "hexSet"

    /** 伏神設定 [IHiddenEnergy] */
    const val KEY_DIVINE_HIDDEN_ENERGY = "hiddenEnergy"

    /** 貴人 [ITianyi] */
    const val KEY_DIVINE_TIANYI = "id_tianyi"

    /** 羊刃 [IYangBlade] */
    const val KEY_DIVINE_YANG_BLADE = "id_yangBlade"

    /** 12消息卦 , 冬至點開始 or 子月開始 [IMonthlyHexagram] */
    const val KEY_MONTH_HEX_IMPL = "monthHexImpl"

  }

  /** 紫微 */
  object Ziwei {

    /** 紫微星，閏月該如何處理 [IPurpleStarBranch] */
    const val KEY_PURPLE_BRANCH = "purpleBranch"

    /** 宮位名字、順序 [IHouseSeq] */
    const val KEY_HOUSE_SEQ = "houseSeq"

    /** (紫微的) 貴人 (天魁天鉞) [ITianyi] */
    const val KEY_TIANYI = "zTianyi"

    /** 四化 [ITransFour] */
    const val KEY_TRANS_FOUR = "transFour"

    /** 星體強弱 [IStrength] */
    const val KEY_STRENGTH = "strength"

    /** 紫微流年 [IFlowYear] */
    const val KEY_FLOW_YEAR = "flowYear"

    /** 紫微流月 [IFlowMonth] */
    const val KEY_FLOW_MONTH = "flowMonth"

    /** 紫微流日 [IFlowDay] */
    const val KEY_FLOW_DAY = "flowDay"

    /** 紫微流時 [IFlowHour] */
    const val KEY_FLOW_HOUR = "flowHour"

    /** 紫微起大運 [IBigRange] */
    const val KEY_BIG_RANGE = "bigRange"
  }

  /** 金口訣 */
  object Pithy {

    /** 天乙貴人 [ITianyi] */
    const val KEY_LIUREN_PITHY_TIANYI = "lp_tianyi"

    /** 貴神順推逆推 [IClockwise] */
    const val KEY_CLOCKWISE = "clockwise"

    /** 12天將順序 [IGeneralSeq] */
    const val KEY_GENERAL_SEQ = "generalSeq"

    /** 12天將干支 [IGeneralStemBranch] */
    const val KEY_GENERAL_STEM_BRANCH = "gsb"
  }

  /** 一掌經 */
  object Palm {

    /** 一掌經：順逆算法 [IPositive] */
    const val KEY_POSITIVE_IMPL = "posImpl"
  }

  /** 二十八星宿 */
  object LunarStation {

    /** 月禽 */
    object MonthImpl {
      const val KEY_GENERAL = "lsM"
      const val KEY_SELECT = "lsM_sel"
    }

    /** 時禽 */
    object HourImpl {
      const val KEY_GENERAL = "lsH"
      const val KEY_SELECT = "lsH_Sel"
    }

    const val KEY_HIDDEN_VENUS_FOE = "lsHvf"
  }
}
