package org.wolpertinger.hidden.forms.entity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResponseRepository implements PanacheRepository<Response> {


    private Response findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }

    public Response findOrCreate(String userId) {
        Response response = findByUserId(userId);
        if (response == null) {
            response = new Response();
            response.userId = userId;
        }
        return response;
    }

    public void createOrUpdate(Response response) {
        Response existing = findByUserId(response.userId);
        if (existing == null) {
            existing = response;
        }
        persist(existing);
    }
}
