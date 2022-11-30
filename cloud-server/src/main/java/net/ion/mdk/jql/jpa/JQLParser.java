package net.ion.mdk.jql.jpa;

import net.ion.mdk.util.ClassUtils;
import org.springframework.core.convert.ConversionService;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;

class JQLParser {

    private final CriteriaBuilder cb;
    private final ConversionService conversionService;
    private boolean has_distinct_query;

    public JQLParser(CriteriaBuilder cb, ConversionService conversionService) {
        this.cb = cb;
        this.conversionService = conversionService;
    }

    boolean isDistinct() {
        return has_distinct_query;
    }

    static Field getField(Class<?> entityType, String fieldName) {
        for (Field f : entityType.getDeclaredFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        Class<?> superType = entityType.getSuperclass();
        if (superType == null) {
            throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
        return getField(superType, fieldName);
    }

    private From getSubQueryRoot(From from, String key, boolean fetch) {
        Class<?> fieldType = ClassUtils.getElementType(getField(from.getJavaType(), key));
        if (JQLRepositoryBase.Util.findRepository(fieldType) != null) {
            this.has_distinct_query |= !fetch;
            Join<Object, Object> join = fetch ? (Join<Object, Object>) from.fetch(key, JoinType.LEFT)
                    : from.join(key, JoinType.LEFT);
            return join;
        }
        return from;
    }

    private final static String SELECT_MORE = "select+";
    public Predicate parse(From<?,?> root, Map<String, Object> filter, boolean fetchData, boolean andConjunction) {
        // "joinColumn명" : { "id@?EQ" : "joinedColumn2.joinedColumn3.columnName" }; // Fetch 자동 수행.
        //   --> @?EQ 기능은 넣되, 숨겨진 고급기능으로..
        // "select@" : ["attr1", "attr2", "attr3" ] 추가??
        // "select+@" : ["attr1", "attr2", "attr3" ] 추가??
        // "groupBy@" : ["attr1", "attr2/attr3" ]

        List<Predicate> predicates = new ArrayList<>();
        Class<?> entityType = root.getJavaType();
        List<String> selectedAttrs = (List<String>)filter.get(SELECT_MORE);
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            int op_start = key.indexOf('@');
            JQLOperator op = null;
            boolean fetch = fetchData;
            if (op_start >= 0) {
                String cmd = key.substring(op_start + 1).toUpperCase();
                key = key.substring(0, op_start);
                op = JQLOperator.valueOf(cmd);
                if (op == JQLOperator.IN) {
                    fetch = false;
                    op = JQLOperator.EQ;
                }
            }
            else {
                op = JQLOperator.EQ;
            }

            Predicate cond;
            From subRoot = getSubQueryRoot(root, key, fetch);
            if (subRoot != root) {
                if (value instanceof Map) {
                    boolean andOp = isAndConjunction(op, true);
                    Map<String, Object> subFilter = (Map<String, Object>) value;
                    if (subFilter.isEmpty()) continue;
                    cond = parse(subRoot, subFilter, fetch, andOp);
                }
                else if (value instanceof Collection && !isAndConjunction(op, false)) {
                    List<Predicate> or_predicates = new ArrayList<>();
                    for (Object c : (Collection)value) {
                        cond = parse(subRoot, (Map)c, fetchData, true);
                        if (cond != null) {
                            or_predicates.add(cond);
                        }
                    }
                    cond = or_predicates.size() == 0 ? null :
                            cb.or(or_predicates.toArray(new Predicate[or_predicates.size()]));
                } else {
                    throw new RuntimeException("invalid JQL operator for joined entity:" +
                            root.getJavaType().getName() + "." + key + " @" + op);
                }
            }
            else {
                if (selectedAttrs != null && !selectedAttrs.contains(key)) {
                    selectedAttrs.add(key);
                }

                Class<?> fieldType = getField(entityType, key).getType();
                Expression<?> column = root.get(key).as(fieldType);
                Class<?> accessType = op.getAccessType(value, fieldType);
                value = conversionService.convert(value, accessType);
                cond = op.createPredicate(cb, column, value);
            }
            if (cond != null) {
                predicates.add(cond);
            }
        }

        if (fetchData) {
            for (String f : getFetchEagerFields(entityType)) {
                /**
                 * SearchQuery 는 FetchType.EAGER 컬럼에 대해 자동 Fetch 를 지원하지 못한다.
                 */
                if (filter.get(f) == null) {
                    root.fetch(f, JoinType.LEFT);
                }
            }
        }

        switch (predicates.size()) {
            case 0:
                return null;//cb.isTrue(cb.literal(true));
            case 1:
                return predicates.get(0);
            default:
                Predicate[] preds = predicates.toArray(new Predicate[predicates.size()]);
                if (andConjunction) {
                    return cb.and(preds);
                } else {
                    return cb.or(preds);
                }
        }
    }

    private boolean isAndConjunction(JQLOperator op, boolean defaultResult) {
        if (op == JQLOperator.EQ || op == JQLOperator.IN) {
            return defaultResult;
        }
        if (op != JQLOperator.AND && op != JQLOperator.OR) {
            throw new RuntimeException("invalid join operator " + op);
        }
        return op == JQLOperator.AND;
    }

    private static HashMap<Class, String[]> autoFetchFields = new HashMap<>();
    private String[] getFetchEagerFields(Class<?> entityType) {
        synchronized (autoFetchFields) {
            String[] fields = autoFetchFields.get(entityType);
            if (fields == null) {
                ArrayList<String> fieldList = new ArrayList<>();
                registerAutoFetchFields(fieldList, entityType);
                fields = fieldList.toArray(new String[fieldList.size()]);
                autoFetchFields.put(entityType, fields);
            }
            return fields;
        }
    }

    private void registerAutoFetchFields(ArrayList<String> fields, Class<?> entityType) {
        if (entityType == Object.class) {
            return;
        }

        for (Field f : entityType.getDeclaredFields()) {
            ManyToOne mto1 = f.getAnnotation(ManyToOne.class);
            OneToOne oto1 = f.getAnnotation(OneToOne.class);
            if (mto1 != null && mto1.fetch() == FetchType.EAGER ||
                    oto1 != null && oto1.fetch() == FetchType.EAGER) {
                fields.add(f.getName());
            }
        }
        registerAutoFetchFields(fields, entityType.getSuperclass());
    }
}

