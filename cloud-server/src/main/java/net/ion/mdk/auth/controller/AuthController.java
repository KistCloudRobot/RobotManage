package net.ion.mdk.auth.controller;

import net.ion.mdk.auth.exception.BadRequestException;
import net.ion.mdk.auth.model.Account;
import net.ion.mdk.auth.model.AuthProvider;
import net.ion.mdk.auth.payload.ApiResponse;
import net.ion.mdk.auth.payload.AuthResponse;
import net.ion.mdk.auth.payload.LoginRequest;
import net.ion.mdk.auth.payload.SignUpRequest;
import net.ion.mdk.auth.repository.UserRepository;
import net.ion.mdk.auth.security.CurrentUser;
import net.ion.mdk.auth.security.TokenProvider;
import net.ion.mdk.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Profile({"oauth", "production"})
@RestController
@RequestMapping("/auth")
/* @zee Local 인증(email + 암호) 을 위한 클래스. */
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired

    private UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/logout")
    public String logout(@CurrentUser UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            String email = userPrincipal.getEmail();
            Account account = userRepo.findByEmail(email).orElseGet(null);
            account.logout();
            userRepo.save(account);
        }
        //authReqRepo.removeAuthorizationRequestCookies(req, resp);
        return "OK";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        Account account = new Account();
        account.setName(signUpRequest.getName());
        account.setEmail(signUpRequest.getEmail());
        account.setPassword(signUpRequest.getPassword());
        account.setProvider(AuthProvider.local);

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        Account result = userRepository.save(account);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }

}
