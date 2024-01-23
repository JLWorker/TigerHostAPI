package tgc.plus.authservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column("id")
    private Long id;

    @Column("user_code")
    private Integer userCode;

    @Column("two_auth_status")
    private Boolean twoAuthStatus;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("active")
    private Boolean active;

    @Column("role")
    private String role;

    @Column("recovery_code")
    private String recoveryCode;

    @Column("code_expired_date")
    private Date codeExpiredDate;

    public User(Integer userCode, Boolean twoAuthStatus, String email, String password, Boolean active, String role, String recoveryCode, Date codeExpiredDate) {
        this.userCode = userCode;
        this.twoAuthStatus = twoAuthStatus;
        this.email = email;
        this.password = password;
        this.active = active;
        this.role = role;
        this.recoveryCode = recoveryCode;
        this.codeExpiredDate = codeExpiredDate;
    }


}
