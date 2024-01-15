package org.wolpertinger.hidden.forms.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Entity
public class Response extends PanacheEntity {
    @NaturalId
    public String userId;
    @OneToMany(mappedBy = "response")
    public List<ComponentResponse> responses;

    public ComponentResponse getResponse(String componentId) {
        for (var response : responses) {
            if (response.componentId.equals(componentId)) {
                return response;
            }
        }
        return null;
    }
}
