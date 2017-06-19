package com.webtec2;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by yunus on 19/06/2017.
 */
public interface CRUDInterface<T> {
    Response create(T t);
    List<T> readAll();
    T read(long id);
    Response update(T t);
    Response delete(long id);
}
