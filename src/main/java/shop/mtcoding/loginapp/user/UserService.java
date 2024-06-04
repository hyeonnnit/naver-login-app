package shop.mtcoding.loginapp.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void doJoin(String username, String password, String email) {
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        userRepository.save(user);
    }

    public User doLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("아이디가 없습니다");
        } else {
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                throw new RuntimeException("비밀번호가 틀렸습니다");
            }
        }
    }

    public User naverLogin(String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "78LzIOD5hE6wybddS928");
        body.add("client_secret", "kNC_yBAazh");
        body.add("state", "1234");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<NaverResponse.TokenDTO> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                request,
                NaverResponse.TokenDTO.class
        );

        System.out.println(response.getBody().toString());

        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        headers1.add("Authorization", "Bearer " + response.getBody().getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request1 = new HttpEntity<>(headers1);

        ResponseEntity<NaverResponse.NaverUserDTO> response1 = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request1,
                NaverResponse.NaverUserDTO.class
        );

        System.out.println("response1: " + response1.getBody().toString());

        String username = "naver_" +response1.getBody().getId();
        User userPS = userRepository.findByUsername(username);

        if (userPS != null){
            System.out.println("강제 로그인 진행");
            return userPS;
        }else {
            System.out.println("강제 회원가입, 강제 로그인 진행");
            User user = User.builder()
                    .username(username)
                    .password(UUID.randomUUID().toString())
                    .email(response1.getBody().getProperties().getNickname() + "@nate.com")
                    .provider("naver")
                    .build();
            User returnUser = userRepository.save(user);
            return  returnUser;
        }
    }
}