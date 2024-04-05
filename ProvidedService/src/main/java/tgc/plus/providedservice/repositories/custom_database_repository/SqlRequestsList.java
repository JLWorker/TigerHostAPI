package tgc.plus.providedservice.repositories.custom_database_repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum SqlRequestsList {

    GET_TARIFFS_WITH_TYPES("select vds_tariffs.tariff_name, vds_tariffs.price_month, vds_tariffs.cpu, vds_tariffs.ram, vds_tariffs.memory, " +
            "cpu_types.type as (cpu_type), ram_types.type as (ram_type), memory_types.type as (memory_type) from vds_tariffs join cpu_types on (vds_tariffs.cpu_type = cpu_types.id)" +
            "join ram_types on (vds_tariffs.ram_type = ram_types.id) join memory_types on (vds_tariffs.memory_type = memory_types.id);"),


    GET_TARIFF_WITH_TYPES_BY_ID("select vds_tariffs.tariff_name, vds_tariffs.price_month, vds_tariffs.cpu, vds_tariffs.ram, vds_tariffs.memory, " +
                                        "cpu_types.type as (cpu_type), ram_types.type as (ram_type), memory_types.type as (memory_type) from vds_tariffs join cpu_types on (vds_tariffs.cpu_type = cpu_types.id)" +
                                        "join ram_types on (vds_tariffs.ram_type = ram_types.id) join memory_types on (vds_tariffs.memory_type = memory_types.id) WHERE vds_tariffs.id= :tariffId;"),

    GET_BLOCK_FOR_ELEMENT("SELECT * FROM %s WHERE id= :elemId FOR UPDATE"),

    GET_BLOCK_FOR_NOT_ACTIVE_ELEMENT("SELECT * FROM %s WHERE id= :elemId AND active=false FOR UPDATE"),

    DELETE_ELEMENT("DELETE FROM %s WHERE id= :elemId"),

    CHANGE_ACTIVE_STATUS("UPDATE %s SET active= NOT active WHERE id= :elemId");

    private String query;

    SqlRequestsList(String query) {
        this.query = query;
    }
}
