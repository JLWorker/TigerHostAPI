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
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.tokens_dto.TokenDataDto;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponseDto;
import tgc.plus.authservice.facades.TokenFacade;

import java.util.List;

@RestController
@RequestMapping("/api/auth/tokens")
@Tag(name = "api/auth/tokens", description = "Tokens controller api")
public class TokenController {

    @Autowired
    private TokenFacade tokenFacade;

    @Operation(summary = "Update access token", description = "This endpoint returns an access token as well as a refresh token in a cookie")
    @Parameter(in = ParameterIn.HEADER, name = "Cookie", schema = @Schema(example = "REFRESH_TOKEN=95dbe4e3-7763-4cec-b136-20a24250255d"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success tokens update", headers = {@Header(name = "Set-Cookie", schema = @Schema(example = "REFRESH_TOKEN=90b999f6-4989-4f96-9d6c-56c5cdbfe10c; Path=/api/auth/tokens/; Domain=localhost; Max-Age=864000; Expires=Wed, 08 May 2024 05:59:16 GMT; Secure; HttpOnly; SameSite=Lax"))})
    })
    @PatchMapping("/update")
    public Mono<UpdateTokenResponseDto> updateToken(@CookieValue("REFRESH_TOKEN") String refreshToken, ServerHttpResponse serverHttpResponse){
        return tokenFacade.updateAccessToken(refreshToken, serverHttpResponse);
    }


    @Operation(summary = "Logout device", description = "Endpoint returns cookie to be reset")
    @Parameters(value = {
            @Parameter(in = ParameterIn.HEADER, name = "Cookie", schema = @Schema(example = "REFRESH_TOKEN=95dbe4e3-7763-4cec-b136-20a24250255d")),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success logout", headers = {@Header(name = "Set-Cookie", description = "This header contains cookies to reset", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))})
    })
    @PostMapping("/logout")
    public Mono<Void> logoutDevice(@CookieValue("REFRESH_TOKEN") String refreshToken, ServerHttpResponse serverHttpResponse){
        return tokenFacade.logoutToken(refreshToken, serverHttpResponse);
    }

    @Operation(summary = "Delete token device", description = "Delete token for connected device to account")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "tokenId", description = "Refresh token id for delete", example = "ID-983924..."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request params, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (use with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Token already removed or not exist"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success delete")
    })
    @DeleteMapping("/token/{tokenId}")
    public Mono<Void> deleteToken(@Pattern(regexp = "^ID-\\d+$", message = "Invalid token id") @PathVariable("tokenId") String tokenId){
        return tokenFacade.deleteToken(tokenId);
    }

    @Operation(summary = "Delete all tokens devices", description = "Delete all tokens for connected devices to account except the calling request token")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "currentTokenId", description = "Refresh token id calling the delete operation", example = "ID-983924..."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request params, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist or refresh token fot this device has already deleted, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (use with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Server inside exception, for ex. - sql operation exception"),
            @ApiResponse(responseCode = "200", description = "Success delete")
    })
    @DeleteMapping("/all/{currentTokenId}")
    public Mono<Void> deleteAllTokens(@Pattern(regexp = "^ID-\\d+$", message = "Invalid current token id") @PathVariable("currentTokenId") String tokenId){
        return tokenFacade.deleteAllTokens(tokenId);
    }


    @Operation(summary = "Get information about account tokens")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "currentTokenId", description = "Refresh token id calling the get operation", example = "ID-983924..."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request params, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist or refresh token fot this device has already deleted, need logout", schema = @Schema(example = "true")),
                    @Header(name = "Set-Cookie", description = "This header contains cookies to reset (use with logout)", schema = @Schema(example = "REFRESH_TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success get info")
    })
    @GetMapping("/all/{currentTokenId}")
    public Mono<List<TokenDataDto>> getAllTokens(@Pattern(regexp = "^ID-\\d+$") @PathVariable("currentTokenId") String tokenId, ServerHttpRequest request){
        return tokenFacade.getAllTokens(tokenId, request.getHeaders().getFirst("Device-Ip"));
    }
}
