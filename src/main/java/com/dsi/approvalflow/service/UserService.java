package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.User;
import com.dsi.approvalflow.mockentity.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: Remove unnecessary static keywords
public class UserService {
    private static List<User> users;
    private static User currentUser;

    public UserService() {
        users = Arrays.asList(
                new User(1L, Arrays.asList(Role.ASSISTANT_CONTROLLER)),
                new User(2L, Arrays.asList(Role.APPLICANT)),
                new User(3L, Arrays.asList(Role.CONTROLLER)),
                new User(4L, Arrays.asList(Role.SECRETARY)),
                new User(5L, Arrays.asList(Role.ASSISTANT_CONTROLLER))
        );
    }

    public static List<User> getUsersByRole(Role role) {
        List<User> filteredUsers = new ArrayList<>();
        for(User user : users) {
            if(user.getRoles().contains(role)) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    public static User getUserById(Long id) {
        for(User user : users) {
            if(user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
