/**
 * @author Giuseppe Valente<valentepeppe@gmail.com>
 * A simple username/password custom authentication
 *
 */

package it.uniroma1.authenticationserver.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import it.uniroma1.authenticationserver.entities.Member;
import it.uniroma1.authenticationserver.repositories.MemberRepository;

@Component
public class CustomAuth implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(CustomAuth.class);

    @Autowired
    private MemberRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("authenticate");
        
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Member u = userRepository.findByUsername(authentication.getName());
        logger.info(u.getUsername());
        //Check same password in DB
        if( u != null && u.getUsername() != null &&
            bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), u.getPassword()) 
            && u.isEnabled()) {
            return new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword(), u.getAuthorities());
        } 
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
