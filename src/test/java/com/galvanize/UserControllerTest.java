package com.galvanize;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository repository;

    @Test
    @Transactional
    @Rollback
    public void testGetAllUsers() throws Exception {
        User user = new User();
        User user1 = new User();

        user.setEmail("joeDow@yahoo.com");
        user.setPassword("1234");
        user1.setEmail("johnnyO@yahoo.com");
        user1.setPassword("5678");

        this.repository.save(user);
        this.repository.save(user1);

        MockHttpServletRequestBuilder request = get("/users");

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[1].id").value(user1.getId()));
    }

    @Test
    @Transactional
    @Rollback
    public void testPostingUser() throws Exception {
        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                            "email": "john@example.com",
                            "password": "something-secret"
                         }
                        """));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());

        this.mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotHaveJsonPath());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetUserById() throws Exception {
        User user = new User();
        User user1 = new User();

        user.setEmail("joeDow@yahoo.com");
        user.setPassword("1234");
        user1.setEmail("johnnyO@yahoo.com");
        user1.setPassword("5678");

        this.repository.save(user);
        this.repository.save(user1);

        MockHttpServletRequestBuilder request = get(String.format("/users/%d", user.getId()));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("joeDow@yahoo.com"))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());
    }

    @Test
    @Transactional
    @Rollback
    public void testPatchUser() throws Exception {
        User user = new User();
        User user1 = new User();

        user.setEmail("joeDow@yahoo.com");
        user.setPassword("1234");
        user1.setEmail("johnnyO@yahoo.com");
        user1.setPassword("5678");

        this.repository.save(user);
        this.repository.save(user1);

        MockHttpServletRequestBuilder request = patch(String.format("/users/%d", user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "email": "john@example.com"
                        }
                        """));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("john@example.com"));

    }

    @Test
    @Transactional
    @Rollback
    public void testDelete() throws Exception {
        User user = new User();
        User user1 = new User();

        user.setEmail("joeDow@yahoo.com");
        user.setPassword("1234");
        user1.setEmail("johnnyO@yahoo.com");
        user1.setPassword("5678");

        this.repository.save(user);
        this.repository.save(user1);

        MockHttpServletRequestBuilder request = delete(String.format("/users/%d", user.getId()));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

    }

    @Test
    @Transactional
    @Rollback
    public void testPostAuthenticate() throws Exception{
        User user = new User();
        User user1 = new User();

        user.setEmail("angelica@example.com");
        user.setPassword("1234");
        user1.setEmail("johnnyO@yahoo.com");
        user1.setPassword("5678");

        this.repository.save(user);
        this.repository.save(user1);

        MockHttpServletRequestBuilder request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "email": "angelica@example.com",
                          "password": "1234"
                        }
                        """));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.user.email").value("angelica@example.com"));

        MockHttpServletRequestBuilder request1 = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "email": "angelica@example.com",
                          "password": "45678"
                        }
                        """));

        this.mvc.perform(request1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.user.id").doesNotHaveJsonPath());
    }
}