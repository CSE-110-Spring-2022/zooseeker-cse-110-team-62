package com.example.zooseeker_t62;

import static org.junit.Assert.assertEquals;

import android.widget.ToggleButton;

import org.junit.Test;


public class SettingsTest {
    @Test
    public void testState() {
        assertEquals(SettingsPage.routeType, SettingsPage.getRouteType());
        SettingsPage.setRouteType(false);
        boolean expected = false;
        assertEquals(expected, SettingsPage.getRouteType());
    }
}
