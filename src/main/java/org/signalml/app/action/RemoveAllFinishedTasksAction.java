/* RemoveAllFinishedTasksAction.java created 2008-02-07
 *
 */
package org.signalml.app.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.task.Task;
import org.signalml.task.TaskStatus;
import org.springframework.context.support.MessageSourceAccessor;

/** RemoveAllFinishedTasksAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RemoveAllFinishedTasksAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RemoveAllFinishedTasksAction.class);

	private ApplicationTaskManager taskManager;
	private Component optionPaneParent;

	public RemoveAllFinishedTasksAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.removeAllFinishedTasks");
		setIconPath("org/signalml/app/icon/removeallfinishedtasks.png");
		setToolTip("action.removeAllFinishedTasksToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		int ans = OptionPane.showRemoveAllFinishedTasks(optionPaneParent);
		if (ans != OptionPane.OK_OPTION) {
			return;
		}

		synchronized (taskManager) {

			int count = taskManager.getTaskCount();
			Task task;
			TaskStatus status;

			for (int i=0; i<count; i++) {
				task = taskManager.getTaskAt(i);
				synchronized (task) {
					status = task.getStatus();
					if (status.isFinished()) {
						taskManager.removeTask(task);
						i--;
						count--;
					}
				}
			}
		}

	}

	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

}
