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


package it.uniroma1.databaseservice.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma1.databaseservice.entities.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    
    public Authority findByAuthorityName(String authorityName);

    public Long findIdByAuthorityName(String authorityName);

    @Query("SELECT r.id FROM Authority r WHERE 1 = 1")
    public List<Long> findAllIdRoles();

}
