package com.example.demo.controllertests;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmFiIn0.fWLuNUE3ELAm0S5h4vqHufcvIgQyDNXfycH9hMXeJjXLRR3G9aAF18gFcKn5frSg-XAfPa1Mzffc9iz1B0bn8w";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldBeAbleToGetAllItems() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andReturn();
        List<Integer> ids = JsonPath.read(result.getResponse().getContentAsString(), "$..id");
        Assert.assertEquals(2, ids.size());
    }

    @Test
    public void shoudBeAbleToFindItemById() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andReturn();
        List<Integer> ids = JsonPath.read(result.getResponse().getContentAsString(), "$..id");
        Assert.assertEquals(1, ids.size());
    }

    @Test
    public void shouldBeAbleToGetItemByName() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item/name/Square%20Widget"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andReturn();
        List<Integer> ids = JsonPath.read(result.getResponse().getContentAsString(), "$..id");
        Assert.assertEquals(1, ids.size());
    }

    @Test
    public void shouldGetNotFoundWhenItemDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(new URI("/api/item/5"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}