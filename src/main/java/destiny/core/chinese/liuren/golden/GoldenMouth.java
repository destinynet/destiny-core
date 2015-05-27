/**
 * Created by smallufo on 2015-05-23.
 */
package destiny.core.chinese.liuren.golden;

import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.StemBranchUtils;

import java.io.Serializable;

/**
 * 六壬金口訣，核心資料結構
 */
public class GoldenMouth implements Serializable {

  /** 八字 */
  private final EightWords ew;

  /** 地分 */
  private final Branch direction;

  /** 月將（太陽星座） */
  private final Branch monthSign;

  /** 貴神 */
  private final StemBranch benefactor;

  public GoldenMouth(EightWords ew, Branch direction, Branch monthSign, StemBranch benefactor) {
    this.ew = ew;
    this.direction = direction;
    this.monthSign = monthSign;
    this.benefactor = benefactor;
  }

  /**
   * 取得「人元」 : 演算法如同「五鼠遁時」法
   * 甲己還是甲 乙庚丙作初
   * 丙辛起戊子 丁壬庚子辰
   * 戊癸壬子頭 時元從子推
   */
  public Stem getHuman() {
    return StemBranchUtils.getHourStem(ew.getDayStem() , direction);
  }

  /** 取得「貴神」 */
  public StemBranch getBenefactor() {
    return benefactor;
  }


  /**
   * 取得「將神」 : 從時辰開始，順數至「地分」
   */
  public StemBranch getJohnson() {
    // 從「地分」領先「時辰」多少
    int steps = direction.getAheadOf(ew.getHourBranch());
    System.out.println("地分 " + direction + " 領先時辰 " + ew.getHourBranch() + "  " + steps + " 步");

    // 接下來，將月將 加上此 step
    Branch branch = monthSign.next(steps);

    Stem stem = StemBranchUtils.getHourStem(ew.getDayStem() , monthSign.next(steps));
    System.out.println("月將 = " + monthSign + " , 加上 " + steps + " 步 , 將神地支 = " + branch + " , 天干為 " + stem);
    return StemBranch.get(stem , branch);
  }


  /**
   * 取得「地分」
   */
  public Branch getDirection() {
    return direction;
  }

  /**
   * 取得八字
   */
  public EightWords getEightWords() {
    return ew;
  }

  /** 取得「月將」的中文稱謂
   *
   * @see <a href="http://zh.wikipedia.org/wiki/十二月將">十二月將</a>
   * */
  public static String getName(Branch branch) {
    switch (branch) {
      case 子 : return "神後"; // 神後者，古之媒氏也。本齊國人，姓賈，字仲獄，欺詐取財而死於十二月子日，除為神後。知人婚姻陰私事。天乙臨，主貴人接引之喜。騰蛇婦女悲泣，朱雀凶喪信息，六合主成合交易，勾陳絕嗣鬥訟。青龍望貴求財喜。天後婚姻成，百事吉。太陰同，玄武文狀怪現。太常失財，先凶後吉。白虎望遠信到家喜。天空主田土凶，多虛少實。
      case 丑 : return "大吉"; // 大吉者，古之牛圈也。本鄭國人，姓鄭，字季賢，病死十一月丑日，除為大吉。知人年命憂喜、六畜、田宅、口舌事。天乙臨求貴，祿直至，百事吉。騰蛇臨之百事喜，朱雀亦然，六合爭訟凶。勾陳男盜女奸，凶。青龍求晉官及財喜。天后陰病暗昧凶，太陰陰權財帛進人口。玄武陰謀鬥訟凶。太常陰財喜，白虎盜賊失財及四足。天空臨之，主四季相會，主殺害之凶，亦主鬥訟。
      case 寅 : return "宮曹"; // 功曹者，古之太史。宋國人，姓孟，字仲賢，欺客印死於十月寅日，除為功曹。知人官事、口舌、文字、信息。天乙加臨，主印信之喜。騰蛇主驚憂，後喜生女也，朱雀遠信、火光，六合婚姻不成，勾陳婦女爭訟，青龍本位大吉，天后婦女婚姻，太陰同天後，玄武財喜不失，太常陰財破，白虎入家凶，天空鬥訟虛詐。
      case 卯 : return "太沖"; // 太衝者，古之盜人也。本秦國人，姓姜，字漢陽，撓擾村邑，為盜門戶死九月卯日，除為太衝。知人本命盜賊，門戶分張事。天乙臨門，貴人得財吉。騰蛇火光文字、官事，朱雀同騰蛇，六合主成就婚姻，勾陳陰訟田宅，青龍臨門立有喜至。天後主婚姻，百事成。太陰同天後，玄武盜財，發動得財之喜。太常外得陰財，須主孝順。白虎傷人凶，外出失財及訟也。天空求事不成，外事勾連。
      case 辰 : return "天罡"; // 酉月以辰為月將，堅剛肅殺之意，天罡是土神，其形為面圓而須多，其色黃，古之獄師也。味甘，其數五。
      case 巳 : return "太乙"; // 申月以巳為將，萬寶成熟之意，故名太乙，是火神，外形為額髙口大，面有斑點，古之鍛人也。味苦，其數四。
      case 午 : return "勝光"; // 未月以午為月將，焰火不息之像故名勝光，是火神，眼雖小，但顏面大，色紅，味苦，其數九，古之御馬人。
      case 未 : return "小吉"; // 午月以未為月將，萬物小成之意，故稱小吉，是土神，外形光澤，色黃，味甘，其數為八，古之藥師。
      case 申 : return "傳送"; // 巳月以申為月將，傳陰送陽之意，傳送是金神，古形為項短，目僻，色黑白，古之行人也。味辛，其數四，古人之形。
      case 酉 : return "從魁"; // 從魁，古之亡徒也。本燕國人，姓孟，字仲任。逃亡客死三月酉日，除為從魁。知人年命、陰私、囚死之事。天乙臨，因人得貴立至喜。騰蛇悲泣臨門凶，朱雀遠信臨門凶，六合婚姻成就，勾陳娼婦臨門凶，青龍財帛臨門喜。天后臨，婦人生產，陰人主家。太陰同天後。玄武盜賊失財，男盜女奸，徒刑為兵。太常陰人常處財喜。白虎凶喪臨門立至。天空臨門為骸骨神，主大葬。
      case 戌 : return "河魁"; // 河魁者，古之亡奴也。本晉國人，姓郭字太宅，病死二月戌日，除為河魁。知人田宅骸骨事也。天乙加臨犯煞凶，騰蛇朱雀陰訟為盜凶。六合爭骸骨，或爭墳墓。勾陳奴僕殺害，亦主訟。青龍貴人帶犬入家凶，天後主悲泣，太陰陰葬喜，玄武盜賊軍兵訟凶，常陰財凶。白虎刀兵斬殺重喪凶，天空鬥訟凶。
      case 亥 : return "登明"; // 登明者，古之獄吏也。本魯國人，姓韓字燕，七坐賊獄，死正月亥日，除為登明。知人縣官、田宅、徵召事。天乙臨，貴人田宅訟。騰蛇信息患病事，朱雀同騰蛇。六合交易喜，及婚姻求就吉。勾陳陰人爭訟田土。青龍得財，望貴人。天后陰權婚成，百事大吉。太陰同天後。玄武鬼怪、盜賊不害。太常悲泣，白虎道路、病符皆無害。天空牢獄凶。
      default: throw new AssertionError("error : " + branch);
    }
  }

}
