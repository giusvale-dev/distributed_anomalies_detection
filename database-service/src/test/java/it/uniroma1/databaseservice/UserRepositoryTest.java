/**
 * MIT No Attribution
 *
 * Copyright 2024 Giuseppe Valente, Antonio Cipriani, Natalia Mucha, Md Anower Hossain
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import it.uniroma1.databaseservice.entities.Authority;
import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.entities.models.UserUI;
import it.uniroma1.databaseservice.repositories.AuthorityRepository;
import it.uniroma1.databaseservice.repositories.MemberRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("dev")
public class UserRepositoryTest {


    @Autowired
    private MemberRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

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

    @Test
    @Transactional
    public void testInsertUserWithRoles() {

        Member userToInset = new Member();
        userToInset.setEmail("test@email.com");
        userToInset.setUsername("username");
        userToInset.setName("name");
        userToInset.setSurname("surname");
        userToInset.setEnabled(true);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userToInset.setPassword(bCryptPasswordEncoder.encode("password"));

        Authority role = authorityRepository.findByAuthorityName("ROLE_SUPERADMIN");
        assertNotNull(role);
        
        Set<Authority> rolesForUser = new HashSet<Authority>();
        rolesForUser.add(role);
        userToInset.setAuthorities(rolesForUser);
        userToInset = userRepository.save(userToInset);

        Member insertedUser = userRepository.findByUsername("username");
        assertNotNull(insertedUser);
        assertEquals("test@email.com", insertedUser.getEmail());
        assertEquals("name", insertedUser.getName());
        assertEquals("surname", insertedUser.getSurname());
        assertEquals(true, insertedUser.getEnabled());
        assertTrue(bCryptPasswordEncoder.matches("password", insertedUser.getPassword()));

        for(Authority a : insertedUser.getAuthorities()) {
            assertNotNull(a);
            assertEquals("ROLE_SUPERADMIN", a.getAuthorityName());
        }

    }

}
