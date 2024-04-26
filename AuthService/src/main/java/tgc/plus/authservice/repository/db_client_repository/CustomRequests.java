package tgc.plus.authservice.repository.db_client_repository;

public interface CustomRequests {

    String GET_ALL_USER_TOKENS ="SELECT user_tokens.token_id, token_meta.device_name, " +
                                "token_meta.application_type, token_meta.device_ip FROM user_tokens " +
                                "JOIN token_meta ON user_tokens.id = token_meta.token_id WHERE user_tokens.user_id= :userId";

    String GET_INFO_FOR_UPDATE_TOKENS ="SELECT users.user_code, users.role, user_tokens.expired_date  FROM users JOIN user_tokens " +
                                       "ON users.id = user_tokens.user_id WHERE user_tokens.refresh_token= :refreshToken AND users.active = true";

    String GET_INFO_ABOUT_USER_TOKEN_AND_META = "SELECT user_tokens.id as user_token_id, user_tokens.user_id as user_id, token_meta.device_ip FROM user_tokens JOIN token_meta " +
                                               "ON user_tokens.id = token_meta.token_id WHERE user_tokens.token_id= :tokenId FOR UPDATE";

}
