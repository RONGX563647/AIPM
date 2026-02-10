package com.ai.dev.platform.modules.sys.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {
    MockMvc mockMvc;
    SysUserService sysUserService = Mockito.mock(SysUserService.class);

    @BeforeEach
    public void setup() throws Exception {
        AuthController controller = new AuthController(sysUserService, new com.ai.dev.platform.modules.sys.service.OAuthStateStore());
        // ensure githubClientId blank to trigger error case
        Field f = AuthController.class.getDeclaredField("githubClientId");
        f.setAccessible(true);
        f.set(controller, "");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetGithubAuthorizeUrl_NotConfigured() throws Exception {
        mockMvc.perform(get("/sys/user/oauth/github/authorize"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"code\":-1")));
    }

    @Test
    public void testRegister_UserExists() throws Exception {
        when(sysUserService.register(anyString(), anyString(), anyString())).thenReturn(false);
        String json = "{\"username\":\"u\",\"password\":\"p\"}";
        mockMvc.perform(post("/sys/user/register").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"code\":-1")));
    }

    @Test
    public void testForgot_UserNotFound() throws Exception {
        when(sysUserService.forgotPassword(anyString())).thenReturn(null);
        String json = "{\"username\":\"no\"}";
        mockMvc.perform(post("/sys/user/forgot").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"code\":-1")));
    }

    @Test
    public void testReset_InvalidToken() throws Exception {
        when(sysUserService.resetPassword(anyString(), anyString())).thenReturn(false);
        String json = "{\"token\":\"x\",\"newPassword\":\"p\"}";
        mockMvc.perform(post("/sys/user/reset").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"code\":-1")));
    }
}
