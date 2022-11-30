package net.ion.mdk.influx;

import io.swagger.v3.oas.annotations.Operation;
import net.ion.mdk.jql.JQLController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Controller
@RequestMapping("/api/influx")
public class InfluxPushController extends JQLController<InfluxTask, Long> {

    private final InfluxClient service;

    InfluxPushController(InfluxClient service) {
        super(service.getRepository());
        this.service = service;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseBody
    @Operation(summary = "폼을 이용한 PushTask 추가 (Test 1회 자동 실행)")
    @Transactional
    public InfluxTask add(@ModelAttribute InfluxTask entity) throws Exception {
        // executeTask just for test
        entity.onLoad();
        this.service.executeTask(entity);
        entity = super.add(entity);
        return entity;
    }

    @Operation(summary = "PushTask 실행 테스트")
    @Transactional
    @GetMapping(path = "/test/{id}")
    @ResponseBody
    public Object test(@PathVariable("id") Long id) throws Exception {
        // executeTask just for test
        InfluxTask entity = super.get(id);
        this.service.executeTask(entity);
        return "Timestamp of last sent data: " + entity.getTimeStampOfLastSentData();
    }

}
