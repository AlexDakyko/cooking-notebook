package com.cookingnotebook.repository;

import java.util.Map;
import java.util.Optional;

public interface SettingsRepository {
    Optional<String> get(String key);
    void set(String key, String value);
    Map<String, String> getAll();
}
