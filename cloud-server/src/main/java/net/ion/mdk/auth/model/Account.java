package net.ion.mdk.auth.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.ion.mdk.jql.jpa.JQLRepositoryBase;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * MDK - Account Model
 */
@Entity
@Table(name = "account", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    private String roles;

    @JsonIgnore
    private transient List<GrantedAuthority> authorities = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getRoles() { return roles; }

    public void setRoles(String roles) { this.roles = roles; }

    @JsonIgnore
    public List<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void logout() {
        this.provider = null;
        this.providerId = null;
    }

    @PostLoad
    void postEntityLoad() {
        if (this.roles != null) {
            String[] roles = this.roles.split(",");
            for (String s : roles) {
                s = s.trim();
                if (s.length() > 0) {
                    this.authorities.add(new SimpleGrantedAuthority("ROLE_" + s));
                }
            }
        }
    }

    @Repository
    public static class JQLRepository extends JQLRepositoryBase<Account, Long> {
        @Override
        public Long getEntityId(Account account) {
            return account.getId();
        }
    }
}
