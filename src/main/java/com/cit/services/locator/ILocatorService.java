package com.cit.services.locator;

import com.cit.models.Location;

public interface ILocatorService {
    Location getLocationFromPanelId(String panelId);
}
