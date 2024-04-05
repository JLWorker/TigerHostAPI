package tgc.plus.authservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Getter
@Setter
@NoArgsConstructor
@Table("two_factor")
@ToString
public class TwoFactor {

    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("device_token")
    private String deviceToken;

    @Column("create_date")
    private Instant createDate;

    public TwoFactor(Long userId, String deviceToken, Instant createDate) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.createDate = createDate;
    }
}
