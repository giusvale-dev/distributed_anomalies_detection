
 package it.uniroma1.userservice.controllers;

 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import javax.validation.constraints.Email;
 import javax.validation.constraints.NotBlank;
 
 import it.uniroma1.userservice.entities.Role;
 import it.uniroma1.userservice.entities.User;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;
 
 @Getter
 @Setter
 @NoArgsConstructor
 public class UserEditModel {
 
     @NotBlank
     private String username;
 
     @Email
     @NotBlank
     private String email;
 
     @NotBlank
     private String password;
 
     @NotBlank
     private String name;
 
     @NotBlank
     private String surname;
 
     @NotBlank
     private Boolean enabled;
 
     private List<String> roles;
 
     public User toUser() {
 
         User u = new User();
         u.setEmail(email);
         u.setEnabled(enabled);
         u.setName(name);
         u.setPassword(password);
         u.setSurname(surname);
         u.setUsername(username);
 
         Set<Role> rolesForUser = new HashSet<Role>();
         if(roles != null && !roles.isEmpty()) {
             for(String tmp : roles) {
                 Role r = new Role();
                 r.setAuthority(tmp);
             }
         }
         u.setAuthorities(rolesForUser);
         return u;
     }
     
 }
 
 
