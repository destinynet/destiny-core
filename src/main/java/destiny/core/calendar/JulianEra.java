/**
 * Created by smallufo on 2017-01-19.
 */
package destiny.core.calendar;

import java.time.DateTimeException;
import java.time.chrono.Era;

/**
 * https://github.com/ThreeTen/threeten-extra/blob/master/src/main/java/org/threeten/extra/chrono/JulianEra.java
 *
 * An era in the Julian calendar system.
 * <p>
 * The Julian calendar system has two eras.
 * The current era, for years from 1 onwards, is known as 'Anno Domini'.
 * All previous years, zero or earlier in the proleptic count or one and greater
 * in the year-of-era count, are part of the 'Before Christ' era.
 * <p>
 * The start of the Julian epoch {@code 0001-01-01 (Julian)} is {@code 0000-12-30 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code JulianEra}.
 * Use {@code getValue()} instead.</b>
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum JulianEra implements Era {

    /**
     * The singleton instance for the era before the current one, 'Before Christ',
     * which has the numeric value 0.
     */
    BC,
    /**
     * The singleton instance for the current era, 'Anno Domini',
     * which has the numeric value 1.
     */
    AD;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JulianEra} from an {@code int} value.
     * <p>
     * {@code JulianEra} is an enum representing the Julian eras of BC/AD.
     * This factory allows the enum to be obtained from the {@code int} value.
     *
     * @param era  the BC/AD value to represent, from 0 (BC) to 1 (AD)
     * @return the era singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static JulianEra of(int era) {
        switch (era) {
            case 0:
                return BC;
            case 1:
                return AD;
            default:
                throw new DateTimeException("Invalid era: " + era);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era BC has the value 0, while the era AD has the value 1.
     *
     * @return the era value, from 0 (BC) to 1 (AD)
     */
    @Override
    public int getValue() {
        return ordinal();
    }

}