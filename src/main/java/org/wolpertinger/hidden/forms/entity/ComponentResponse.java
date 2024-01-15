package org.wolpertinger.hidden.forms.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ComponentResponse extends PanacheEntity {
    public String componentId;
    public String value;
    @ManyToOne
    public Response response;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
