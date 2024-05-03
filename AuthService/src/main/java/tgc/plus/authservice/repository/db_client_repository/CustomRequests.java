package tgc.plus.authservice.repository.db_client_repository;

public interface CustomRequests {

    String GET_ALL_USER_TOKENS ="SELECT user_tokens.token_id, token_meta.device_name, " +
                                "token_meta.application_type, token_meta.device_ip FROM user_tokens " +
                                "JOIN token_meta ON user_tokens.id = token_meta.token_id WHERE user_tokens.user_id= :userId";

    String GET_USER_AND_COUNT_TOKENS = "SELECT users.id, users.two_factor_status, users.user_code, users.email, users.role, count(user_tokens.id) as tokens_count from users join user_tokens " +
            "ON (users.id = user_tokens.user_id) WHERE users.email=:email";

    String GET_INFO_ABOUT_USER_TOKEN_AND_META = "SELECT user_tokens.id as user_token_id, user_tokens.user_id as user_id, token_meta.device_ip FROM user_tokens JOIN token_meta " +
                                               "ON user_tokens.id = token_meta.token_id WHERE user_tokens.token_id= :tokenId FOR UPDATE";

}
