package main.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    @Query(value="{'email' : ?0}", delete=true)
    public void deleteByEmail(String email);
}
