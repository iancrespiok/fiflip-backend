package com.fiflip.backend.admin.application;

import java.util.Optional;

public interface AdminAuthUseCases {

    Optional<String> login(String password);

    boolean isTokenValid(String token);
}
