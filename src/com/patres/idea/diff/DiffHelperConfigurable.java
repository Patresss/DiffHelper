package com.patres.idea.diff;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.patres.idea.diff.ui.DiffHelperConfigurableGUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * This ProjectConfigurable class appears on Settings dialog,
 * to let user to configure this plugin's behavior.
 */
public class DiffHelperConfigurable implements SearchableConfigurable {

    private DiffHelperConfigurableGUI mGUI;
    private final DiffHelperConfig config;

    @SuppressWarnings("FieldCanBeLocal")
    private final Project mProject;

    public DiffHelperConfigurable(@NotNull Project project) {
        mProject = project;
        config = DiffHelperConfig.getInstance(project);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Diff Helper Plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "preference.DiffHelperConfigurable";
    }

    @NotNull
    @Override
    public String getId() {
        return "preference.DiffHelperConfigurable";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mGUI = new DiffHelperConfigurableGUI();
        mGUI.createUI(mProject);
        return mGUI.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return mGUI.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        mGUI.apply();
    }

    @Override
    public void reset() {
        mGUI.reset();
    }

    @Override
    public void disposeUIResources() {
        mGUI = null;
    }
}