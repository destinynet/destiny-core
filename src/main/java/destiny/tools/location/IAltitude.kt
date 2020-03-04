/** 2009/12/7 下午6:24:26 by smallufo  */
package destiny.tools.location

interface IAltitude {

  /** 從經緯度取得高度 , 單位為 meter */
  fun getAltitude(longitude: Double, latitude: Double): Double?

}

