package destiny.utils;

import java.io.Serializable;

public interface AbstractDao<T>
{
  public T get(Serializable id);
  
  public T save(T t);
  
  public T update(T t);
  
  public void delete(T t);
}
