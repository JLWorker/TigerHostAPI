//package tgc.plus.callservice.configs;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ClientHttpConnector;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.resources.ConnectionProvider;
//
//import java.time.Duration;
//
//@Configuration
//public class WebClientConfig {
//
//    public WebClient webClient;
//
//    @Value("${sms.center.url}")
//    private String baseUrl;
//
//    @PostConstruct
//    void init(){
//        ConnectionProvider connectionProvider = ConnectionProvider.builder("smsCenter")
//                .maxConnections(100)
//                .build();
//        HttpClient httpClient = HttpClient.create(connectionProvider);
//        webClient = WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(baseUrl).build();
//    }
//
//}
