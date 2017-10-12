package com.patres.idea.diff.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.patres.idea.diff.DiffHelperBundle;
import com.patres.idea.diff.DiffHelperConfig;
import com.patres.idea.diff.utils.DiffNameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class NewDiffAction extends AnAction {

    private final static String TEMPLATE_NAME = "template.sql";
    private final static String DIFF_TEMPLATE_NAME = "Diff";
    private final static String TEMPLATE_CONTENT = convertStreamToString(NewDiffAction.class.getResourceAsStream("/" + TEMPLATE_NAME));
    private FileTemplate diffTemplate;

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        try {
            updateFileTemplate(project);
            VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
            virtualFile = getFile(virtualFile);
            createDiff(project, virtualFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void updateFileTemplate(Project project) throws IOException {
        diffTemplate = FileTemplateManager.getInstance(project).getTemplate(DIFF_TEMPLATE_NAME);
        if (diffTemplate == null) {
            diffTemplate = FileTemplateManager.getInstance(project).addTemplate(DIFF_TEMPLATE_NAME, "sql");
            diffTemplate.setText(TEMPLATE_CONTENT);
        }
    }

    private void createDiff(Project project, VirtualFile virtualFile) {
        validVirtualFile(virtualFile);
        String taskName = getTaskName(project);
        if(taskName != null && !taskName.isEmpty()) {
            String fileName = DiffNameUtils.getFileName(project, taskName);
            Path targetPath = Paths.get(virtualFile.getCanonicalPath(), fileName);
            if (targetPath.toFile().exists()) {
                Messages.showMessageDialog(project, DiffHelperBundle.message("error.fileExistsPath", targetPath.toString()), DiffHelperBundle.message("error.fileExists"), Messages.getWarningIcon());
            } else {
                PsiDirectory directory = PsiManager.getInstance(project).findDirectory(virtualFile);
                CreateFileFromTemplateAction.createFileFromTemplate(fileName, diffTemplate, directory, "", true);
            }
        }
    }

    private String getTaskName(Project project) {
        String taskName;
        if(DiffHelperConfig.getInstance(project).isTaskNameFromChangelistCheck()) {
            taskName = DiffNameUtils.getTicketFromChangelist(project);
        } else {
            taskName = DiffNameUtils.getTicketFromInput(project);
        }
        return taskName;
    }

    private void validVirtualFile(VirtualFile virtualFile) throws IllegalArgumentException {
        if (virtualFile == null || virtualFile.getCanonicalPath() == null) {
            throw new IllegalArgumentException(DiffHelperBundle.message("error.directoryNotFound"));
        }
    }

    private VirtualFile getFile(VirtualFile file) {
        if (file != null && !file.isDirectory()) {
            file = file.getParent();
        }
        return file;
    }

}
