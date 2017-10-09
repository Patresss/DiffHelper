package com.patres.idea.diff.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.patres.idea.diff.DiffHelperBundle;

import java.awt.datatransfer.StringSelection;

public class CopyDiffPathAction extends AnAction {

    private final static String DIFF_FOLDER = "DIFFS/";

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        isValidateFile(virtualFile);
        String copiedPath = copyToClipboard(virtualFile);
        setStatusBarText(e.getProject(), DiffHelperBundle.message("action.copiedPath", copiedPath));
    }

    private void setStatusBarText(Project project, String message) {
        if (project != null) {
            final StatusBarEx statusBar = (StatusBarEx) WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.setInfo(message);
            }
        }
    }

    private String copyToClipboard(VirtualFile virtualFile) {
        String canonicalPath = virtualFile.getCanonicalPath();
        String path = canonicalPath.substring(canonicalPath.lastIndexOf(DIFF_FOLDER) + DIFF_FOLDER.length());
        CopyPasteManager.getInstance().setContents(new StringSelection(path));
        return path;
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(isValidateFile(virtualFile));
    }

    private boolean isValidateFile(VirtualFile virtualFile) {
        return virtualFile != null && virtualFile.getCanonicalPath() != null && virtualFile.getCanonicalPath().contains(DIFF_FOLDER);
    }


}
