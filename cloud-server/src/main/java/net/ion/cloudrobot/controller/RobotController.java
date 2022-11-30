package net.ion.cloudrobot.controller;

import net.ion.cloudrobot.model.Robot;
import net.ion.mdk.jql.JQLController;
import net.ion.mdk.jql.JQLRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/robot")
public class RobotController extends JQLController<Robot, String> {

    protected RobotController(JQLRepository<Robot, String> repository) {
        super(repository);
    }
}
