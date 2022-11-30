package net.ion.mdk.influx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.ion.mdk.util.CyclicTask;
import net.ion.mdk.util.CyclicTaskScheduler;
//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "influx_task")
public class InfluxTask extends CyclicTask {

    @Repository
    public static class JQLRepository
            extends CyclicTask.JQLRepository<InfluxTask> {

        @Override
        public Long getEntityId(InfluxTask entity) {
            return entity.getId();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(CyclicTaskScheduler.class);

    @Column(nullable = false)
    @Schema(description="Influx Bucket 명", example = "ex) my_bucket", required = true)
    @Getter @Setter
    private String influxBucket;

    @Column(nullable = false)
    @Schema(description="Influx Measurement 명", example = "ex) my_measurement", required = true)
    @Getter @Setter
    private String influxMeasurement;

    @Column(nullable = false)
    @Schema(description="SQL SELECT 문 (Influx 에 저장할 데이터 칼럼명을 ','로 구분)", example = "ex) stack_power", required = true)
    @Getter @Setter
    private String sqlSelect;

    @Column(nullable = false)
    @Schema(description="SQL FROM 문 (데이터를 검색할 테이블 명)", example = "ex) cart_metrics", required = true)
    @Getter @Setter
    private String sqlFrom;

    @Column(nullable = true)
    @Schema(description="SQL WHERE 문 (데이터 검색 범위 설정)", example = "ex) time > '2020-01-01 00:00:00'")
    @Getter @Setter
    private String sqlWhere;

    @Column(nullable = false)
    @Schema(description="ID 컬럼 명 (auto increment 지정 필수)", example = "ex) _id", required = true)
    @Getter @Setter
    private String idColumn;

    @Column(nullable = false)
    @Schema(description="데이터 수집 시간이 저장된 컬럼 명(Timestamp 타입 필수)", example = "ex) time", required = true)
    @Getter @Setter
    private String timeColumn;

    @Column(nullable = false)
    @Schema(description="Influx Tag 로 사용할 칼럼 명들 (',' 로 구분하여 복수 칼럼 지정 가능)", example = "ex) gateway_id", required = true)
    @Getter @Setter
    private String tagColumns;

    @Column(insertable = false)// , columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Getter @Setter
    private Long lastSentIndex;

    @JsonIgnore
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private transient HashSet<String> tagSet;

    @JsonIgnore
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Getter
    private transient Timestamp timeStampOfLastSentData;

    @PostLoad
    void onLoad() {
        if (lastSentIndex == null) lastSentIndex = 0L;

        tagSet = new HashSet<>();
        for (String tag : tagColumns.split(",")) {
            String col = tag.trim();
            if (col.length() > 0) {
                tagSet.add(col);
            }
        }
        tagSet.add(timeColumn);
        tagSet.add(idColumn);
    }

    public String buildTimeSeriesQuery(LocalDateTime tsStart, LocalDateTime tsEnd) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(timeColumn).append(", ").append(tagColumns).append(", ").append(sqlSelect).
                append(" FROM ").append(sqlFrom).
                append(" WHERE ").append(timeColumn).append(" > '").append(tsStart).
                append("' AND ").append(timeColumn).append(" <= '").append(tsEnd).append('\'');

        if (sqlWhere != null && sqlWhere.length() > 0) {
            sb.append(" AND (").append(sqlWhere).append(")");
        }
        return sb.toString();
    }

    public String buildFirstTimestampQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(timeColumn).
                append(" FROM ").append(sqlFrom).
                append(" ORDER BY ").append(timeColumn).
                append(" LIMIT 1");
        return sb.toString();
    }

    public String buildTimeSeriesQuery(int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").
                append(timeColumn).append(", ").
                append(tagColumns).append(", ").
                append(idColumn).append(", ").
                append(sqlSelect).
                append("\nFROM ").append(sqlFrom).
                append("\nWHERE ").append(idColumn).append(" > ").append(lastSentIndex);

        if (sqlWhere != null && sqlWhere.length() > 0) {
            sb.append("\n\tAND (").append(sqlWhere).append(")");
        }
        sb.append("\nORDER BY ").append(idColumn);
        sb.append("\nLIMIT ").append(limit);
        return sb.toString();
    }

    public void updateLastSentIndex(List<Map<String, Object>> rs) {
        Map<String, Object> lastEntity = rs.get(rs.size() - 1);
        Number id = (Number) lastEntity.get(idColumn);
        this.lastSentIndex = id.longValue();
        this.timeStampOfLastSentData = (Timestamp)lastEntity.get(timeColumn);
        logger.info("last entity sent to Influx: " + lastEntity);
    }

    public Point makePoint(Map<String, Object> row) {

        Point point = Point.measurement(this.influxMeasurement);

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (val == null) continue;

            if (tagSet.contains(key)) {
                if (key.equals(timeColumn)) {
                    Timestamp ts = (Timestamp)val;
                    point.time(ts.toInstant(), WritePrecision.S);
                }
                else if (!key.equals(idColumn)) {
                    point.addTag(key, val.toString());
                }
                continue;
            }

            Class<?> cls = val.getClass();
            if (cls == Boolean.class) {
                point.addField(key, (Boolean)val);
            }
            else if (Number.class.isAssignableFrom(cls)) {
                point.addField(key, (Number)val);
            }
            else {
                point.addField(key, val.toString());
            }
        }
        return point;
    }

}
