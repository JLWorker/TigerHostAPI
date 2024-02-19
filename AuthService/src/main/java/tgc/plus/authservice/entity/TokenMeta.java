package tgc.plus.authservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table("token_meta")
public class TokenMeta {

    @Id
    @Column("id")
    private Long id;

    @Column("token_id")
    private Long tokenId;

    @Column("device_ip")
    private String deviceIp;

    @Column("device_name")
    private String deviceName;

    @Column("application_type")
    private String applicationType;

    @Column("create_date")
    private Instant createDate;

    public TokenMeta(Long tokenId, String deviceIp, String deviceName, String applicationType, Instant createDate) {
        this.tokenId = tokenId;
        this.deviceIp = deviceIp;
        this.deviceName = deviceName;
        this.applicationType = applicationType;
        this.createDate = createDate;
    }
}
