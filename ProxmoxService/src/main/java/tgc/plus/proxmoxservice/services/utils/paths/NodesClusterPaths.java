package tgc.plus.proxmoxservice.services.utils.paths;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum NodesClusterPaths implements ClusterPaths{

    GET_NODES_LOADS("/nodes"),

    GET_NODE_STORAGE("/nodes/{node}/storage");

    private String url;

    NodesClusterPaths(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}
