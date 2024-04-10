package com.mcfev.fdn;

public class AppInfo {
    private String appName;
    private boolean toDelete;

    public AppInfo(String appName, boolean toDelete) {
        this.appName = appName;
        this.toDelete = toDelete;
    }

    public String getAppName() {
        return appName;
    }

    public boolean isToDelete() {
        return toDelete;
    }
}

