package plg.visualizer.prototype;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.model.Process;
import plg.visualizer.BPMNVisualizer;

public class PlgVisualizerPrototype {

	public static void main(String[] args) {
		Process p = new Process("test");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		
		JFrame f = new JFrame("Test Frame");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setLayout(new BorderLayout());
		f.add(new BPMNVisualizer(p), BorderLayout.CENTER);
		f.setVisible(true);
	}
}