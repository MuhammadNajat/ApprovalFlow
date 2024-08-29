package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.ApplicationRole;
import com.dsi.approvalflow.mockentity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
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

        applicationRoles = applicationRoles.stream()
                .filter(applicationRole -> applicationRole.getApplicationId() != applicationId)
                .collect(Collectors.toList());

        System.out.println("After removing applicationRoles");
        for(ApplicationRole data : applicationRoles) {
            System.out.println("Application ID: " + data.getApplicationId() + " Role: " + data.getRole().toString());
        }
    }

    public void setRolesForApplication(Long applicationId, List<Role> roles) {
        removeRolesForApplication(applicationId);
        for (Role role : roles) {
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
}
