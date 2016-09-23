package plugin.wrapper;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.gui.ApplyPatchPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.intellij.testFramework.LightPlatformTestCase.getProject;

/**
 * Created by bdanglot on 9/21/16.
 */
public class ApplyPatchWrapper extends DialogWrapper {

	private final JComponent panel;

	private final Project project;

	private Patch selectedPatch;
	private PsiStatement patchStatement;
	private PsiExpression buggyExpression;

	public ApplyPatchWrapper(Project project, List<Patch> patches) {
		super(true);
		this.project = project;
		this.panel = new ApplyPatchPanel(this, patches);
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return this.panel;
	}


	@NotNull
	@Override
	protected Action[] createActions() {
		Action[] actions = new Action[2];
		actions[0] = new ApplyPatchAction();
		actions[1] = this.getCancelAction();
		return actions;
	}

	@Override
	protected void doOKAction() {
		//TODO Apply effectively changes
		super.doOKAction();
	}

	@Nullable
	@Override
	protected ValidationInfo doValidate() {
		//TODO Apply effectively changes
		return super.doValidate();
	}

	@Override
	public void doCancelAction() {
		//TODO CancelAllChanges
		super.doCancelAction();
	}

	public void setSelectedPatch(Patch selectedPatch) {
		System.out.println(selectedPatch.asString() + " selected! ");
		this.selectedPatch = selectedPatch;
		this.patchStatement = JavaPsiFacade.getElementFactory(this.project).createStatementFromText(this.selectedPatch.asString(), null);
		final SourceLocation location = this.selectedPatch.getSourceLocation();
		PsiClass classToBeFix = JavaPsiFacade.getInstance(this.project).findClass(this.selectedPatch.getRootClassName(), new EverythingGlobalScope(this.project));
		classToBeFix.accept(new JavaRecursiveElementVisitor() {
			@Override
			public void visitIfStatement(PsiIfStatement statement) {
				//TODO check the computation of position
				if (location.getBeginSource() == statement.getTextOffset() &&
						location.getEndSource() == statement.getTextOffset() + statement.getTextLength() - 1) {
					buggyExpression = statement.getCondition();
					System.out.println("This conditional " + statement.getCondition().getText() + " should replaced by " + selectedPatch.asString());
				}
				super.visitIfStatement(statement);
			}
		});
	}

	private class ApplyPatchAction extends AbstractAction {

		ApplyPatchAction() {
			super("ApplyPatch");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CommandProcessor.getInstance().executeCommand(getProject(), () -> ApplicationManager.getApplication().runWriteAction( () -> {
				PsiClass classToBeFix = JavaPsiFacade.getInstance(project).findClass(selectedPatch.getRootClassName(), new EverythingGlobalScope(project));
				OpenFileDescriptor descriptor = new OpenFileDescriptor(project, classToBeFix.getContainingFile().getVirtualFile(), buggyExpression.getTextOffset());
				Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
				Document modifiedDocument = editor.getDocument();
				//Apply the patch
				modifiedDocument.replaceString(buggyExpression.getTextOffset(), buggyExpression.getTextOffset() + buggyExpression.getTextLength(), patchStatement.getText());
				//Move caret to modification
				editor.getCaretModel().moveToOffset(buggyExpression.getTextOffset());
				//Select patch
				editor.getSelectionModel().setSelection(buggyExpression.getTextOffset(), buggyExpression.getTextOffset() + buggyExpression.getTextLength());
				close(0);
			}), "ApplyPatch", CommandProcessor.getInstance().getCurrentCommandGroupId());
		}
	}
}
