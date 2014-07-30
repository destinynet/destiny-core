package destiny.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassFinder
{
  /** 列出此 packageName 之下的所有 classes , 如果沒有，則傳回 null */
  @Nullable
  @SuppressWarnings("rawtypes")
  public static Set<Class> getClasses(@NotNull String packageName , boolean recursive) throws ClassNotFoundException
  {
    Set<Class> classes = Collections.synchronizedSet(new HashSet<Class>());
    // Get a File object for the package
    File directory = null;
    try
    {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null)
        throw new ClassNotFoundException("Can't get class loader.");
      
      String path = packageName.replace('.', '/');
      //FIXME : 追蹤這個議題 http://stackoverflow.com/questions/3112255/how-to-make-maven-add-directory-entries-when-packaging 
      URL url = classLoader.getResource(path);             // ClassLoader.getResource() ，絕對不可以加上 "/"
      URL url2 = ClassFinder.class.getResource("/"+path); // Class.getResource() 一定要加上 "/"
      System.out.println("url = " + url);
      System.out.println("url2 = " + url2);
      
      if (url == null)
        throw new ClassNotFoundException("No resource for " + path);

      directory = new File(url.getFile());
    }
    catch (NullPointerException x)
    {
      throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package");
    }
    if (directory.exists())
    {
      for(File file : directory.listFiles())
      {
        String fileName = file.getName();
        if (file.isDirectory() && recursive)
        {
          String subPkg = packageName+'.'+file.getName();
          Set<Class> subClasses = ClassFinder.getClasses(subPkg , recursive);
          //System.out.println("searching dir : " + subPkg + " , found " + subClasses.size() + " classes");
          classes.addAll(subClasses);
        }
        else if (file.getName().endsWith(".class"))
        {
          String fqcn = packageName + '.' + fileName.substring(0, fileName.length() - 6);
          //System.out.println("class name : " + fqcn);
          try
          {
            classes.add(Class.forName(fqcn));  
          }
          catch(NoClassDefFoundError ignored)
          {
          }
          catch(ClassNotFoundException ignored)
          {
          }
        }
      }
    }
    else
    {
      throw new ClassNotFoundException(packageName + " does not appear to be a valid package");
    }
    if (classes.size() == 0)
      return null;
    return classes;
  }
}
