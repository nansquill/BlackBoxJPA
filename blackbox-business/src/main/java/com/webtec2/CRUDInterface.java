package com.webtec2;

import javax.ws.rs.core.Response;
import java.util.List;


public interface CRUDInterface<T> {
    //Create new T -> Path: /create 
    Response create(T t);
    //Read all T data -> Path: / 
    List<T> readAll();
    //Read T data by id -> Path: /{id} 
    T read(long id);
    //Update T data by obj -> Path: /update
    Response update(T t);
    //delete T obj -> Path: /delete
    Response delete(long id);
}
