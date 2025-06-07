/**
 * Created by smallufo on 2021-03-23.
 */
package destiny.core.chinese.lunarStation

import destiny.core.ILoop

enum class House {
  山, 林, 崗, 湯, 火, 野, 刀, 砧, 路, 江, 湖, 田,
  水, 園, 井, 天, 草, 岸, 風, 湯火, 月
}

enum class SelfHouse(val house: House) : ILoop<SelfHouse> {
  山(House.山), 林(House.林), 崗(House.崗), 湯(House.湯), 火(House.火), 野(House.野),
  刀(House.刀), 砧(House.砧), 路(House.路), 江(House.江), 湖(House.湖), 田(House.田);

  override fun next(n: Int): SelfHouse {
    return get(ordinal + n)
  }

  companion object {
    operator fun get(index: Int): SelfHouse {
      return entries[index.mod(entries.size)]
    }
  }
}

enum class OppoHouse(val house: House) : ILoop<OppoHouse> {
  山(House.山), 水(House.水), 田(House.田), 園(House.園), 井(House.井), 刀(House.刀),
  天(House.天), 草(House.草), 岸(House.岸), 風(House.風), 湯火(House.湯火), 月(House.月);

  override fun next(n: Int): OppoHouse {
    return get(ordinal + n)
  }

  companion object {
    operator fun get(index: Int): OppoHouse {
      return entries[index.mod(entries.size)]
    }
  }
}
