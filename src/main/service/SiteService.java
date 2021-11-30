package main.service;

import main.db.Site;
import main.db.SiteRepository;
import main.db.User;
import main.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserRepository userRepository;

    public Site findSiteByUrl(String url) {
        return siteRepository.findByUrl(url);
    }

    public List<Site> findAll() { return siteRepository.findAll(); }

    public void saveSite(Site site, User user) {
        Set<User> users = site.getUsers();
        if (users == null) {
            users = new HashSet<User>();
        }
        users.add(user);
        site.setUsers(users);
        siteRepository.save(site);

        Set<Site> sites = user.getSites();
        if (sites == null) {
            sites = new HashSet<Site>();
        }
        sites.add(site);
        user.setSites(sites);
        userRepository.save(user);
    }
}
