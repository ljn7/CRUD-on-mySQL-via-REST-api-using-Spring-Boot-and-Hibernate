package com.ljn7.restapiwithdb;

// import java.util.Arrays;
// import java.util.List;

// import javax.net.ssl.SSLEngineResult.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.Assert.assertThat;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active:test")
// @TestPropertySource(
//   locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
// @WebMvcTest(controllers = DataResourceController.class)
public class DataResourceControllerTest {

    @Autowired
    private MockMvc mvc;

    // @Autowired(required = true)
    // private Logger logger;

    // @Autowired
    // private DataResourceController repocontroller;

    // @MockBean
    // private SqlDataController sqlDataController;

    @Test
    void testAddUserWithAcceptableParameter() throws Exception {

        User user = new User("UnitTestNew", 1, "Bot");

        MvcResult result = mvc.perform(
                    post("/user/add")
                    .param("name", user.getName())
                    .param("age", Integer.toString(user.getAge()))
                    .param("gender", user.getGender())
                    .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        user.setId(11);
        ObjectMapper objectMapper = new ObjectMapper();
        String userString = objectMapper.writeValueAsString(user);
        assertEquals(json, userString);

    }

    @Test
    void testAddUserWithUnacceptableParameter() throws Exception {

        User user = new User("UnitTest   Space", 0, "125");

        MvcResult result = mvc.perform(
                post("/user/add")
                .param("name", user.getName())
                .param("age", Integer.toString(user.getAge()))
                .param("gender", user.getGender())
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest())
            .andDo(print())
            .andReturn();
    
        assertEquals(result.getResponse().getContentAsString(), "");
    }

    @Test
    void testDeleteUser() throws Exception{

        String idString = "9";
        MvcResult result = mvc.perform(
                delete("/user/delete")
                .param("id", idString)
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print())
            .andReturn();
         
        String userJson = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        User user = mapper.readValue(userJson, User.class);
        assertEquals(Integer.toString(user.getId()), idString);

    }

    @Test
    void testFindUserByAgeWithBlankStringValueShouldReturnAllUsers() throws Exception {
        
        String ageString = "";
        MvcResult result = mvc.perform(
                get("/user/search/age")
                .param("age", ageString)
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        
        assertNotEquals(result.getResponse().getContentAsString(), "");

    }
    @Test
    void testFindUserByAgeWithStringValue() {

    }
    @Test
    void testFindUserByAgeWithHypen() throws Exception {

        String ageString = "-";
        MvcResult result = mvc.perform(
                get("/user/search/age")
                .param("age", ageString)
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        
        assertNotEquals(result.getResponse().getContentAsString(), "");
    }

    @Test
    void testFindUserByAgeWithStringValueAndAnotherStringValue() throws Exception {

        String ageString = "1-50";
        MvcResult result = mvc.perform(
                get("/user/search/age")
                .param("age", ageString)
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        
        assertNotEquals(result.getResponse().getContentAsString(), "");
        
    }
    @Test
    void testFindUserByName() throws Exception {

        String ageString = "Test";
        MvcResult result = mvc.perform(
                get("/user/search/name")
                .param("name", ageString)
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        
        assertNotEquals(result.getResponse().getContentAsString(), "");
    }

    @Test
    void testGetUsers() throws Exception {

        MvcResult result = mvc.perform(
                get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertNotEquals(result.getResponse().getContentAsString(), "");
    }

    @Test
    void testUpdateUser() throws Exception {

        User user = new User("UnitTest update", 2, "Bot");
        user.setId(10);

        MvcResult result = mvc.perform(
                    put("/user/update")
                    .param("id", Integer.toString(user.getId()))
                    .param("name", user.getName())
                    .param("age", Integer.toString(user.getAge()))
                    .param("gender", user.getGender())
                    .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        user.setName("UnitTest Update");
        ObjectMapper objectMapper = new ObjectMapper();
        String userString = objectMapper.writeValueAsString(user);
        assertEquals(json, userString);
    }
}
