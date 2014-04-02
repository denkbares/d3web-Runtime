/*
 * Copyright (C) 2014 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.core.io.progress;

/**
 * Use this ProgressListener to fake the progress of an operation, for which you can't
 * get the exact progress.<b></b>
 * <p/>
 * Created by Albrecht Striffler (denkbares GmbH) on 31.03.14.
 */
public class FakeProgressListener implements ExtendedProgressListener {

	public static final float NORMAL_PROGRESS_MAX = 0.8f;
	private static final float MAX = 0.99f;
	public static final int WAIT_TIME = 10;
	public static final int SECOND = 1000;
	private final ExtendedProgressListener delegate;
	private final FakeProgressThread thread;

	/**
	 * <b>Attention: After instantiating this FakeProgressListener, you have to make sure that the original/delegate
	 * listener is no longer used directly! Only set the progress of the delegate through this FakeProgressListener.</b>
	 *
	 * @param runTimeInSeconds the time after which the listener should reach 80%
	 */
	public FakeProgressListener(ExtendedProgressListener delegate, float runTimeInSeconds) {
				this.delegate = delegate;
				this.thread = new FakeProgressThread(delegate, runTimeInSeconds);
	}

	/**
	 * Use this method to fake the progress of this listener while an operation is in process, for which you can't
	 * get the exact progress.<b></b>
	 * <b>Attention: After calling this method, you have to make sure that the original/delegate listener is no longer
	 * used directly! Only set the progress of the delegate through this FakeProgressListener.</b>
	 */
	public void fakeProgress() {
		if (thread.isAlive() || thread.isInterrupted()) return;
		thread.start();
	}

	@Override
	public float getProgress() {
		return delegate.getProgress();
	}

	@Override
	public String getMessage() {
		return delegate.getMessage();
	}

	@Override
	public void updateProgress(float percent, String message) {
		thread.interrupt();
		while (thread.isAlive()) {
			// wait until thread is down
		}
		delegate.updateProgress(percent, message);
	}

	private static class FakeProgressThread extends Thread {

		private final ExtendedProgressListener listener;
		private final float runTimeInSeconds;

		FakeProgressThread(ExtendedProgressListener listener, float runTimeInSeconds) {
			this.listener = listener;
			this.runTimeInSeconds = runTimeInSeconds;
		}

		@Override
		public void run() {
			float progress = listener.getProgress();
			float delta = NORMAL_PROGRESS_MAX / (runTimeInSeconds * (SECOND / WAIT_TIME)) ;
			String message = listener.getMessage();
			while (progress < MAX) {
				if (isInterrupted()) return;
				if (progress < NORMAL_PROGRESS_MAX) {
					listener.updateProgress(progress + delta, message);
				} else {
					listener.updateProgress(progress + (1f - progress) * delta, message);
				}
				progress = listener.getProgress();
				try {
					sleep(WAIT_TIME);
				}
				catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		FakeProgressListener fakeProgressListener = new FakeProgressListener(new ConsoleProgressListener(), 1);
		fakeProgressListener.fakeProgress();
		Thread.sleep(1000);
		fakeProgressListener.updateProgress(1f, "hu");
	}
}
