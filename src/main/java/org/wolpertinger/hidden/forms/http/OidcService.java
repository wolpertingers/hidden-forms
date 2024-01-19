package org.wolpertinger.hidden.forms.http;

import com.vaadin.quarkus.annotation.VaadinSessionScoped;
import io.quarkus.oidc.UserInfo;
import jakarta.inject.Inject;

@VaadinSessionScoped
public class OidcService {

    @Inject
    UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
