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

package de.d3web.empiricalTesting.caseVisualization.jung;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.empiricalTesting.ConfigLoader;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.TestSuite;
import de.d3web.empiricalTesting.caseVisualization.BotHelper;
import de.d3web.empiricalTesting.caseVisualization.CaseVisualizer;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;

/**
 * This class visualizes the generated graph and
 * offers the ability to save the graph to a 
 * PDF file.
 * 
 * @author Sebastian Furth
 *
 */
public class JUNGCaseVisualizer implements CaseVisualizer {
	
	/**
	 * The graph which is visualized.
	 */
	private CaseTree<RatedTestCase, EdgeFinding> graph;
	
	/**
	 * The VisualizationViewer which actually is the
	 * visualized graph.
	 */
	private VisualizationViewer<RatedTestCase, EdgeFinding> vv;
	
	private static JUNGCaseVisualizer instance = new JUNGCaseVisualizer();
	
	
	/**
	 * Private Constructor that ensures noninstantiability.
	 */
	private JUNGCaseVisualizer() {}

	
	/**
	 * Initializes the graph.
	 * @param cases List<SequentialTestCase> cases which
	 *              will be visualized.
	 */
	private void init(List<SequentialTestCase> cases) {
		this.graph = CaseTreeFactory.getInstance().generateGraph(cases);
		setupVisualizationViewer();
	}
	
	
	/**
	 * Returns an instance of JUNGCaseVisualizer.
	 * @return JUNGCaseVisualizer.
	 */
	public static JUNGCaseVisualizer getInstance() {
		return instance;
	}
	
	
	/**
	 * Shows the specified testsuite visualized
	 * in a simple JFrame
	 * @param testsuite TestSuite which will be
	 *                  visualized.
	 */
	public void showInJFrame(TestSuite testsuite) {
		showInJFrame(testsuite.getRepository());
	}
	
	
	/**
	 * Shows the specified List<SequentialTestCase>
	 * visualized in a simple JFrame
	 * @param cases List<SequentialTestCase> which will
	 * 				be visualized.
	 */
	public void showInJFrame(List<SequentialTestCase> cases) {
		
		init(cases);
		
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        frame.pack();
        frame.setVisible(true);	
	}
	
	
	/**
	 * Saves the graph visualization to a <b>PDF file</b> which
	 * will be created at the committed filepath. Before the
	 * graph is saved to the file there is a check if you 
	 * want to partition the tree.
	 * 
	 * @param testsuite TestSuite which's cases will be 
	 * 					visualized by this class. 
	 * @param filepath String which specifies where the 
	 *                 created <b>PDF file</b> will be stored.
	 */
	@Override
	public void writeToFile(TestSuite t, String filepath) {
	
		String partitionTree = ConfigLoader.getInstance().getProperty("partitionTree");
		
		if (partitionTree.equals("true")) {
		
			QuestionChoice firstQuestion = (QuestionChoice) t.getRepository().get(0).
								getCases().get(0).getFindings().get(0).getQuestion();
			List<AnswerChoice> firstAnswers = firstQuestion.getAllAlternatives();
			
			for (Answer answerOfFirstQuestion : firstAnswers) {
				
				TestSuite partitioned = 
					t.getPartiallyAnsweredSuite((AnswerChoice) answerOfFirstQuestion);
				
				if(partitioned.getRepository().size() > 0) {
					writeToFile(partitioned.getRepository(), 
							checkFilePath(filepath, answerOfFirstQuestion.toString()));
				}
			}
			
		} else {
			
			writeToFile(t.getRepository(), checkFilePath(filepath, ""));
		}
		
		
	}
	
	
	/**
	 * Saves the graph visualization to a <b>PDF file</b> which
	 * will be created at the committed filepath.
	 * 
	 * @param cases List<SequentialTestCase> which's elements
	 *              will be visualized by this class. 
	 * @param filepath String which specifies where the 
	 *                 created <b>PDF file</b> will be stored.
	 */
	@Override
	public void writeToFile(List<SequentialTestCase> cases, String filepath) {
		
		init(cases);
		checkFilePath(filepath, "");
		
		int w = vv.getGraphLayout().getSize().width;
        int h = vv.getGraphLayout().getSize().height;
		Document document = new Document();
		
		try {
			
			PdfWriter writer = 
				PdfWriter.getInstance(document, new FileOutputStream(filepath));
			document.setPageSize(new Rectangle(w, h));
			document.open();	

            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(w, h);
            Graphics2D g2 = tp.createGraphics(w, h);         
            paintGraph(g2);
            
            g2.dispose();
            tp.sanityCheck();
            cb.addTemplate(tp, 0, 0);
            cb.sanityCheck();
            
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass().getName())
			.warning("Can not access the specified file (" + filepath +
					 "), probably because it is opened. " +
					 "Close the file and try again."); 
			
		} catch (DocumentException e) {
			Logger.getLogger(this.getClass().getName())
			.warning("Error while writing to file (" + filepath + 
					"). The file was not created. " + e.getMessage());
		} 
		
