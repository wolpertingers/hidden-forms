package org.wolpertinger.hidden.forms.ui;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.vaadin.flow.component.radiobutton.dataview.RadioButtonGroupListDataView;

/**
 * Hack for conflicting setter error of Jackson in {@link com.vaadin.flow.component.radiobutton.RadioButtonGroup#setItems}.
 */
public class RadioButtonGroup<T> extends com.vaadin.flow.component.radiobutton.RadioButtonGroup<T> {

    @SafeVarargs
    @JsonSetter
    @Override
    public final RadioButtonGroupListDataView<T> setItems(T... items) {
        return super.setItems(items);
    }
}
