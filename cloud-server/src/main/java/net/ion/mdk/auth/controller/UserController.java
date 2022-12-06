package net.ion.mdk.auth.controller;

import net.ion.mdk.auth.exception.ResourceNotFoundException;
import net.ion.mdk.auth.model.Account;
import net.ion.mdk.auth.repository.UserRepository;
import net.ion.mdk.auth.security.CurrentUser;
import net.ion.mdk.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller
 */
/* @zee OAuth 샘플. 2022.04.25 현재 사용되지 않음. */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * @param userPrincipal
     * @return Account
     */
    @GetMapping("/auth/user/me")
    @PreAuthorize("hasRole('USER')")
    public Account getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
