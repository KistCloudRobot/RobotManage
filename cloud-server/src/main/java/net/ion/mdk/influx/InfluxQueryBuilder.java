package net.ion.mdk.influx;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import net.ion.mdk.jql.jpa.JQLOperator;

import java.time.ZoneId;
import java.util.*;

public abstract class InfluxQueryBuilder {
    @Parameter(description="Influx bucket 명", example = "my_bucket")
    private String bucket;

    @Schema(description="Influx measurement(table) 명", example = "my_measurement")
    @Getter @Setter
    private String measurement;

    private final String timeKey;
    private final String[] tagKeys;
    private ZoneId timezone = ZoneId.systemDefault();
    private static final PropertyNamingStrategies.SnakeCaseStrategy caseConverter = new PropertyNamingStrategies.SnakeCaseStrategy();

    protected InfluxQueryBuilder(String bucket, String measurement, String timeKey, String[] tagKeys, boolean needPivot) {
        this.bucket = bucket;
        this.measurement = measurement;
        this.timeKey = timeKey;
        this.tagKeys = tagKeys;
    }

    public String getMeasurement() {
        return this.measurement;
    }

    public String getBucket() {
        return bucket;
    }

    public String getTimeKey() {
        return this.timeKey;
    }

    private boolean isTagKey(String key) {
        for (String k : tagKeys) {
            if (k.equals(key)) return true;
        }
        return key.equals("_time");
    }

//    public FluxQuery buildQuery(InfluxJQL filter, String start, String stop, String[] sorts, int limit) {
//        if (filter == null) filter = noFilter;
//
//        FluxQuery query = new FluxQuery();
//        query.select(convertFieldNames(filter.fields));
//        query.range(start, stop);
//        query.setAggregate(filter.aggregate);
//        query.setFilter(make_flux_filter(filter.filter, true));
//        query.setFunctions(filter.functions.toArray(new String[filter.functions.size()]));
//        query.setGroupBy(convertFieldNames(filter.groupBy));
//        query.sort(convertFieldNames(sorts));
//        query.limit(limit);
//        return query;
//    }

    public String make_flux_filter(Map<String, Object> filter, boolean andConjunction) {
        if (filter == null) return null;

        String delimiter = andConjunction ? " and " : " or ";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JQLOperator op = JQLOperator.EQ;
            int idx_op = key.indexOf('@');
            if (idx_op >= 0) {
                op = JQLOperator.valueOf(key.substring(idx_op + 1).toUpperCase());
                key = key.substring(0, idx_op);
            }

            if (key.equals(this.timeKey)) {
                key = "_time";
            }
            else if (key.charAt(0) != '_'){
                key = caseConverter.translate(key);
            }

            String op1 = null, op2 = null;
            switch (op) {
                case AND:
                case OR:
                    sb.append('(');
                    sb.append(make_flux_filter((Map)value, op == JQLOperator.AND));
                    sb.append(')');
                    break;
                case EQ: op1 = " == "; break;
                case NE: op1 = " != "; break;
                case GT: op1 = " > ";  break;
                case GE: op1 = " >= "; break;
                case LT: op1 = " < ";  break;
                case LE: op1 = " <= "; break;
                case GE_LT: op1 = " >= "; op2 = " < ";  break;
                case GE_LE: op1 = " >= "; op2 = " <= "; break;
                case GT_LT: op1 = " > ";  op2 = " < ";  break;
                case GT_LE: op1 = " > ";  op2 = " <= "; break;
                case ISNULL: op1 = (Boolean)value ? "not exists" : "exists"; break;
                default:
                    throw new RuntimeException("not supported operator " + op);
            }

            if (op2 != null) {
                sb.append('(');
                Object[] values = (Object[])value;
                dumpPredicate(key, op1, values[0], sb);
                sb.append(" and ");
                dumpPredicate(key, op1, values[1], sb);
                sb.append(')');
            }
            else if (op1 != null) {
                if (op == JQLOperator.ISNULL) {
                    sb.append(op1).append(" r.").append(key);
                }
                else {
                    dumpPredicate(key, op1, value, sb);
                }
            }
            sb.append(delimiter);
        }
        if (sb.length() == 0) return null;

        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }

    private void dumpPredicate(String key, String op, Object value, StringBuilder sb) {
        sb.append("r.").append(key);
        sb.append(op);
        if (value.getClass() == String.class && !key.equals("_time")) {
            sb.append('"').append(value.toString()).append('"');
        }
        else {
            sb.append(value);
        }
    }

    public String[] convertFieldNames(String[] fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) return null;
        String[] out = new String[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i ++) {
            String key = fieldNames[i];
            out[i] = convertFieldName(key);
        }
        return out;
    }

    public String convertFieldName(String fieldName) {
        return caseConverter.translate(fieldName);
    }
}