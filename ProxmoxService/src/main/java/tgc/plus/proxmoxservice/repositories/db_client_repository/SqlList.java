package tgc.plus.proxmoxservice.repositories.db_client_repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum SqlList {

    GET_ALL_USER_VMS("SELECT vds.vm_id, vds.active, vds.start_date, vds.expired_date, vds_payment.price," +
            "vds_payment.price_month, vds_tariff.tariff_id, vds_tariff.period_id, vds_tariff.os_id from " +
            "vds JOIN vds_tariff ON vds.id = vds_tariff.vds_id JOIN vds_payment ON vds.id = vds_payment.vds_id " +
            "WHERE vds.user_code= :userCode");


    private String sqlRequest;

    SqlList(String sqlRequest) {
        this.sqlRequest = sqlRequest;
    }
}
