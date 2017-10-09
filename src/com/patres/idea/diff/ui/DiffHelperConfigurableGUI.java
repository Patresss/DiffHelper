package com.patres.idea.diff.ui;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.patres.idea.diff.DiffHelperConfig;
import com.patres.idea.diff.utils.DiffNameUtils;

import javax.swing.*;
import java.awt.*;


public class DiffHelperConfigurableGUI {


    private JPanel rootPanel;
    private JCheckBox taskNameFromChangelistCheckBox;
    private JCheckBox nickNameFromGitCheckBox;
    private JTextField nickNameField;
    private JLabel nickNameLabel;
    private DiffHelperConfig diffHelperConfig;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    public DiffHelperConfigurableGUI() {
        // $$$setupUI$$$ will be executed here (inserted automatically)
    }

    public void createUI(Project project) {
        diffHelperConfig = DiffHelperConfig.getInstance(project);
        nickNameFromGitCheckBox.addItemListener(e -> {
            nickNameField.setEnabled(!nickNameFromGitCheckBox.isSelected());
            nickNameLabel.setEnabled(!nickNameFromGitCheckBox.isSelected());
            if(nickNameFromGitCheckBox.isSelected()) {
                nickNameField.setText(DiffNameUtils.getGitUsername());
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public boolean isModified() {
        boolean modified = false;
        modified |= !nickNameField.getText().equals(diffHelperConfig.getNickNameFromGit());
        modified |= !taskNameFromChangelistCheckBox.isSelected() == diffHelperConfig.isTaskNameFromChangelistCheck();
        modified |= !nickNameFromGitCheckBox.isSelected() == diffHelperConfig.isNickNameFromGitCheck();
        return modified;
    }

    public void apply() {
        diffHelperConfig.setNickNameFromGitCheck(nickNameFromGitCheckBox.isSelected());
        diffHelperConfig.setTaskNameFromChangelistCheck(taskNameFromChangelistCheckBox.isSelected());
        if (nickNameFromGitCheckBox.isSelected()) {
            diffHelperConfig.setNickNameFromGit(DiffNameUtils.getGitUsername());
        } else {
            diffHelperConfig.setNickNameFromGit(nickNameField.getText());
        }
    }

    public void reset() {
        nickNameFromGitCheckBox.setSelected(diffHelperConfig.isNickNameFromGitCheck());
        taskNameFromChangelistCheckBox.setSelected(diffHelperConfig.isTaskNameFromChangelistCheck());
        nickNameField.setText(diffHelperConfig.getNickNameFromGit());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.setRequestFocusEnabled(true);
        final Spacer spacer1 = new Spacer();
        rootPanel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();


    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}