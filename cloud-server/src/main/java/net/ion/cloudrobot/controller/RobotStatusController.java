package net.ion.cloudrobot.controller;

import net.ion.cloudrobot.model.RobotStatus;
import net.ion.mdk.jql.JQLController;
import net.ion.mdk.jql.JQLRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Robot Status Read Api
 */
@RestController
@RequestMapping("/api/robot-status")
public class RobotStatusController extends JQLController<RobotStatus, Long> {

    protected RobotStatusController(JQLRepository<RobotStatus, Long> repository) {
        super(repository);
    }
}
