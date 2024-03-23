/**
 * @author Giuseppe Valente<valentepeppe@gmail.com>
 * Interface for roles
 */
package it.uniroma1.authenticationserver.repositories;

import org.springframework.data.repository.CrudRepository;

import it.uniroma1.authenticationserver.entities.Authority;


public interface AuthorityRepository extends CrudRepository<Authority, Long>{
    public Authority findByAuthorityName(String authorityName);
}
