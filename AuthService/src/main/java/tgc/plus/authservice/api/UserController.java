package tgc.plus.authservice.api;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.exceptions_dto.VersionExceptionResponse;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditEmailData;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditPhoneData;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.facades.UserFacade;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;

@RestController
@RequestMapping("/api/account")
@Tag(name = "api/account", description = "User controller API")
public class UserController extends UserFacade {


    @Autowired
    private UserFacade userFacade;

    @Operation(summary = "User registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions, user already exist"),
            @ApiResponse(responseCode = "200", description = "User was save")
    })
    @PostMapping("/reg")
    public Mono<Void> registration(@RequestBody @Valid @JsonView(UserData.RegistrationUserData.class) UserData regData) {
        return userFacade.registerUser(regData);
    }

    @Operation(summary = "Login user", description = "This endpoint return access and refresh tokens with other metadata. Also this endpoint this endpoint is the starting point of entry via 2fa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "404", content = @Content(),description = "User not found"),
            @ApiResponse(responseCode = "401", content = @Content(), description = "Incorrect account password"),
            @ApiResponse(responseCode = "202", content = @Content(schema = @Schema()), headers = {@Header(name = "2FA-Token",  schema= @Schema(example = "usiahjs67HSuzsj...."))},
                    description = "User account has active 2fa status, need redirect user on page with enter 2fa code"),
            @ApiResponse(responseCode = "200", description = "Success Login user")
    })
    @PostMapping("/login")
    public Mono<TokensResponse> login(@RequestBody @Valid UserLogin logData, ServerHttpRequest request) {
        return userFacade.loginUser(logData, request.getHeaders().getFirst("Device-Ip"));
    }

    @Operation(summary = "Change phone in account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = VersionExceptionResponse.class)),description = "Incorrect account version"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "200", description = "Success change phone number")
    })
    @PatchMapping("/phone")
    public Mono<Void> changePhone(@RequestBody @Valid @JsonView(UserChangeContacts.ChangePhone.class) UserChangeContacts userChangeContacts) {
        return userFacade.changeAccountContacts(userChangeContacts.getPhone(), MailServiceCommand.UPDATE_PHONE, new EditPhoneData(userChangeContacts.getPhone()));
    }

    @Operation(summary = "Change email in account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = VersionExceptionResponse.class)),description = "Incorrect account version"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "200", description = "Success change email")
    })
    @PatchMapping("/email")
    public Mono<Void> changeEmail(@RequestBody @Valid @JsonView(UserChangeContacts.ChangeEmail.class) UserChangeContacts userChangeContacts){
        return userFacade.changeAccountContacts(userChangeContacts.getEmail(), MailServiceCommand.UPDATE_EMAIL, new EditEmailData(userChangeContacts.getEmail()));
    }

    @Operation(summary = "Change password for account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions or invalid passwords"),
            @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = VersionExceptionResponse.class)),description = "Incorrect account version"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "200", description = "Success change password")
    })
    @PatchMapping("/password")
    public Mono<Void> changePassword(@RequestBody @Valid @JsonView(RestorePassword.AuthChange.class) RestorePassword restorePassword){
        return userFacade.changeAccountPassword(restorePassword);
    }

    @Operation(summary = "Get info about account", description = "This endpoint return main account information: email, phone number and 2fa status")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = VersionExceptionResponse.class)),description = "Incorrect account version"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "200", description = "Get info about user")
    })
    @GetMapping("/info")
    public Mono<UserInfoResponse> getAccountInfo(){
        return userFacade.getInfoAboutAccount();
    }

    @Operation(summary = "Create recovery code for reset password", description = "This endpoint generate recovery url and send recovery information on user email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "404", content = @Content(),description = "User not found"),
            @ApiResponse(responseCode = "200", description = "Successful request")
    })
    @PatchMapping("/recovery")
    public Mono<Void> createRecoveryCode(@RequestBody @Valid @JsonView(RestorePassword.Restore.class) RestorePassword restorePassword){
        return userFacade.generateRecoveryCode(restorePassword.getEmail());
    }

    @Operation(summary = "Check recovery code after change password", description = "This endpoint check recovery code after the operation to change it on the site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions or passwords mismatch or recovery code expired"),
            @ApiResponse(responseCode = "404", content = @Content(),description = "Password already changed"),
            @ApiResponse(responseCode = "200", description = "Successful change")
    })
    @PatchMapping("/check")
    public Mono<Void> checkRecoveryCode(@RequestBody @Valid @JsonView(RestorePassword.Check.class) RestorePassword restorePassword){
        return userFacade.checkAccountRecoveryCode(restorePassword.getPassword(), restorePassword.getPasswordConfirm(), restorePassword.getCode());
    }

}
