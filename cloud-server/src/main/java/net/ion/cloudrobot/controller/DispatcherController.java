package net.ion.cloudrobot.controller;

import net.ion.cloudrobot.model.OrgDispatcher;
import net.ion.cloudrobot.model.Robot;
import net.ion.mdk.jql.JQLController;
import net.ion.mdk.jql.JQLRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatcher")
public class DispatcherController extends JQLController<OrgDispatcher, String> {

    protected DispatcherController(JQLRepository<OrgDispatcher, String> repository) {
        super(repository);
    }
}
