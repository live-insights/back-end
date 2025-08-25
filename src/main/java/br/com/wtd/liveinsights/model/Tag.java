package br.com.wtd.liveinsights.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")})
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Tag() {}
    public Tag(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
