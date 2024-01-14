package org.wolpertinger.hidden.forms.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.dom.impl.ThemeListImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ThemeListDeserializer extends StdDeserializer<ThemeList> {

    public ThemeListDeserializer() {
        this(null);
    }

    protected ThemeListDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ThemeList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        throw new UnsupportedOperationException("Oh no! ThemeListDeserializer was called!!");
    }
}
