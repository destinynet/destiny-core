package destiny.core

import destiny.core.EventCategory.*
import kotlinx.serialization.Serializable


/**
 * 生命領域 —— [EventType] 的上層分組，**純粹為了表單好選**。
 *
 * 四五十個選項攤平在下拉選單裡很傷眼，所以摺疊成幾組。它的職責到此為止。
 *
 * ## 不得進入任何餵給 LLM 的出口
 *
 * 事件類型該怎麼歸群，**依當事人的處境而定**：選舉中的人會把 `COMPETITION_WIN` 與
 * `MAJOR_ACHIEVEMENT`、`PROMOTION_APPOINTMENT` 看成一件事；商人則會把 `COMPETITION_WIN`
 * 與 `MAJOR_FINANCIAL_GAIN`、`CAREER_SETBACK` 放在一起。這兩種歸法在本 enum 裡分屬不同組，
 * 而它們都是對的 —— 一個固定的切法只能服務其中一種。
 *
 * 因此把本 enum 交給判讀端，等於塞給它一個未經驗證的先驗，還讓它看起來像事實。
 * 判讀端只該看見 [EventType]，歸群由它依當時的幾何與處境自行決定、自行命名。
 */
enum class EventCategory {
  /** 感情與婚姻 */
  ROMANCE_AND_MARRIAGE,

  /** 家庭與家族 */
  FAMILY_AND_RELATIVES,

  /** 事業與學業 */
  CAREER_AND_ACADEMICS,

  /** 財務與資產 */
  FINANCE_AND_ASSETS,

  /** 健康與意外 */
  HEALTH_AND_ACCIDENTS,

  /** 名聲與人際 */
  REPUTATION_AND_RELATIONSHIPS,

  /** 心靈與宗教 */
  SPIRITUAL_AND_RELIGIOUS,

  /** 其他 */
  OTHERS,
}

/**
 * 定義所有可選的具體生命事件。
 *
 * **命名慣例：`<領域>_<變化>`** —— 領域在前、變化在後（`RELATIONSHIP_START` /
 * `CAREER_SETBACK` / `FREEDOM_LOSS`）。新增項目一律照此，不要寫成動詞開頭或 `_OR_` 並列。
 * 早期成員有幾個不合此規（`VICTIM_OF_ATTACK`、`GOING_VIRAL`、`MEET_KEY_MENTOR`、
 * `START_HIGHER_EDUCATION`），保留原名以免動到既有素材，不作為後續的範本。
 *
 * **語態預設為第一人稱受方**：`BETRAYAL` 即「我遭背叛」。少數以加害者為主詞者
 * （[VIOLENT_OFFENDER]）因為打破了這個預設，才在名稱上標示出來。
 *
 * **好壞不由型別承載**：類型只說「發生了什麼」，好壞交給 `AbstractEvent.sentiment`。
 * 早期成員有不少把價值判斷寫進名稱（`MAJOR_ACHIEVEMENT`、`CAREER_SETBACK`…），
 * 新增項目應盡量以「是否出於己意」「發生了什麼變化」這類可觀察的判準畫界線。
 *
 * @param category 事件所屬的類別
 */
