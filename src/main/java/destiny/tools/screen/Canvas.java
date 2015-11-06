/**
 * @author smallufo
 * Created on 2002/8/28 at 下午 11:30:06
 */
package destiny.tools.screen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Canvas implements Serializable
{
  private int width;
  private int height;
  
  private byte[] content;
  
  @Nullable
  private Canvas parent = null;
  @NotNull
  private List<Child> children = new ArrayList<>();
  
  public Canvas(int width , int height)
  {
    this.width  = width;
    this.height = height;
    content = new byte[width*height];
    for (int i=0 ; i < content.length ; i++)
    {
      content[i] = ' ';
    }
  }
  
  public void setBackground(char c)
  {
    for (int i=0 ; i < content.length ; i++)
    {
      content[i] = (byte) c ;
    }
  }
  
  int getWidth()  { return this.width;  }
  public int getHeight() { return this.height; }
  //public byte[] getContent() { return this.content; }
  
  /**
   * 新增子 Canvas
   */
  public void add(@NotNull Canvas c , int x , int y)
  {
    c.setParent(this);
    children.add(new Child(c , x, y));
  }
  
  /**
   * 取得目前 Canvas 的 Container
   */
  @Nullable
  public Canvas getParent()
  {
    return this.parent;
  }
  
  /**
   * 設定目前 Canvas 的 Container
   */
  public void setParent(Canvas c)
  {
    this.parent = c;
  }
  
  /**
   * 取得目前 Canvas 的大小
   */
  int getSize()
  {
    int sum = 0;
    if (children.size() == 0)
      sum = width*height;
    else
    {
      sum = sum + width*height;
      for (Child aChildren : children) {
        sum = sum + (aChildren).getCanvas().getSize();
      }
    }  
    return sum;
  }
  
  /**
   * 設定橫向字元
   */
  public void setText(@NotNull byte[] b , int x , int y)
  {
    /**
     * [x,y] = content[ (y-1)*w + x -1 ]
     */
    for (int i=0 ; i < b.length ; i++)
    {
      content[(y-1)*width + x+i -1] = b[i];
    }
  }//setText();

  /**
   * 設定橫向字元
   */
  public void setText(@NotNull String s , int x , int y)
  {
    byte[] b = s.getBytes();
    this.setText(b , x , y);
  }
  
  @Nullable
  byte[] getContent()
  {
    byte[] result = null;
    //如果沒有 children , 就傳回自己的 content[]
    if ( this.children.size() == 0)
    {
      result = this.content;
    }
    else
    {
      //有 children
      for (Child aChildren : children) {
        Child child = (Child) aChildren;
        byte[] childContent = child.getCanvas().getContent();
        for (int j = 0; j < childContent.length; j++) {
          //走訪childContent , 必須計算 childContent 每個 byte 在 parent 的座標 , 並且複製過去
          int childX = j - ((j / child.getCanvas().getWidth())) * child.getCanvas().getWidth() + 1;
          int childY = (j / child.getCanvas().getWidth()) + 1;
          this.content[(child.getY() + (childY - 1) - 1) * this.width + (child.getX() + (childX - 1)) - 1] = childContent[j];
        }
      }//for
      result = this.content ;
    }//else
    return result;
  }//getByte()
  
  @NotNull
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    for (int i=0 ; i<height ; i++)
    {
      String s = new String(this.getContent(), i*width , width);
      sb.append(s);
      sb.append('\n');
    }
    return sb.toString();
  }//toString()
}
