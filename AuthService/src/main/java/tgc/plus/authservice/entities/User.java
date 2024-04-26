package tgc.plus.authservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @Column("id")
    private Long id;

    @Column("user_code")
    private String userCode;

    @Column("two_auth_status")
    private Boolean twoAuthStatus;

    @Column("two_factor_secret")
    private String twoFactorSecret;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("password")
    private String password;

    @Column("active")
    private Boolean active;

    @Column("role")
    private String role;

    @Column("recovery_code")
    private String recoveryCode;

    @Column("code_expired_date")
    private Instant codeExpiredDate;

    @Column("registration_date")
    private Instant registrationDate;

    @Transient
    private List<UserToken> refreshTokens;

    public User(String userCode, String email, String password, String role) {
        this.userCode = userCode;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = true;
        this.phone = null;
        this.twoAuthStatus = false;
        this.codeExpiredDate = null;
        this.recoveryCode = null;
        this.registrationDate = Instant.now();
    }
}
