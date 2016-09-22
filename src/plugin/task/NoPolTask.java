package plugin.task;

import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.NoFailingTestCaseException;
import fr.inria.lille.repair.nopol.NoSuspiciousStatementException;
import plugin.actors.ActorManager;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.wrapper.ApplyPatchWrapper;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.List;

import static plugin.Plugin.config;

/**
 * Created by bdanglot on 9/21/16.
 */
public class NoPolTask extends Task.Backgroundable {


	public NoPolTask(@Nullable Project project, @Nls(capitalization = Nls.Capitalization.Title) @NotNull String title) {
		super(project, title, false);
	}

	private Object response;
	private Future<Object> future;

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {
		Timeout timeout = new Timeout(200000);
		this.future = Patterns.ask(ActorManager.remoteActor, config, timeout);
		try {
			this.response = Await.result(future, timeout.duration());
		} catch (Exception e) {

		}
	}

	@Override
	public void onError(@NotNull Exception error) {
		Messages.showMessageDialog(getProject(), this.response != null ? this.response.toString() : "null", "Error", Messages.getErrorIcon());
	}

	@Override
	public void onSuccess() {
		super.onSuccess();
		//TODO maybe add super class
		if (this.response instanceof NoSuspiciousStatementException ) {
			Messages.showMessageDialog(getProject(), this.response.toString(), ((NoSuspiciousStatementException) this.response).header, Messages.getWarningIcon());
		} else if (this.response instanceof NoFailingTestCaseException) {
			Messages.showMessageDialog(getProject(), this.response.toString(), ((NoFailingTestCaseException) this.response).header, Messages.getWarningIcon());
		} else if (this.response instanceof List) {
			List<Patch> patches = (List<Patch>) this.response;
			if (patches.isEmpty())
				Messages.showMessageDialog(getProject(), "NoPol could not found any fix", "Fail", Messages.getWarningIcon());
			else {
				ApplyPatchWrapper dialog = new ApplyPatchWrapper(getProject(), patches);
				dialog.getPeer().setTitle("NoPol");
				dialog.show();
			}
		} else
			Messages.showMessageDialog(getProject(), this.response != null ? this.response.toString() : "null", "Error", Messages.getErrorIcon());
	}

	@Override
	public void onCancel() {
		super.onCancel();
		this.future.failed();
		Messages.showMessageDialog(getProject(), "The job has been cancelled", "Cancelled", Messages.getErrorIcon());
	}
}
