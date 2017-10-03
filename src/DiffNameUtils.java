import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiffNameUtils {

    private final static String DEFAULT_USER = "Unknown-user";
    private final static String FORMAT_DATE = "yyyyMMddHHmm";

    private DiffNameUtils() {
    }

    public static String getFileName(Project project) {
        return String.format("%s_%s_%s.sql", getFormattedDataTime(), getGitUsername(), getTicket(project));
    }

    private static String getFormattedDataTime() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE);
        return date.format(formatter);
    }

    private static String getGitUsername() {
        try {
            Process process = Runtime.getRuntime().exec("git config user.name");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            return DEFAULT_USER;
        }
    }

    private static String getTicket(Project project) {
        return ChangeListManager.getInstance(project).getDefaultListName();
    }
}
