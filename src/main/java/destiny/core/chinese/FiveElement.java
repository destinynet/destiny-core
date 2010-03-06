/**
 * @author smallufo 
 * Created on 2005/7/2 at 上午 05:46:48
 */ 
package destiny.core.chinese;

import java.io.Serializable;

public enum FiveElement implements FiveElementIF , Serializable
{
  木('木'),
  火('火'),
  土('土'),
  金('金'),
  水('水');
  
  private char name;
  
  FiveElement(char c)
  {
    this.name = c;
  }
  
  @Override
  public String toString()
  {
    return String.valueOf(name);
  }
  
  public FiveElement getFiveElement()
  {
    return this;
  }
  
  public boolean equals(FiveElementIF f)
  {
    if (f.getFiveElement() == this)
      return true;
    else
      return false;
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
        return null;
    }
  }//produceTo()

  
  /** 此五行是否生另一五行 */
  public boolean isProducingTo(FiveElementIF f)
  {
    if (f.getFiveElement().getProducer() == this)
      return true;
    else
      return false;
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
        return null;
    }
  }//producedBy()
  
  
  /** 此五行是否被另一五行所生 */
  public boolean isProducedBy(FiveElementIF f)
  {
    if (f.getFiveElement().getProduct() == this)
      return true;
    else
      return false;
  }
  
  
  /** 取得此五行所剋之五行 （木克土） */
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
        return null;
    }
  }//beatTo()
  
  
  /** 此五行是否剋另一五行 , Dominator : 支配者 */
  public boolean isDominatorOf(FiveElementIF f)
  {
    if (f.getFiveElement().getDominator() == this)
      return true;
    else
      return false;
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
        return null;
    }
  }//beatenBy()
  
  /** 此五行是否被另一五行所剋 */
  public boolean isDominatedBy(FiveElementIF f)
  {
    if (f.getFiveElement().getDominateOver() == this)
      return true;
    else
      return false;
  }
  
}
