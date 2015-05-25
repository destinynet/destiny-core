/** 2009/10/12 上午3:27:09 by smallufo */
package destiny.core.calendar.eightwords.fourwords;

import java.io.Serializable;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** 四字斷終生 的 key */
public class FourWordsKey implements Serializable
{
  /** 日干 */
  private Stem dayStem;
  
  /** 月支 */
  private Branch monthBranch;
  
  /** 時支 */
  private Branch hourBranch;

  public FourWordsKey(Stem dayStem, Branch monthBranch, Branch hourBranch)
  {
    super();
    this.dayStem = dayStem;
    this.monthBranch = monthBranch;
    this.hourBranch = hourBranch;
  }

  public Stem getDayStem()
  {
    return dayStem;
  }

  public Branch getMonthBranch()
  {
    return monthBranch;
  }

  public Branch getHourBranch()
  {
    return hourBranch;
  }

  @NotNull
  @Override
  public String toString()
  {
    return "FourWordsKey [日干=" + dayStem + ", 月支=" + monthBranch + ", 時支=" + hourBranch + "]";
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dayStem == null) ? 0 : dayStem.hashCode());
    result = prime * result + ((hourBranch == null) ? 0 : hourBranch.hashCode());
    result = prime * result + ((monthBranch == null) ? 0 : monthBranch.hashCode());
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FourWordsKey other = (FourWordsKey) obj;
    if (dayStem == null)
    {
      if (other.dayStem != null)
        return false;
    }
    else if (!dayStem.equals(other.dayStem))
      return false;
    if (hourBranch == null)
    {
      if (other.hourBranch != null)
        return false;
    }
    else if (!hourBranch.equals(other.hourBranch))
      return false;
    if (monthBranch == null)
    {
      if (other.monthBranch != null)
        return false;
    }
    else if (!monthBranch.equals(other.monthBranch))
      return false;
    return true;
  }
  
  
  
}

