package tgc.plus.proxmoxservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Setter
@Getter
@NoArgsConstructor
@ToString
@Table("vds")
public class Vds {

    @Id
    @Column("id")
    private Long id;

    @Column("user_code")
    private String userCode;

    @Column("template_id")
    private Integer templateId;

    @Column("vm_password")
    private String vmPassword;

    @Column("vm_id")
    private String vmId;

    @Column("vm_number")
    private Integer vmNumber;

    @Column("vm_node")
    private String vmNode;

    @Column("auto_payment")
    private boolean autoPayment;

    @Column("active")
    private boolean active;

    @Column("expired_date")
    private Instant expiredDate;


    public Vds(String userCode, Integer templateId, String vmId, String vmPassword, Integer vmNumber, String vmNode, Instant expiredDate) {
        this.userCode = userCode;
        this.templateId = templateId;
        this.vmId = vmId;
        this.vmPassword = vmPassword;
        this.vmNumber = vmNumber;
        this.vmNode = vmNode;
        this.autoPayment = false;
        this.active = true;
        this.expiredDate = expiredDate;
    }
}
