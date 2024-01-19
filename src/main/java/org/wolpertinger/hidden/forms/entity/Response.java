package org.wolpertinger.hidden.forms.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Response extends PanacheEntity {

    @NaturalId
    public String userId;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ComponentResponse> responses;

    public Response() {
    }

    public Response(String userId) {
        this.userId = userId;
    }

    public ComponentResponse getResponse(String componentId) {
        if (responses == null) {
            responses = new ArrayList<>();
        }
        var optionalResponse = responses.stream().filter(cr -> cr.getComponentId().equals(componentId)).findAny();
        return optionalResponse.orElseGet(() -> new ComponentResponse().setComponentId(componentId).setResponse(this));
    }

    public void addResponse(ComponentResponse componentResponse) {
        if (responses == null) {
            responses = new ArrayList<>();
        }
        responses.add(componentResponse);
    }
}
