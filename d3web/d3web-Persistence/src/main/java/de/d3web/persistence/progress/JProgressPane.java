/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * Created on 22.07.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.persistence.progress;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * ProgressPane. Zeigt Fortschritt an. Event mit START öffnet Dialogbox, ENDE schließt sie, UPDATE, na ratet mal.
 * @author mweniger
 */
public class JProgressPane extends JDialog implements ProgressListener {

	ProgRunner runner;

	class ProgRunner implements Runnable {
		ProgressEvent evt;

		public ProgRunner(ProgressEvent evt) {
			this.evt = evt;
		}
		public void run() {

			setVisible(true);

		}
	}

	JProgressBar progBar;
	JLabel text;

	public JProgressPane(Frame owner, String title) throws HeadlessException {
		this(owner, title, true);
	}

	public JProgressPane(Frame owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	public JProgressPane(Dialog owner, String title) throws HeadlessException {
		this(owner, title, true);
	}

	public JProgressPane(Dialog owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	/*
	 * winzig kleine vin-diesel-kobolde haben mein leben übernommen. so hat das alles keinen#
	 * sinn mehr. ich werde nun von euch gehen. lebt alle wohl.
	 */
	private void initialize() {
		progBar = new JProgressBar();
		text = new JLabel(" ");

		runner = new ProgRunner(null);

		progBar.setPreferredSize(new Dimension(300, 15));

		JPanel p = new JPanel(new BorderLayout());
		p.add(progBar, BorderLayout.CENTER);
		p.setBorder(new EmptyBorder(20, 20, 20, 20));

		text.setBorder(new EmptyBorder(0, 20, 20, 20));

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setResizable(false);
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(p, BorderLayout.CENTER);

		getContentPane().add(text, BorderLayout.SOUTH);

		pack();

		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2);

	}

	/* (non-Javadoc)
	 * @see de.d3web.utilities.swing.jprogresspane.ProgressListener#updateProgress(de.d3web.utilities.swing.jprogresspane.ProgressEvent)
	 */
	public void updateProgress(ProgressEvent evt) {

		//new ProgRunner(evt)

		//System.out.println("Progress: " + evt.getTaskDescription() + ": " + evt.getCurrentValue() + " of " + evt.getFinishedValue());
		
		text.setText(evt.getTaskDescription());

		if (evt.getType() == ProgressEvent.START) {
			runner.evt = evt;
			SwingUtilities.invokeLater(runner);
		}

		if (evt.getType() == ProgressEvent.DONE) {
			setVisible(false);
		}

		progBar.setMaximum((int) evt.getFinishedValue());
		progBar.setValue((int) evt.getCurrentValue());

	}

	public static void main(String[] args) {
		JProgressPane p = new JProgressPane(new JFrame(), "Titel");
		p.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		p.progBar.setMaximum(5);
		p.progBar.setValue(3);
		p.setVisible(true);
	}

}
