package destiny.tools.screen;

/**
 * @author smallufo
 * @date 2002/8/28
 * @time 下午 11:41:56
 */
public class testCanvas
{
  public static void main(String[] args)
  {
    Canvas c1 = new Canvas(30, 30);
    Canvas c2 = new Canvas(20, 20);
    Canvas c3 = new Canvas(10, 10);
    c1.add(c2, 5, 5);
    c2.add(c3, 5, 7);

    c1.setBackground('|');
    c2.setBackground('-');
    c3.setBackground('.');

    c1.setText("這是最大的c1".getBytes(), 5, 2);
    c2.setText("大家好".getBytes(), 5, 5);
    c2.setText("這是c2".getBytes(), 5, 6);
    c3.setText("這是c3".getBytes(), 2, 2);
    System.out.println(c3);
    System.out.println(c2);
    System.out.println(c1);
  }
}
