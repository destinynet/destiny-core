/**
 * Created by smallufo on 2017-05-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple3;

import java.util.function.Function;

public abstract class YearBranchFunction implements Function<Tuple3<ZContext.YearType, StemBranch , StemBranch> , Branch> {


}
