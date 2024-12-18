package net.codersky.skyutils.time;

/**
 * Interface used to represent a platform independent task
 * that can be cancelled with the {@link #cancel()} method.
 * Implementations of this interface are generally just a
 * wrapper of a platform dependant class such as BukkitTask.
 *
 * @since SkyUtils 1.0.0
 */
public interface Task {

	/**
	 * Cancels this task, meaning that the task won't be executed.
	 *
	 * @since SkyUtils 1.0.0
	 */
	void cancel();
}
