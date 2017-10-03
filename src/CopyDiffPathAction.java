import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class CopyDiffPathAction extends AnAction {

    private final static String DIFF_FOLDER = "DIFF/";

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        isValidateFile(virtualFile);
        copyToClipboard(virtualFile);
    }

    private void copyToClipboard(VirtualFile virtualFile) {
        String[] split = virtualFile.getCanonicalPath().split(DIFF_FOLDER);
        if(split.length >= 1) {
            String path = split[1];
            StringSelection stringSelection = new StringSelection(path);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        }
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
