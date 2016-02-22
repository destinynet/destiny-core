package destiny.tools;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface AbstractDao<T> {
  Optional<T> get(Serializable id);
  T save(T t);
  T update(T t);
  void delete(T t);
  void evict(Serializable id);
  
  Class<T> getClassType();
  
  T flush(T t);
  T merge(T t);
  void refresh(T t);
  
  long getCount();
  List<T> findAll();
  List<T> findAll(int start, int count);
}
