package de.d3web.kernel.verbalizer.test;

import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.domainModel.answers.AnswerYes;
import de.d3web.kernel.verbalizer.VerbalizationManager;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

public class ManualTests {
	public static void main(String[] args) {

		AnswerYes yes = new AnswerYes();
		AnswerNo no = new AnswerNo();
		
		System.out.println(VerbalizationManager.getInstance().verbalize(yes, RenderingFormat.HTML));
		System.out.println(VerbalizationManager.getInstance().verbalize(no, RenderingFormat.HTML));
	}
}
