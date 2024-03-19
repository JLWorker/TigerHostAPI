package tgc.plus.proxmoxservice.services.utils.paths;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum NodesClusterPaths implements ClusterPaths {

    GET_STORAGE_FOR_VM("/api2/json/nodes/{node}/storage/{storage}/content/{volume}"),

    GET_NODES_LOADS("/nodes");

    private String url;

    NodesClusterPaths(String url) {
        this.url = url;
    }
    @Override
    public String getUrl() {
        return url;
    }
}
