package tgc.plus.proxmoxservice.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.reactive.server.WebTestClient;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmUsersInfo;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.requests.UserChangePassword;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.TimestampsVm;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.UserAllVms;
import tgc.plus.proxmoxservice.listeners.MessageListener;

import java.time.Duration;
import java.util.Objects;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "tgc.plus.proxmoxservice.listeners"))
public class VmControllerTest {

    @MockBean
    private MessageListener messageListener;

    @Autowired
    private WebTestClient webTestClient;

    private static final String authTokenWithVm = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJfY29kZSI6IjRmMTdkYzI2LTAxNzEtNGVmYy1hOTg3LWQxYzRkNWI4YzllYSIsImV4cCI6MTcxMTI4ODA4NSwiaWF0IjoxNzExMjg0ODg1fQ.5Jb0cn08dS1BoeEINeUBzO-iKUohRPbGKXAuPVuVDaE";
    private static final String authTokenWithoutVm = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJfY29kZSI6IjQyY2NlNmZmLWQ4MjktNGJmNi05YTE0LWZmNTZmZjA4MjdmNyIsImV4cCI6MTcxMTI2ODY4OSwiaWF0IjoxNzExMjY1NDg5fQ.xQTcOquOlMRaqzCk7GSpG8ufNkCifIlizLS07Z7WAQU";

    private final String incorrectVmId = "vm_950185404";
    private final String correctVmId = "vm_977508733";


    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeEach
    void init(){
         webTestClient = webTestClient.mutate().responseTimeout(Duration.ofSeconds(3))
                .baseUrl("http://localhost:8085")
                .defaultHeader("Authorization", String.format("Bearer_%s", authTokenWithVm))
                .build();
    }

    @Test
    public void getUserVms(){

        webTestClient.get()
                .uri("/api/vm/all")
                .exchange()
                .expectBody(UserAllVms.class)
                .consumeWith(consumer -> {
                    Assertions.assertFalse(consumer.getStatus().isError(), String.format("Response has status %s", consumer.getStatus()));
                    logger.info("Response status {}", consumer.getStatus());
                    Objects.requireNonNull(consumer.getResponseBody()).getUserVms().forEach(el -> logger.info(el.toString()));
                });

    }


    @Test
    public void getVmTime(){
        webTestClient.get()
                .uri("/api/vm/{vmId}/time", correctVmId)
                .exchange()
                .expectBody(TimestampsVm.class)
                .consumeWith(consumer -> {
                    Assertions.assertFalse(consumer.getStatus().isError(), String.format("Response has status %s", consumer.getStatus()));
                    logger.info("Response status {}, timestamps: start - {}, end - {}", consumer.getStatus(), consumer.getResponseBody().getStartDate(), consumer.getResponseBody().getExpireDated());
                });

    }

    @Test
    public void checkVmTrialPeriod(){
        webTestClient.get()
                .uri("/api/vm/{vmId}/check_trial", correctVmId)
                .exchange()
                .expectBody()
                .consumeWith(consumer -> {
                    Assertions.assertFalse(consumer.getStatus().isError(), String.format("Response has status %s", consumer.getStatus()));
                    logger.info("Response status {}", consumer.getStatus());
                });

    }

    @Test
    public void checkVmStorage(){
        webTestClient.get()
                .uri("/api/vm/{vmId}/check_storage?newSize={newSize}", correctVmId, 1)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    public void changeUserPassword(){
        UserChangePassword userChangePassword = new UserChangePassword(correctVmId, "root", "123", "123");
        webTestClient.post()
                .uri("/api/vm/user/password", correctVmId)
                .bodyValue(userChangePassword)
                .exchange()
                .expectStatus()
                .isOk();
    }


    @Test
    public void getAllUsers(){
        webTestClient.get()
                .uri("/api/vm/{vmId}/users", correctVmId)
                .exchange()
                .expectAll(status -> status.expectStatus().isOk(),
                        body->{
                             body.expectBody(ProxmoxVmUsersInfo.class)
                                     .consumeWith(consumer -> Objects.requireNonNull(consumer.getResponseBody()).getVmUsers()
                                             .forEach(el->logger.info(el.toString())));
                        });
    }

}
