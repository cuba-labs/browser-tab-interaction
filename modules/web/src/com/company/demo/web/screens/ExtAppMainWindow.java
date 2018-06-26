package com.company.demo.web.screens;

import com.haulmont.cuba.core.global.UuidSource;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.cuba.web.gui.components.mainwindow.WebNewWindowButton;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ExtAppMainWindow extends AppMainWindow {
    @Inject
    private Button openDashboardBtn;
    @Inject
    private UuidSource uuidSource;
    @Inject
    private TextField dashboardTextField;

    private String lastOpenedDashboardId = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setupTabOpener();

        dashboardTextField.addValueChangeListener(this::updateDashboardText);
    }

    private void updateDashboardText(ValueChangeEvent e) {
        if (lastOpenedDashboardId != null) {
            Dashboard dashboard = Dashboard.getFromSession(VaadinSession.getCurrent(), lastOpenedDashboardId);

            if (dashboard != null) {
                Component vDashboard = dashboard.unwrap(Component.class);
                UI ui = vDashboard.getUI();

                if (!ui.isClosing()) {
                    ui.access(() -> {
                        // here we can update state
                        dashboard.updateText((String) e.getValue());
                    });
                }
            } else {
                showNotification("Dashboard is not loaded yet");
            }
        } else {
            showNotification("Open dashboard first");
        }
    }

    private void setupTabOpener() {
        String tabId = uuidSource.createUuid().toString();

        URL pageUrl;
        try {
            String locationWithoutParams = ControllerUtils.getLocationWithoutParams();
            pageUrl = new URL(locationWithoutParams + String.format("open?screen=%s&params=tabId:%s", "dashboard", tabId));

        } catch (MalformedURLException ignored) {
            LoggerFactory.getLogger(WebNewWindowButton.class).warn("Couldn't get URL of current Page");
            return;
        }

        ExternalResource currentPage = new ExternalResource(pageUrl);
        BrowserWindowOpener opener = new BrowserWindowOpener(currentPage);
        opener.setWindowName("_blank");

        opener.extend(openDashboardBtn.unwrap(AbstractComponent.class));

        this.lastOpenedDashboardId = tabId;
    }
}