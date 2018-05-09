package destiny.astrology.prediction

/**
 * 繼承自 Mappable , 只是此推運法是「離散收斂」的 : 只要日期不同，有可能收斂到同一個日子<br></br>
 * 例如太陽返照，太陰返照。同年（月）當中的時間，其實是收斂到同樣的盤
 */
internal interface IDiscrete : Mappable
