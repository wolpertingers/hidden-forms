package org.wolpertinger.hidden.forms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ClassList;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InvalidApplicationConfigurationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wolpertinger.hidden.forms.entity.ComponentResponse;
import org.wolpertinger.hidden.forms.entity.Response;
import org.wolpertinger.hidden.forms.entity.ResponseRepository;
import org.wolpertinger.hidden.forms.json.ClassListDeserializer;
import org.wolpertinger.hidden.forms.json.ThemeListDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
@Route("")
public class MainView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectMapper mapper;

    @Inject
    ResponseRepository repository;

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            SimpleModule module = new SimpleModule();
            module.addDeserializer(ClassList.class, new ClassListDeserializer());
            module.addDeserializer(ThemeList.class, new ThemeListDeserializer());
            mapper.registerModules(module, new JavaTimeModule());
        }
        return mapper;
    }

    public MainView() throws IOException {
        String configFilePath = ConfigProvider.getConfig().getValue("wolpertinger.config.path", String.class);
        Path path = Paths.get(configFilePath);
        BufferedReader reader = Files.newBufferedReader(path);

        UI.getCurrent().setLocale(Locale.GERMAN);

        var components = getMapper().readTree(reader);
        var vaadinComponents = new ArrayList<AbstractField>();

        for (var component : components) {
            JavaType javaType = getType(component);
            Object parsedComponent = getMapper().treeToValue(component.get("config"), javaType);
            if (!(parsedComponent instanceof AbstractField vaadinComponent)) {
                String error = "Config is not a vaadin field: " + javaType.getTypeName();
                logger.error(error);
                throw new InvalidApplicationConfigurationException(error);
            }
            vaadinComponents.add(vaadinComponent);
        }

        // create submittable form
//        var responseForm = new ResponseForm(vaadinComponents);
//
//        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
//        addClassName("centered-content");
//
//        add(responseForm);
    }

    private void storeResponse(List<Component> components) {
        Response response = repository.findOrCreate("1234");
        List<ComponentResponse> responses = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof AbstractField field && field.getId().isPresent()) {
                responses.add(getResponse(field));
            }
        }
        response.responses = responses;
        repository.createOrUpdate(response);
        Notification.show("Antworten wurden gespeichert", 1000, Notification.Position.TOP_CENTER);
    }

    private ComponentResponse getResponse(AbstractField field) {
        var response = new ComponentResponse();
        response.componentId = field.getId().get();
        response.value = (String) field.getValue();
        return response;
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
