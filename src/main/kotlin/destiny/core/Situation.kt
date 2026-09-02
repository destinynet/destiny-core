package destiny.core

import destiny.core.EventCategory.*
import kotlinx.serialization.Serializable


/**
 * 生命領域 —— [Situation] 的上層分組，**純粹為了表單好選**。
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
 * 判讀端只該看見 [Situation]，歸群由它依當時的幾何與處境自行決定、自行命名。
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
 * 定義所有可選的具體生命處境。
 *
 * **命名慣例：`<領域>_<變化>`** —— 領域在前、變化在後（`RELATIONSHIP_START` /
 * `CAREER_SETBACK` / `FREEDOM_LOSS`）。新增項目一律照此，不要寫成動詞開頭或 `_OR_` 並列。
 * 早期成員有幾個不合此規（[GOING_VIRAL]、[MEET_KEY_MENTOR]、[START_HIGHER_EDUCATION]），
 * 保留原名以免動到既有資料，不作為後續的範本。
 *
 * **主詞不編進名稱**：這是本 enum 取代前身 `EventType` 的理由。
 * 前身把主詞不一致地編進了型別名稱 ——「遭攻擊」與「加害他人」是同一件物理事件的兩個主詞，
 * 卻成了兩個成員、還落在兩個不同的 [EventCategory]；而「開始一段重要戀情」
 * （主動追求 vs 被追）卻沒編。同一根軸被編了兩次就會打架。
 * ⇒ 主詞一律走 [roles]（型別層：哪些主詞可能）與 `AbstractEvent.role`（事件層：這一筆是哪個）。
 *
 * **好壞不由型別承載**：類型只說「發生了什麼」，好壞交給 `AbstractEvent.sentiment`。
 * 早期成員有不少把價值判斷寫進名稱（[MAJOR_ACHIEVEMENT]、[CAREER_SETBACK]…），
 * 新增項目應盡量以「發生了什麼變化」這類可觀察的判準畫界線。
 *
 * ⚠️ **「結果」不等於「好壞」。** [COMPETITION_WIN] ＋ `sentiment = NEGATIVE`
 * 是一句成立的話：「他贏了選舉，而那毀了他」。勝敗是**結果**（可觀察，屬型別），
 * 好壞是**評價**（因人而異，屬 sentiment），兩者正交，都要保留 ——
 * 不要因為名稱看起來帶價值判斷就想把 [COMPETITION_WIN] / [COMPETITION_LOSS] 併成一個。
 */
