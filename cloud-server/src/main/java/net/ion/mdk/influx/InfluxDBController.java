package net.ion.mdk.influx;

import com.influxdb.query.FluxTable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import net.ion.mdk.jql.JQLController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/influx")
public class InfluxDBController {

    private final InfluxClient service;
    private final InfluxRepository nativeRepo;

    InfluxDBController(InfluxClient service) {
        this.service = service;
        nativeRepo = new InfluxRepository(service, null, null);
    }

    @Operation(summary = "Time series 검색 : Chart 용")
    @PostMapping(path = "/{bucket}/{measurement}/series", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map series(@Parameter(description="Influx bucket 명")
                          @PathVariable("bucket") String bucket,
                      @Parameter(description="Influx measurement(table) 명")
                         @PathVariable("measurement") String measurement,
                         @RequestBody InfluxJQL jql) throws Exception {
        InfluxRepository repo = new InfluxRepository(this.service, bucket, measurement);
        List<Map<String, Object>> result = repo.queryTimeSeries(jql);
        return JQLController.newSimpleMap("content", result);
    }

    @Operation(summary = "Influx entities 검색 (DB 쿼리와 유사)")
    @PostMapping(path = "/{bucket}/{measurement}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map list(@Parameter(description="Influx bucket 명")
                        @PathVariable("bucket") String bucket,
                    @Parameter(description="Influx measurement(table) 명")
                       @PathVariable("measurement") String measurement,
                       @RequestBody InfluxJQL jql) throws Exception {
        InfluxRepository repo = new InfluxRepository(this.service, bucket, measurement);
        List<Map<String, Object>> result = repo.queryEntities(jql);
        return JQLController.newSimpleMap("content", result);
    }

    @Operation(summary = "GroupKey(=TagKeys) 별로 마지막 entities 검색 (DB 쿼리와 유사)")
    @PostMapping(path = "/{bucket}/{measurement}/recent", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map list_recent(@Parameter(description="Influx bucket 명")
                           @PathVariable("bucket") String bucket,
                           @Parameter(description="Influx measurement(table) 명")
                       @PathVariable("measurement") String measurement,
                       @RequestBody InfluxJQL jql) throws Exception {
        if (InfluxJQL.isEmpty(jql.getStart())) {
            jql.setStart("-100y");
        }
        jql.setFunctions(new String[] {"last()"});
        InfluxRepository repo = new InfluxRepository(this.service, bucket, measurement);
        List<Map<String, Object>> result = repo.queryEntities(jql);
        return JQLController.newSimpleMap("content", result);
    }

    @Operation(summary = " Influx query 결과를 변환없이 출력(디버깅용)")
    @PostMapping(path = "/{bucket}/{measurement}/raw-result", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object raw_result(@Parameter(description="Influx bucket 명")
                                 @PathVariable("bucket") String bucket,
                             @Parameter(description="Influx measurement(table) 명")
                             @PathVariable("measurement") String measurement,
                             @RequestBody InfluxJQL jql) throws Exception {
        InfluxRepository repo = new InfluxRepository(this.service, bucket, measurement);
        List<FluxTable> result = repo.nativeQuery(jql);
        return result;
    }

    @Operation(summary = "Flux Query Text 를 직접 실행 후 RDB Entity 형식으로 출력")
    @PostMapping(path = "/{bucket}/{measurement}/native-list", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map native_list(@RequestBody String flux) throws Exception {
        List<FluxTable> tables = this.service.queryFlux(flux);
        ArrayList<Map<String, Object>> result = nativeRepo.extractEntities(tables, flux.contains("aggregate("));
        return JQLController.newSimpleMap("content", result);
    }

    @Operation(summary = "Flux Query Text 를 직접 실행 후 Time series 형식으로 출력")
    @PostMapping(path = "/{bucket}/{measurement}/native-series", consumes = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public Map native_series(@RequestBody String flux) throws Exception {
        List<FluxTable> tables = this.service.queryFlux(flux);
        ArrayList<Map<String, Object>> result = nativeRepo.extractTimeSeries(tables);
        return JQLController.newSimpleMap("content", result);
    }

    @Operation(summary = "Flux Query Text 를 직접 실행 후 결과를 변환없이 출력 (디버깅 용)")
    @PostMapping(path = "/{bucket}/{measurement}/native-raw-result", consumes = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public List native_raw(@RequestBody String flux) throws Exception {
        List<FluxTable> tables = this.service.queryFlux(flux);
        return tables;
    }
}
