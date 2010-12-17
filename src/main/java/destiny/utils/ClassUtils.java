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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSet;

public class ClassUtils implements Serializable
{
  public enum PERMISSION { READABLE , WRITABLE , READWRITABLE }
  
  private final static Set<String> idSet = new ImmutableSet.Builder<String>()
   .add("javax.persistence.Id")
   .add("javax.persistence.EmbeddedId")
   .build();
  
  
  // How to get annotations of a member variable ?
  // http://stackoverflow.com/questions/4453159/how-to-get-annotations-of-a-member-variable
  /**
   * 判斷是否是 JPA 的 id
   */
  public static boolean isId(Class<?> clazz , String propertyName)
  {
    for(Field field : clazz.getDeclaredFields())
    {
      if (field.getName().equals(propertyName))
      {
        //System.out.println("found " + field.getName());
        for(Annotation annotation : field.getDeclaredAnnotations())
        {
          //System.out.println("\t anno : " + annotation.annotationType().getName());
          return idSet.contains(annotation.annotationType().getName());
        }
      }
    }
    return false;
  }
  
  /** 
   * 判斷能否更改其值
   */
  public static boolean isWritable(Class<?> clazz , String propertyName)
  {
    BeanInfo beanInfo;
    try
    {
      beanInfo = Introspector.getBeanInfo(clazz);
      
      PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
      for(PropertyDescriptor pd : pds)
      {
        if (pd.getName().equals(propertyName))
        {
          //System.out.println("found " + propertyName);
          //System.out.println("read  : " + pd.getReadMethod());
          //System.out.println("write : " + pd.getWriteMethod());
          return (pd.getWriteMethod() != null);
        }
      }
    }
    catch (IntrospectionException e)
    {
      e.printStackTrace();
    }
    
    return false;
  }
  
  /** 取得某種 type 的 property list */
  public static List<String> getProperties(Class<?> clazz , PERMISSION type)
  {
    Set<String> set = new TreeSet<String>();
    try
    {
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
      
      PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
      for(PropertyDescriptor pd : pds)
      {
        //System.out.println(pd.getName() + " : getPropertyType = " + pd.getPropertyType());
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
    
    //排序 , id , key , user 放前面
    if(set.contains("id"))
    {
      resultList.add("id");
      set.remove("id");
    }
    if(set.contains("key"))
    {
      resultList.add("key");
      set.remove("key");
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
