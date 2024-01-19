package org.wolpertinger.hidden.forms.entity;

import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class ComponentResponse extends PanacheEntity implements ValueProvider<ComponentResponse, Object>, Setter<ComponentResponse, Object> {
    private String componentId;
    private String value;
    @Transient
    private Class<?> valueClass;
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

    public void setValueClass(Class<?> valueClass) {
        this.valueClass = valueClass;
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
    public Object apply(ComponentResponse response) {
        var value = response.getValue();
        if (value != null && valueClass != null) {
            if (valueClass.isAssignableFrom(LocalDateTime.class)) {
                return LocalDateTime.parse(value);
            }
            if (valueClass.isAssignableFrom(LocalDate.class)) {
                return LocalDate.parse(value);
            }
            if (valueClass.isAssignableFrom(LocalTime.class)) {
                return LocalTime.parse(value);
            }
        }
        return value;
    }
}