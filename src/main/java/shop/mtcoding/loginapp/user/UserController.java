package shop.mtcoding.loginapp.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final HttpSession session; // IoC 등록되어 있음 (스프링 실행 되면)

    // http://localhost:8080/oauth/callback?code=3u9fk
    @GetMapping("/oauth/callback")
    public String oauthCallback(String code) {
        System.out.println("callback::"+code);
        User sessionUser = userService.naverLogin(code);
        session.setAttribute("sessionUser", sessionUser);
        return "redirect:/shop";
    }

    @GetMapping("/join-form")
    public String joinForm(){
        return "user/join-form";
    }

    @GetMapping("/login-form")
    public String loginForm(){
        return "user/login-form";
    }

    @PostMapping("/join")
    public String join(String username, String password, String email){
        userService.doJoin(username, password, email);
        return "redirect:/login-form";
    }

    @PostMapping("/login")
    public String login(String username, String password){
        User sessionUser = userService.doLogin(username, password);
        session.setAttribute("sessionUser", sessionUser); // 로그인 되면 세션 저장
        return "redirect:/shop";
    }
}