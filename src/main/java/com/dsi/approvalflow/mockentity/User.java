package com.dsi.approvalflow.mockentity;

import com.dsi.approvalflow.mockentity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private List<Role> roles;
}
