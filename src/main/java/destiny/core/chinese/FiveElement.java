/**
 * @author smallufo 
 * Created on 2005/7/2 at 上午 05:46:48
 */ 
package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public enum FiveElement implements IFiveElement, Serializable {
  木('木'),
  火('火'),
  土('土'),
  金('金'),
  水('水');
  
  private final char name;
  
  FiveElement(char c)
  {
    this.name = c;
  }
  
  @NotNull
  @Override
  public String toString()
  {
    return String.valueOf(name);
  }
  
  @NotNull
  public FiveElement getFiveElement()
  {
    return this;
  }
  
  public boolean equals(@NotNull IFiveElement f)
  {
    return f.getFiveElement() == this;
  }
  
  /** 取得此五行所生的五行（木生火） */
  public FiveElement getProduct()
  {
    switch (this)
    {
      case 木:
        return 火;
      case 火:
        return 土;
      case 土:
        return 金;
      case 金:
        return 水;
      case 水:
        return 木;
      default:
        throw new RuntimeException("impossible");
    }
  }//produceTo()

  
  /** 此五行是否生另一五行 */
  public boolean isProducingTo(@NotNull IFiveElement f)
  {
    return f.getFiveElement().getProducer() == this;
  }

  /** 取得哪個五行生此五行 （生木者為水） */
  public FiveElement getProducer()
  {
    switch (this)
    {
      case 木:
        return 水;
      case 火:
        return 木;
      case 土:
        return 火;
      case 金:
        return 土;
      case 水:
        return 金;
      default:
        throw new RuntimeException("impossible");
    }
  }//producedBy()
  
  
  /** 此五行是否被另一五行所生 */
  public boolean isProducedBy(@NotNull IFiveElement f)
  {
    return f.getFiveElement().getProduct() == this;
  }
  
  
  /** 取得此五行所剋之五行 （木剋土） */
  public FiveElement getDominateOver()
  {
    switch (this)
    {
      case 木:
        return 土;
      case 火:
        return 金;
      case 土:
        return 水;
      case 金:
        return 木;
      case 水:
        return 火;
      default:
        throw new RuntimeException("impossible");
    }
  }//beatTo()
  
  
  /** 此五行是否剋另一五行 , Dominator : 支配者 */
  public boolean isDominatorOf(@NotNull IFiveElement f)
  {
    return f.getFiveElement().getDominator() == this;
  }
  
  /** 取得此五行被哪個五行剋 （木被金剋） */
  public FiveElement getDominator()
  {
    switch (this)
    {
      case 木:
        return 金;
      case 火:
        return 水;
      case 土:
        return 木;
      case 金:
        return 火;
      case 水:
        return 土;
      default:
        throw new RuntimeException("impossible");
    }
  }//beatenBy()
  
  /** 此五行是否被另一五行所剋 */
  public boolean isDominatedBy(@NotNull IFiveElement f)
  {
    return f.getFiveElement().getDominateOver() == this;
  }
  
}
