/*
 * @author smallufo
 * @date 2004/8/13
 * @time 下午 07:27:07
 */
package destiny.tools.ColorCanvas;

import java.io.Serializable;


class Child implements Serializable
{
  private ColorCanvas colorCanvas;
  private int x;
  private int y;

  public Child(ColorCanvas c , int x , int y)
  {
    this.colorCanvas = c;
    this.x = x;
    this.y = y;
  }
  
  public void setParent(ColorCanvas c)
  {
    this.colorCanvas.setParent(c);
  }
  
  public ColorCanvas getCanvasCanvas()
  {
    return this.colorCanvas;
  }
  
  public int getX() { return this.x; }
  public int getY() { return this.y; }
}
