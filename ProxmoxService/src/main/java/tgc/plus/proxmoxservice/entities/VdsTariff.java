package tgc.plus.proxmoxservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "vds_tariff")
@Component
public class VdsTariff {

    @Id
    @Column("id")
    private Long id;

    @Column("vds_id")
    private Long vdsId;

    @Column("tariff_id")
    private Integer tariffId;

    @Column("period_id")
    private Integer periodId;

    @Column("os_id")
    private Integer osId;

    public VdsTariff(Long vdsId, Integer tariffId, Integer periodId, Integer osId) {
        this.vdsId = vdsId;
        this.tariffId = tariffId;
        this.periodId = periodId;
        this.osId = osId;
    }
}
