package destiny.utils;

import java.io.Serializable;
import java.util.List;

public interface AbstractDao<T>
{
  public T get(Serializable id);
  public T save(T t);
  public T update(T t);
  public void delete(T t);
  
  public Class<T> getClassType();
  
  public T flush(T t);
  public T merge(T t);
  public void refresh(T t);
  
  public long getCount();
  public List<T> findAll();
  public List<T> findAll(int start, int count);
}
