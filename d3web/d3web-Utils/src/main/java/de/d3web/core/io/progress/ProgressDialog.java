/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author volker_belli
 * @created 27.04.2011
 */
public class ProgressDialog extends JFrame implements ProgressListener {

	private static final long serialVersionUID = 3380517180403399192L;
	private final JLabel statusLabel = new JLabel("initializing");
	private final JLabel tickLabel = new JLabel(TICKS[0]);
	private final JProgressBar progressBar = new JProgressBar(0, 100);

	private Runnable cancelAction = null;
	private final int width;
	private final int height;

	public ProgressDialog(String string) {
		this(string, -1, -1);
	}

	public ProgressDialog(String string, int width, int height) {
		super(string);
		this.width = width;
		this.height = height;
		init();
	}

	private void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			// do nothing special,
			// we can also survive with swing Look&Feel
			e.printStackTrace(System.err);
		}

		setMinimumSize(new Dimension(250, 50));
		tickLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
		tickLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

		JPanel contents = (JPanel) getContentPane();
		if (width != -1 && height != -1) {
			contents.setPreferredSize(new Dimension(width, height));
		}
		contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contents.add(statusLabel, BorderLayout.NORTH);
		contents.add(tickLabel, BorderLayout.EAST);
		contents.add(progressBar);

		this.setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent event) {
				doCancel();
			}
		});
	}

	@Override
	public void updateProgress(final float percent, final String message) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					updateProgress(percent, message);
				}
			});
		}
		else if (percent < 1f) {
			if (!isVisible()) {
				this.pack();
				this.setLocationRelativeTo(null);
				this.setVisible(true);
			}
			statusLabel.setText(message);
			progressBar.setValue((int) (percent * 100));
			nextTick();
		}
		else {
			this.dispose();
		}
	}

	private static final ImageIcon[] TICKS = new ImageIcon[] {
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_0.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_1.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_2.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_3.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_4.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_5.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_6.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_7.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_8.png")),
			new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("de/d3web/core/io/progress/spinner_9.png"))
	};

	private int tickIndex = 0;
	private long lastTick = 0;

	private void nextTick() {
		long now = System.currentTimeMillis();
		if (now > lastTick + 100) {
			lastTick = now;
			tickIndex = (tickIndex + 1) % TICKS.length;
			tickLabel.setIcon(TICKS[tickIndex]);
		}
	}

	public void setCancelAction(Runnable action) {
		this.cancelAction = action;
	}

	private void doCancel() {
		if (cancelAction != null) {
			cancelAction.run();
		}
	}

	public float getProgress() {
		return (float) progressBar.getPercentComplete();
	}
}
