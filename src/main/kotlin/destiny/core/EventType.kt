package destiny.core

import destiny.core.EventCategory.*


enum class EventCategory {
  ROMANCE_AND_MARRIAGE,         // 感情與婚姻
  FAMILY_AND_RELATIVES,         // 家庭與家族
  CAREER_AND_ACADEMICS,         // 事業與學業
  FINANCE_AND_ASSETS,           // 財務與資產
  HEALTH_AND_ACCIDENTS,         // 健康與意外
  REPUTATION_AND_RELATIONSHIPS, // 名聲與人際
  SPIRITUAL_AND_RELIGIOUS,      // 心靈與宗教
  OTHERS                        // 其他
}

/**
 * 定義所有可選的具體生命事件。
 * @param category 事件所屬的類別
 * @param repeatable 是否有重複發生的可能
 */
enum class EventType(
  val category: EventCategory,
  val repeatable: Boolean
) {
  // 感情與婚姻
  FIRST_LOVE(ROMANCE_AND_MARRIAGE, false),              // 初戀
  SIGNIFICANT_RELATIONSHIP(ROMANCE_AND_MARRIAGE, true), // 開始重要戀情
  ENGAGEMENT_MARRIAGE(ROMANCE_AND_MARRIAGE, true),      // 訂婚／結婚
  BREAKUP_DIVORCE(ROMANCE_AND_MARRIAGE, true),          // 分手／離婚
  AFFAIR_OR_MARITAL_CRISIS(ROMANCE_AND_MARRIAGE, true), // 外遇／關係危機

  // 家庭與家族
  BIRTH_OF_CHILD(FAMILY_AND_RELATIVES, true),             // 生子／收養子女
  RELATIVE_DECEASED(FAMILY_AND_RELATIVES, true),          // 親人亡故
  FAMILY_CHANGE(FAMILY_AND_RELATIVES, true),              // 家庭關係重大變化
  BEING_ABANDONED_DISOWNED(FAMILY_AND_RELATIVES, false),  // 被遺棄／斷絕關係
  ABANDONING_FAMILY(FAMILY_AND_RELATIVES, true),          // 拋棄家人／子女
  RELOCATION_IMMIGRATION(FAMILY_AND_RELATIVES, true),     // 搬家／移民
  PET_DEATH(FAMILY_AND_RELATIVES, true),                  // 寵物亡故

  // 事業與學業
  START_HIGHER_EDUCATION(CAREER_AND_ACADEMICS, true), // 進入高等教育
  GRADUATION(CAREER_AND_ACADEMICS, true),             // 畢業
  FIRST_JOB(CAREER_AND_ACADEMICS, false),             // 第一份工作
  ENTREPRENEURSHIP(CAREER_AND_ACADEMICS, true),       // 創業
  PROMOTION_KEY_POSITION(CAREER_AND_ACADEMICS, true), // 升遷／獲得重要職位
  MAJOR_ACHIEVEMENT(CAREER_AND_ACADEMICS, true),      // 重大成就／得獎
  UNEMPLOYMENT_LAYOFF(CAREER_AND_ACADEMICS, true),    // 失業／資遣
  CAREER_CHANGE(CAREER_AND_ACADEMICS, true),          // 轉換跑道
  STEP_DOWN_RESIGN(CAREER_AND_ACADEMICS, true),       // 下台／請辭
  RETIREMENT(CAREER_AND_ACADEMICS, true),             // 退休

  // 財務與資產
  INHERITANCE_WINDFALL(FINANCE_AND_ASSETS, true),   // 繼承遺產／大筆獲利
  REAL_ESTATE_PURCHASE(FINANCE_AND_ASSETS, true),   // 買房
  REAL_ESTATE_SALE(FINANCE_AND_ASSETS, true),       // 賣房
  BANKRUPTCY_MAJOR_LOSS(FINANCE_AND_ASSETS, true),  // 破產／重大虧損

  // 健康與意外
  MAJOR_ILLNESS(HEALTH_AND_ACCIDENTS, true),          // 確診重大疾病
  MAJOR_SURGERY(HEALTH_AND_ACCIDENTS, true),          // 大型手術
  HEALTH_RECOVERY(HEALTH_AND_ACCIDENTS, true),        // 健康恢復
  SERIOUS_ACCIDENT(HEALTH_AND_ACCIDENTS, true),       // 重大意外 (非人為蓄意)
  VICTIM_OF_ATTACK(HEALTH_AND_ACCIDENTS, true),       // 遭攻擊／受害

  // 名聲與人際
  REPUTATION_CRISIS(REPUTATION_AND_RELATIONSHIPS, true),        // 名聲危機
  REPUTATION_RESTORED(REPUTATION_AND_RELATIONSHIPS, true),      // 名譽恢復
  GOING_VIRAL(REPUTATION_AND_RELATIONSHIPS, true),              // 爆紅
  LEGAL_CRISIS(REPUTATION_AND_RELATIONSHIPS, true),             // 法律危機
  ACQUITTAL_OR_EXONERATION(REPUTATION_AND_RELATIONSHIPS, true), // 司法平反
  LIBERATION_OR_RELEASE(REPUTATION_AND_RELATIONSHIPS, true),    // 重獲自由／釋放
  CONTEST_DEFEATED(REPUTATION_AND_RELATIONSHIPS, true),         // 競逐失利 (如選舉、商業奪權)
  FALLING_OUT(REPUTATION_AND_RELATIONSHIPS, true),              // 與摯友／夥伴決裂
  RECONCILIATION(REPUTATION_AND_RELATIONSHIPS, true),           // 和解
  MEET_KEY_MENTOR(REPUTATION_AND_RELATIONSHIPS, true),          // 遇到重要導師
  VIOLENT_OFFENDER(REPUTATION_AND_RELATIONSHIPS, true),         // 暴力行為／加害他人

  // 心靈與宗教
  BAPTISM(SPIRITUAL_AND_RELIGIOUS, false),            // 受洗
  CONVERSION(SPIRITUAL_AND_RELIGIOUS, true),          // 宗教皈依
  LEAVE_RELIGION(SPIRITUAL_AND_RELIGIOUS, true),      // 退教
  ORDINATION_MONASTIC(SPIRITUAL_AND_RELIGIOUS, false),// 剃度出家／受戒
  SPIRITUAL_AWAKENING(SPIRITUAL_AND_RELIGIOUS, true), // 精神覺醒
  PILGRIMAGE(SPIRITUAL_AND_RELIGIOUS, true),          // 朝聖
  RELIGIOUS_CEREMONY(SPIRITUAL_AND_RELIGIOUS, true),  // 其他重大宗教儀式

  // 其他
  OTHERS(EventCategory.OTHERS, true), // 其他事件
}
