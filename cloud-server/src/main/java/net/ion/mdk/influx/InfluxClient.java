package net.ion.mdk.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import net.ion.mdk.util.CyclicTaskScheduler;
import net.ion.mdk.util.NativeQueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:influx.properties")
public class InfluxClient extends CyclicTaskScheduler<InfluxTask> {

    private static final Logger logger = LoggerFactory.getLogger(InfluxClient.class);

    @Value("${token}")
    private String token;

    @Value("${org}")
    private String org;

    @Value("${url}")
    private String url;

    @Value("${enable-scheduler}")
    private boolean enableScheduler;
    private String timezoneHeader;

    private static int MAX_POINTS_PER_PUSH = 10000;

    private final NativeQueryHelper sqlHelper;
    private InfluxDBClient influxClient;

    InfluxClient(InfluxTask.JQLRepository taskRepo, EntityManager em) {
        super(taskRepo);
        this.sqlHelper = new NativeQueryHelper(em);
    }

    @PostConstruct
    void onCreated() {
        this.influxClient = InfluxDBClientFactory.create(url, token.toCharArray());
        this.timezoneHeader = buildFluxTimezone(ZoneId.systemDefault());
        if (enableScheduler) {
            super.startScheduler();
        }
    }

    @Override
    protected void executeTask(InfluxTask task) throws Exception {
        String sql = task.buildTimeSeriesQuery(MAX_POINTS_PER_PUSH);
        List<Map<String, Object>> rs = sqlHelper.executeQuery(sql);

        if (rs.size() == 0) return;

        ArrayList<Point> points = new ArrayList<>();
        for (Map<String, Object> row : rs) {
            Point p = task.makePoint(row);
            points.add(p);
        }

        WriteApiBlocking writeApi = influxClient.getWriteApiBlocking();
        writeApi.writePoints(task.getInfluxBucket(), org, points);
        task.updateLastSentIndex(rs);
    }


    private String buildSql(InfluxTask task) {
        Instant ts = this.getTimestampOfLastSentData(task.getInfluxBucket(), task.getInfluxMeasurement());
        if (ts == null) {
            String sql = task.buildFirstTimestampQuery();
            List<Map<String, Object>> res = sqlHelper.executeQuery(sql);
            if (res.size() > 0) {
                ts = ((Timestamp) res.get(0).get(task.getTimeColumn())).toInstant();
            }
        }
        if (ts == null) {
            ts = Instant.EPOCH;
        }

        LocalDateTime tsStart = LocalDateTime.ofInstant(ts,ZoneId.of("UTC"));
        LocalDateTime tsEnd = tsStart.plusHours(1);
        LocalDateTime tsLimit = LocalDateTime.now().minusSeconds(20);
        if (tsEnd.isAfter(tsLimit)) {
            tsEnd = tsLimit;
        }
        return task.buildTimeSeriesQuery(tsStart, tsEnd);
    }

    public Instant getTimestampOfLastSentData(String bucket, String measurement) {
        String query_flux = "from(bucket: \""+bucket+"\")\n" +
                "  |> range(start: -10y)" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \""+measurement+"\")" +
                "  |> keep(columns: [\"_time\"])" +
                "  |> last(column: \"_time\")";
        List<FluxTable> tables = queryFlux(query_flux);

        Instant ts = null;
        if (tables.size() > 0) {
            List<FluxRecord> records = tables.get(0).getRecords();
            if (records.size() > 0) {
                ts = records.get(0).getTime();
            }
        }
        return ts;
    }

    public List<FluxTable> queryFlux(String flux) {
        flux = timezoneHeader + flux;
        logger.info("executeFlux: " + flux);
        QueryApi queryApi = influxClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);
        return tables;
    }

    private static String buildFluxTimezone(ZoneId timezone) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nimport \"timezone\"\n");
        sb.append("option location = timezone.");
        if (timezone.toString().equals("Asia/Seoul")) {
            // @zee 2022-04-01 현재 사용 중인 InfluxDB 버전이 "Asia/Seoul" timezone 을 지원하지 않음.
            // 이 문제를 우회하기 위한 임시 조치임.
            sb.append("fixed(offset: 9h)\n");
        }
        else {
            sb.append("location(name: \"").append(timezone).append("\")\n");
        }
        return sb.toString();
    }

}
