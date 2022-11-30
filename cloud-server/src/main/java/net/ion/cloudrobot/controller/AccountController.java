package net.ion.cloudrobot.controller;

import net.ion.mdk.auth.model.Account;
import net.ion.mdk.jql.JQLController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController extends JQLController<Account, Long> {

    protected AccountController(Account.JQLRepository repository) {
        super(repository);
    }
}
