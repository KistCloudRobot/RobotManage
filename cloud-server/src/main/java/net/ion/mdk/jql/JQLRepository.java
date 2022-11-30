package net.ion.mdk.jql;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.List;

public interface JQLRepository<ENTITY, ID> {

    Class<ENTITY> getEntityType();

    String getPrimaryKey();

    ENTITY find(ID id);

    Iterable<ENTITY> list();

    Page<ENTITY> list(Pageable pageRequest);

    Iterable<ENTITY> list(Sort sort, int limit);

    Iterable<ENTITY> list(Map<String, Object> conditions, int limit);

    Page<ENTITY> list(Map<String, Object> conditions, Pageable pageReq);

    Iterable<ENTITY> list(Map<String, Object> filterConditions, Sort sort, int limit);

    long  count(Map<String, Object> filterConditions);

    List<ENTITY> getEntities(ID[] idList);

    ENTITY findTop(Map<String, Object> filterConditions, Sort sort);


    ENTITY insert(ENTITY entity);

    ENTITY update(ID id, Map<String, Object> updateSet) throws IOException;

    List<ENTITY> update(ID[] idList, Map<String, Object> updateSet) throws IOException;

    void update(Collection<ENTITY> entities) throws IOException;

    void delete(ID id);

    int delete(ID[] idList);

    int delete(Collection<ID> idList);

}