@Serializable
enum class EventType(
  val category: EventCategory
) {
  // ── 感情與婚姻 ──

  /** 開始重要戀情 */
  RELATIONSHIP_START(ROMANCE_AND_MARRIAGE),

  /** 訂婚／結婚 */
  ENGAGEMENT_MARRIAGE(ROMANCE_AND_MARRIAGE),

  /** 分手／離婚 */
  RELATIONSHIP_END(ROMANCE_AND_MARRIAGE),

  /** 外遇／關係危機 */
  RELATIONSHIP_CRISIS(ROMANCE_AND_MARRIAGE),

  // ── 家庭與家族 ──

  /** 生子／收養子女 */
  CHILD_BIRTH(FAMILY_AND_RELATIVES),

  /** 親人亡故 */
  FAMILY_LOSS(FAMILY_AND_RELATIVES),

  /** 家庭衝突 */
  FAMILY_CONFLICT(FAMILY_AND_RELATIVES),

  /** 搬家／移民 */
  RELOCATION_IMMIGRATION(FAMILY_AND_RELATIVES),

  // ── 事業與學業 ──

  /** 進入高等教育 */
  START_HIGHER_EDUCATION(CAREER_AND_ACADEMICS),

  /** 畢業 */
  GRADUATION(CAREER_AND_ACADEMICS),

  /**
   * 學業挫敗 —— 落榜、重要考試失利、被當／退學、論文未過、學位未取得。
   * 與 [CAREER_SETBACK] 同粒度，差別只在領域。
   *
   * **只收非自願者**：為創業或轉行而主動休退學不是挫敗，走 [CAREER_CHANGE]。
   * 界線畫在「是否出於己意」而非「後果好壞」—— 好壞留給 sentiment 表態。
   */
  ACADEMIC_SETBACK(CAREER_AND_ACADEMICS),

  /** 創業 */
  ENTREPRENEURSHIP(CAREER_AND_ACADEMICS),

  /** 升遷／獲得重要職位 */
  PROMOTION_APPOINTMENT(CAREER_AND_ACADEMICS),

  /** 重大成就／得獎 */
  MAJOR_ACHIEVEMENT(CAREER_AND_ACADEMICS),

  /** 事業挫敗 */
  CAREER_SETBACK(CAREER_AND_ACADEMICS),

  /** 失業／資遣 */
  UNEMPLOYMENT_LAYOFF(CAREER_AND_ACADEMICS),

  /** 轉換跑道 */
  CAREER_CHANGE(CAREER_AND_ACADEMICS),

  /** 下台／請辭 */
  STEP_DOWN_RESIGN(CAREER_AND_ACADEMICS),

  /** 退休 */
  RETIREMENT(CAREER_AND_ACADEMICS),

  // ── 財務與資產 ──

  /** 財務收入／重大投資回報 */
  MAJOR_FINANCIAL_GAIN(FINANCE_AND_ASSETS),

  /** 破產／重大虧損 */
  MAJOR_FINANCIAL_LOSS(FINANCE_AND_ASSETS),

  /** 繼承遺產／大筆獲利 */
  INHERITANCE_WINDFALL(FINANCE_AND_ASSETS),

  /** 買房 */
  REAL_ESTATE_PURCHASE(FINANCE_AND_ASSETS),

  /** 賣房 */
  REAL_ESTATE_SALE(FINANCE_AND_ASSETS),

  // ── 健康與意外 ──

  /** 確診重大疾病 */
  MAJOR_ILLNESS(HEALTH_AND_ACCIDENTS),

  /** 大型手術 */
  MAJOR_SURGERY(HEALTH_AND_ACCIDENTS),

  /**
   * 長期療程 —— 需持續數週以上、且會取代日常生活的醫療處置。
   * 洗腎、化療、放療、長期復健、住院戒治、精神科住院、安寧療護。
   *
   * 判準是「**生活被治療接管**」，不是病情輕重，也不是療程有沒有效。
   * 單次或短期處置不屬此列（門診、一個療程的口服藥、單次醫美），
   * 那類多半根本不該進生命事件時間軸。
   *
   * ## 與鄰近四個值的界線
   *
   * | 值 | 那一天實際發生的事 | 性質 |
   * |---|---|---|
   * | [MAJOR_ILLNESS] | **知道了** —— 診斷落地 | 資訊事件，單日 |
   * | [MAJOR_SURGERY] | **被處置了** —— 一次性介入 | 單次事件，單日 |
   * | 本值 | **生活被接管了** —— 療程開始 | 期間的起點 |
   * | [HEALTH_RECOVERY] | **好了** —— 狀態回復 | 狀態 |
   * | [FREEDOM_LOSS] | **被關起來了** | 期間的起點，非自願 |
   *
   * 這五個**正交**，同一段病程可以各記一筆（確診 → 手術 → 療程開始 → 療程結束 → 追蹤無復發），
   * 五個日期各有各的盤。壓成一兩筆就分不出是哪一天。
   *
   * 兩條容易踩錯的：
   *
   * - **非自願的收容／戒治走 [FREEDOM_LOSS]**（該值的 KDoc 已涵蓋「強制收容／住院」）。
   *   本值留給自願入院或至少未被剝奪行動自由者 —— 兩者互補而非重疊。
   * - **敘述以疾病為主詞者（「因某病住院」）歸 [MAJOR_ILLNESS]**；本值適用於敘述以療程本身
   *   為主詞者（「開始洗腎」「進入復健」）。界線畫在可觀察的動作上，與命名慣例一致。
   *
   * ## 表達方式
   *
   * 有起有訖的療程請以 `PeriodEvent` 表達，`to` 即療程結束 ——
   * **療程結束不等於痊癒**（出院後復發是常態），所以不要把 `to` 另記成一筆 [HEALTH_RECOVERY]，
   * 那會把負向結局記成正向事件。同理，只知道「那段時間慢慢好轉」而指不出日期時，
   * 用 `PeriodEvent` 的區間表達，不要為 [HEALTH_RECOVERY] 編一個日期。
   *
   * ## 為什麼沒有 `_<變化>` 尾綴
   *
   * 它指的是一段期間而非一個點狀變化，故不取 `_START` / `_END`；
   * 期間的兩端由 `PeriodEvent` 的 `from` / `to` 承載，不必再拆成兩個型別。
   */
  LONG_TERM_MEDICAL_TREATMENT(HEALTH_AND_ACCIDENTS),

  /**
   * 健康恢復 —— **只在恢復本身有明確日期時使用**：醫師宣告痊癒／緩解、追蹤確認無復發、
   * 療程完成之認定。這類是特定一天發生、可觀察、非自選的事件。
   *
   * 若只知道「那段時間慢慢好了」，指不出哪一天，則以
   * [LONG_TERM_MEDICAL_TREATMENT] 的 `PeriodEvent` 區間表達，**不要編一個日期**。
   * 出院日亦然 —— 那是該 `PeriodEvent` 的 `to`，不是獨立的一筆恢復事件。
   */
  HEALTH_RECOVERY(HEALTH_AND_ACCIDENTS),

  /** 重大意外（非人為蓄意） */
  SERIOUS_ACCIDENT(HEALTH_AND_ACCIDENTS),

  /** 遭攻擊／受害 */
  VICTIM_OF_ATTACK(HEALTH_AND_ACCIDENTS),

  // ── 名聲與人際 ──

  /** 名聲危機 */
  REPUTATION_CRISIS(REPUTATION_AND_RELATIONSHIPS),

  /** 名譽恢復 */
  REPUTATION_RESTORED(REPUTATION_AND_RELATIONSHIPS),

  /** 爆紅 */
  GOING_VIRAL(REPUTATION_AND_RELATIONSHIPS),

  /**
   * 私密失守 —— **非自願地失去對自身資訊的控制權**：病歷／診斷外洩、遭他人出櫃、
   * 私密影像外流、住址身分被起底、家人的事被翻出來。
   *
   * 判準是「是否出於己意」，**與揭露的內容可不可歸責無關** —— 這正是它與
   * [REPUTATION_CRISIS] 的分野。中風被媒體揭破不是名聲危機，當事人沒做錯任何事。
   *
   * ## 與鄰近四個值的界線
   *
   * | 值 | 那一天實際發生的事 | 可觀察判準 |
   * |---|---|---|
   * | [REPUTATION_CRISIS] | 對此人的**評價**崩塌 | 揭露的是此人的**作為**，且被認為可歸責 |
   * | 本值 | 此人的**私密被迫公開** | 揭露的是**處境／狀態**，非自願；可歸責與否不論 |
   * | [GOING_VIRAL] | 曝光**量**暴增 | 只講量，不表態自願與否 |
   * | [BETRAYAL] | 信任圈內的人反手 | 加害者在信任範圍內；本值不預設洩漏者是誰 |
   * | [PUBLIC_CONFRONTATION] | 此人**主動**公開對抗 | 主詞是此人自己 |
   *
   * 分野落在「**揭露的是作為，還是處境**」—— 可觀察，不必判斷道不道德。
   *
   * 與 [REPUTATION_CRISIS] **正交**，同一天可以各記一筆：「家醜外揚」多半兩者皆成立
   * （私密被揭 ＋ 評價受損），拆成兩筆才分得出哪一項在盤上有反應。
   *
   * ## 為什麼名稱不帶價值判斷
   *
   * 依本 enum 的「好壞不由型別承載」，價性交給 `sentiment` —— 而本值的價性**真的會兩邊跑**：
   * 病歷外洩多半 NEGATIVE，被迫出櫃卻有人事後記為 MIXED 甚至 POSITIVE。
   * 若命名成「醜事被揭」之類，那些個案就標不上來。
   *
   * ## 為什麼沒有對稱項
   *
   * [REPUTATION_CRISIS]／[REPUTATION_RESTORED] 與 [FREEDOM_LOSS]／[FREEDOM_RESTORED] 都成對，
   * 讀到這裡會預期第三對 —— **刻意沒有**。名譽可以平反、自由可以釋放，
   * 但已經公開的事實收不回去，「私密恢復」找不到對應的可觀察事件。
   */
  PRIVACY_LOSS(REPUTATION_AND_RELATIONSHIPS),

  /** 法律危機 */
  LEGAL_CHALLENGE(REPUTATION_AND_RELATIONSHIPS),

  /** 有利的法律結果 */
  FAVORABLE_LEGAL_OUTCOME(REPUTATION_AND_RELATIONSHIPS),

  /**
   * 失去人身自由 —— 入獄服刑、羈押、綁架、非法監禁、軟禁、強制收容／住院。
   *
   * 判準是「行動自由被剝奪」，**不預設有無暴力**，故與 [VICTIM_OF_ATTACK] 正交：
   * 遭綁架又受傷者兩筆都記。也不等同 [LEGAL_CHALLENGE] —— 訴訟纏身不必然失去自由，
   * 而軟禁與綁架根本不經法律程序。
   */
  FREEDOM_LOSS(REPUTATION_AND_RELATIONSHIPS),

  /** 重獲自由／釋放 —— [FREEDOM_LOSS] 的對稱項（命名對照 [REPUTATION_CRISIS] / [REPUTATION_RESTORED]） */
  FREEDOM_RESTORED(REPUTATION_AND_RELATIONSHIPS),

  /** 競逐得勝 */
  COMPETITION_WIN(REPUTATION_AND_RELATIONSHIPS),

  /** 競逐失利（如選舉、商業奪權） */
  COMPETITION_LOSS(REPUTATION_AND_RELATIONSHIPS),

  /** 公開對抗／控訴 */
  PUBLIC_CONFRONTATION(REPUTATION_AND_RELATIONSHIPS),

  /** 與摯友／夥伴決裂 */
  FALLING_OUT(REPUTATION_AND_RELATIONSHIPS),

  /**
   * 遭背叛／被出賣／遭小人陷害 —— 單向且在暗處：對方仍在信任範圍內卻反手，
   * 或從未浮上檯面就在扯後腿。
   *
   * 與 [FALLING_OUT] 的差別在於**是否雙向且公開**：決裂是兩邊都知道翻臉了，
   * 背叛則是受方事後才知道。與 [PUBLIC_CONFRONTATION] 的差別同理。
   */
  BETRAYAL(REPUTATION_AND_RELATIONSHIPS),

  /** 和解 */
  RECONCILIATION(REPUTATION_AND_RELATIONSHIPS),

  /** 遇到重要導師 */
  MEET_KEY_MENTOR(REPUTATION_AND_RELATIONSHIPS),

  /** 暴力行為／加害他人 —— 少數以加害者為主詞者，故名稱標示出來 */
  VIOLENT_OFFENDER(REPUTATION_AND_RELATIONSHIPS),

  // ── 心靈與宗教 ──

  /** 受洗 */
  BAPTISM(SPIRITUAL_AND_RELIGIOUS),

  /** 宗教皈依 */
  CONVERSION(SPIRITUAL_AND_RELIGIOUS),

  /** 退教 */
  LEAVE_RELIGION(SPIRITUAL_AND_RELIGIOUS),

  /** 剃度出家／受戒 */
  ORDINATION_MONASTIC(SPIRITUAL_AND_RELIGIOUS),

  /** 精神覺醒 */
  SPIRITUAL_AWAKENING(SPIRITUAL_AND_RELIGIOUS),

  /** 朝聖 */
  PILGRIMAGE(SPIRITUAL_AND_RELIGIOUS),

  /** 其他重大宗教儀式 */
  RELIGIOUS_CEREMONY(SPIRITUAL_AND_RELIGIOUS),

  // ── 其他 ──

  /** 其他事件 */
  OTHERS(EventCategory.OTHERS),
}
