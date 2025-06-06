package destiny.core.electional

import kotlinx.serialization.Serializable

@Serializable
enum class ElectionalPurpose {
  // Personal
  MARRIAGE_REGISTRATION,  // 婚姻登記
  WEDDING_CEREMONY,       // 婚禮儀式
  MOVING_IN,              // 搬家入宅
  START_CONSTRUCTION,     // 動土裝修
  TRAVEL_START,           // 出行啟程
  MEDICAL_APPOINTMENT,    // 重要求醫/手術
  SIGN_CONTRACT,          // 簽署合約
  IMPORTANT_MEETING,      // 重要會面
  DATING,                 // 約會
  LOVE_CONFESSION,        // 告白
  CAR_DELIVERY,           // 交車
  BUY_LOTTERY,            // 彩券
  CONCEPTION,             // 受孕

  // Business
  BUSINESS_OPENING,       // 開業開市
  PRODUCT_LAUNCH,         // 產品發布
  NEGOTIATION,            // 商業談判

  // Spiritual
  RELIGIOUS_CEREMONY,     // 宗教儀式/祭祀
  FENGSHUI_ADJUSTMENT,    // 風水調整

  // General / Other
  GENERAL_GOOD_DAY,       // 平日通用吉日
  OTHER;                  // 其他
}
