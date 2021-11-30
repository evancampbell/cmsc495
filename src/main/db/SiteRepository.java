package main.db;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;


public interface SiteRepository extends MongoRepository<Site, String> {
    Site findByUrl(String url);

    List<Site> findAll();

}