@Serializable
enum class Situation(
  /** 事件所屬的類別 —— 只為表單摺疊選項，判讀端看不到。理由見 [EventCategory]。 */
  val category: EventCategory,
  /**
   * 這個處境**在現實中可能有哪些主詞**。
   *
   * 記的是型別層的可能性，不是某一筆事件的事實 —— 後者是 `AbstractEvent.role`。
   *
   * ## 填格原則：排除不可能，不是猜最常見
   *
   * ```
   * 另一端在現實中真的不可能  → 單值
   * 另一端只是比較少見        → 兩值，交給事件層逐筆說
   * ```
   *
   * ⚠️ **不要用「看動作是誰做的」那條舊判準** —— 它是**循環的**：
   * 「動作是什麼」取決於名稱怎麼取，而名稱是我們自己取的。
   * 照那條判準，「升遷／獲得重要職位」判成承受方（任命是別人做的）、
   * 「進入高等教育」判成發起方（就讀是他做的），儘管兩者的結構一模一樣
   * （他爭取、別人核可、他接受）。判準若隨命名擺動，就不是判準。
   *
   * ⚠️ **命名指引不再列「是否出於己意」，因為它現在就是本欄這根軸。**
   * 把它留在命名指引裡，等於邀請未來的成員再一次把主詞編進型別名稱 ——
   * 那正是本 enum 取代前身要消滅的東西，所以那一條被刪掉了（此處記下這一刀，免得它靜默）。
   *
   * 但成員 KDoc **仍可**用它來畫**型別之間**的界線 —— **前提是分流的目的地存在**：
   * [ACADEMIC_SETBACK] 把自願的那一半分流到 [CAREER_CHANGE]、
   * [LONG_TERM_MEDICAL_TREATMENT] 把非自願的那一半分流到 [FREEDOM_LOSS]。
   * 那種情形下「主動休學去創業」與「被退學」不只主詞不同、**後續的人生也不同**，
   * 是真的兩個 situation，不是同一個 situation 的兩個主詞。⇒ 兩處看似矛盾，實則分工。
   *
   * ⚠️ **把「多半是 X」寫成「固定是 X」的代價不是措辭問題。**
   * 那一格會變成假的事實：統計端若依主詞分層，母體落在被寫死的型別上時，
   * 量到的是**型別誤差**而不是主詞的獨立效果，而且量到之後看不出來。
   * 誠實的代價只是表單多問一個問題。
   *
   * ## 值域大小的三種意義
   *
   * UI 直接讀這裡的大小決定怎麼呈現，**不要為個別成員寫特例**：
   *
   * | size | 意義 | UI |
   * |---|---|---|
   * | 2 | 兩種主詞都可能 | 二選一 ＋「說不準」 |
   * | 1 | 另一端不可能 | **唯讀顯示** —— 告訴使用者系統讀懂了這件事的性質 |
   * | 0 | 這根軸不適用 | 目前無成員（測試擋著）；出現時再定 |
   *
   * ⛔ **與 [EventCategory] 同樣不得進入任何餵給 LLM 判讀的出口。**
   * 理由略有不同：分組是沒有事實可言的切法，而本欄是有事實的先驗 ——
   * 但把先驗餵給判讀端，量到的就是它複述先驗的能力，不是它從盤上讀出了什麼。
   * 抽取端（intake）用它決定「要不要問」是可以的，判讀端一律看不到。
   */
  val roles: Set<EventRole>,
  /**
   * 這個處境可不可以出現在**預測輸出**裡。
   *
   * ⚠️ **這是產品判準，不是本體論判準。** [DEATH] 當然是人生大事，也確實可以被記錄與研究；
   * `false` 只表示「不要對使用者預測它」。
   *
   * 之所以需要這一格，是因為同一份字彙同時服務三個消費者：**輸入表單**（使用者自述）、
   * **素材抽取**（從傳記文本抓事件）、**預測輸出**（模型寫給使用者看的話）。
   * 凡是進了字彙的成員，模型都可以拿來寫預測 —— 而一個算命產品不該對使用者說
   * 「您可能會病逝」。前兩個消費者仍照用不誤。
   *
   * ⚠️ **必要條件而非充分條件。** 使用者實際讀到的是預測報告的 `headline` / `watchFor`
   * 那些自由散文，本旗標只擋得住**結構化欄位**（型別代號、schema 的 enum 值域）。
   * 散文那一半擋不到，要靠提示詞裡的明文規則。兩邊都要做，少一邊等於沒做。
   *
   * ⛔ 判讀側的清單與 schema 一律以 `Situation.entries.filter { it.forecastable }`
   * **現算**，不得手抄一份 —— 手抄的那一份不會跟著這裡改，
   * 而它失效時是靜默的（清單看起來仍然合理）。本專案已經為手抄付過代價。
   */
  val forecastable: Boolean = true,
) {
  // ── 感情與婚姻 ──

  /** 開始重要戀情 */
  RELATIONSHIP_START(ROMANCE_AND_MARRIAGE, Roles.BOTH),

  /** 訂婚／結婚 */
  ENGAGEMENT_MARRIAGE(ROMANCE_AND_MARRIAGE, Roles.BOTH),

  /** 分手／離婚 */
  RELATIONSHIP_END(ROMANCE_AND_MARRIAGE, Roles.BOTH),

  /** 外遇／關係危機 */
  RELATIONSHIP_CRISIS(ROMANCE_AND_MARRIAGE, Roles.BOTH),

  // ── 家庭與家族 ──

  /** 生子／收養子女 */
  CHILD_BIRTH(FAMILY_AND_RELATIVES, Roles.BOTH),

  /** 親人亡故 */
  FAMILY_LOSS(FAMILY_AND_RELATIVES, Roles.RECIPIENT_ONLY),

  /**
   * 親屬的重大健康危機 —— **未亡故**：父母中風、配偶確診癌症、子女重傷住院。
   *
   * [FAMILY_LOSS] 只收亡故，而「親人病重但還在」是生命敘事裡份量極重、
   * 且往往持續數年的一格。本值出現之前這一格無處可放，只能誤標成 [MAJOR_ILLNESS] ——
   * 但那是**本人**的病，兩者在盤上不該混為一談。
   *
   * 親人**過世**請改記 [FAMILY_LOSS]；若病危與過世相隔甚遠，兩筆都記。
   */
  FAMILY_HEALTH_CRISIS(FAMILY_AND_RELATIVES, Roles.RECIPIENT_ONLY),

  /** 家庭衝突 */
  FAMILY_CONFLICT(FAMILY_AND_RELATIVES, Roles.BOTH),

  /** 搬家／移民 */
  RELOCATION_IMMIGRATION(FAMILY_AND_RELATIVES, Roles.BOTH),

  // ── 事業與學業 ──

  /** 進入高等教育 */
  START_HIGHER_EDUCATION(CAREER_AND_ACADEMICS, Roles.BOTH),

  /** 畢業 */
  GRADUATION(CAREER_AND_ACADEMICS, Roles.INITIATOR_ONLY),

  /**
   * 學業挫敗 —— 落榜、重要考試失利、被當／退學、論文未過、學位未取得。
   * 與 [CAREER_SETBACK] 同粒度，差別只在領域。
   *
   * **只收非自願者**：為創業或轉行而主動休退學不是挫敗，走 [CAREER_CHANGE]。
   * 界線畫在「是否出於己意」而非「後果好壞」—— 好壞留給 sentiment 表態。
   */
  ACADEMIC_SETBACK(CAREER_AND_ACADEMICS, Roles.RECIPIENT_ONLY),

  /** 創業 */
  ENTREPRENEURSHIP(CAREER_AND_ACADEMICS, Roles.INITIATOR_ONLY),

  /** 升遷／獲得重要職位 */
  PROMOTION_APPOINTMENT(CAREER_AND_ACADEMICS, Roles.BOTH),

  /** 重大成就／得獎 */
  MAJOR_ACHIEVEMENT(CAREER_AND_ACADEMICS, Roles.BOTH),

  /** 事業挫敗 */
  CAREER_SETBACK(CAREER_AND_ACADEMICS, Roles.BOTH),

  /** 失業／資遣 */
  UNEMPLOYMENT_LAYOFF(CAREER_AND_ACADEMICS, Roles.RECIPIENT_ONLY),

  /** 轉換跑道 */
  CAREER_CHANGE(CAREER_AND_ACADEMICS, Roles.BOTH),

  /** 下台／請辭 */
  STEP_DOWN_RESIGN(CAREER_AND_ACADEMICS, Roles.BOTH),

  /** 退休 */
  RETIREMENT(CAREER_AND_ACADEMICS, Roles.BOTH),

  // ── 財務與資產 ──

  /** 財務收入／重大投資回報 */
  MAJOR_FINANCIAL_GAIN(FINANCE_AND_ASSETS, Roles.BOTH),

  /** 破產／重大虧損 */
  MAJOR_FINANCIAL_LOSS(FINANCE_AND_ASSETS, Roles.BOTH),

  /** 繼承遺產／大筆獲利 */
  INHERITANCE_WINDFALL(FINANCE_AND_ASSETS, Roles.RECIPIENT_ONLY),

  /** 買房 */
  REAL_ESTATE_PURCHASE(FINANCE_AND_ASSETS, Roles.INITIATOR_ONLY),

  /** 賣房 */
  REAL_ESTATE_SALE(FINANCE_AND_ASSETS, Roles.BOTH),

  // ── 健康與意外 ──

  /** 確診重大疾病 */
  MAJOR_ILLNESS(HEALTH_AND_ACCIDENTS, Roles.RECIPIENT_ONLY),

  /**
   * 精神狀態發作 —— 憂鬱發作、躁期、恐慌發作、精神病症狀發作、創傷後反應大幅惡化。
   *
   * `EPISODE` 一詞取自精神醫學的 depressive / manic / psychotic episode，
   * 選它正是因為它**不預設輕重、不預設就醫、不帶價值判斷** ——
   * 一段從未看過醫生的重度憂鬱期與一次住院的躁期在本值裡是同一格。
   *
   * ## 與鄰近三個值的界線
   *
   * | 值 | 那一天實際發生的事 |
   * |---|---|
   * | [MAJOR_ILLNESS] | **知道了** —— 診斷落地，資訊事件 |
   * | 本值 | **狀態本身** —— 發作了，不預設有沒有就醫 |
   * | [LONG_TERM_MEDICAL_TREATMENT] | **生活被治療接管** —— 療程開始 |
   * | [FREEDOM_LOSS] | **被關起來了** —— 強制住院仍走該值 |
   *
   * 本值與 [LONG_TERM_MEDICAL_TREATMENT] **正交**，同一段病程可以各記一筆
   * （發作 → 開始治療）；壓成一筆就分不出是哪一天。
   *
   * ⭐ **自殺未遂歸本值。** [SERIOUS_ACCIDENT] 明文排除人為蓄意、[DEATH] 是既遂 ——
   * 這一格在本值出現之前是空的，而它顯然是生命事件。
   *
   * **輕重不由本值承載**：進料側寫進 `details`，判讀側交給 `ForecastWindow.intensity`。
   * 把輕重編進型別會重蹈「主詞編進名稱」的覆轍。
   *
   * ## 主詞
   *
   * - [EventRole.INITIATOR] ＝ 自傷／自殺未遂（動作是他做的）
   * - [EventRole.RECIPIENT] ＝ 被病程推著走的發作
   *
   * ⚠️ 一度被填成只有承受端，而那**與上面那顆 ⭐ 直接打架**：既然自殺未遂歸本值，
   * 發起端就不可能是「現實中不成立」；[DEATH] 那一格還明寫著「[EventRole.INITIATOR] ＝ 自殺」。
   * 同一根軸的相鄰兩格，既遂算發起、未遂算不可能 —— 那不是判準，是漏填。
   */
  MENTAL_HEALTH_EPISODE(HEALTH_AND_ACCIDENTS, Roles.BOTH),

  /** 大型手術 */
  MAJOR_SURGERY(HEALTH_AND_ACCIDENTS, Roles.BOTH),

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
   *
   * ## 主詞為什麼兩種都收（[roles] `=` 兩值）
   *
   * ⚠️ 一度被填成只有承受端（「生活被治療接管」），而那**與本 KDoc 自己打架**：
   * 上面明寫「非自願的收容／戒治走 [FREEDOM_LOSS]，**本值留給自願入院**或至少未被剝奪
   * 行動自由者」。既然非自願的那一半已經被分流出去，剩下的這一半怎麼會只可能是承受端？
   *
   * 兩種主詞都常見且在生命敘事裡份量不同：自己決定開始一段長期療程（戒治、選擇性化療方案、
   * 主動進復健）vs. 被病程推著走。⇒ 這一格由 `AbstractEvent.role` 逐筆決定。
   */
  LONG_TERM_MEDICAL_TREATMENT(HEALTH_AND_ACCIDENTS, Roles.BOTH),

  /**
   * 健康恢復 —— **只在恢復本身有明確日期時使用**：醫師宣告痊癒／緩解、追蹤確認無復發、
   * 療程完成之認定。這類是特定一天發生、可觀察、非自選的事件。
   *
   * 若只知道「那段時間慢慢好了」，指不出哪一天，則以
   * [LONG_TERM_MEDICAL_TREATMENT] 的 `PeriodEvent` 區間表達，**不要編一個日期**。
   * 出院日亦然 —— 那是該 `PeriodEvent` 的 `to`，不是獨立的一筆恢復事件。
   */
  HEALTH_RECOVERY(HEALTH_AND_ACCIDENTS, Roles.RECIPIENT_ONLY),

  /**
   * 重大意外 —— **非人為蓄意**：車禍、墜落、火災、天災、工安事故、醫療意外。
   * 蓄意者走 [VIOLENCE]。
   *
   * ## 主詞
   *
   * - [EventRole.INITIATOR] ＝ **他造成的**（酒駕撞人、操作失誤釀成工安事故）
   * - [EventRole.RECIPIENT] ＝ 他遭遇的
   *
   * ⚠️ 兩者**可以同時為真**（肇事者自己也重傷）—— 但 role 記的是**動作是誰做的**，
   * 與傷亡落在誰身上無關。同時為真時依動作端填，傷勢另記。
   *
   * ## 這一格是「排除不可能」原則的價值示範
   *
   * 它一度被填成只有承受端，而那是**舊判準加上「意外」這個名稱**的自然結果：
   * 意外聽起來就是發生在人身上的事。但問一句「另一端真的不可能嗎」就露餡了 ——
   * 「他酒駕撞死人」重大意外 ✓、非人為蓄意 ✓、當事人是動作端 ✓，
   * 而型別層卻宣告發起端不可能，於是這一格**無處可放**：標 [VIOLENCE] 是錯的（那是蓄意），
   * 只能落 [OTHERS]。傳記裡「造成一場致命事故」份量極重，往往接著
   * [LEGAL_PROCEEDING_START] 與 [FREEDOM_LOSS]，正是最不該掉進 [OTHERS] 的一類。
   *
   * 合併前身兩個暴力成員的理由（同一件物理事件的兩個主詞）對非蓄意這一半同樣成立，
   * 只是當初沒被套用過來。
   */
  SERIOUS_ACCIDENT(HEALTH_AND_ACCIDENTS, Roles.BOTH),

  /**
   * 暴力 —— 人為蓄意的身體傷害：毆打、械鬥、槍擊、性侵、家暴、戰場上的施暴與受暴。
   *
   * **取代前身的兩個成員**（「遭攻擊／受害」與「暴力行為／加害他人」）。
   * 那是同一件物理事件的兩個主詞，卻被拆成兩個型別、還落在兩個不同的 [EventCategory] ——
   * 於是「互毆」這種再普通不過的情形無處可歸，而依主詞分層的統計會把兩邊當成不同的事。
   * 主詞現在走 [roles]：本值兩端都可能，逐筆由 `AbstractEvent.role` 回答。
   *
   * ⚠️ 它被分在「健康與意外」是**表單便利，不是事實** ——
   * 加害端與健康無關。[EventCategory] 的 KDoc 已宣告分組沒有事實可言，此處只是又一例。
   *
   * 與 [FREEDOM_LOSS] **正交**（該值的判準是行動自由被剝奪，不預設有無暴力）：
   * 遭綁架又受傷者兩筆都記。非蓄意者走 [SERIOUS_ACCIDENT]。
   */
  VIOLENCE(HEALTH_AND_ACCIDENTS, Roles.BOTH),

  /**
   * 死亡。
   *
   * ## 主詞
   *
   * - [EventRole.INITIATOR] ＝ 自殺（動作是他做的）
   * - [EventRole.RECIPIENT] ＝ 病逝／意外／遭殺害
   *
   * 邊界三則：**安樂死**視同發起端（是他要求並簽署的，執行者只是代行）；
   * **拒絕治療**同樣是發起端（不作為也是選擇）；**慢性自毀**（酗酒、長期不就醫至死）
   * 是承受端 —— 那沒有一個可指認的致死動作，硬判成發起端等於把價值判斷寫進主詞。
   *
   * ⭐ **自殺未遂不在此值**，走 [MENTAL_HEALTH_EPISODE]。本值只收既遂。
   *
   * ## 為什麼 [forecastable] `= false`
   *
   * 純產品理由，與本體論無關：本值在**輸入表單**（記錄親歷者的生命終點）與
   * **素材抽取**（傳記文本的死亡日期是研究上最乾淨的一類標記）都照用不誤，
   * 只是不進**預測輸出** —— 一個算命產品不該對使用者說「您可能會病逝」。
   * 該旗標的完整說明見其 KDoc，特別是「它只擋得住結構化欄位」那一段。
   */
  DEATH(HEALTH_AND_ACCIDENTS, Roles.BOTH, forecastable = false),

  // ── 名聲與人際 ──

  /** 名聲危機 */
  REPUTATION_CRISIS(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /** 名譽恢復 */
  REPUTATION_RESTORED(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /** 爆紅 */
  GOING_VIRAL(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

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
  PRIVACY_LOSS(REPUTATION_AND_RELATIONSHIPS, Roles.RECIPIENT_ONLY),

  /**
   * 法律程序開始 —— 遭起訴、被告、提告他人、被搜索約談、進入仲裁或重大訴訟。
   *
   * **改名自前身的「法律危機」**：那個名稱**把價值判斷寫進了型別**，
   * 違反本 enum 的「好壞不由型別承載」。而且它與自己承接的內容矛盾 ——
   * 本值兩種主詞都收，其中「他提告別人」（主動興訟、發動追討）根本不是危機。
   * 中性化之後，好壞才真的交給 `sentiment`：同一個型別可以是危機，也可以是反擊。
   *
   * 判準是「程序啟動」這個可觀察的點，結果另記
   * [LEGAL_OUTCOME_FAVORABLE] / [LEGAL_OUTCOME_UNFAVORABLE]。
   * 訴訟纏身不必然失去自由 —— 那是 [FREEDOM_LOSS]，兩者不互相蘊含。
   */
  LEGAL_PROCEEDING_START(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /**
   * 有利的法律結果 —— 勝訴、獲判無罪、不起訴、和解成立且對己有利、撤銷原判。
   *
   * **改名自前身的 `FAVORABLE_LEGAL_OUTCOME`**：形容詞開頭，違反 `<領域>_<變化>` 的命名慣例。
   * 改成 `LEGAL_OUTCOME_` 前綴之後，與 [LEGAL_OUTCOME_UNFAVORABLE] 在下拉選單裡也自然相鄰。
   *
   * ⭐ **部分勝訴部分敗訴 → 兩筆都記**，本值與 [LEGAL_OUTCOME_UNFAVORABLE] 各一。
   * 這是本 enum 既有的「正交就各記一筆」原則（同 [PRIVACY_LOSS] / [REPUTATION_CRISIS]）。
   *
   * ⛔ **不要加第三個 `_MIXED`。** 那等於把 `sentiment` 的值抄進型別名稱 ——
   * 「混合」是評價那一軸的事，而評價已經有自己的欄位。
   */
  LEGAL_OUTCOME_FAVORABLE(REPUTATION_AND_RELATIONSHIPS, Roles.RECIPIENT_ONLY),

  /**
   * 不利的法律結果 —— 敗訴、有罪判決、遭裁罰、和解條件不利、上訴駁回。
   *
   * 前身只有「有利」那一半，不利的結果沒有對應成員，只能標成程序本身或名聲危機 ——
   * 兩者都不是「判決下來了」這個可觀察的點。
   *
   * 部分勝訴部分敗訴的處理見 [LEGAL_OUTCOME_FAVORABLE]（兩筆都記，不設 `_MIXED`）。
   * 判決導致入監另記 [FREEDOM_LOSS] —— 判決與執行是兩天。
   */
  LEGAL_OUTCOME_UNFAVORABLE(REPUTATION_AND_RELATIONSHIPS, Roles.RECIPIENT_ONLY),

  /**
   * 失去人身自由 —— 入獄服刑、羈押、綁架、非法監禁、軟禁、強制收容／住院。
   *
   * 判準是「行動自由被剝奪」，**不預設有無暴力**，故與 [VIOLENCE] 正交：
   * 遭綁架又受傷者兩筆都記。也不等同 [LEGAL_PROCEEDING_START] —— 訴訟纏身不必然失去自由，
   * 而軟禁與綁架根本不經法律程序。
   */
  FREEDOM_LOSS(REPUTATION_AND_RELATIONSHIPS, Roles.RECIPIENT_ONLY),

  /** 重獲自由／釋放 —— [FREEDOM_LOSS] 的對稱項（命名對照 [REPUTATION_CRISIS] / [REPUTATION_RESTORED]） */
  FREEDOM_RESTORED(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /** 競逐得勝 */
  COMPETITION_WIN(REPUTATION_AND_RELATIONSHIPS, Roles.INITIATOR_ONLY),

  /** 競逐失利（如選舉、商業奪權） */
  COMPETITION_LOSS(REPUTATION_AND_RELATIONSHIPS, Roles.INITIATOR_ONLY),

  /** 公開對抗／控訴 */
  PUBLIC_CONFRONTATION(REPUTATION_AND_RELATIONSHIPS, Roles.INITIATOR_ONLY),

  /** 與摯友／夥伴決裂 */
  FALLING_OUT(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /**
   * 背叛／出賣／暗中陷害 —— 單向且在暗處：仍在信任範圍內的人反手，
   * 或從未浮上檯面就在扯後腿。
   *
   * 與 [FALLING_OUT] 的差別在於**是否雙向且公開**：決裂是兩邊都知道翻臉了，
   * 背叛則是受方事後才知道。與 [PUBLIC_CONFRONTATION] 的差別同理。
   *
   * **兩端都收**：「他遭背叛」與「他背叛了別人」是同一件事的兩個主詞。
   * 前身只收前者（名稱即「遭背叛」），於是傳記裡再常見不過的「他出賣了提攜他的人」
   * 無處可歸。⇒ 主詞走 [roles]，逐筆由 `AbstractEvent.role` 回答。
   */
  BETRAYAL(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /** 和解 */
  RECONCILIATION(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  /** 遇到重要導師 */
  MEET_KEY_MENTOR(REPUTATION_AND_RELATIONSHIPS, Roles.BOTH),

  // ── 心靈與宗教 ──

  /**
   * 受洗 —— **兩端都收**：成人受洗是自己決定的，嬰兒受洗則完全由父母決定，
   * 當事人連知情都談不上。兩者在生命敘事裡的份量完全不同。
   */
  BAPTISM(SPIRITUAL_AND_RELIGIOUS, Roles.BOTH),

  /** 宗教皈依 */
  CONVERSION(SPIRITUAL_AND_RELIGIOUS, Roles.BOTH),

  /**
   * 退教 —— **兩端都收**：自行離開，或遭逐出教門／絕罰／破門。
   * 後者是別人做的動作，與前者只是同一個狀態變化的兩種主詞。
   */
  LEAVE_RELIGION(SPIRITUAL_AND_RELIGIOUS, Roles.BOTH),

  /** 剃度出家／受戒 */
  ORDINATION_MONASTIC(SPIRITUAL_AND_RELIGIOUS, Roles.BOTH),

  /** 精神覺醒 */
  SPIRITUAL_AWAKENING(SPIRITUAL_AND_RELIGIOUS, Roles.RECIPIENT_ONLY),

  /** 朝聖 */
  PILGRIMAGE(SPIRITUAL_AND_RELIGIOUS, Roles.INITIATOR_ONLY),

  /** 其他重大宗教儀式 */
  RELIGIOUS_CEREMONY(SPIRITUAL_AND_RELIGIOUS, Roles.BOTH),

  // ── 其他 ──

  /** 其他事件 */
  OTHERS(EventCategory.OTHERS, Roles.BOTH),
  ;

  /**
   * [roles] 的三個值域常數。
   *
   * 命名刻意用 `_ONLY` 尾綴呼應 [EventRole] 的值名：在位置參數的脈絡下，
   * 常數名是呼叫點唯一的標籤，而「**另一端不可能**」正是單值那一格唯一在說的事，
   * 應該在每一個呼叫點都看得見。
   *
   * ## 為什麼是巢狀 object，不是 file-level `private val`
   *
   * file-level property 依**原始碼順序**初始化。常數若放在檔案頂層，
   * 只要有人日後在它**上方**加一個會讀到 [entries] 的 top-level property
   * （例如照 [forecastable] 的叮囑「在這個檔案裡快取一份可預測清單」），
   * 所有成員的 [roles] 就會**全部變成 `null`** —— 不拋例外、不出警告、編譯照過。
   * 那是本檔最容易被觸發的靜默失效，而 [forecastable] 的 KDoc 幾乎是在邀請它。
   *
   * 巢狀 object 對這種順序攻擊免疫：`Situation.Roles` 是獨立類別，
   * 不參與 file facade 的初始化順序。
   *
   * ## ⛔ 不能改放 `companion object`
   *
   * enum 的 companion 在 entries 之後才初始化。但**這條是編譯期就被擋下的**
   * （`companion object of enum class 'Situation' is uninitialized here`），
   * 不是執行期拿到 `null` —— 差別有實質意義：編譯器擋得住的錯誤是**安全的**錯誤，
   * 不需要在文件或測試裡防守。上面那個順序坑才需要，因為只有它是靜默的。
   */
  private object Roles {
    /** 兩種主詞都可能 —— 另一端在現實中確實會發生，交給事件層逐筆決定。 */
    val BOTH = setOf(EventRole.INITIATOR, EventRole.RECIPIENT)

    /** 只可能是發起端 —— 承受端在現實中不成立。 */
    val INITIATOR_ONLY = setOf(EventRole.INITIATOR)

    /** 只可能是承受端 —— 發起端在現實中不成立。 */
    val RECIPIENT_ONLY = setOf(EventRole.RECIPIENT)
  }
}
