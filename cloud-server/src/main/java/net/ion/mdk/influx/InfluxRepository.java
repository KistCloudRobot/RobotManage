package net.ion.mdk.influx;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.influxdb.query.FluxColumn;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import net.ion.mdk.jql.jpa.JQLOperator;
import org.apache.commons.text.CaseUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class InfluxRepository {

    private static final PropertyNamingStrategies.SnakeCaseStrategy toSnakeCase = new PropertyNamingStrategies.SnakeCaseStrategy();
    private final String bucket;
    private final String measurement;
    private final InfluxClient influxClient;
    private final int defaultLimit;

    public InfluxRepository(InfluxClient client, String bucket, String measurement) {
        this(client, bucket, measurement, 1000);
    }

    public InfluxRepository(InfluxClient client, String bucket, String measurement, int defaultLimit) {
        this.influxClient = client;
        this.bucket = bucket;
        this.measurement = measurement;
        this.defaultLimit = defaultLimit;
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public String getMeasurement() {
        return this.measurement;
    }

    public String getBucket() {
        return bucket;
    }

    public List<Map<String, Object>> queryEntities(InfluxJQL jql) {
        String flux = jql.buildFlux(true, this);
        List<FluxTable> tables = influxClient.queryFlux(flux);
        if (!jql.hasValidLimit()) ensureNotTooBigResult(tables);
        ArrayList<Map<String, Object>> res = extractEntities(tables, !jql.isEmpty(jql.getAggregate()));
        return res;
    }

    public List<Map<String, Object>> queryTimeSeries(InfluxJQL jql) {
        String flux = jql.buildFlux(false, this);
        List<FluxTable> tables = influxClient.queryFlux(flux);
        if (!jql.hasValidLimit()) ensureNotTooBigResult(tables);
        ArrayList<Map<String, Object>> res = extractTimeSeries(tables);
        return res;
    }

    public List<FluxTable> nativeQuery(InfluxJQL jql) {
        String flux = jql.buildFlux(false, this);
        List<FluxTable> tables = influxClient.queryFlux(flux);
        return tables;
    }

    private void ensureNotTooBigResult(List<FluxTable> tables) {
        int cntRecord = 0;
        for (FluxTable table : tables) {
            cntRecord += table.getRecords().size();
        }
        if (cntRecord >= defaultLimit) {
            throw new RuntimeException("Error: Too many results. Limit must be specified!");
        };
    }

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

            key = toPhysicalColumnName(key);

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

    protected String[] toPhysicalColumnNames(String[] fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) return null;
        String[] out = new String[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i ++) {
            String key = fieldNames[i];
            out[i] = toPhysicalColumnName(key);
        }
        return out;
    }

    protected String toPhysicalColumnName(String key) {
        if (key.charAt(0) != '_') {
            key = toSnakeCase.translate(key);
        }
        return key;
    }

    protected String toLogicalAttributeName(String key) {
        if (key.charAt(0) != '_') {
            key = CaseUtils.toCamelCase(key, false, camelCaseDelimiters);
        }
        return key;
    }

    public ArrayList<Map<String, Object>> extractTimeSeries(List<FluxTable> tables) {
        ArrayList<Map<String, Object>> results = new ArrayList<>();

        for (FluxTable table : tables) {
            ArrayList<Number[]> values = new ArrayList<>();
            HashMap series = new HashMap();
            series.put("data", values);
            results.add(series);

            for (FluxRecord rec : table.getRecords()) {
                if (values.size() == 0) {
                    for (FluxColumn col : table.getGroupKey()) {
                        String key = toLogicalAttributeName(col.getLabel());
                        series.put(key, rec.getValueByKey(col.getLabel()));
                    }
                }
                Number[] data = new Number[2];
                data[0] = rec.getTime().toEpochMilli();
                data[1] = (Number)rec.getValue();
                values.add(data);
            }
        }
        return results;
    }

    public ArrayList<Map<String, Object>> extractEntities(List<FluxTable> tables, boolean isAggregated) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        for (FluxTable table : tables) {
            for (FluxRecord rec : table.getRecords()) {
                Map<String, Object> camelValues = new HashMap<>();
                for(Map.Entry<String, Object> entry : rec.getValues().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (ignorableEntityKeys.contains(key)) continue;

                    if ("_time".equals(key)) {
                        Instant time = rec.getTime();
                        if (isAggregated) {
                            time = time.minusSeconds(1);
                        }
                        value = ZonedDateTime.ofInstant(time, ZoneId.systemDefault());
                    }
                    else {
                        key = toLogicalAttributeName(key);
                    }
                    camelValues.put(key, value);
                }
                result.add(camelValues);
            }
        }
        return result;
    }

    static final char[] camelCaseDelimiters = {'_'};
    static HashSet<String> ignorableEntityKeys = new HashSet<>();
    static {
        ignorableEntityKeys.add("result");
        ignorableEntityKeys.add("table");
    }

}
