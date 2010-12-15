/**
 * @author smallufo
 * Created on 2010/12/15 at 下午4:13:36
 */
package destiny.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassUtils implements Serializable
{
  public enum PERMISSION { READABLE , WRITABLE , READWRITABLE }
  
  public static List<String> getProperties(Class<?> clazz , PERMISSION type)
  {
    Set<String> set = new TreeSet<String>();
    try
    {
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
      
      PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
      for(PropertyDescriptor pd : pds)
      {
        if ( pd.getPropertyType() != Class.class)
        {
          switch(type)
          {
            case READABLE : //可讀 
            {
              if (pd.getReadMethod() != null)
                set.add(pd.getName());
              break;
            }
            case WRITABLE : //可寫
            {
              if (pd.getWriteMethod() != null)
                set.add(pd.getName());
              break;
            }
            case READWRITABLE : //可讀+可寫
            {
              if (pd.getReadMethod() != null && pd.getWriteMethod() != null)
                set.add(pd.getName());
            }
          }
          //System.out.println(pd.getName()  + " : " + pd.getPropertyType() + " : " + (pd.getReadMethod() == null ? "null" : pd.getReadMethod().getName()) + " , " + (pd.getWriteMethod() == null ? "null" : pd.getWriteMethod().getName()) );  
        }
      }
    }
    catch (IntrospectionException e)
    {
      e.printStackTrace();
    }
    
    List<String> resultList = new ArrayList<String>();
    
    //排序 , id , user 放前面
    if(set.contains("id"))
    {
      resultList.add("id");
      set.remove("id");
    }
    if(set.contains("user"))
    {
      resultList.add("user");
      set.remove("user");
    }
    
    //剩下的隨便放
    for(String s : set)
      resultList.add(s);
    
    return resultList;
    
  }
}
