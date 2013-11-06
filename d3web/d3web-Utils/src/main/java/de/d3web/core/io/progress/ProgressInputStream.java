package de.d3web.core.io.progress;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends FilterInputStream {

	private final ProgressListener listener;
	private final String message;
	private long processed = 0;

	public ProgressInputStream(InputStream in, ProgressListener listener, String message) {
		super(in);
		this.listener = listener;
		this.message = message;
	}

	private void updateProgess(long count) throws IOException {
		processed += count;
		int available = available();
		float progress = processed / (float) (available + processed);
		listener.updateProgress(progress, message);
	}

	@Override
	public int read() throws IOException {
		int data = super.read();
		updateProgess(1);
		return data;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int count = super.read(b, off, len);
		updateProgess(count);
		return count;
	}

	@Override
	public long skip(long n) throws IOException {
		long count = super.skip(n);
		updateProgess(count);
		return count;
	}

	@Override
	public void close() throws IOException {
		super.close();
		listener.updateProgress(1f, message);
	}
}
