package tgc.plus.authservice.api.mobile;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.facades.UserFacade;

@RestController
@RequestMapping("/account")
public class UserController {


    @Autowired
    UserFacade userFacade;

    @PostMapping("/reg")
    public Mono<Void> registration(@RequestBody @Valid @JsonView(UserData.RegistrationUserData.class) UserData regData) {
        return userFacade.registerUser(regData);
    }

    @PostMapping("/login")
    public Mono<TokensResponse> login(@RequestBody @Valid UserLogin logData, ServerHttpRequest request) {
        return userFacade.loginUser(logData, request.getHeaders().getFirst("Device-Ip"));
    }

    @PatchMapping("/phone")
    public Mono<Void> changePhone(@RequestBody @Valid @JsonView(UserChangeContacts.ChangePhone.class) UserChangeContacts userChangeContacts, @RequestHeader("Version") Long version) {
        return userFacade.changePhone(userChangeContacts, version);
    }

    @PatchMapping("/email")
    public Mono<Void> changeEmail(@RequestBody @Valid @JsonView(UserChangeContacts.ChangeEmail.class) UserChangeContacts userChangeContacts, @RequestHeader("Version") Long version){
        return userFacade.changeEmail(userChangeContacts, version);
    }

    @PatchMapping("/password")
    public Mono<Void> changePassword(@RequestBody @Valid @JsonView(RestorePassword.AuthChange.class) RestorePassword restorePassword, @RequestHeader("Version") Long version){
        return userFacade.changePassword(restorePassword, version);
    }

    @GetMapping("/info")
    public Mono<UserInfoResponse> userInfoResponseMono(@RequestHeader("Version") Long version){
        return userFacade.getInfoAboutAccount(version);
    }

    @PatchMapping("/recovery")
    public Mono<Void> createRecoveryCode(@RequestBody @Valid @JsonView(RestorePassword.Restore.class) RestorePassword restorePassword){
        return userFacade.generateRecoveryCode(restorePassword);
    }

    @PatchMapping("/check")
    public Mono<Void> checkRecoveryCode(@RequestBody @Valid @JsonView(RestorePassword.Check.class) RestorePassword restorePassword){
        return userFacade.checkRecoveryCode(restorePassword);
    }

}
