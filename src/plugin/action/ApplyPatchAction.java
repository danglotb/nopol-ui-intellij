package plugin.action;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.SourceLocation;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by bdanglot on 9/21/16.
 */
@Deprecated
public class ApplyPatchAction extends AbstractAction {

	//TODO the old and the new statement should be computed before this Action
	//TODO retrieve the selected patch in the ApplyPatchPanel

	private final Patch patch;
	private final Project project;

	public ApplyPatchAction(Project project, Patch patchToBeApplied) {
		super("Apply Patch");
		this.project = project;
		this.patch = patchToBeApplied;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final SourceLocation location = this.patch.getSourceLocation();
		PsiStatement patchStatement = JavaPsiFacade.getElementFactory(this.project).createStatementFromText(this.patch.asString(), null);
		PsiClass classToBeFix = JavaPsiFacade.getInstance(this.project).findClass(this.patch.getRootClassName(), new EverythingGlobalScope(this.project));
		classToBeFix.accept(new JavaRecursiveElementVisitor() {
			@Override
			public void visitIfStatement(PsiIfStatement statement) {
				//TODO check the computation of position
				if (location.getBeginSource() == statement.getTextOffset() &&
						location.getEndSource() == statement.getTextOffset() + statement.getTextLength() - 1) {
					statement.getCondition().replace(patchStatement);
					System.out.println("This conditional " + statement.getCondition() + " should replaced by " + patch.asString());
				}
				super.visitIfStatement(statement);
			}
		});

	}
}
