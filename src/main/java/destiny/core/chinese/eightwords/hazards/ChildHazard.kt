/**
 * Created by smallufo on 2022-07-12.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.IPattern
import java.util.*


sealed class ChildHazard : IPattern {
  //object 直難關 : ChildHazard()
  object 百日關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "俗忌百曰不出大門，房門不忌。夫百曰關者，專以十二生肖月忌各所內百犯之，童限月內百曰必有星辰難養。"
    }
  }

  object 千日關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "犯之主有驚風、吐乳之災，忌住難星。另一說法；三歲之前不宜到外婆家，或莫至外婆供奉祖先牌位處。"
    }
  }

  object 閻王關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "日主旺不防，弱則難養。犯此小時應該避免看誦經作法或作功德場合，難養，帶天德、月德可解。"
    }
  }

  object 鬼門關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "夫鬼門關者，以十二支生人、逢各所值時辰、論小兒時上、並童限逢之不可遠行。一生不宜進陰廟、有應公、萬善祠、墳墓區及殯儀館等。宜燒地府錢給地府眾鬼神制化則吉。"
    }
  }

  object 雞飛關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關童命犯之難養，夜生不妨，限遇亦凶，以年干生人取用。避免孩子看殺雞、殺魚、殺鴨等行為，可燒牛頭馬面錢、或稱牛馬將軍錢，制化即吉。"
    }
  }

  object 鐵蛇關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關最凶，童命時上帶、童限更值之則難養。並忌見麻痘凶，壯命行限值之亦有災凶，重則倒壽、十有九驗，以命納音取用，如甲子生人戌時是。兒時出麻疹、疹痘須特別小心，也容易遭動物咬傷，若遭動物咬傷速就醫，回家用陰陽水燒化銅蛇鐵狗錢于其中則吉。"
    }
  }

  object 斷橋關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關以月建遇十二時支論，乃指南中第一關，小兒生時帶著難養，壯命行限值之、加以刻度凶星到，必倒壽無有不驗。犯此忌過橋、汲水照影。宜用水官錢祭祀水官大帝。"
    }
  }

  object 落井關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關以年干取、如甲己生人巳時是，君流年浮沉合之少壯命皆有水災之厄，切宜防慎吉。"
    }
  }

  object 四柱關 : ChildHazard() {
    override fun getNotes(locale: Locale): String? {
      return "此關俗忌坐轎車、止忌生時，帶著童限不忌，大抵亦無甚凶。犯此忌坐欄杆椅太早。"
    }
  }

  object 短命關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關生時上帶、主驚呼夜啼難養，如日干健則無事，日主弱凶。"
    }
  }

  object 浴盆關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此生下地之時不可用腳盆，須用鐵鍋、火盆之類，洗之後無忌也，只忌月內一周之外不忌。"
    }
  }

  object 湯火煞 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "若小兒犯此招湯火之災，亦忌此限。蓋人生水火乃養生之要、何能免乎，知防慎則吉矣。用火神錢制化、或到火神廟拜拜即吉。"
    }
  }

  object 水火關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "民俗制化法：祭火神及水官大帝，用火神錢拜火神、用水官錢拜水官大帝。"
    }
  }

  object 深水關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "犯此關之人與前世父母糾纏不清，應該避免在清明節、端午節、中秋節、除夕祭拜祖先，滿月及周歲皆要提前一天舉行。"
    }
  }

  class 將軍箭(val arrows: Int) : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "一箭傷人三歲死，二箭傷人六歲亡，三箭傷人九歲亡，四箭傷人十二亡。"
    }
  }

  object 桃花煞 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此煞男女皆忌，乃五行沐浴也，主淫亂不節，女命最忌、亦名咸池殺。一生感情所困。"
    }
  }

  object 紅豔煞 : ChildHazard()
  object 流霞煞 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "女主產厄、男主刀傷。又雲：犯此男主他鄉死、女主產後亡。"
    }
  }

  object 夜啼關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "小兒若犯此關煞，定是三周半夜啼。此關利害難治，只是兩樣起例不同，因並錄之、云後一例有驗。"
    }
  }

  object 白虎關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關時上帶，主見驚風之症，又忌出痘時帶難養，童限遇之則有血光損傷之厄。燒白虎錢制化。"
    }
  }

  object 雷公打腦關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "限遇此關流年天厄、卒暴、陽刃、火值之，主雷火之厄，若遇天月二德可解。犯此忌驚聞鑼鼓、雷公及大聲叫喊。"
    }
  }

  object 天狗關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "小兒行年童限值之有驚怖血光之疾，命中時上帶則有狗傷之厄，切宜防之、慎之。犯此月內怕聞犬吠聲。小孩易有顏面傷害破相，刀、剪、鑽、針須收好，用天狗錢制天狗煞。"
    }
  }

  object 四季關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關詳考乃四季天地荒蕪曰，人命逢此曰有始無終，苗而不秀亦難養。季節交換注意身體保護。"
    }
  }

  object 急腳關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關即八座殺，惟忌修造動土凶。犯此須避動土、修造、開挖事。宜用山神土地錢制化。"
    }
  }

  object 急腳煞 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此煞如甲乙年生人遇申酉時是也，主幼小之年難養。"
    }
  }

  object 五鬼關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關只是死氣也，四柱多見難養，童限值之主有跌傷。犯此忌近病殯儀館、墓地、亂葬崗、棺木店及入庵堂、寺觀。"
    }
  }

  object 金鎖關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "惟犯此關不可佩帶金銀鐵鎖之物，及紐扣串繩索之類、有驗。"
    }
  }

  object 金鎖匙 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "少帶金銀飾品即吉。"
    }
  }

  object 直難關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關主多啾唧星卻不為害，琴堂所說更靈，而此複載隨人用。易受又硬又直的利器所傷。"
    }
  }

  object 取命關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "曰主旺則多病疾，主弱則難養成。莫至供奉七爺、八爺廟、有應公、萬善祠、十八王公廟，並須遠離喪葬場合、廟宇建醮、法會、中元普渡場合皆忌。宜用本命錢制化。"
    }
  }

  object 斷腸關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "勿看殺豬羊，勿入屠宰場。小孩應避免看殺雞、殺鴨等殺生行為。宜用牛馬將軍錢制化。"
    }
  }

  object 埋兒關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此關埋兒驚凶喪。勿看出山、凶喪。自幼難養。宜重拜父母或拜神明為契子保平安。"
    }
  }

  object 天吊關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "逆病庶生之子。從小兩眼呆滯、眼珠翻白。"
    }
  }

  object 休庵關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "犯此忌入庵寺見僧尼。制化法：燒七星錢、請七星娘娘來保護、燒床母經及三十六婆姐錢則吉。"
    }
  }

  object 撞命關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "此命不亡需夭折，也用過房就別宗。亥年生於亥時是。從小難養、體弱多病、易有夭折現象，拜認乾爹乾媽可平安。"
    }
  }

  object 下情關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "莫讓小孩看人磨刀，宜拜神明為契父，俗雲：稱父母為叔叔、阿姨。"
    }
  }

  object 劫煞關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "小心提防錢財損失。"
    }
  }

  object 血刃關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "易有意外血光之災，或車禍及撞傷，用本命錢或車厄錢制化。"
    }
  }

  object 基敗關 : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "出生後身體狀況差、容易營養不良，忌看蓋房子、打地基，宜拜神明為契子、或注意腸胃方面的治療便能轉危為安。"
    }
  }
}


