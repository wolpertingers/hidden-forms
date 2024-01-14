package org.wolpertinger.hidden.forms.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.util.Map;

@Entity
public class Question extends PanacheEntity {
    private String className;
    private Map<String, Object> config;
}
