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
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.exceptions_dto.VersionExceptionResponse;
import tgc.plus.authservice.dto.tokens_dto.TokenData;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.facades.TokenFacade;

import java.util.List;

@RestController
@RequestMapping("/api/tokens")
@Tag(name = "api/tokens", description = "Tokens controller api")
public class TokenController {

    @Autowired
    private TokenFacade tokenFacade;

    @Operation(summary = "Update access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", content = @Content(), description = "Request has bad request body, for example - validation exceptions"),
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Logout", description = "Refresh token not exist or expired, need logout", schema = @Schema(example = "true"))
            },description = "Refresh token not exist or expired"),
            @ApiResponse(responseCode = "200", description = "Success tokens update")
    })
    @PatchMapping("/update")
    public Mono<UpdateTokenResponse> updateToken(@RequestBody @Valid UpdateToken updateToken){
        return tokenFacade.updateAccessToken(updateToken);
    }

    @Operation(summary = "Delete token device", description = "Delete token for connected device to account")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "tokenId", description = "Refresh token id for delete", example = "ID-983924..."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Token already removed or not exist"),
            @ApiResponse(responseCode = "200", description = "Success delete")
    })
    @DeleteMapping("/token/{tokenId}")
    public Mono<Void> deleteToken(@Pattern(regexp = "^ID-\\d+$") @PathVariable("tokenId") String tokenId){
        return tokenFacade.deleteToken(tokenId);
    }

    @Operation(summary = "Delete all tokens devices", description = "Delete all tokens for connected devices to account except the calling request token")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "currentTokenId", description = "Refresh token id calling the delete operation", example = "ID-983924..."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist or refresh token fot this device has already deleted, need logout", schema = @Schema(example = "true"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "200", description = "Success delete")
    })
    @DeleteMapping("/all/{currentTokenId}")
    public Mono<Void> deleteAllTokens(@Pattern(regexp = "^ID-\\d+$") @PathVariable("currentTokenId") String tokenId){
        return tokenFacade.deleteAllTokens(tokenId);
    }


    @Operation(summary = "Get information about account tokens")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "currentTokenId", description = "Refresh token id calling the get operation", example = "ID-983924..."),
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist or refresh token fot this device has already deleted, need logout", schema = @Schema(example = "true"))
            },description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success get info")
    })
    @GetMapping("/all/{currentTokenId}")
    public Mono<List<TokenData>> getAllTokens(@Pattern(regexp = "^ID-\\d+$") @PathVariable("currentTokenId") String tokenId, ServerHttpRequest request){
        return tokenFacade.getAllTokens(tokenId, request.getHeaders().getFirst("Device-Ip"));
    }
}
