/* TaskStatus.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** An enumeration of possible task statuses.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TaskStatus implements Serializable, MessageSourceResolvable {

	/** The task is new and was never submitted for execution.
	 */
	NEW(100),

	/** The task is currently being executed.
	 */
	ACTIVE(100),

	/** The task is waiting to be executed.
	 */
	ACTIVE_WAITING(90),

	/** The task is executing, but has been signalled to suspend.
	 */
	REQUESTING_SUSPEND(100),

	/** The task is suspended.
	 */
	SUSPENDED(50),

	/** The task is executing, but has been signalled to abort.
	 */
	REQUESTING_ABORT(100),

	/** The task has been aborted.
	 */
	ABORTED(0),

	/** The task encountered an error.
	 */
	ERROR(10),

	/** The task has successfully finished with a result.
	 */
	FINISHED(20);

	private int importance;

	private TaskStatus() {
	}

	private TaskStatus(int importance) {
		this.importance = importance;
	}

	/** Returns the <i>importance</i> of the status. This is used for sorting of tasks.
	 *
	 *
	 */
	public int getImportance() {
		return importance;
	}

	/** Returns true if the status is NEW.
	 */
	public boolean isNew() {
		return (this == NEW);
	}

	/** Returns true if the status is ACTIVE.
	 */
	public boolean isActiveAndRunning() {
		return (this == ACTIVE);
	}

	/** Returns true if the status is ACTIVE_WAITING.
	 */
	public boolean isActiveAndWaiting() {
		return (this == ACTIVE_WAITING);
	}

	/** Returns true if the status is ACTIVE or ACTIVE_WAITING.
	 */
	public boolean isActive() {
		return (this == ACTIVE || this == ACTIVE_WAITING);
	}

	/** Returns true if the status is ACTIVE, ACTIVE_WAITING, REQUESTING_ABORT or REQUESTING_SUSPEND.
	 */
	public boolean isRunning() {
		return (this == ACTIVE || this == ACTIVE_WAITING || this == REQUESTING_ABORT || this == REQUESTING_SUSPEND);
	}

	/** Returns true if the status is SUSPENDED.
	 */
	public boolean isSuspended() {
		return (this == SUSPENDED);
	}

	/** Returns true if the status is REQUESTING_ABORT.
	 */
	public boolean isRequestingAbort() {
		return (this == REQUESTING_ABORT);
	}

	/** Returns true if the status is REQUESTING_SUSPEND.
	 */
	public boolean isRequestingSuspend() {
		return (this == REQUESTING_SUSPEND);
	}

	/** Returns true if the status is ABORTED.
	 */
	public boolean isAborted() {
		return (this == ABORTED);
	}

	/** Returns true if the status is ERROR.
	 */
	public boolean isError() {
		return (this == ERROR);
	}

	/** Returns true if the status is FINISHED.
	 */
	public boolean isFinished() {
		return (this == FINISHED);
	}

	/** Returns true if the status is FINISHED or ERROR.
	 */
	public boolean isResultAvailable() {
		return (this == FINISHED || this == ERROR);
	}

	/** Returns true if the status is NEW.
	 */
	public boolean isStartable() {
		return (this == NEW);
	}

	/** Check whether the task can be requested to abort. This is
	 * possible if the task is or will soon be active or suspended.
	 * @return true if the task can be aborted (is ACTIVE,
	 * ACTIVE_WAITING, REQUESTING_SUSPEND or SUSPENDED)
	 * @see org.signalml.task.LocalTask#abort
	 */
	public boolean isAbortable() {
		return (this == ACTIVE || this == ACTIVE_WAITING || this == REQUESTING_SUSPEND || this == SUSPENDED);
	}

	/** Returns true if the status is SUSPENDED.
	 */
	public boolean isResumable() {
		return (this == SUSPENDED);
	}

	/** Returns true if the status is ACTIVE, ACTIVE_WAITING.
	 */
	public boolean isSuspendable() {
		return (this == ACTIVE || this == ACTIVE_WAITING);
	}

	/** Returns empty array of Objects
	 */
	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	/** Returns array of Strings with only one element: "taskStatus.TYPE", where TYPE is current task status
	 */
	@Override
	public String[] getCodes() {
		return new String[] { "taskStatus." + toString() };
	}

	/** Returns name of the current status
	 */
	@Override
	public String getDefaultMessage() {
		return toString();
	}

}