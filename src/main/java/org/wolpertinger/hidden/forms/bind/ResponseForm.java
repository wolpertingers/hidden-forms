package org.wolpertinger.hidden.forms.bind;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wolpertinger.hidden.forms.entity.ComponentResponse;
import org.wolpertinger.hidden.forms.entity.Response;

import java.util.List;


public class ResponseForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Binder<Response> binder = new BeanValidationBinder<>(Response.class);
    private Response response;

    private final Button save = new Button("Bestätigen");

    public ResponseForm(List<AbstractField> fields) {
        addClassName("response-form");
        for (var field : fields) {
            var componentId = field.getId();
            if (componentId.isPresent()) {
                ComponentResponse componentResponse = response.getResponse(componentId.get());
//                binder.forField(field)
//                        .bind(ComponentResponse::getValue, ComponentResponse::setValue);
            }
        }
    }

    public void setResponse(Response response) {
        this.response = response;
        binder.readBean(response);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> validateAndSave());
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(response);
            fireEvent(new SaveEvent(this, response));
            Notification.show("Antworten wurden gespeichert", 1000, Notification.Position.TOP_CENTER);
        } catch (ValidationException e) {
            logger.error("Response form validation failed", e);
        }
    }

    public static abstract class ResponseFormEvent extends ComponentEvent<ResponseForm> {
        private Response response;

        protected ResponseFormEvent(ResponseForm source, Response response) {
            super(source, false);
            this.response = response;
        }

        public Response getResponse() {
            return response;
        }
    }

    public static class SaveEvent extends ResponseFormEvent {
        SaveEvent(ResponseForm source, Response response) {
            super(source, response);
        }
    }

    public static class DeleteEvent extends ResponseFormEvent {
        DeleteEvent(ResponseForm source, Response response) {
            super(source, response);
        }

    }

    public static class CloseEvent extends ResponseFormEvent {
        CloseEvent(ResponseForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
