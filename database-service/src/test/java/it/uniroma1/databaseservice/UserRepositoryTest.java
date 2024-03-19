/**
 * MIT No Attribution
 *
 *Copyright 2024 Giuseppe Valente <valentepeppe@gmail.com>
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *software and associated documentation files (the "Software"), to deal in the Software
 *without restriction, including without limitation the rights to use, copy, modify,
 *merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *permit persons to whom the Software is furnished to do so.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package it.uniroma1.databaseservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.uniroma1.databaseservice.entitis.models.UserUI;
import it.uniroma1.databaseservice.repositories.UserRepository;

@SpringBootTest
@ActiveProfiles("dev")
public class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    @Test
    public void loadUsersTest() {
        long rows = userRepository.count();
        assertTrue(rows > 900);
    }

    @Test
    public void testQuerySearchUser() {
        List<UserUI> list = userRepository.searchUsers("es");
        assertNotNull(list);
        assertTrue(!list.isEmpty());
        for (UserUI userUI : list) {
            assertNotNull(userUI);
            boolean isCompliantUser = userUI.getEmail().contains("es") ||
            userUI.getUsername().contains("es") ||
            userUI.getName().contains("es") ||
            userUI.getSurname().contains("es");
            assertTrue(isCompliantUser);
        }
    }

}
