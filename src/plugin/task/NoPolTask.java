package plugin.task;

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

	//TODO implements Success and Cancel method


	@Override
	public void onError(@NotNull Exception error) {
		super.onError(error);
	}

	@Override
	public void onSuccess() {
		super.onSuccess();
		if (this.response instanceof List) {
			Messages.showMessageDialog(getProject(), this.response.toString(), "Success", Messages.getInformationIcon());
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
