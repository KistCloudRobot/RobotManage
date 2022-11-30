package net.ion.mdk.jql.jpa;

import net.ion.mdk.util.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JPAQueryBuilder<ENTITY, ID> {

    private Class<ID> idType;
    private Class<ENTITY> entityType;
    private String pkColName;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private EntityManager entityManager;
    private boolean hasGeneratedId;

    public JPAQueryBuilder() {
    }

    // @zee final 선언 금지 -> Proxy 에 의해 overriding 되지 않는다.
    public Class<ENTITY> getEntityType() {
        return entityType;
    }

    public abstract ID getEntityId(ENTITY entity);

    public String getPrimaryKey() {
        return pkColName;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public boolean hasGeneratedId() {
        return hasGeneratedId;
    }

    /* Specification 사용예. 삭제하지 말 것.
    protected Specification<ENTITY> buildSearchSpecification(HashMap<String, Object> filterConditions) {
        if (filterConditions == null) {
            return null;
        }
        return (root, query, cb) -> {
            return build_where(root, filterConditions, ??);
        };
    }
     */

    protected <T> Query buildSearchQuery(Object filter, Sort sort, Class<T> resultType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query;
        if (resultType != entityType) {
            query = cb.createQuery(resultType);
        }
        else {
            query = cb.createQuery(entityType);
        }

        Root<ENTITY> root = query.from(entityType);

        Predicate where = build_where(root, filter, query);
        if (where != null) query.where(where);
        if (sort != null) {
            List<Order> orders = QueryUtils.toOrders(sort, root, cb);
            query.orderBy(orders);
        }
        if (resultType != entityType) {
            ArrayList<Selection<?>> selections = new ArrayList<>();
            createSelection(root, resultType, selections);
            query.multiselect(selections);
        }
        Query sql = entityManager.createQuery(query);
        return sql;
    }

    private ArrayList<Selection<?>> createSelection(Root<ENTITY> root, Class<?> resultType, ArrayList<Selection<?>> selections) {
        Class<?> superType = resultType.getSuperclass();
        if (superType != Object.class) {
            createSelection(root, superType, selections);
        }

        for (Field f : resultType.getDeclaredFields()) {
            String name = f.getName();
            selections.add(root.get(name).as(f.getType()));
        }
        return selections;
    }

    protected Query buildCountQuery(Map<String, Object> filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ENTITY> root = query.from(entityType);
        Predicate where = build_where(root, filter, null);
        if (where != null) query.where(where);
        query.select(cb.count(root));
        Query sql = entityManager.createQuery(query);
        return sql;
    }


    protected Query buildUpdateQuery(Object filter, Map<String, Object> updateSet) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate update = cb.createCriteriaUpdate(entityType);
        Root<?> root = update.from(entityType);
        for (Map.Entry<String, Object> entry : updateSet.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (false && (value instanceof Map || value instanceof List)) {
                Field f = JQLParser.getField(entityType, key);
                Type t = ClassUtils.getFirstGenericParameter(f.getGenericType());
                Class<?> joinType = ClassUtils.toClass(t);
                JQLRepositoryBase jqlRepository = JQLRepositoryBase.Util.findRepository(joinType);
                if (value instanceof Map) {
                    Object id = jqlRepository.convertID(((Map)value).get("id"));
                    value = jqlRepository.find(id);
                }
                else {
                    List list = (List)value;
                    for (int i = list.size(); --i >= 0; ) {
                        Object v = list.get(i);
                        Object id = jqlRepository.convertID(((Map)v).get("id"));
                        v = jqlRepository.find(id);
                        list.set(i, v);
                    }
                }
            }
            update.set(key, value);
        }
        Predicate where = build_where(root, filter, null);
        if (where != null) update.where(where);
        Query sql = entityManager.createQuery(update);
        return sql;
    }

    protected Query buildDeleteQuery(Object filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete delete = cb.createCriteriaDelete(entityType);
        Root<?> root = delete.from(entityType);
        Predicate where = build_where(root, filter, null);
        if (where != null) delete.where(where);
        Query sql = entityManager.createQuery(delete);
        return sql;
    }


    @PostConstruct
    private void initTypeInfo() {
        for (Method m : getClass().getDeclaredMethods()) {
            if (m.getName().equals("getEntityId")) {
                Class<?> ret_t = m.getReturnType();
                if (ret_t != Object.class) {
                    this.idType = (Class<ID>) ret_t;
                    this.entityType = (Class<ENTITY>) m.getParameterTypes()[0];
                    Field pkField = JPAUtils.findIdField(this.entityType);
                    this.pkColName = pkField.getName();
                    this.hasGeneratedId = pkField.getAnnotation(GeneratedValue.class) != null;
                    return;
                }
            }
        }

        if (this.entityType == null) {
            throw new RuntimeException("getEntityId method not found");
        }
    }

    public <T> T convert(Object arg, Class<T> type) {
        return conversionService.convert(arg, type);
    }

    protected ID convertID(Object _id) {
        return conversionService.convert(_id, this.idType);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    private static final Map _emptyMap = new HashMap();
    private Predicate build_where(Root<?> root, Object filter, CriteriaQuery<?> query) {
        CriteriaBuilder cb = getCriteriaBuilder();
        Map filterMap;
        if (filter instanceof Map) {
            filterMap = (Map) filter;
        }
        else if (filter != null) {
            filterMap = new HashMap();
            filterMap.put(this.pkColName + "@eq", filter);
            /** 참고 ID 검색 함수를 아래와 같이 처리할 수도 있다. (단, FetchType.EAGER 에 대한 다중 query 발생)
                Expression<?> column = root.get(this.pkColName).as(this.idType);
                return JQLOperator.EQ.createPredicate(cb, column, filter);
            */
        }
        else {
            /**
             * 참고) 왜 불필요하게 JQLParser 를 수행하는가?
             * CriteriaQuery 는 FetchType.EAGER 가 지정된 Column 에 대해
             * 자동으로 fetch 하는 기능이 없다. (2022.02.18 현재) 이에 대량 쿼리 발생시
             * 그 개수만큼 Join 된 칼럼을 읽어오는 추가적인 쿼리가 발생한다.
             * Parser 는 내부에서 이를 별도 검사하여 처리한다.
             */
            filterMap = _emptyMap;
        }

        JQLParser parser = new JQLParser(cb, conversionService);
        Predicate q = parser.parse(root, filterMap, query != null, true);
        if (parser.isDistinct() && query != null) {
            /** @zee 2022.01.26
             * OneToMany column 에 대한 @in 검색 시 동일 엔터티 중복 결과를 피하기 위해 distinct 가 필요하다.
             * JPA 검색은 일반적으로 Key를 포함한 Entity 전체를 검색하므로, distinct 를 항상 사용해도 무방하다.
             */
            query.distinct(true);
        }
        return q;
    }


    protected ENTITY find(ID id) {
        return entityManager.find(entityType, id);
    }

}