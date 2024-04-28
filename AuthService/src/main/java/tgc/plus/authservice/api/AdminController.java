package tgc.plus.authservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.admin_dto.ChangeAccountDto;
import tgc.plus.authservice.dto.admin_dto.GetUsersByDateDto;
import tgc.plus.authservice.dto.admin_dto.UserResponseDto;
import tgc.plus.authservice.facades.AdminFacade;

import java.util.List;

@RestController
@RequestMapping("/api/auth/admin")
@Tag(name = "api/auth/admin", description = "Admin controller api")
public class AdminController {

    @Autowired
    private AdminFacade adminFacade;

    @Operation(summary = "Get all roles in system")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token (ONLY WITH ROLE ADMIN!!)", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(array = @ArraySchema(schema = @Schema(example = "ADMIN"))))
    })
    @GetMapping("/roles")
    public Mono<List<String>> promotionToAdmin(){
        return adminFacade.getSystemRoles();
    }

    @Operation(summary = "Set account setting")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token (ONLY WITH ROLE ADMIN!!)", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "User with user code not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/account")
    public Mono<Void> changeAccount(@Valid @RequestBody ChangeAccountDto changeAccountDTO){
        return adminFacade.changeAccount(changeAccountDTO);
    }

    @Operation(summary = "Delete all user refresh tokens")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "userCode", example = "95dbe4e3-7763-4c...."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token (ONLY WITH ROLE ADMIN!!)", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request params, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/account/{userCode}/tokens")
    public Mono<Void> closeAllActiveSessions(@PathVariable("userCode") String userCode){
        return adminFacade.closeAccountSessions(userCode);
    }

    @Operation(summary = "Get all user by params")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token (ONLY WITH ROLE ADMIN!!)", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request params, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/account/all")
    public Mono<List<UserResponseDto>> getAllUsers(@Valid @ModelAttribute GetUsersByDateDto getUsersByDateDTO){
        return adminFacade.getUsersByDateAndLimit(getUsersByDateDTO);
    }

    @Operation(summary = "Get all user by email")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "email", example = "435743@bk.ru"),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token (ONLY WITH ROLE ADMIN!!)", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request params, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "User with email not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/account/{email}")
    public Mono<UserResponseDto> getAllUsers(@PathVariable("email") String email){
        return adminFacade.getUserByEmail(email);
    }

}
