/**
 * @author smallufo
 * Created on 2010/12/15 at 下午4:13:36
 */
package destiny.tools;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

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

public class ClassUtils implements Serializable {

  public enum PERMISSION {READABLE, WRITABLE, BOTH, OR}

  private final static Set<String> idSet = new ImmutableSet.Builder<String>()
    .add("javax.persistence.Id")
    .add("javax.persistence.EmbeddedId")
    .build();


  // How to get annotations of a member variable ?
  // http://stackoverflow.com/questions/4453159/how-to-get-annotations-of-a-member-variable

  /**
   * 判斷是否是 JPA 的 id
   */
  public static boolean isId(@NotNull Class<?> clazz, String propertyName) {
    boolean value = false;
    for (Field field : clazz.getDeclaredFields()) {
      if (field.getName().equals(propertyName)) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
          String typeName = annotation.annotationType().getName();
          if (idSet.contains(typeName)) {
            value = true;
            break;
          }
        }
      }
    }
    return value;
  }

  /**
   * 判斷能否更改其值
   */
  public static boolean isWritable(Class<?> clazz, String propertyName) {
    BeanInfo beanInfo;
    try {
      beanInfo = Introspector.getBeanInfo(clazz);

      PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor pd : pds) {
        if (pd.getName().equals(propertyName)) {

          return (pd.getWriteMethod() != null);
        }
      }
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    return false;
  }

  /** 取得某種 type 的 property list */
  @NotNull
  public static List<String> getProperties(Class<?> clazz, @NotNull PERMISSION type) {
    Set<String> set = new TreeSet<>();
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

      PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor pd : pds) {
        if (pd.getPropertyType() != Class.class) {
          switch (type) {
            case READABLE: //可讀
            {
              if (pd.getReadMethod() != null)
                set.add(pd.getName());
              break;
            }
            case WRITABLE: //可寫
            {
              if (pd.getWriteMethod() != null)
                set.add(pd.getName());
              break;
            }
            case BOTH: //可讀+可寫
            {
              if (pd.getReadMethod() != null && pd.getWriteMethod() != null)
                set.add(pd.getName());
              break;
            }
            case OR: //未指定
              set.add(pd.getName());
          }
        }
      }
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    List<String> resultList = new ArrayList<>();

    //排序 , id , key , user 放前面
    if (set.contains("id")) {
      resultList.add("id");
      set.remove("id");
    }
    if (set.contains("key")) {
      resultList.add("key");
      set.remove("key");
    }
    if (set.contains("user")) {
      resultList.add("user");
      set.remove("user");
    }

    //剩下的隨便放
    resultList.addAll(new ArrayList<>(set));

    return resultList;
  }

}
