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
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditEmailPayloadDto;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditPhonePayloadDto;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.facades.UserFacade;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;

@RestController
@RequestMapping("/api/auth/account")
@Tag(name = "api/auth/account", description = "User controller API")
public class UserController {


    @Autowired
    private UserFacade userFacade;

    @Operation(summary = "User registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request have bad request body, for ex. - validation exceptions, user already exist or password mismatch"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "User was save")
    })
    @PostMapping("/reg")
    public Mono<Void> registration(@RequestBody @Valid @JsonView(UserDataDto.RegistrationUserData.class) UserDataDto regData) {
        return userFacade.registerUser(regData);
    }

    @Operation(summary = "Login user", description = "This endpoint return access and refresh tokens in cookie and other metadata. Also this endpoint is the starting point of entry via 2fa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for ex. - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), description = "Incorrect account password or user not found"),
            @ApiResponse(responseCode = "403", content = @Content(),description = "User was ban"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "202", content = @Content(schema = @Schema()), headers = {@Header(name = "2FA-Token",  schema= @Schema(example = "usiahjs67HSuzsj...."))},
                    description = "User account has active 2fa status, need redirect user on page with enter 2fa code"),
            @ApiResponse(responseCode = "200", description = "Success Login user", headers = {@Header(name = "Set-Cookie", schema = @Schema(example = "REFRESH_TOKEN=90b999f6-4989-4f96-9d6c-56c5cdbfe10c; Path=/api/auth/tokens/; Domain=localhost; Max-Age=864000; Expires=Wed, 08 May 2024 05:59:16 GMT; Secure; HttpOnly; SameSite=Lax"))})
    })
    @PostMapping("/login")
    public Mono<TokensResponseDto> login(@RequestBody @Valid UserLoginDto logData, ServerWebExchange exchange) {
        return userFacade.loginUser(logData, exchange.getRequest().getHeaders().getFirst("Device-Ip"), exchange.getResponse());
    }

    @Operation(summary = "Change phone in account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for ex. - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success change phone number")
    })
    @PatchMapping("/phone")
    public Mono<Void> changePhone(@RequestBody @Valid @NotNull(message = "Contact can`t be null") @JsonView(UserChangeContactsDto.ChangePhone.class) UserChangeContactsDto userChangeContactsDto) {
        return userFacade.changeAccountContacts(userChangeContactsDto.getPhone(), MailServiceCommand.UPDATE_PHONE, new EditPhonePayloadDto(userChangeContactsDto.getPhone()));
    }

    @Operation(summary = "Change email in account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for ex. - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success change email")
    })
    @PatchMapping("/email")
    public Mono<Void> changeEmail(@RequestBody @Valid @JsonView(UserChangeContactsDto.ChangeEmail.class) UserChangeContactsDto userChangeContactsDto){
        return userFacade.changeAccountContacts(userChangeContactsDto.getEmail(), MailServiceCommand.UPDATE_EMAIL, new EditEmailPayloadDto(userChangeContactsDto.getEmail()));
    }

    @Operation(summary = "Change password for account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for ex. - validation exceptions or invalid passwords"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success change password")
    })
    @PatchMapping("/password")
    public Mono<Void> changePassword(@RequestBody @Valid @JsonView(RestorePasswordDto.AuthChange.class) RestorePasswordDto restorePasswordDto){
        return userFacade.changeAccountPassword(restorePasswordDto);
    }

    @Operation(summary = "Get info about account", description = "This endpoint return main account information: email, phone number and 2fa status")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Get info about user")
    })
    @GetMapping("/info")
    public Mono<UserInfoResponseDto> getAccountInfo(){
        return userFacade.getInfoAboutAccount();
    }

    @Operation(summary = "Create recovery code for reset password", description = "This endpoint generate recovery url and send recovery information on user email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "404", content = @Content(),description = "User not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Successful request")
    })
    @PatchMapping("/recovery")
    public Mono<Void> createRecoveryCode(@RequestBody @Valid @JsonView(RestorePasswordDto.Restore.class) RestorePasswordDto restorePasswordDto){
        return userFacade.generateRecoveryCode(restorePasswordDto.getEmail());
    }

    @Operation(summary = "Check recovery code after change password", description = "This endpoint check recovery code after the operation to change it on the site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions or passwords mismatch or recovery code expired"),
            @ApiResponse(responseCode = "404", content = @Content(),description = "Password already changed"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Successful change")
    })
    @PatchMapping("/check")
    public Mono<Void> checkRecoveryCode(@RequestBody @Valid @JsonView(RestorePasswordDto.Check.class) RestorePasswordDto restorePasswordDto){
        return userFacade.checkAccountRecoveryCode(restorePasswordDto.getPassword(), restorePasswordDto.getPasswordConfirm(), restorePasswordDto.getCode());
    }

}
