package com.distributionAndLogisticsManagementPlatform.serviceImpl;

import com.distributionAndLogisticsManagementPlatform.entity.User;
import com.distributionAndLogisticsManagementPlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

//Add your annotations here
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userDetail = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                userDetail.getEmail(),
                userDetail.getPassword(),
                new ArrayList<>()
        );
    }

    public User getUserDetail() {
        return userDetail;
    }
}
