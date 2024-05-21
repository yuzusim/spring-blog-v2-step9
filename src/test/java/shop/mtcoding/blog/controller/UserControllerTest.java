package shop.mtcoding.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import shop.mtcoding.blog._core.utils.JwtUtil;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 1. 통합 테스트 (스프링의 모든 빈을 IOC에 등록하고 테스트 하는 것)
 */

@AutoConfigureMockMvc //MockMvc IOC 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest {

    private  ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void userinfd_test() throws Exception {
        // given
        Integer id = 1;
        String jwt = JwtUtil.create(
                User.builder()
                        .id(1)
                        .username("ssar")
                        .password("1234")
                        .email("ssar@nate.com")
                        .build()
        );

        // when
        ResultActions actions = mvc.perform(
                get("/api/users/"+id)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("respBody : "+respBody);

        // then

    }


    @Test
    public void login_fail_test() throws Exception {
        // given
        UserRequest.LoginDTO reqDTO = new UserRequest.LoginDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("12345");

        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/login")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isUnauthorized()); // header 검증

        actions.andExpect(jsonPath("$.status").value(401));
        actions.andExpect(jsonPath("$.msg").value("인증되지 않았습니다"));
        actions.andExpect(jsonPath("$.body").isEmpty());
    }


    @Test
    public void login_success_test() throws Exception {
        // given
        UserRequest.LoginDTO reqDTO = new UserRequest.LoginDTO();
        reqDTO.setUsername("ssar");
        reqDTO.setPassword("1234");

        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/login")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String respBody = actions.andReturn().getResponse().getContentAsString();
        //System.out.println("respBody : "+respBody);
        String jwt = actions.andReturn().getResponse().getHeader("Authorization");
        System.out.println("jwt = " + jwt);

        // then
        actions.andExpect(status().isOk()); // header 검증
        actions.andExpect(result -> result.getResponse().getHeader("Authorization").contains("Bearer " + jwt));


        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body").isEmpty());
    }


    @Test
    public void join_test() throws Exception {
        // given
        //테스트 위해 본코드를 고치지 마라
        //그냥 게터, 세터 써
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setUsername("haha");
        reqDTO.setPassword("1234");
        reqDTO.setEmail("haha@nate.com");

        //스트링으로 바꿔서 제이슨으로 보내야 함
        //제이슨으로 컨버팅해야됨
        String reqBody  = om.writeValueAsString(reqDTO); //익셉션처리 해줘야함(알트엔트)

        //System.out.println("reqBody" + reqBody);

        // when
        //통신 해보자
        ResultActions actions = mvc.perform(// 익셉션 처리해서 실행해봐도 안됨 보낸게 없으니까??....
                post("/join")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        //contentType 꼭 적어 메세지 컨버팅 할꺼아니야
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
//        int statusCode = actions.andReturn().getResponse().getStatus();
        System.out.println("respBody" +respBody);
//        System.out.println("statusCode" +statusCode);


        // then
        actions.andExpect(jsonPath("$.status").value(200));

    }

}
