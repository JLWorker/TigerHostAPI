package tgc.plus.proxmoxservice.services.utils.paths;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum NodeClusterPaths implements ClusterPaths {

    CREATE_VM("");

    private String url;

    NodeClusterPaths(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
