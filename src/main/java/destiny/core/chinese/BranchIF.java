/**
 * @author smallufo
 * Created on 2005/7/5 at 上午 11:35:32
 */
package destiny.core.chinese;

public interface BranchIF<T> {

  Branch getBranch();

  int getAheadOf(T other);
}
