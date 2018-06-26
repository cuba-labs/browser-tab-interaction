package com.company.demo.web.screens;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Label;
import com.vaadin.server.VaadinSession;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.Map;

public class Dashboard extends AbstractWindow {

    public static String DASHBOARD_SESSION_ATTRIBUTE_PREFIX = "dashboard_";

    @Inject
    private Label dashboardText;

    @WindowParam
    private String tabId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (tabId != null) {
            setToSession(VaadinSession.getCurrent(), this, tabId);
        }
    }

    public void updateText(String newText) {
        dashboardText.setValue(newText);

        showNotification("Dashboard text updated");
    }

    @Nullable
    public static Dashboard getFromSession(VaadinSession session, String tabId) {
        @SuppressWarnings("unchecked")
        WeakReference<Dashboard> reference =
                (WeakReference<Dashboard>) session.getAttribute(DASHBOARD_SESSION_ATTRIBUTE_PREFIX + tabId);
        if (reference == null || reference.get() == null) {
            return null;
        }
        return reference.get();
    }

    public static void setToSession(VaadinSession session, Dashboard dashboard, String tabId) {
        WeakReference<Dashboard> reference = new WeakReference<>(dashboard);

        session.setAttribute(
                DASHBOARD_SESSION_ATTRIBUTE_PREFIX + tabId,
                reference
        );
    }
}