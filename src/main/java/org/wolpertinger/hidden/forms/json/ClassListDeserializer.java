package org.wolpertinger.hidden.forms.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vaadin.flow.dom.ClassList;
import com.vaadin.flow.dom.impl.ImmutableClassList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ClassListDeserializer extends StdDeserializer<ClassList> {

    public ClassListDeserializer() {
        this(null);
    }

    protected ClassListDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ClassList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        throw new UnsupportedOperationException("Oh no! ClassListDeserializer was called!!");
    }
}
