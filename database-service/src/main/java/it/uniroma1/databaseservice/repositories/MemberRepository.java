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
package it.uniroma1.databaseservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma1.databaseservice.entities.Member;
import it.uniroma1.databaseservice.entities.models.UserUI;

public interface MemberRepository extends JpaRepository<Member, Long>{

    @Query("SELECT NEW it.uniroma1.databaseservice.entities.models.UserUI(u.id, u.username, u.email, u.name, u.surname, u.enabled)" +
            "FROM Member u " +
            "WHERE u.username LIKE %?1%" +
            "   OR u.email LIKE %?1% " +
            "   OR u.name LIKE %?1% " +
            "   OR u.surname LIKE %?1%")
    public List<UserUI> searchUsers(String queryString);

    @Query("SELECT NEW it.uniroma1.databaseservice.entities.models.UserUI(u.id, u.username, u.email, u.name, u.surname, u.enabled) " +
            "FROM Member u " +
            "WHERE 1 = 1")
    public List<UserUI> findAllUsers();


    @Query("SELECT m FROM Member m WHERE m.username = ?1")
    public Member findByUsername(String username);

    public Member findById(long id);

    @Query("SELECT a.authorityName " +
           "FROM Member u " +
           "INNER JOIN u.authorities a " +
           "WHERE u.id = ?1"
    )
    public List<String> rolesForUserId(long userId);

    @Query("SELECT m.id FROM Member m WHERE m.username = ?1")
    public long loadMemberIdByUsername(String username);

}
