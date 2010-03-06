package destiny.astrology;

public class Constants
{
  /** 平均日 */
  public final static double MEAN_DAY     = 24*60*60; //86400
  /** 恆星日 , 23h 56m 4s */
  public final static double SIDEREAL_DAY = 23*60*60 + 56*60 + 4.09074; //86 164.0907
  
  /** 平年 (Gregorian Year , 365 or 366) */
  public final static double MEAN_YEAR     = 86400*365.0000;    //31536000
  /** 回歸年 (From one equinox to the next)*/
  public final static double TROPICAL_YEAR = 86400*365.242190;  //31556925.2
  /** 恆星年 (Between maximum elevation passages of a fixed star)*/
  public final static double SIDEREAL_YEAR = 86400*365.256363;  //31558149.8
  
  /** 恆星月 (1994-2000) */
  public final static double SIDEREAL_MONTH    = 86400 * 27.321662; // 2360591.6
  /** 會合月 (新月到新月) */
  public final static double SYNODICAL_MONTH   = 86400 * 29.530589; // 2551442.89
  /** 回歸月 */
  public final static double TROPICAL_MONTH    = 86400 * 27.321582; // 2360584.68
  /** 異常月 (通過近地點 , perigee) , 1994-2000 */
  public final static double ANOMALISTIC_MONTH = 86400 * 27.554550; // 2380713.12
  /** 龍月 ? (The Draconic month is the time between one node passage to the next. )*/
  public final static double DRACONIC_MONTH    = 86400 * 27.212221; // 2351135.89
  
  /*
  public static final  SE_ECL_NUT           = -1;
  public static final  SE_SUN               = 0 ;
  public static final  SE_MOON              = 1 ;
  public static final  SE_MERCURY           = 2 ;
  public static final  SE_VENUS             = 3 ;
  public static final  SE_MARS              = 4 ;
  public static final  SE_JUPITER           = 5 ;
  public static final  SE_SATURN            = 6 ;
  public static final  SE_URANUS            = 7 ;
  public static final  SE_NEPTUNE           = 8 ;
  public static final  SE_PLUTO             = 9 ;
  public static final  SE_MEAN_NODE         = 10;
  public static final  SE_TRUE_NODE         = 11;
  public static final  SE_MEAN_APOG         = 12;
  public static final  SE_OSCU_APOG         = 13;
  public static final  SE_EARTH             = 14;
  public static final  SE_CHIRON            = 15;
  public static final  SE_PHOLUS            = 16;
  public static final  SE_CERES             = 17;
  public static final  SE_PALLAS            = 18;
  public static final  SE_JUNO              = 19;
  public static final  SE_VESTA             = 20;
  public static final  SE_FICT_OFFSET       = 40;
  public static final  SE_NFICT_ELEM        = 15;

  // Hamburger or Uranian "planets"
  public static final  SE_CUPIDO            = 40;
  public static final  SE_HADES             = 41;
  public static final  SE_ZEUS              = 42;
  public static final  SE_KRONOS            = 43;
  public static final  SE_APOLLON           = 44;
  public static final  SE_ADMETOS           = 45;
  public static final  SE_VULKANUS          = 46;
  public static final  SE_POSEIDON          = 47;
  
  // other fictitious bodies 
  public static final  SE_ISIS              = 48;
  public static final  SE_NIBIRU            = 49;
  public static final  SE_HARRINGTON        = 50;
  public static final  SE_NEPTUNE_LEVERRIER = 51;
  public static final  SE_NEPTUNE_ADAMS     = 52;
  public static final  SE_PLUTO_LOWELL      = 53;
  public static final  SE_PLUTO_PICKERING   = 54;

  public static final  SE_AST_OFFSET        = 10000;
  */
}