/**
 * @author smallufo 
 * Created on 2007/6/12 at 上午 6:09:35
 */ 
package destiny.astrology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

public class PlanetTest extends TestCase
{
  /** 測試從 "sun" 取得 Planet.SUN */
  public void testGetPlanetFromString()
  {
    assertSame(Planet.SUN , Planet.get("sun"));
    assertSame(Planet.SUN , Planet.get("SUN"));
    assertSame(Planet.SUN , Planet.get("Sun"));
    assertNull(Planet.get("xxx"));
  }
  
  /** 將 太陽 up-case 再 down-cast , 比對 equality 以及 same */
  public void testPlanetEqual() 
  {
    Planet sun = Planet.SUN;
    Planet sun2 = Planet.SUN;
    
    Set<Point> points = new HashSet<Point>();
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
  public void testPlanetEqualReflection() throws Exception, SecurityException, IllegalAccessException, NoSuchFieldException
  {
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
    Class clazz = Planet.class;
    List<Star> list = Arrays.asList((Star[])clazz.asSubclass(clazz).getDeclaredField("values").get(null));
    Star sunInList = list.get(0);
    //System.out.println("比對 list 中的太陽是否相同 , hashcode = " + sunInList.hashCode() + " , " + sun.hashCode());
    assertEquals(sunInList , sun);
    assertSame(sunInList , sun);
    
  }
  
  public void testPlanet() 
  {
    assertEquals("太陽" , Planet.SUN.getName(Locale.TAIWAN));
    assertEquals("日" , Planet.SUN.getAbbreviation(Locale.TAIWAN));
    
    Locale locale = Locale.ENGLISH;
    assertEquals("Sun" , Planet.SUN.getName(locale));
    assertEquals("Su" , Planet.SUN.getAbbreviation(locale));
    
  }
  
  public void testPlanets()
  {
    for (Planet planet : Planet.values)
    {
      assertNotNull(planet);
      assertNotNull(planet.toString());
    }
  }

}
