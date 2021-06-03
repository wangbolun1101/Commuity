package com.example.commuity.controller;

import com.example.commuity.dto.AccessTokenDTO;
import com.example.commuity.dto.GithubUser;
import com.example.commuity.mappper.UserMapper;
import com.example.commuity.model.User;
import com.example.commuity.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
//http://ping.chinaz.com/github.com

@Controller

public class AuthorizeController {
   @Autowired
   private GithubProvider githubProvider;
   @Autowired
   private UserMapper userMapper;
   @Value("5efbd1a6025b8cc891e1")
   private String clientId;
   @Value("55121823824a862cd1b80f26f1d1a39f32cd4580")
   private String clientSecret;
   @Value("http://localhost:8080/callback")
   private String redirectUri;

   @GetMapping("/callback")
   public String callback(@RequestParam(name="code") String code,
                          @RequestParam(name="state") String state,
                          HttpServletRequest request,
                          HttpServletResponse response){
        AccessTokenDTO accesstokenDTO = new AccessTokenDTO();
        accesstokenDTO.setCode(code);
        accesstokenDTO.setRedirect_uri(redirectUri);
        accesstokenDTO.setClient_id(clientId);
        accesstokenDTO.setClient_secret(clientSecret);
        accesstokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accesstokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
       if(githubUser != null){
            User user = new User();
           String token = UUID.randomUUID().toString();
           user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
           //登陆成功，写cookie和session
            return "redirect:/";
       }else{
           return "redirect:/";
           //登陆失败，重新登录
       }

    }
}
