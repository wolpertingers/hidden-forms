package org.wolpertinger.hidden.forms.bind;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import org.wolpertinger.hidden.forms.entity.ComponentResponse;

public class ComponentResponseBinder extends Binder<ComponentResponse> {
    @Override
    public BinderValidationStatus<ComponentResponse> validate() {
        var superResult = super.validate();
        var response = getBean();
        if (superResult.isOk() && response.isRequired()) {
            var binding = getBinding("value");
            if (binding.isPresent()) {
                var error = BindingValidationStatus.createUnresolvedStatus(binding.get());
                superResult.getFieldValidationStatuses().add(error);
            }
        }
        return superResult;
    }
}
