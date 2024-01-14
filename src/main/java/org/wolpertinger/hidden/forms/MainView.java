package org.wolpertinger.hidden.forms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ClassList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InvalidApplicationConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {
    private ObjectMapper mapper;

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.registerSubtypes(ClassList.class);
        }
        return mapper;
    }

    public MainView() throws IOException {
        Path path = Paths.get("src/test/resources/config.json");
        BufferedReader reader = Files.newBufferedReader(path);

        var components = getMapper().readTree(reader);

        for (var component : components) {
            JavaType javaType = getType(component);
            Object vaadinComponent = getMapper().treeToValue(component.get("config"), javaType);
            if (!(vaadinComponent instanceof Component)) {
                throw new InvalidApplicationConfigurationException("Config is not a vaadin component: " + javaType.getTypeName());
            }
            add((Component) vaadinComponent);
        }

        var radioButtonGroup = new RadioButtonGroup<String>();
        radioButtonGroup.setLabel("Dumm?");
        radioButtonGroup.setItems("true", "false");
        add(radioButtonGroup);

        // Use TextField for standard text input
        TextField textField = new TextField("Your name");
        textField.addThemeName("bordered");

        // Button click listeners can be defined as lambda expressions
        Button button = new Button("Say hello", e -> Notification
                .show(String.format("Hello %s!", textField.getValue())));

        // Theme variants give you predefined extra styles for components.
        // Example: Primary button is more prominent look.
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // You can specify keyboard shortcuts for buttons.
        // Example: Pressing enter in this view clicks the Button.
        button.addClickShortcut(Key.ENTER);

        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content");

        add(textField, button);
    }

    private JavaType getType(JsonNode classDefinition) {
        String className = classDefinition.get("className").textValue();
        JavaType baseType = getMapper().getTypeFactory().constructFromCanonical(className);
        return getMapper().getTypeFactory().constructParametricType(baseType.getRawClass(), getTypes(classDefinition.get("genericTypes")));
    }

    private JavaType[] getTypes(JsonNode genericTypes) {
        if (genericTypes == null) {
            return null;
        }
        List<JavaType> types = new ArrayList<>();
        for (var genericType : genericTypes) {
            types.add(getType(genericType));
        }
        return types.toArray(JavaType[]::new);
    }
}
