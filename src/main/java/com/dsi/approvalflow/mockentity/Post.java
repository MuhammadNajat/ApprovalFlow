package com.dsi.approvalflow.mockentity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Post {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post that = (Post) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name);
    }
}
