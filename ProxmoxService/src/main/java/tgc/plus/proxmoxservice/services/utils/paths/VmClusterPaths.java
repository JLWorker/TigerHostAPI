package tgc.plus.proxmoxservice.services.utils.paths;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum VmClusterPaths implements ClusterPaths{

    CLONE_VM("/nodes/{node}/qemu/{vmId}/clone"), //kafka

    GET_VM_CURRENT("/nodes/{node}/qemu/{vmId}/status/current"), //kafka

    GET_VM_DISKS_INFO("/nodes/{node}/qemu/{vmId}/agent/get-fsinfo"), //webclient

    GET_VM_USERS("/nodes/{node}/qemu/{vmId}/agent/get-users"), //kafka

    CHANGE_VM_USER_PASSWORD("/nodes/{node}/qemu/{vmId}/agent/set-user-password"), //webclient

    EXECUTE_VM_LINUX_COMMAND("/nodes/{node}/qemu/{vmId}/agent/exec"), //kafka

    EXECUTE_VM_STATUS("/nodes/{node}/qemu/{vmId}/agent/exec-status?pid={pid}"), //kafka

    START_VM("/nodes/{node}/qemu/{vmId}/status/start"), //kafka

    STOP_VM("/nodes/{node}/qemu/{vmId}/status/stop"), //scheduler

    DESTROY_VM("/nodes/{node}/qemu/{vmId}"), //scheduler

    CHANGE_VM_CHARACTERS("/nodes/{node}/qemu/{vmId}/config"); //kafka

    private String url;

    VmClusterPaths(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}
