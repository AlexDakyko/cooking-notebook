package com.cookingnotebook.service;

import com.cookingnotebook.model.ApplicationSettings;
import com.cookingnotebook.repository.SettingsRepository;

public class SettingsService {
    private final SettingsRepository repository;

    public SettingsService(SettingsRepository repository) {
        this.repository = repository;
    }

    public ApplicationSettings loadSettings() {
        ApplicationSettings settings = new ApplicationSettings();
        repository.get("theme")
                .ifPresent(settings::setTheme);
        repository.get("language")
                .ifPresent(settings::setLanguage);
        repository.get("windowSize")
                .ifPresent(settings::setWindowSize);
        return settings;
    }

    public void saveSettings(ApplicationSettings settings) {
        repository.set("theme", settings.getTheme());
        repository.set("language", settings.getLanguage());
        repository.set("windowSize", settings.getWindowSize());
    }
}
