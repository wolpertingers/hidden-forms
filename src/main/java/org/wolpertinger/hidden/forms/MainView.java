package org.wolpertinger.hidden.forms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
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
import org.wolpertinger.hidden.forms.http.OidcService;
import org.wolpertinger.hidden.forms.json.ClassListDeserializer;
import org.wolpertinger.hidden.forms.json.ThemeListDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Route("")
@ApplicationScoped
public class MainView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectMapper mapper;
    private final List<Binder<ComponentResponse>> binders = new ArrayList<>();

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

    @Inject
    @SuppressWarnings({"unchecked", "rawtypes"})
    public MainView(ResponseRepository repository, OidcService oidc) throws IOException {
        var userInfo = oidc.getUserInfo();
        var greeting = new H2("Hallo " + userInfo.getString("given_name") + "!");
        add(greeting);

        String configFilePath = ConfigProvider.getConfig().getValue("wolpertinger.config.path", String.class);
        Path path = Paths.get(configFilePath);
        BufferedReader reader = Files.newBufferedReader(path);

        var response = repository.findOrCreate(userInfo.getPreferredUserName());

        var components = getMapper().readTree(reader);
        for (var component : components) {
            JavaType javaType = getType(component);
            Object parsedComponent = getMapper().treeToValue(component.get("config"), javaType);
            if (!(parsedComponent instanceof Component vaadinComponent)) {
                String error = "Config is not a vaadin component: " + javaType.getTypeName();
                logger.error(error);
                throw new InvalidApplicationConfigurationException(error);
            }
            var field = (AbstractField) vaadinComponent;
            var fieldId = field.getId();
            if (fieldId.isPresent()) {
                var componentResponse = response.getResponse(fieldId.get());
                Class<?> valueClass = parseClassName(component);
                componentResponse.setValueClass(valueClass);
                var binder = new Binder<ComponentResponse>();
                binder.setBean(componentResponse);
                binder.forField(field).bind(componentResponse, componentResponse);
                binders.add(binder);
                add(vaadinComponent);
            }
        }

        Button submit = new Button("Bestätigen", e -> submitForm(repository, response));
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        submit.addClickShortcut(Key.ENTER);

        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content");

        add(submit);
    }

    private void submitForm(ResponseRepository repository, Response response) {
        var nonOkBinders = binders.stream().filter(b -> !b.validate().isOk()).count();
        if (nonOkBinders == 0) {
            for (var binder : binders) {
                var componentResponse = binder.getBean();
                componentResponse.setResponse(response);
                response.addResponse(componentResponse);
            }
            repository.update(response);
            Notification.show("Antworten wurden gesendet.", 3000, Notification.Position.TOP_CENTER);
        }
    }

    private Class<?> parseClassName(JsonNode classDefinition) {
        var valueClass = classDefinition.get("valueClass");
        if (valueClass != null) {
            try {
                return Class.forName(valueClass.textValue());
            } catch (ClassNotFoundException e) {
                String error = "Class for value cannot be found: " + valueClass.textValue();
                logger.error(error);
                throw new InvalidApplicationConfigurationException(error);
            }
        }
        return null;
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

