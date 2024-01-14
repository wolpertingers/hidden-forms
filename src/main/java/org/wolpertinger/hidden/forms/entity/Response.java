package org.wolpertinger.hidden.forms.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Entity
public class Response extends PanacheEntity {
    @NaturalId
    public String userId;
    public List<ComponentResponse> responses;
}
