package plugin.action;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiUtilBase;
import fr.inria.lille.repair.common.config.Config;
import plugin.task.NoPolTask;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static plugin.Plugin.config;

/**
 * Created by bdanglot on 9/21/16.
 */
public class NoPolAction extends AbstractAction {

	private final DialogWrapper parent;
	private final AnActionEvent event;

	public NoPolAction(DialogWrapper parent, AnActionEvent event) {
		super("Fix Me");
		this.event = event;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Project project = event.getData(PlatformDataKeys.PROJECT);
		Editor editor = event.getData(PlatformDataKeys.EDITOR);
		PsiFile currentFile = PsiUtilBase.getPsiFileInEditor(editor, project);
		if (JavaFileType.INSTANCE != currentFile.getFileType())
			return;
		VirtualFile file = PsiUtilBase.getPsiFileInEditor(editor, project).getVirtualFile();
		String fullQualifiedNameOfCurrentFile = ((PsiJavaFile) currentFile).getPackageName() + "." + currentFile.getName();
		fullQualifiedNameOfCurrentFile = fullQualifiedNameOfCurrentFile.substring(0, fullQualifiedNameOfCurrentFile.length() - JavaFileType.DEFAULT_EXTENSION.length() - 1);
		if (ProjectRootManager.getInstance(project).getFileIndex().isInSource(file)) {
			config.setProjectTests(fullQualifiedNameOfCurrentFile.split(" "));
		}

		Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);

		String srcFolder = "";
		VirtualFile[] rootsFolder = ModuleRootManager.getInstance(module).getSourceRoots();
		for (int i = 0; i < rootsFolder.length - 1; i++) {
			srcFolder += rootsFolder[i].getCanonicalPath() + ":";
		}
		srcFolder += rootsFolder[rootsFolder.length - 1].getCanonicalPath();
		config.setProjectSourcePath(srcFolder.split(":"));

		String projectClasspath = "";//CompilerModuleExtension.getInstance(module).getCompilerOutputPath().getCanonicalPath() + ":" + CompilerModuleExtension.getInstance(module).getCompilerOutputPathForTests().getCanonicalPath();
		VirtualFile[] roots = ModuleRootManager.getInstance(module).orderEntries().classes().getRoots();
		for (int i = 0; i < roots.length; i++) {
			String pathFile = roots[i].getCanonicalPath();
			if (!pathFile.contains("jdk"))
				projectClasspath += (pathFile.endsWith("jar!/") ? pathFile.substring(0, pathFile.length() - 2) : pathFile) + ":";
		}

		//TODO
		projectClasspath += "/home/bdanglot/Documents/jdk1.7.0_80/bin/../lib/tools.jar";

		config.setProjectClasspath(projectClasspath);

		//TODO fix this for remoting NoPol
		if (config.getSynthesis() == Config.NopolSynthesis.SMT) {
			config.setSolver(Config.NopolSolver.Z3);
		}

		ProgressManager.getInstance().run(new NoPolTask(project, "NoPol is Fixing"));

		this.parent.close(0);
	}
}
