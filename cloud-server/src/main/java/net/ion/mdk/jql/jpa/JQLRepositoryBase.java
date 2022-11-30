package net.ion.mdk.jql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ion.mdk.jql.JQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

public abstract class JQLRepositoryBase<ENTITY, ID> extends JPAQueryBuilder<ENTITY, ID> implements JQLRepository<ENTITY, ID> {

    private final static HashMap<Class<?>, JQLRepositoryBase<?,?>>jqlServices = new HashMap<>();

    @Autowired
    private MappingJackson2HttpMessageConverter jsonConverter;

    public JQLRepositoryBase() {
        super();
    }

    @PostConstruct
    private void initService() {
        jqlServices.put(this.getEntityType(), this);
    }

    public ObjectMapper getObjectMapper() {
        return jsonConverter.getObjectMapper();
    }

    @Override
    public ENTITY find(ID id) {
        return super.find(id);
    }

    @Override
    public Iterable<ENTITY> list() {
        return list(null, null, -1);
    }

    @Override
    public Page<ENTITY> list(Pageable pageRequest) {
        return list(null, pageRequest);
    }

    @Override
    public Iterable<ENTITY> list(Sort sort, int limit) {
        return list(null, sort, limit);
    }

    @Override
    public Iterable<ENTITY> list(Map<String, Object> conditions, int limit) {
        return list(conditions, (Sort)null, limit);
    }

    @Override
    public Page<ENTITY> list(Map<String, Object> conditions, Pageable pageReq) {
        return list(conditions, pageReq, super.getEntityType());
    }

    //@Override
    public final <T> Page<T> list(Map<String, Object> conditions, Pageable pageReq, Class<T> resultType) {
        Query query = super.buildSearchQuery(conditions, pageReq.getSort(), resultType);
        int size = pageReq.getPageSize();
        int offset = (int) (pageReq.getPageNumber()) * size;
        List<T> res = query.setMaxResults(size)
                .setFirstResult(offset).getResultList();
        long count = count(conditions);
        return new PageImpl(res, pageReq, count);
    }

    @Override
    public long count(Map<String, Object> filterConditions) {
        long count = (Long) super.buildCountQuery(filterConditions).getSingleResult();
        return count;
    }

    @Override
    public Iterable<ENTITY> list(Map<String, Object> filterConditions, Sort sort, int limit) {
        return list(filterConditions, sort, limit, super.getEntityType());
    }

    //@Override
    public <T> List<T> list(Map<String, Object> filterConditions, Sort sort, int limit, Class<T> resultType) {
        Query query = super.buildSearchQuery(filterConditions, sort, resultType);

        /** Hibernate 버그: limit 값이 생략되면, join 된 row 수만큼 entity 가 생성된다.
         *  이에 limit 값 항상 명시 필요함. */
        if (limit < 0) limit = Integer.MAX_VALUE;

        query = query.setMaxResults(limit);
        List<T> res = query.getResultList();
        if (resultType != super.getEntityType()) {
            for (int i = res.size(); --i >= 0; ) {
                T v = (T)super.convert(res.get(i), resultType);
                res.set(i, v);
            }
        }
        return res;
    }

    @Override
    public List<ENTITY> getEntities(ID[] idList) {
        Query query = super.buildSearchQuery(idList, null, null);
        List<ENTITY> res = query.getResultList();
        return res;
    }



    @Override
    public ENTITY findTop(Map<String, Object> filterConditions, Sort sort) {
        Query query = super.buildSearchQuery(filterConditions, sort, this.getEntityType());
        List<ENTITY> res = query.setMaxResults(1).getResultList();
        return res.size() > 0 ? res.get(0) : null;
    }

    @Override
    @Transactional
    public ENTITY insert(ENTITY entity) {
        if (hasGeneratedId()) {
            ID id = getEntityId(entity);
            if (id != null) {
                throw new IllegalArgumentException("Entity can not be created with id");
            }
        }
        getEntityManager().persist(entity);
        return entity;
    }

    // Insert Or Update Entity
    // @Override
    public ENTITY insertOrUpdate(ENTITY entity) {
        ID id = getEntityId(entity);
        if (id != null) {
            return update(entity);
        }
        else {
            return insert(entity);
        }
    }

    // @Override
    @Transactional()
    public ENTITY update(ENTITY entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    @Transactional()
    public ENTITY update(ID id, Map<String, Object> updateSet) throws IOException {
        ENTITY entity = find(id);
        getObjectMapper().updateValue(entity, updateSet);
        return update(entity);
    }

    @Override
    @Transactional()
    public List<ENTITY> update(ID[] idList, Map<String, Object> updateSet) throws IOException {
        ArrayList<ENTITY> list = new ArrayList<>();
        for (ID id: idList) {
            list.add(update(id, updateSet));
        }
        return list;
    }

    @Override
    @Transactional()
    public void update(Collection<ENTITY> entities) throws IOException {
        for (ENTITY e: entities) {
            update(e);
        }
    }

    @Override
    @Transactional()
    public void delete(ID id) {
        EntityManager em = super.getEntityManager();
        ENTITY entity = em.find(getEntityType(), id);
        if (entity != null) em.remove(entity);
    }

    @Override
    @Transactional()
    public int delete(ID[] idList) {
        Query sql = super.buildDeleteQuery(idList);
        return sql.executeUpdate();
    }

    @Override
    @Transactional()
    public int delete(Collection<ID> idList) {
        Query sql = super.buildDeleteQuery(idList);
        return sql.executeUpdate();
    }

    public static class Util {
        public static JQLRepositoryBase findRepository(Class<?> entityType) {
            return JQLRepositoryBase.jqlServices.get(entityType);
        }
    }
}