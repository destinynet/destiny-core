/**
 * Created by smallufo on 2018-03-06.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.iching.Symbol
import destiny.core.iching.SymbolAcquired
import kotlin.math.abs

object ChartMntRules {

  fun getChartRules(chart: IChartMnt) : List<ChartPattern> {
    val list: List<(chart: IChartMnt) -> ChartPattern?> = listOf(
      this::match10 ,
      this::beneathSameOrigin ,
      this::reversed,
      this::parentTriplet,
      this::contTriplet,
      this::robbery,
      this::pure
      )

    return list.mapNotNull {
      it.invoke(chart)
    }.toList()
  }

  /**
   * [ChartPattern.八純卦]
   * 「八純卦」在玄空風水中，屬大凶之局，指的是在玄空九個宮位中，山飛星與向飛星全部相同，
   * 八純卦屬大凶之格局，會有剩孤家一人、妻離子散的情形，家破人亡之情形，
   * 建議遇到此大凶格局的房屋，不要選擇居住於此房屋，
   * 其實此格局只有在五運屋(坐東南朝西北)或(坐西北朝東南)有兼向時才會發生。
   * 此局凶暴不宜，且無化解之法，因此避之方為上策。
   */
  fun pure(chart: IChartMnt): ChartPattern.八純卦? {
    return chart.blocks.all { block ->
      block.mnt == block.dir
    }.let {
      if (it)
        ChartPattern.八純卦
      else
        null
    }
  }

  /** 七星打劫 */
  fun robbery(chart: IChartMnt): ChartPattern.七星打劫? {
    return chart.getMntDirSpec()
      ?.takeIf { it === MntDirSpec.雙星到向 }
      ?.takeIf { beneathSameOrigin(chart) == null } // 去除伏吟 6局
      ?.let {
      val set147 = listOf(1, 4, 7)
      val set258 = listOf(2, 5, 8)
      val set369 = listOf(3, 6, 9)

      val sets = listOf(set147, set258, set369)
      val set: List<Int> = sets.first { list -> list.contains(chart.period) }

      val 離乾震 = listOf(Symbol.離, Symbol.乾, Symbol.震)
      val 離宮ints: Set<Int> = 離乾震
        .map { symbol -> chart.getChartBlockFromSymbol(symbol) }
        .map { chartBlock -> chartBlock.dir }
        .toSet()

      val 坎兌巽 = listOf(Symbol.坎, Symbol.兌, Symbol.巽)
      val 坎宮ints = 坎兌巽
        .map { symbol -> chart.getChartBlockFromSymbol(symbol) }
        .map { chartBlock -> chartBlock.dir }
        .toSet()

      when {
        離宮ints.containsAll(set) -> {
          val map = 離乾震.map { symbol -> symbol to chart.getChartBlockFromSymbol(symbol).dir }.toMap()
          ChartPattern.七星打劫(Symbol.離, map)
        }
        坎宮ints.containsAll(set) -> {
          val map = 坎兌巽.map { symbol -> symbol to chart.getChartBlockFromSymbol(symbol).dir }.toMap()
          ChartPattern.七星打劫(Symbol.坎, map)
        }
        else -> null
      }
    }
  }


  /**
   * 連珠三般卦是指運盤中每宮山星、向星和運星所代表的三個數字是互相連貫的三個數字，
   * 共有九種情況，分別為一二三、二三四、三四五、四五六、五六七、六七八、七八九、八九一、九一二。
   *
   * 中州派玄空學裏對連珠三般卦是凶局而論，又稱此種格局為連茹格，
   * 連茹者即連根撥起之意，所以連茹格並非真正的連珠卦（而真的連珠卦是指父母三般卦）
   *
   * 此星盤格局有一個特殊的性質，一般均主前吉後凶，一發財旋之即敗。
   * 即剛搬遷入此格局之宅，吉事不斷，但剛一發財，凶事必然隨之而來。
   * 所以連茹卦的特點須注意，切不可圖一時之吉，遺終身之凶！這是連茹三般卦的一個重要性質。
   *
   * 但同時須于排龍訣互相應用。
   *
   * 若排吉龍吉得此局，形巒配合，運內亦許發吉，但運退後即主大敗其財；
   * 若排凶龍，形巒相背，則必主人財二絕。
   */
  fun contTriplet(chart: IChartMnt): ChartPattern.連珠三般卦? {
    fun distance(v1: Int, v2: Int): Int {
      return abs(v1 - v2).let { abs ->
        when {
          abs <= 6 -> abs
          abs == 8 -> 1 // 1 & 9
          else -> 2 // 7(1,8 or 2,9)
        }
      }
    }

    return Symbol.values().all {
      chart.getChartBlockFromSymbol(it).let { block ->
        distance(block.period, block.mnt) <= 2
          && distance(block.period, block.dir) <= 2
          && distance(block.mnt, block.dir) <= 2
      }
    }.let {
      if (it) ChartPattern.連珠三般卦 else null
    }
  }

  /**
   * 父母三般卦又稱天地父母三般卦，
   * 指運盤中每宮山星、向星和運星所代表的三個數字是一四七、二五八、三六九這三組數字的組合。
   *
   * 「父母三般卦」的出現，意味著宅命盤中每宮的元運可以貫通上、中、下三元，一般認為是吉象，
   * 其主要性質是旺財，主出巨富，但不主顯貴，或富大於貴，適於經商之用。
   *
   * 但需要注意的是，「父母三般卦」必是 [MntDirSpec.上山下水] 的格局
   * 因此，在「父母三般卦」局中，向星的當令星所在方位必須有水加持，才能使吉運生效，否則有可能出現由吉變凶的情況。
   */
  fun parentTriplet(chart: IChartMnt): ChartPattern.父母三般卦? {
    val set1 = setOf(1, 4, 7)
    val set2 = setOf(2, 5, 8)
    val set3 = setOf(3, 6, 9)

    return Symbol.values().all {
      val blockNums: Set<Int> = chart.getChartBlockFromSymbol(it).let { chartBlock ->
        setOf(chartBlock.period, chartBlock.mnt, chartBlock.dir)
      }
      blockNums.containsAll(set1) || blockNums.containsAll(set2) || blockNums.containsAll(set3)
    }.let {
      if (it) ChartPattern.父母三般卦 else null
    }
  }

  /**
   * 全局合十 :
   * 各宮得天盤 與 山盤(或向盤) 相加都為 10
   *
   * 範例：
   * 七運，子山午向 , 山盤 與 天盤 相加 均為 10
  ４１　８６　６８
  巽六　離二　坤四
  　　　　　　　　
  ５９　３２　１４
  震五　中七　兌九
  　　　　　　　　
  ９５　７７　２３
  艮一　坎三　乾八
   *  */
  fun match10(chart: IChartMnt): ChartPattern.合十? {
    return when {
      chart.blocks.all { block -> block.period + block.mnt == 10 } -> ChartPattern.合十(MntDir.山)
      chart.blocks.all { block -> block.period + block.dir == 10 } -> ChartPattern.合十(MntDir.向)
      else -> null
    }
  }


  /**
   * 全局伏吟 (元旦盤) :
   * 五入中 順飛 , 形成 與後天八卦 相同的盤勢， 稱為 伏吟
   * 例如：
   *
  7 運 , 庚山 甲向 , 向盤分佈 呈現與洛書相同 , 故 , 全局伏吟(元旦盤)

  ８４　４９　６２
  巽六　離二　坤四
  　　　　　　　　
  ７３　９５　２７
  震五　中七　兌九
  　　　　　　　　
  ３８　５１　１６
  艮一　坎三　乾八
   * */
  fun beneathSameOrigin(chart: IChartMnt): ChartPattern.伏吟元旦盤? {
    return when {
      chart.blocks.all { block -> SymbolAcquired.getSymbolNullable(block.mnt) == block.symbol } ->
        ChartPattern.伏吟元旦盤(MntDir.山)
      chart.blocks.all { block ->
        SymbolAcquired.getSymbolNullable(block.dir) == block.symbol
      } -> ChartPattern.伏吟元旦盤(
        MntDir.向
      )
      else -> null
    }
  }


  /**
   * 全局反吟 : (山或向) 五入中 , 逆飛 , 形成 山盤（或向盤）全局反吟
   * 例如：
   *
  七運，卯山酉向 , 山盤 5入中 , 逆飛 , 形成 山盤與洛書盤對沖

  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八

   TODO : 另一種反吟 : 山飛星與向飛星對角合十，亦是為「反吟」的格局
   詳見 http://www.grand-tao.org/wap/xuanzheninfo.php?id=15
   * */
  fun reversed(chart: IChartMnt): ChartPattern.反吟? {
    return chart.getCenterBlock().let { cb ->
      val block巽 = chart.getChartBlockFromSymbol(Symbol.巽)
      if (cb.mnt == 5 && block巽.mnt == 6)
        ChartPattern.反吟(MntDir.山)
      else if (cb.dir == 5 && block巽.dir == 6)
        ChartPattern.反吟(MntDir.向)
      else
        null
    }
  }


  // ================================ 單宮 ================================

  /**
   * 單宮合十
   */
  fun match10(chartBlock: ChartBlock): BlockPattern.合十? {
    return chartBlock.period.let {
      when {
        it + chartBlock.mnt == 10 -> BlockPattern.合十(MntDir.山)
        it + chartBlock.dir == 10 -> BlockPattern.合十(MntDir.向)
        else -> null
      }
    }
  }

  /**
   * 單宮伏吟(元旦盤) :
   * 山盤 或 向盤 飛星數 與 座落宮位的洛書數相同
   *
   * 七運，卯山酉向

  |--山盤伏吟(這裡不討論) , 由 [beneathSameSky] 實作
  V
  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７ <-- 7 即為 兌宮洛書數， 伏吟(元旦盤)
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八  <-- 向盤 伏吟 (這裡不討論) , 由 [beneathSameSky] 實作

   */
  fun beneathSameOrigin(chartBlock: ChartBlock): BlockPattern.伏吟元旦盤? {
    return chartBlock.let {
      when {
        SymbolAcquired.getSymbol(it.mnt) === it.symbol -> BlockPattern.伏吟元旦盤(MntDir.山)
        SymbolAcquired.getSymbol(it.dir) === it.symbol -> BlockPattern.伏吟元旦盤(MntDir.向)
        else -> null
      }
    }
  }

  /**
   * 單宮伏吟(天盤) :
   * 天盤的數字與 山盤 或 向盤 飛星相同
   *
   * 七運，卯山酉向

  |--山盤伏吟
  V
  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７ <-- 7 即為 兌宮洛書數，這裡也是另一種伏吟 , 由 [beneathSameOrigin] 實作
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八  <-- 向盤 伏吟

   * */
  fun beneathSameSky(chartBlock: ChartBlock): BlockPattern.伏吟天盤? {
    return chartBlock.let {
      when (it.period) {
        it.mnt -> BlockPattern.伏吟天盤(MntDir.山)
        it.dir -> BlockPattern.伏吟天盤(MntDir.向)
        else -> null
      }
    }
  }


  /**
   * 單宮 反吟 (元旦盤) :
   * 某宮位內， 山 或 向 的飛星與元旦盤（洛書數）合十
   *
   * 八運，卯山酉向
   *
  　　　|-- 山星 1 + 離(9) = 10 , 山盤 反吟
  　　　v
  ５２　１６　３４
  巽七　離三　坤五
  　　　　　　　　
  ４３　６１　８８
  震六　中八　兌一
  　　　　　　　　
  ９７　２５　７９
  艮二　坎四　乾九
   */
  fun reversed(block: ChartBlock): BlockPattern.反吟元旦盤? {
    return block.symbol?.let { symbol ->
      when {
        (SymbolAcquired.getIndex(symbol) + block.mnt) == 10 -> BlockPattern.反吟元旦盤(MntDir.山)
        (SymbolAcquired.getIndex(symbol) + block.dir) == 10 -> BlockPattern.反吟元旦盤(MntDir.向)
        else -> null
      }
    }
  }

}
