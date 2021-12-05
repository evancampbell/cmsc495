package main.service;

import main.db.Site;
import main.db.SiteRepository;
import main.db.User;
import main.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
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
        if (siteRepository.findByUrl(site.getUrl()) != null) {
            site = siteRepository.findByUrl(site.getUrl());
        }
        Set<User> users = site.getUsers();
        if (users == null) {
            users = new HashSet<User>();
        }
        if (!users.contains(user)) {
            users.add(user);
        }
        site.setUsers(users);
        siteRepository.save(site);

        Set<Site> sites = user.getSites();
        if (sites == null) {
            sites = new HashSet<Site>();
        }
        boolean add = true;
        for (Site s : sites) {
            if (s.getUrl().equals(site.getUrl())) {
                add = false;
            }
        }
        if (add) sites.add(site);

        user.setSites(sites);
        userRepository.save(user);
    }

    public void removeUser(String url, User user) {
        Site site = siteRepository.findByUrl(url);
        Set<User> users = site.getUsers();
        for (Iterator<User> it = users.iterator(); it.hasNext();) {
            User u = it.next();
            if (u.getEmail().equals(user.getEmail())) {
                it.remove();
            }
        }
        site.setUsers(users);
        siteRepository.save(site);
        Set<Site> sites = user.getSites();
        for (Iterator<Site> it = sites.iterator(); it.hasNext();) {
            Site s = it.next();
            if (url.equals(s.getUrl())) {
                it.remove();
            }
        }
        user.setSites(sites);
        userRepository.save(user);
    }
}
