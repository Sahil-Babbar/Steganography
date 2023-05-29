package com.stegnography.Repository;

import com.stegnography.domain.User;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User,String> {
    User findByEmail(String email);
    User findPasswordByEmail(String email);
}
