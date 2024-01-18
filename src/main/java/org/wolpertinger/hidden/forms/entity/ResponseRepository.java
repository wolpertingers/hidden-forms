package org.wolpertinger.hidden.forms.entity;

import com.vaadin.quarkus.annotation.VaadinSessionScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.transaction.Transactional;

@VaadinSessionScoped
public class ResponseRepository implements PanacheRepository<Response> {


    private Response findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }

    public Response findOrCreate(String userId) {
        Response response = findByUserId(userId);
        if (response == null) {
            response = new Response(userId);
            response.userId = userId;
        }
        return response;
    }

    @Transactional
    public void update(Response response) {
        persistAndFlush(response);
    }
}