		document.close();
	}

	
	/**
	 * Actually writes the Graph as a Graph2D
	 * Object to the PDF file.
	 * @param g2 Graph2D Object to which
	 *  		 the graph will be written.
	 */
	private void paintGraph(Graphics2D g2) {

		vv.setSize(vv.getGraphLayout().getSize());      
		Container c = new Container();
		c.add(vv);
        c.addNotify();
        c.validate();
        c.paintComponents(g2);
	}


	/**
	 * Creates and customizes a VisualizationViewer Object 
	 * which is necessary for displaying the graph / tree.
	 */
	private void setupVisualizationViewer() {
        
        vv = generateVisualizationViewer();
        
        VertexLabelAsShapeRenderer<RatedTestCase, EdgeFinding> vlasr = 
        	new VertexLabelAsShapeRenderer<RatedTestCase, EdgeFinding>(vv.getRenderContext());
        
        // Vertex transformation
        vv.getRenderContext().setVertexLabelTransformer(new VertexTransformer(graph));
        vv.getRenderContext().setVertexShapeTransformer(vlasr);
        vv.getRenderContext().setVertexFillPaintTransformer(new VertexColorTransformer());
        
        
        // Edge transformation 
        vv.getRenderContext().setEdgeLabelTransformer(new EdgeTransformer(graph));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new EdgeColorTransformer(graph));
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<RatedTestCase, EdgeFinding>());
        vv.getRenderContext().setArrowFillPaintTransformer(new EdgeColorTransformer(graph));
        vv.getRenderContext().setArrowDrawPaintTransformer(new EdgeColorTransformer(graph));
        vv.getRenderContext().setEdgeStrokeTransformer(new EdgeStrokeTransformer(graph));
        vv.getRenderContext().setEdgeArrowStrokeTransformer(new EdgeStrokeTransformer(graph));
        vv.getRenderContext().getEdgeLabelRenderer().setRotateEdgeLabels(false);
        
        // customize the renderer
        vv.getRenderer().setVertexLabelRenderer(vlasr);
        vv.setBackground(Color.WHITE);
            
        final DefaultModalGraphMouse<String,Number> graphMouse = 
            new DefaultModalGraphMouse<String,Number>();

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
	}


	/**
	 * Actually generates the VizualizationViewer Object.
	 * 
	 * @return VizualizationViewer Object which needs further
	 *         customization.
	 */
	private VisualizationViewer<RatedTestCase, EdgeFinding> generateVisualizationViewer() {

		int distY = DistanceDeterminer.getInstance().determineDistance(graph.getVertices());
		
		TreeLayout<RatedTestCase, EdgeFinding> treeLayout = 
			new TreeLayout<RatedTestCase, EdgeFinding>(graph, 250, distY);
		
        Dimension preferredSize = new Dimension(1400, 800);
        
        final VisualizationModel<RatedTestCase, EdgeFinding> visualizationModel = 
            new DefaultVisualizationModel<RatedTestCase, EdgeFinding>(treeLayout, 
            		                                              preferredSize);
		
        return new VisualizationViewer<RatedTestCase, EdgeFinding>(visualizationModel, 
        		                                               preferredSize);
	} 
	
	
	/**
	 * Checks the if everything is ok with the output path
	 * specified for the PDF File. If there is something
	 * wrong it will be corrected.
	 * 
	 * @param filepath String the specified filepath
	 * @param addOn String extra information which should be
	 *                     in the filename
	 * @return String correct filepath
	 */
	private String checkFilePath(String filepath, String addOn){

		addOn = BotHelper.getInstance().removeBadChars(addOn);
		
		if (filepath.equals("")) {
			return "pdfFile" + addOn + ".pdf";
		} else if (filepath.endsWith("/")) {
			return filepath + addOn + ".pdf";
		} else if(filepath.endsWith(".pdf")) {
			if(!addOn.equals("")) {
				return filepath.substring(0, filepath.length() - 4) + "_" + addOn + ".pdf";
			}
		} 
		
		return filepath;
	
	}
			
}
