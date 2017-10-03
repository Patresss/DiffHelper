import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NewDiffAction extends AnAction {


    private final static String TEMPLATE_NAME = "template.sql";

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        virtualFile = getFile(virtualFile);
        copyTemplate(project, virtualFile);
        virtualFile.refresh(false, false);
    }

    private void copyTemplate(Project project, VirtualFile virtualFile) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("/" + TEMPLATE_NAME)) {
            validVirtualFile(virtualFile);
            Path targetPath = Paths.get(virtualFile.getCanonicalPath(), DiffNameUtils.getFileName(project));
            if (targetPath.toFile().exists()) {
                Messages.showMessageDialog(project, "File " + targetPath.toString() + " exists in folder", "File Exists", Messages.getWarningIcon());
            } else {
                Files.copy(is, targetPath);
            }
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Error", Messages.getErrorIcon());
        }
    }

    private void validVirtualFile(VirtualFile virtualFile) throws IllegalArgumentException {
        if (virtualFile == null || virtualFile.getCanonicalPath() == null) {
            throw new IllegalArgumentException("Cannot find directory path");
        }
    }

    private VirtualFile getFile(VirtualFile file) {
        if (file != null && !file.isDirectory()) {
            file = file.getParent();
        }
        return file;
    }


}
