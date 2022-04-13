package com.example.demo.controllertests;

import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

private UserControllerTest userControllerTest;
private UserRepository userRepo=mock(UserRepository.class);
private CartRepository cartRepo=mock(CartRepository.class);



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    private String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmFiIn0.fWLuNUE3ELAm0S5h4vqHufcvIgQyDNXfycH9hMXeJjXLRR3G9aAF18gFcKn5frSg-XAfPa1Mzffc9iz1B0bn8w";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()) // enable security for the mock set up
                .build();
    }

    @Test
    public void shouldBeAbleToFindUserById() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test_user_id");
        createUserRequest.setPassword("test_password");
        createUserRequest.setConfirmPassword("test_password");
        MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                .content(json.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Integer id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");
        String uri = "/api/user/id/" + id;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(new URI(uri))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        Assert.assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void shouldBeAbleToFindUserByName() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test_user1");
        createUserRequest.setPassword("test_password1");
        createUserRequest.setConfirmPassword("test_password1");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                .content(json.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/user/test_user1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        Assert.assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void shouldBeAbleToCreateUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test_user");
        createUserRequest.setPassword("test_password");
        createUserRequest.setConfirmPassword("test_password");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                .content(json.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldComplainWhenConfirmPasswordDoesentMatch() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test_user");
        createUserRequest.setPassword("test_password");
        createUserRequest.setConfirmPassword("test_passw");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                .content(json.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldComplainPasswordLessThanSevenChars() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test_user");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                .content(json.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldComplainWhenUserDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/user/invalidUser"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}