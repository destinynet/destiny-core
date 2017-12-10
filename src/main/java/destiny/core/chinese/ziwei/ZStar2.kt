package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * Created by smallufo on 2017-12-10.
 */
abstract class ZStar2 : ZStar {

  val func : Function<Branch>

  constructor(nameKey: String, resource: String, type: Type, func: Function<Branch>) : super(nameKey, resource , type) {
    this.func = func
  }

  constructor(nameKey: String, resource: String, abbrKey: String, type: Type , func: Function<Branch>) : super(nameKey , resource , abbrKey , type) {
    this.func = func
  }

}