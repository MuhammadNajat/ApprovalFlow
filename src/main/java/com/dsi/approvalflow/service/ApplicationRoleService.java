package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.ApplicationRole;
import com.dsi.approvalflow.mockentity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ApplicationRoleService {
    //private List<ApplicationRole> applicationRoles;
    private Map<Long, List<Role>> applicationRoles = new HashMap<>();

    public void setRolesForApplication(Long applicationId, List<Role> roles) {
        applicationRoles.put(applicationId, roles);
    }

    public List<Role> getRolesForApplication(Long applicationId) {
        return applicationRoles.get(applicationId);
    }

    public void removeRolesForApplication(Long applicationId) {
        applicationRoles.remove(applicationId);
    }
}
