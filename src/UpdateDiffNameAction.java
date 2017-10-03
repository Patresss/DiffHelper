import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class UpdateDiffNameAction extends AnAction {

    private final static Pattern PATTERN = Pattern.compile("^[0-9]{12}_.*_.*\\.sql$");

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        if (isValidateFile(virtualFile)) {
            try {
                renameFile(project, virtualFile);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        } else {
            Messages.showMessageDialog(project, "File does not matches to pattern " + PATTERN.pattern(), "File Exists", Messages.getWarningIcon());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(isValidateFile(virtualFile));
    }

    private boolean isValidateFile(VirtualFile virtualFile) {
        return virtualFile != null && !virtualFile.isDirectory() && PATTERN.matcher(virtualFile.getName()).matches();
    }

    private void renameFile(Project project, VirtualFile virtualFile) throws IOException {
        String newName = DiffNameUtils.getFileName(project);
        if(!virtualFile.getName().equals(newName)) {
            if(virtualFile.getParent() != null && virtualFile.getParent().getCanonicalPath() != null) {
                Path newPath = Paths.get(virtualFile.getParent().getCanonicalPath(), newName);
                if(newPath.toFile().exists()) {
                    Messages.showMessageDialog(project, "File with name " +  newName + " exists in folder", "File Exists", Messages.getWarningIcon());
                } else {
                    runRenameThread(virtualFile, newName);
                }
            } else {
                throw new RuntimeException("Error with file");
            }
        }
    }

    private void runRenameThread(VirtualFile virtualFile, String newName) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                virtualFile.rename(null, newName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
