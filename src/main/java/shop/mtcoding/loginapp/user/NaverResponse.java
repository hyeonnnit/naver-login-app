package shop.mtcoding.loginapp.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.security.Timestamp;

public class NaverResponse {
    @Data // getter, setter
    public static class TokenDTO {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("expires_in")
        private Integer expiresIn;
        private String scope;
        @JsonProperty("refresh_token_expires_in")
        private Integer refreshTokenExpiresIn;
    }

    @Data
    public static class NaverUserDTO{
        private Long id;

        @JsonProperty("response")
        private Properties properties;

        @Data
        class Properties{
            private String name;
        }
    }

}
