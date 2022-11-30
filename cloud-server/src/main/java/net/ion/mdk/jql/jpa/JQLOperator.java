package net.ion.mdk.jql.jpa;

import net.ion.mdk.util.ClassUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum JQLOperator {
    IN,
    AND,
    OR,
    EQ(null) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            Predicate cond;
            if (value.getClass().isArray()) {
                cond = column.in((Object[]) value);
            }
            else if (value instanceof Collection) {
                cond = column.in((Collection<?>) value);
            }
            else {
                cond = cb.equal(column, value);
            }
            return cond;
        }
    },

    NE(null) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            return cb.not(EQ.createPredicate(cb, column, value));
        }
    },


    LIKE(null) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            Predicate cond;
            if (value.getClass().isArray()) {
                List<Predicate> or_predicates = new ArrayList<>();
                for (Object s : (Object[])value) {
                    cond = cb.like((Expression<String>) column, s.toString());
                    or_predicates.add(cond);
                }
                cond = cb.or(or_predicates.toArray(new Predicate[or_predicates.size()]));
            }
            else if (value instanceof Collection) {
                List<Predicate> or_predicates = new ArrayList<>();
                for (Object s : (Collection)value) {
                    cond = cb.like((Expression<String>) column, s.toString());
                    or_predicates.add(cond);
                }
                cond = cb.or(or_predicates.toArray(new Predicate[or_predicates.size()]));
            }
            else {
                cond = cb.like((Expression<String>) column, value.toString());
            }
            return cond;
        }
    },

    NOTLIKE(null) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            return cb.not(LIKE.createPredicate(cb, column, value));
        }
    },

    ISNULL {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            if (value == Boolean.TRUE) {
                return cb.isNull(column);
            }
            else if (value == Boolean.FALSE) {
                return cb.isNotNull(column);
            }
            else {
                throw new RuntimeException("invalid argument: " + value);
            }
        }
    },

    GT {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            return cb.greaterThan(column, (Expression)cb.literal(value));
        }
    },

    GE {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            return cb.greaterThanOrEqualTo(column, (Expression)cb.literal(value));
        }
    },

    LT {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            return cb.lessThan(column, (Expression)cb.literal(value));
        }
    },

    LE {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            return cb.lessThanOrEqualTo(column, (Expression)cb.literal(value));
        }
    },

    GE_LT(true) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            Object[] range = (Object[])value;
            Predicate from = cb.greaterThanOrEqualTo(column, (Expression) cb.literal(range[0]));
            Predicate to = cb.lessThan(column, (Expression) cb.literal(range[1]));
            return cb.and(from, to);
        }
    },

    GE_LE(true) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            Object[] range = (Object[])value;
            Predicate from = cb.greaterThanOrEqualTo(column, (Expression) cb.literal(range[0]));
            Predicate to = cb.lessThanOrEqualTo(column, (Expression) cb.literal(range[1]));
            return cb.and(from, to);
        }
    },

    GT_LT(true) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            Object[] range = (Object[])value;
            Predicate from = cb.greaterThan(column, (Expression) cb.literal(range[0]));
            Predicate to = cb.lessThan(column, (Expression) cb.literal(range[1]));
            return cb.and(from, to);
        }
    },

    GT_LE(true) {
        public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
            Object[] range = (Object[])value;
            Predicate from = cb.greaterThan(column, (Expression) cb.literal(range[0]));
            Predicate to = cb.lessThanOrEqualTo(column, (Expression) cb.literal(range[1]));
            return cb.and(from, to);
        }
    };

    private final boolean isArray;
    private final boolean acceptCollection;

    JQLOperator() {
        this.isArray = false;
        this.acceptCollection = false;
    }

    JQLOperator(boolean isArray) {
        this.isArray = isArray;
        this.acceptCollection = false;
    }

    JQLOperator(Object unused) {
        this.isArray = false;
        this.acceptCollection = true;
    }

    public Predicate createPredicate(CriteriaBuilder cb, Expression column, Object value) {
        throw new RuntimeException("not implemented");
    }

    public Class<?> getAccessType(Object value, Class<?> fieldType) {
        if (this.isArray) {
            fieldType = ClassUtils.getArrayType(fieldType);
        }
        else if (this == ISNULL) {
            return boolean.class;
        }
        else if (this.acceptCollection && (value.getClass().isArray() || value instanceof Collection)) {
            fieldType = ClassUtils.getArrayType(fieldType);
        }
        return fieldType;
    }
}
