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
package it.uniroma1.userservice.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import it.uniroma1.userservice.entities.Role;
import it.uniroma1.userservice.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEditModel {
    
    @NotNull
    @NotBlank
    private long id;
    
    @Email
    @NotBlank
    private String email;

    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private Boolean enabled;

    private List<String> authorities;

      public User toUser() {

        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setEnabled(enabled);
        u.setName(name);
        u.setPassword(password);
        u.setSurname(surname);

        Set<Role> rolesForUser = new HashSet<Role>();
        if(authorities != null && !authorities.isEmpty()) {
            for(String tmp : authorities) {
                Role r = new Role();
                r.setAuthority(tmp);
                rolesForUser.add(r);
            }
        }
        u.setAuthorities(rolesForUser);
        return u;
    }

}
