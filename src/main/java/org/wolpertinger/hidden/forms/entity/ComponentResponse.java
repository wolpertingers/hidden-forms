package org.wolpertinger.hidden.forms.entity;

import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ComponentResponse extends PanacheEntity implements ValueProvider<ComponentResponse, String>, Setter<ComponentResponse, Object> {
    private String componentId;
    private String value;
    @ManyToOne
    private Response response;

    public ComponentResponse setComponentId(String componentId) {
        this.componentId = componentId;
        return this;
    }

    public String getComponentId() {
        return componentId;
    }

    public ComponentResponse setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public ComponentResponse setResponse(Response response) {
        this.response = response;
        return this;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public void accept(ComponentResponse response, Object value) {
        response.setValue(value.toString());
    }

    @Override
    public String apply(ComponentResponse response) {
        return response.getValue();
    }
}