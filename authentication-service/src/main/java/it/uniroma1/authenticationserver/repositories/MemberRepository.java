/**
 * @author Giuseppe Valente <valente.1160073@uniroma1.it>
 */

package it.uniroma1.authenticationserver.repositories;

import org.springframework.data.repository.CrudRepository;

import it.uniroma1.authenticationserver.entities.Member;

public interface MemberRepository extends CrudRepository<Member, Long>{

    public Member findByEmail(String email);
    public Member findByUsername(String username); 

}
