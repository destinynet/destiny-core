/**
 * @author smallufo 
 * Created on 2007/6/12 at 上午 6:09:35
 */ 
package destiny.astrology;


import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PlanetTest
{
  /** 測試從 "sun" 取得 Planet.SUN */
  @Test
  public void testGetPlanetFromString()
  {
    assertSame(Planet.SUN , Planet.get("sun").get());
    assertSame(Planet.SUN , Planet.get("SUN").get());
    assertSame(Planet.SUN , Planet.get("Sun").get());
    assertTrue(!Planet.get("xxx").isPresent());
  }
  
  /** 將 太陽 up-case 再 down-cast , 比對 equality 以及 same */
  @Test
  public void testPlanetEqual() 
  {
    Planet sun = Planet.SUN;
    Planet sun2 = Planet.SUN;
    
    Set<Point> points = new HashSet<>();
    points.add(sun2);
    
    Iterator<Point> pointsIt = points.iterator();
    while(pointsIt.hasNext())
    {
      Point p = pointsIt.next();
      assertEquals("destiny.astrology.Planet" , p.getClass().getName());
      if (p instanceof Planet)
      {
         Planet p2 = (Planet) p;
         assertSame(p2 , sun);
         assertSame(p , sun);
      }
      else
        throw new RuntimeException("Error , it should be Planet ");
    }
  }
  
  /** 透過 reflection 產生 太陽 , 與直接產生的太陽，比對 equality 以及 same */
  @SuppressWarnings("unchecked")
  @Test
  public void testPlanetEqualReflection() throws Exception {
    Planet sun = Planet.SUN;
    
    /*
    HoroscopeSetting setting = new HoroscopeSetting();
    Set<Point> diaplayablePoints = setting.getDisplayablePoints();
    Iterator<Point> diaplayableIt = diaplayablePoints.iterator();
    while(diaplayableIt.hasNext())
    {
      Point p = diaplayableIt.next();
      if(p.toString().equals("太陽"))
      {
        // 比對太陽是否相同
        Star s = (Star) p;
        assertSame(s , sun);
        assertEquals(s , sun);
      }
    }
    */
    
    //從 reflection 產生 太陽
    @SuppressWarnings("rawtypes")
    Class clazz = Planet.class;
    List<Star> list = Arrays.asList((Star[])clazz.asSubclass(clazz).getDeclaredField("values").get(null));
    Star sunInList = list.get(0);
    //System.out.println("比對 list 中的太陽是否相同 , hashcode = " + sunInList.hashCode() + " , " + sun.hashCode());
    assertEquals(sunInList , sun);
    assertSame(sunInList , sun);
    
  }

  @Test
  public void testPlanet() 
  {
    assertEquals("太陽" , Planet.SUN.getName(Locale.TAIWAN));
    assertEquals("日" , Planet.SUN.getAbbreviation(Locale.TAIWAN));
    
    Locale locale = Locale.ENGLISH;
    assertEquals("Sun" , Planet.SUN.getName(locale));
    assertEquals("Su" , Planet.SUN.getAbbreviation(locale));
  }

  @Test
  public void testPlanets()
  {
    for (Planet planet : Planet.values)
    {
      assertNotNull(planet);
      assertNotNull(planet.toString());
    }
  }

  @Test
  public void testCompare()
  {
    assertTrue(Planet.SUN.compareTo(Planet.MOON) < 0);
    assertTrue(Planet.MOON.compareTo(Planet.MERCURY) < 0);
    assertTrue(Planet.MERCURY.compareTo(Planet.VENUS) < 0);
    assertTrue(Planet.VENUS.compareTo(Planet.MARS) < 0);
    assertTrue(Planet.MARS.compareTo(Planet.JUPITER) < 0);
    assertTrue(Planet.JUPITER.compareTo(Planet.SATURN) < 0);
    assertTrue(Planet.SATURN.compareTo(Planet.URANUS) < 0);
    assertTrue(Planet.URANUS.compareTo(Planet.NEPTUNE) < 0);
    assertTrue(Planet.NEPTUNE.compareTo(Planet.PLUTO) < 0);
  }

}
