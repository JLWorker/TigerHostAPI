package tgc.plus.authservice.repository.db_client_repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum SqlList {

    GET_ALL_USER_TOKENS("SELECT user_tokens.token_id as token_id, token_meta.device_name as name, token_meta.application_type as type, token_meta.device_ip as ip FROM user_tokens JOIN token_meta ON user_tokens.id = token_meta.token_id WHERE user_tokens.user_id= :userId AND user_tokens.token_id != :tokenId");

    private String sqlRequest;

    SqlList(String sqlRequest) {
        this.sqlRequest = sqlRequest;
    }
}
