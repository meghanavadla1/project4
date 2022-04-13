package com.example.demo.controllertests;

import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<ModifyCartRequest> json;

    @Autowired
    private JacksonTester<CreateUserRequest> jsonCreateUser;

    private String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmFiIn0.fWLuNUE3ELAm0S5h4vqHufcvIgQyDNXfycH9hMXeJjXLRR3G9aAF18gFcKn5frSg-XAfPa1Mzffc9iz1B0bn8w";

    private void setUp(String username) throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword("test_password");
        createUserRequest.setConfirmPassword("test_password");
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/user/create"))
                .content(jsonCreateUser.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBeAbleToAddToCart() throws Exception {
        setUp("prab");
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("prab");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        String productName = JsonPath.read(postResult.getResponse().getContentAsString(), "$.items[0].name");
        assertEquals("Round Widget", productName);
    }

    @Test
    public void shouldBeAbleToRemoveItemsFromCart() throws Exception {
        final double DELTA = 1e-15;
        setUp("prabh");
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("prabh");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk());

        MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/removeFromCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        Double total = JsonPath.read(postResult.getResponse().getContentAsString(), "$.total");
        Assert.assertEquals(0.00, total, DELTA);
    }

    @Test
    public void shouldGetErrorIfUserDoesnotExists() throws Exception {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("invalid");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldGetErrorIfTokenIsMissing() throws Exception {
        setUp("test_user");
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test_user");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post(new URI("/api/cart/addToCart"))
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}