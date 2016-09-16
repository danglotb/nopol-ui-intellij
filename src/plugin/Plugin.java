package plugin;

import actors.ActorManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiUtilBase;
import fr.inria.lille.repair.common.config.Config;


/**
 * Created by bdanglot on 9/6/16.
 */
public class Plugin extends AnAction {

    private String pathToSolverSMT = "/home/bdanglot/workspace/nopol/nopol/lib/z3/z3_for_linux";

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    public Plugin() {
        super("NoPol: Fix me!");
        try {
            ActorManager.createActorSystem(getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile currentFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (JavaFileType.INSTANCE != currentFile.getFileType())
            return;

        VirtualFile file = PsiUtilBase.getPsiFileInEditor(editor, project).getVirtualFile();
        String fullQualifiedNameOfCurrentFile = ((PsiJavaFile) currentFile).getPackageName() + "." + currentFile.getName();
        fullQualifiedNameOfCurrentFile = fullQualifiedNameOfCurrentFile.substring(0, fullQualifiedNameOfCurrentFile.length() - JavaFileType.DEFAULT_EXTENSION.length() - 1);
        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);

        String srcFolder = ModuleRootManager.getInstance(module).getSourceRoots()[0].getCanonicalPath();
        String pathToOutput = "";//CompilerModuleExtension.getInstance(module).getCompilerOutputPath().getCanonicalPath() + ":" + CompilerModuleExtension.getInstance(module).getCompilerOutputPathForTests().getCanonicalPath();

        VirtualFile[] roots = ModuleRootManager.getInstance(module).orderEntries().classes().getRoots();
        for (int i = 0 ; i < roots.length ; i++) {
            String pathFile = roots[i].getCanonicalPath();
            if (!pathFile.contains("jdk"))
                pathToOutput += (pathFile.endsWith("jar!/") ? pathFile.substring(0, pathFile.length()-2) : pathFile) + ":";
        }
        pathToOutput += "/home/bdanglot/Documents/jdk1.7.0_80/bin/../lib/tools.jar";

        String cmd = buildConfig();
        cmd += "--source " + srcFolder + " ";
        cmd += "--classpath " + pathToOutput + " ";
        cmd += ProjectRootManager.getInstance(project).getFileIndex().isInSource(file) ? "--test " + fullQualifiedNameOfCurrentFile + " " : "";
        cmd += "--solver-path " + pathToSolverSMT;

        System.out.println(cmd);

        ActorManager.remoteActor.tell(cmd, ActorManager.actor);
    }

    private String buildConfig() {
        String config = "--synthesis " + String.valueOf(Config.INSTANCE.getSynthesis()).toLowerCase() + " ";
        String type;
        switch (Config.INSTANCE.getType()) {
            case CONDITIONAL:
                type = "condition";
                break;
            default:
                type = String.valueOf(Config.INSTANCE.getType());
                break;
        }
        config += "--type " + type + " ";
        return config;
    }


}
