/**
 * Created by smallufo on 2021-03-23.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.chinese.Branch.*
import kotlin.test.Test
import kotlin.test.assertEquals


internal class ILunarStationContextTest {

  @Test
  fun testOppoHouseMap() {
    ILunarStationContext.getOppoHouseMap(LunarStation.房).also { map ->
      assertEquals(OppoHouse.山, map[午])
      assertEquals(OppoHouse.月, map[巳])
    }

    ILunarStationContext.getOppoHouseMap(LunarStation.心).also { map ->
      assertEquals(OppoHouse.山, map[未])
      assertEquals(OppoHouse.月, map[午])
    }

    ILunarStationContext.getOppoHouseMap(LunarStation.尾).also { map ->
      assertEquals(OppoHouse.山, map[寅])
      assertEquals(OppoHouse.月, map[丑])
    }

    ILunarStationContext.getOppoHouseMap(LunarStation.箕).also { map ->
      assertEquals(OppoHouse.山, map[申])
      assertEquals(OppoHouse.月, map[未])
    }

    ILunarStationContext.getOppoHouseMap(LunarStation.角).also { map ->
      assertEquals(OppoHouse.山, map[亥])
      assertEquals(OppoHouse.月, map[戌])
    }

    ILunarStationContext.getOppoHouseMap(LunarStation.亢).also { map ->
      assertEquals(OppoHouse.山, map[巳])
      assertEquals(OppoHouse.月, map[辰])
    }

    ILunarStationContext.getOppoHouseMap(LunarStation.氐).also { map ->
      assertEquals(OppoHouse.山, map[申])
      assertEquals(OppoHouse.月, map[未])
    }
  }

  @Test
  fun testSelfHouseMap() {
    ILunarStationContext.getSelfHouseMap(LunarStation.房).also { map ->
      assertEquals(SelfHouse.山, map[午])
      assertEquals(SelfHouse.田, map[巳])
    }

    ILunarStationContext.getSelfHouseMap(LunarStation.心).also { map ->
      assertEquals(SelfHouse.山, map[未])
      assertEquals(SelfHouse.田, map[午])
    }

    ILunarStationContext.getSelfHouseMap(LunarStation.尾).also { map ->
      assertEquals(SelfHouse.山, map[寅])
      assertEquals(SelfHouse.田, map[丑])
    }

    ILunarStationContext.getSelfHouseMap(LunarStation.箕).also { map ->
      assertEquals(SelfHouse.山, map[申])
      assertEquals(SelfHouse.田, map[未])
    }

    ILunarStationContext.getSelfHouseMap(LunarStation.角).also { map ->
      assertEquals(SelfHouse.山, map[亥])
      assertEquals(SelfHouse.田, map[戌])
    }

    ILunarStationContext.getSelfHouseMap(LunarStation.亢).also { map ->
      assertEquals(SelfHouse.山, map[巳])
      assertEquals(SelfHouse.田, map[辰])
    }

    ILunarStationContext.getSelfHouseMap(LunarStation.氐).also { map ->
      assertEquals(SelfHouse.山, map[申])
      assertEquals(SelfHouse.田, map[未])
    }
  }
}
