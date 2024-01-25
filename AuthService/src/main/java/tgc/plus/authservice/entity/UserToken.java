package tgc.plus.authservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Table("user_tokens")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserToken {

    @Id
    @Column("id")
    private Long id;

    @Column("token_id")
    private String tokenId;

    @Version
    @Column("version")
    private Long version;

    @Column("user_id")
    private Long userId;

    @Column("refresh_token")
    private String refreshToken;

    @Column("expired_date")
    private Date expiredDate;

    @Column("active_date")
    private Date activeDate;

    @Transient
    private TokenMeta meta;

    public UserToken(String tokenId, Long userId, String refreshToken, Date expiredDate, Date activeDate) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiredDate = expiredDate;
        this.activeDate = activeDate;
    }
}
