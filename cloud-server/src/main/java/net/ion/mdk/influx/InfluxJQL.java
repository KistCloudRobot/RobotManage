package net.ion.mdk.influx;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InfluxJQL {

    @Schema(description="검색 범위 시작", example = "ex) -7d, 2022-02-02T00:00:00Z", required = true)
    @Getter @Setter
    private String start;

    @Null
    @Schema(description="검색 범위 종료", example = "ex) -7d, 2022-02-02T00:00:00Z", nullable = true)
    @Getter @Setter
    private String stop;


    @Schema(description="Aggregate 시간 단위", example = "ex) 15m, 1h, 1d", nullable = true)
    @Getter @Setter
    private String aggregate;

    @Schema(description="Aggregate 유형(기본값: mean)", example = "ex) min, max, sum 등등", nullable = true)
    @Getter @Setter
    private String aggregateType;

    @Schema(description="검색 결과 정렬 (time 과 tagKey 는 기본 포함됨. 생략 시 필드 전체 선택)", nullable = true)
    @Getter @Setter
    private String[] sorts;

    @Schema(description="검색 결과 개수 제한", example = "10", nullable = true)
    @Getter @Setter
    private Integer limit;

    @Schema(description="JQL Filter (출력 결과에 대한 필터)",
            example = "{\n \"stackVoltage@ge\": 65,\n  \"gatewayId\" : \"a32104293020\"\n}", nullable = true)
    @Getter @Setter
    private Map<String, Object> filter;

    @Schema(description="검색 결과에 포함할 필드들 (time 과 tagKey 는 기본 포함됨. 생략 시 필드 전체 선택)", nullable = true)
    @Getter @Setter
    private String[] fields;

    @Schema(description="추가적인 Influx 함수들", nullable = true)
    @Getter @Setter
    private String[] functions;

    @Schema(description="Group-By field 목록", nullable = true)
    @Getter @Setter
    private String[] groupBy;

    private ZoneId timezone = ZoneId.systemDefault();

    public InfluxJQL timezone(ZoneId zoneId) {
        this.timezone = zoneId;
        return this;
    }

    public InfluxJQL range(String start, String stop) {
        this.start = toTimeString(start, true);
        this.stop = toTimeString(stop, false);
        return this;
    }

    public InfluxJQL select(String[] fields) {
        this.fields = fields;
        return this;
    }

    public InfluxJQL filter(HashMap<String, Object> filter) {
        this.filter = filter;
        return this;
    }

    public InfluxJQL sort(String[] sorts) {
        this.sorts = sorts;
        return this;
    }

    public InfluxJQL aggregate(String aggregate) {
        this.aggregate = aggregate;
        return this;
    }

    public InfluxJQL limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public InfluxJQL groupBy(String[] fields) {
        this.groupBy = fields;
        return this;
    }

    public InfluxJQL functions(String[] functions) {
        this.functions = functions;
        return this;
    }

    public String buildFlux(boolean doPivot, InfluxRepository repo) {
        final String bucket = repo.getBucket();
        final String measurement = repo.getMeasurement();

        StringBuilder sb = new StringBuilder();

        assertNotEmpty(bucket, "bucket");
        sb.append("from(bucket: ").append(toQuote(bucket)).append(")");

        assertNotEmpty(start, "start");
        sb.append("\n |> range(").append("start: ").append(toTimeString(start, true));
        if (!isEmpty(stop)) {
            sb.append(", stop: ").append(toTimeString(stop, false)).append(")");
        }
        else {
            sb.append(")");
        }

        assertNotEmpty(measurement, "measurement");
        sb.append("\n |> filter(fn: (r) => r._measurement == ").append(toQuote(measurement)).append(")");
        if (!isEmpty(fields)) {
            sb.append("\n |> filter(fn: (r) => ");
            for (String col : repo.toPhysicalColumnNames(fields)) {
                sb.append("r._field == ").append(toQuote(col)).append(" or ");
            }
            sb.setLength(sb.length() - 4);
            sb.append(')');
        }

        if (!isEmpty(aggregate)) {
            String type = isEmpty(aggregateType) ? "mean" : aggregateType;
            sb.append("\n |> aggregateWindow(every: ").append(aggregate)
                    .append(", fn: ").append(type).append(")");
        }

        if (!isEmpty(functions)) {
            for (String function : functions) {
                sb.append("\n |> ").append(function);
            }
        }

        sb.append("\n |> drop(columns: [\"_measurement\", \"_start\", \"_stop\"").append("])");

        if (doPivot) {
            sb.append("\n |> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")");
            // @zee pivot 다음에 groupKey 를 변경하여야 한다.
            if (isEmpty(groupBy)) {
                // @zee 참고) group 을 _measurement 로 지정하지 않으면, tagKey 로 그룹핑된 table(=resultSet) 들이 생성됨.
                sb.append("\n |> group(columns: [\"_measurement\"])");
            }
        }
        else {
            sb.append("\n |> filter(fn: (r) => exists r._value)");
        }

        String filter = build_filter(this.filter, repo);
        if (!isEmpty(filter)) {
            sb.append("\n |> filter(fn: (r) => ").append(filter).append(')');
        }

        if (!isEmpty(groupBy)) {
            sb.append("\n |> group(columns: [");
            for (String f : repo.toPhysicalColumnNames(groupBy)) {
                sb.append(toQuote(f));
            }
            sb.append("])");
        }

        // @zee sort 는 pivot 다음에 실행하여야 한다.
        if (!isEmpty(sorts)) {
            for (String col : sorts) {
                boolean is_desc = false;
                switch (col.charAt(0)) {
                    case '-':
                        is_desc = true;
                        // no break;
                    case '+':
                        col = col.substring(1);
                    default:
                        break;
                }

                sb.append("\n |> sort(columns: [");
                sb.append(toQuote(repo.toPhysicalColumnName(col)));
                sb.append("], desc: ").append(is_desc).append(")");
            }
        }

        int safe_limit = this.hasValidLimit() ? this.limit : repo.getDefaultLimit();
        sb.append("\n |> limit(n: ").append(safe_limit).append(')');

        return sb.toString();
    }

    private String toTimeString(String time, boolean isStart) {
        if (isEmpty(time)) return null;

        if (time.lastIndexOf('-') < 4) {
            return time;
        }
        if (time.length() > 10 && time.charAt(10) == 'T') {
            return time;
        }

        if (time.length() == 10) {
            if (isStart) {
                time += " 00:00:00";
            }
            else {
                time += " 23:59:59.999";
            }
        }
        Timestamp dt = Timestamp.valueOf(time);
        String res = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dt.toInstant().atZone(ZoneId.systemDefault()));
        return res;
    }

    private String toQuote(String v) {
        return '"' + v + '"';
    }

    private boolean isEmpty(Collection v) {
        return v == null || v.size() == 0;
    }

    private boolean isEmpty(Object[] v) {
        return v == null || v.length == 0;
    }

    public static boolean isEmpty(String v) {
        return v == null || v.trim().length() == 0;
    }

    private boolean isEmpty(Object v) {
        return v == null || isEmpty(v.toString());
    }

    private void assertNotEmpty(String v, String key) {
        if (v == null || v.trim().length() == 0) {
            throw new RuntimeException("Error: " + key + " is not specified");
        }
    }

    private void assertNotNull(Object v, String key) {
        if (v == null) {
            throw new RuntimeException("Error: " + key + " is not specified");
        }
    }

    private String build_filter(Object filter, InfluxRepository repo) {
        if (filter instanceof Map) {
            if (!((Map)filter).isEmpty()) {
                return repo.make_flux_filter((Map<String, Object>) filter, true);
            }
        }
        else if (filter instanceof String) {
            return filter.toString();
        }
        return null;
    }

    public boolean hasValidLimit() {
        return limit != null && limit > 0;
    }
}
