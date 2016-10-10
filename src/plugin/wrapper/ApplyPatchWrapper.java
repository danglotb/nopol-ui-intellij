package plugin.wrapper;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.DocCommandGroupId;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.EverythingGlobalScope;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.gui.ApplyPatchPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by bdanglot on 9/21/16.
 */
public class ApplyPatchWrapper extends DialogWrapper {

	private final JComponent panel;

	private final Project project;

	private Patch selectedPatch;
	private PsiStatement patchStatement;
	private PsiElement buggyElement;

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
			public void visitStatement(PsiStatement statement) {
				if (location.getBeginSource() == statement.getTextOffset() &&
						location.getEndSource() == statement.getTextOffset() + statement.getTextLength() - 1) {
					buggyElement = statement;
				}
				super.visitStatement(statement);
			}
		});
	}

	private class ApplyPatchAction extends AbstractAction {

		ApplyPatchAction() {
			super("ApplyPatch");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			PsiClass classToBeFix = JavaPsiFacade.getInstance(project).findClass(selectedPatch.getRootClassName(), new EverythingGlobalScope(project));
			OpenFileDescriptor descriptor = new OpenFileDescriptor(project, classToBeFix.getContainingFile().getVirtualFile(), buggyElement.getTextOffset());
			Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
			Document modifiedDocument = editor.getDocument();

			final String patch;

			if (selectedPatch.getType() == StatementType.CONDITIONAL) {
				buggyElement = ((PsiIfStatement) buggyElement).getCondition();
				patch = patchStatement.getText();
			} else {
				String newline = FileDocumentManager.getInstance().getFile(modifiedDocument).getDetectedLineSeparator();
				StringBuilder sb = new StringBuilder();
				sb.append("if( ");
				sb.append(patchStatement.getText());
				sb.append(" ) {" + newline);
				sb.append(buggyElement.getText() + newline);
				sb.append("}");
				patch = sb.toString();
			}

			CommandProcessor.getInstance().executeCommand(project, () -> WriteCommandAction.runWriteCommandAction(project, () -> {
				//Apply the patch
				modifiedDocument.replaceString(buggyElement.getTextOffset(), buggyElement.getTextOffset() + buggyElement.getTextLength(), patch);
				//Move caret to modification
				editor.getCaretModel().moveToOffset(buggyElement.getTextOffset());
				//Select patch
				editor.getSelectionModel().setSelection(buggyElement.getTextOffset(), buggyElement.getTextOffset() +
						(selectedPatch.getType() == StatementType.CONDITIONAL ? buggyElement.getTextLength() : patch.length()));
				PsiDocumentManager.getInstance(project).commitDocument(modifiedDocument);
				CodeStyleManager.getInstance(project).reformat(PsiDocumentManager.getInstance(project).getPsiFile(modifiedDocument), false);
			}), "Apply Patch", DocCommandGroupId.noneGroupId(modifiedDocument));

			PsiDocumentManager.getInstance(project).commitDocument(modifiedDocument);

			close(0);
		}
	}


}