package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.ApplicationRole;
import com.dsi.approvalflow.mockentity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class ApplicationRoleService {

    // TODO: save as per actual design

    private List<ApplicationRole> applicationRoles = new ArrayList<>();

    public void removeRolesForApplication(Long applicationId) {
        System.out.println("Before removing applicationRoles");
        for(ApplicationRole data : applicationRoles) {
            System.out.println("Application ID: " + data.getApplicationId() + " Role: " + data.getRole().toString());
        }

        // TODO: persist in database using application id
        applicationRoles = applicationRoles.stream()
                .filter(applicationRole -> !Objects.equals(applicationRole.getApplicationId(), applicationId))
                .collect(Collectors.toList());

        System.out.println("After removing applicationRoles");
        for(ApplicationRole data : applicationRoles) {
            System.out.println("Application ID: " + data.getApplicationId() + " Role: " + data.getRole().toString());
        }
    }

    public void setRolesForApplication(Long applicationId, List<Role> reviewerRoles) {
        removeRolesForApplication(applicationId);
        // TODO: use stream/map
        for (Role role : reviewerRoles) {
            ApplicationRole applicationRole = new ApplicationRole(applicationId, role.toString());
            applicationRoles.add(applicationRole);
        }

        System.out.println("After adding applicationRoles");
        for(ApplicationRole data : applicationRoles) {
            System.out.println("Application ID: " + data.getApplicationId() + " Role: " + data.getRole().toString());
        }
    }

    //Usage?
    public List<Role> getRolesForApplication(Long applicationId) {
        List<Role> roles = new ArrayList<>();
        for(ApplicationRole applicationRole : applicationRoles) {
            if(applicationRole.getApplicationId() == applicationId) {
                roles.add(Role.valueOf(applicationRole.getRole()));
            }
        }
        return roles;
    }

    public List<Long> getApplicationsForRole(Role role) {
        List<Long> applicationIds = new ArrayList<>();
        for(ApplicationRole applicationRole : applicationRoles) {
            if(Role.valueOf(applicationRole.getRole()) == role) {
                applicationIds.add(applicationRole.getApplicationId());
            }
        }
        return applicationIds;
    }

    public List<Long> getApplicationsForRoles(List<Role> roles) {
        List<Long> applicationIds = new ArrayList<>();
        for(Role role : roles) {
            for (ApplicationRole applicationRole : applicationRoles) {
                if (Role.valueOf(applicationRole.getRole()) == role) {
                    applicationIds.add(applicationRole.getApplicationId());
                }
            }
        }
        return applicationIds;
    }
}
