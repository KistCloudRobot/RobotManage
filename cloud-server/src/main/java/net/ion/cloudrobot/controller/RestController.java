package net.ion.cloudrobot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.v3.core.util.Json;
import net.ion.cloudrobot.model.File;
import net.ion.cloudrobot.model.OrgDispatcher;
import net.ion.cloudrobot.model.Robot;
import net.ion.cloudrobot.model.RobotStatus;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInput;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 외부 제공용 Rest Api
 */
@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest")
public class RestController {

    private Robot.JQLRepository robotRepository;
    private RobotStatus.JQLRepository statusRepository;

    private File.JQLRepository fileRepository;

    public RestController(Robot.JQLRepository robotRepository, RobotStatus.JQLRepository statusRepository, File.JQLRepository fileRepository) {
        this.robotRepository = robotRepository;
        this.statusRepository = statusRepository;
        this.fileRepository = fileRepository;
    }

    /**
     * 로봇 상태 정보 수집 API (시립대 -> 아이온 로봇 상태 정보)
     * @param status
     * @return
     */
    @PostMapping("/robot")
    public ResponseEntity<Object> robotStatus(@RequestBody RobotStatus status) {
        Map<String, Object> body = new HashMap<>();

        try {
            statusRepository.insert(status);

            Robot robot = robotRepository.find(status.getRobotId());
            robot.setRobotStatus(status.getRobotStatus());
            robot.setUpdateTime(status.getUpdateTime());
            robotRepository.update(robot);

            body.put("isSuccess", true);
            body.put("failMsg", "");
        } catch (Exception e) {
            e.printStackTrace();
            body.put("isSuccess", false);
            body.put("failMsg", e.getMessage());
        }

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    /**
     * 다운로드 URL 제공 Api
     * @param dispatcherKey
     * @param request
     * @return
     */
    @GetMapping("/download/url")
    public ResponseEntity<Object> downloadUrl(@RequestParam(value = "key", required = true) String dispatcherKey, HttpServletRequest request) {

        Map<String, Object> body = new HashMap<>();

        HashMap<String, Object> filter = new HashMap<>();
        filter.put("dispatcherId", dispatcherKey);

        //Sort Option
        String[] sortColumns = {"-fId"};
        Sort sort = buildSort(sortColumns);
        File downloadFile = fileRepository.findTop(filter, sort);

        String downloadUrl = request.getRequestURL().toString().replace(request.getRequestURI(),"") + "/api/file/download/";
        body.put("url", downloadUrl+downloadFile.getFileId());

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    /**
     * 로봇 상태 정보 (LG전자에게 제공)
     * @return
     */
    @GetMapping("/robot/list")
    public ResponseEntity<Object> robotList() {
        Map<String, Object> body = new HashMap<>();
        body.put("content", robotRepository.list());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    /**
     * Sort 처리
     * @param columns
     * @return
     */
    private Sort buildSort(String columns[]) {
        if (columns == null || columns.length == 0) return null;

        ArrayList<Sort.Order> orders = new ArrayList<>();
        for (String col : columns) {
            col = col.trim();
            col = col.replace("_", "__");

            Sort.Order order;
            switch (col.charAt(0)) {
                case '-':
                    col = col.substring(1).trim();
                    order = Sort.Order.desc(col);
                    break;
                case '+':
                    col = col.substring(1).trim();
                    // no-break;
                default:
                    order = Sort.Order.asc(col);
                    break;
            }
            orders.add(order);
        }
        return Sort.by(orders);
    }
}
