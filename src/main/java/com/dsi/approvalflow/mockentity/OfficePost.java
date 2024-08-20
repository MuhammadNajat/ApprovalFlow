package com.dsi.approvalflow.mockentity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class OfficePost {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficePost that = (OfficePost) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name);
    }
}
