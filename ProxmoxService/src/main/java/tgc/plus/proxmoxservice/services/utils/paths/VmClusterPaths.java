package tgc.plus.proxmoxservice.services.utils.paths;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum VmClusterPaths implements ClusterPaths{

    CLONE_VM("/nodes/{node}/qemu/{vmId}/clone"),

    GET_VM_CURRENT("/nodes/{node}/qemu/{vmId}/status/current"),

    GET_VM_DISKS_INFO("/nodes/{node}/qemu/{vmId}/agent/get-fsinfo");
    private String url;

    VmClusterPaths(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
