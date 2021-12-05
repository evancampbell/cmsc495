package main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import main.db.Site;
import main.db.SiteRepository;
import main.db.User;
import main.db.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SSUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Logger logger;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteByEmail(String email) {
        User user = userRepository.findByEmail(email);
        Set<Site> subscribedSites = userRepository.findByEmail(email).getSites();
        for (Site s : subscribedSites) {
            Set<User> users = s.getUsers();
            for (User u : users) {
                if (u.getEmail().equals(email)) {
                    users.remove(u);
                    s.setUsers(users);
                }
            }

            //System.out.println("\nUser to delete: " + user.getId());
            //System.out.println("Result of removal: " + b);
            siteRepository.save(s);
        }
        userRepository.deleteByEmail(email);
    }

    public List<User> findAll() { return userRepository.findAll(); }

    public void saveUser(User user, String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

}
