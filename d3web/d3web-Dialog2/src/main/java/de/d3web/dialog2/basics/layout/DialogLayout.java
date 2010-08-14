/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.dialog2.basics.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DialogLayout {

	public static String LAYOUTFILE_STRING = "dialoglayout.xml";

	private List<QContainerLayout> qContainerList;

	private QuestionPageLayout questionPageLayout;

	public static Logger logger = Logger.getLogger(DialogLayout.class);

	public QContainerLayout getQContainerLayoutDefinitionForID(String id) {
		for (QContainerLayout qCont : qContainerList) {
			if (qCont.getQContID().equals(id)) {
				return qCont;
			}
		}
		return null;
	}

	public List<QContainerLayout> getQContainerList() {
		return qContainerList;
	}

	public QuestionPageLayout getQuestionPageLayout() {
		return questionPageLayout;
	}

	public boolean hasDefinitonsForQContainerID(String id) {
		if (qContainerList.size() == 0) {
			return false;
		}
		for (QContainerLayout qCont : qContainerList) {
			if (qCont.getQContID().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public void init(String kbid) {
		qContainerList = new ArrayList<QContainerLayout>();
		questionPageLayout = new QuestionPageLayout();
		new DialogLayoutLoader(this).init(kbid);
	}

	public void setQContainerList(List<QContainerLayout> containerList) {
		qContainerList = containerList;
	}

	public void setQuestionPageLayout(QuestionPageLayout questionPageLayout) {
		this.questionPageLayout = questionPageLayout;
	}

}
