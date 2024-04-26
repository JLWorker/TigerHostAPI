package tgc.plus.authservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorCode;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.facades.TwoFactorFacade;

@RestController
@RequestMapping("/api/auth/2fa")
@Tag(name = "api/2fa", description = "2FA controller api")
public class TwoAuthController {

    @Autowired
    private TwoFactorFacade twoFactorFacade;

    @Operation(summary = "Switch 2fa status in account")
    @Parameters(value = {
            @Parameter(in = ParameterIn.HEADER, name = "Version", description = "Version for optimistic block in database for account", example = "3"),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "200", description = "Successful change")
    })
    @PatchMapping("/switch")
    public Mono<Void> check2FaCode(){
        return twoFactorFacade.switch2Fa();
    }

    @Operation(summary = "Get 2fa QR code for account")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "404", content = @Content(),description = "Secret code not found for account"),
            @ApiResponse(responseCode = "500", description = "Error create qr code"),
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/qr")
    public Mono<QrCodeData> getQrCode(){
        return twoFactorFacade.getQrCode();
    }

    @Operation(summary = "Verify 2fa code", description = "Verify 2fa google code and then return pair of tokens for device")
    @Parameter(name = "2FA-Token", in = ParameterIn.HEADER, description = "2FA token", example = "uhdYUhskn879jd47sh...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), description = "Invalid 2fa session token"),
            @ApiResponse(responseCode = "400", content = @Content(),description = "2fa code incorrect"),
            @ApiResponse(responseCode = "200", description = "Successful verify")
    })
    @PostMapping("/verify-code")
    public Mono<TokensResponse> verify2FaCode(@RequestBody @Valid TwoFactorCode twoFactorCode, @RequestHeader("2FA-Token") String token, ServerWebExchange exchange){
        return twoFactorFacade.verifyCode(twoFactorCode, token, exchange.getRequest().getHeaders().getFirst("Device-Ip"), exchange.getResponse());
    }
}
