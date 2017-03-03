/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:36:05
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/** 天干/地支/十神 的工具箱 */
public class ReactionsUtil
{
  /** 實作 地支藏干 */
  private HiddenStemsIF hiddenStemsImpl = new HiddenStemsStandardImpl();
  
  /** constructor , 不指定地支藏干，地支藏干使用內定的標準設定 */
  public ReactionsUtil()
  {
  }
  
  /** constructor , 指定地支藏干 */
  public ReactionsUtil(HiddenStemsIF hiddenStemsImpl)
  {
    this.hiddenStemsImpl = hiddenStemsImpl;
  }
  
  /**
   * 取得天干的相互關係
   * <BR>actor == 乙 , actee == 甲 , 傳回劫財 (乙是甲的劫財)
   * <BR>actor == 丙 , actee == 甲 , 傳回食神 (丙是甲的食神)
   * @param actor 作用者（天干）
   * @param actee 被作用者（日干）
   * @return 天干十神
   */
  @NotNull
  public final Reactions getReaction( @NotNull Stem actor , @NotNull Stem actee)
  {
    if (actor.getFiveElement().isProducingTo(actee.getFiveElement()) )
    {
      if (actor.getBooleanValue() == actee.getBooleanValue())
        return Reactions.偏印;
      else
        return Reactions.正印;
    }
    
    if (actor.getFiveElement().isDominatorOf(actee.getFiveElement()))
    {
      if (actor.getBooleanValue() == actee.getBooleanValue())
        return Reactions.七殺;
      else
        return Reactions.正官;
    }
    
    if (actor.getFiveElement().isDominatedBy(actee.getFiveElement()))
    {
      if (actor.getBooleanValue() == actee.getBooleanValue())
        return Reactions.偏財;
      else
        return Reactions.正財;
    }
    
    if (actor.getFiveElement().equals(actee.getFiveElement()))
    {
      if (actor.getBooleanValue() == actee.getBooleanValue())
        return Reactions.比肩;
      else
        return Reactions.劫財;
    }
      
    if (actor.getFiveElement().isProducedBy(actee.getFiveElement()))
    {
      if (actor.getBooleanValue() == actee.getBooleanValue())
        return Reactions.食神;
      else
        return Reactions.傷官;
    }
    
    throw new RuntimeException("Error occurred when Reactions.getReaction("+actor + " , " + actee + ")!");
  }
  
  /**
   * 地支藏干對天干的關係
   * @param actor 作用者（地支）
   * @param actee 被作用者（日干）
   * @return 地支十神 List <Reactions>
   */
  @NotNull
  public final List<Reactions> getReactions( Branch actor , @NotNull Stem actee)
  {
    List<Reactions> result = new ArrayList<>();
    List<Stem> hiddenStems = this.hiddenStemsImpl.getHiddenStems(actor);
    result.addAll(hiddenStems.stream().map(eachHiddenStems -> this.getReaction(eachHiddenStems, actee)).collect(Collectors.toList()));
    return result;
  }
  
  /**
   * 從天干以及其關係，取得其目標天干，例如：甲的劫財，傳回乙
   * @param actor 主角
   * @param reactions 相對關係
   * @return 傳回目標天干 
   */
  @NotNull
  public static Stem getHeavenlyStems(@NotNull Stem actor , @NotNull Reactions reactions)
  {
    switch (reactions)
    {
      case 比肩:
        return actor;
      case 劫財:
        return Stem.getHeavenlyStems(actor.getFiveElement(), !actor.getBooleanValue());
      case 正印:
        return Stem.getHeavenlyStems(actor.getFiveElement().getProducer(), !actor.getBooleanValue());
      case 偏印:
        return Stem.getHeavenlyStems(actor.getFiveElement().getProducer(), actor.getBooleanValue());
      case 食神:
        return Stem.getHeavenlyStems(actor.getFiveElement().getProduct(), actor.getBooleanValue());
      case 傷官:
        return Stem.getHeavenlyStems(actor.getFiveElement().getProduct(), !actor.getBooleanValue());
      case 正官:
        return Stem.getHeavenlyStems(actor.getFiveElement().getDominator(), !actor.getBooleanValue());
      case 七殺:
        return Stem.getHeavenlyStems(actor.getFiveElement().getDominator(), actor.getBooleanValue());
      case 正財:
        return Stem.getHeavenlyStems(actor.getFiveElement().getDominateOver(), !actor.getBooleanValue());
      case 偏財:
        return Stem.getHeavenlyStems(actor.getFiveElement().getDominateOver(), actor.getBooleanValue());
    }
    throw new RuntimeException("RuntimeException while ReactionsUtil.getHeavenlyStems("+actor + "," + reactions+")");
  }
  
  
}
