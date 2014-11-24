package plg.generator.process;

import java.util.Random;

import plg.exceptions.IllegalSequenceException;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.Event;
import plg.model.gateway.Gateway;

public class ProcessGenerator {

	private Process process;
	private String currentActivityName = "";
	private int currentDepth = 0;
	
	public static void randomizeProcess(Process process) {
		new ProcessGenerator(process).begin();
	}
	
	private ProcessGenerator(Process process) {
		this.process = process;
	}
	
	private void begin() {
		Event start = process.newStartEvent();
		Event end = process.newEndEvent();
		PatternFrame p = newInternalPattern();
		try {
			process.newSequence(start, p.getLeftBound());
			process.newSequence(p.getRightBound(), end);
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
	}
	
	private PatternFrame newInternalPattern() {
		if (currentDepth <= 4) {
			currentDepth++;
			Double d = new Random().nextDouble();
			if (d >= 0 && d <= 0.3) {
				return newActivity();
			} else if (d > 0.3 && d <= 0.6) {
				return newSequence();
			} else {
				return newAndBranch();
			}
		} else {
			return newActivity();
		}
	}
	
	private PatternFrame newActivity() {
		Task t = process.newTask("Activity " + askNewName());
		return new PatternFrame(t, t);
	}
	
	private PatternFrame newSequence() {
		PatternFrame p1 = newInternalPattern();
		PatternFrame p2 = newInternalPattern();
		try {
			process.newSequence(p1.getRightBound(), p2.getLeftBound());
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
		return new PatternFrame(p1.getLeftBound(), p2.getRightBound());
	}
	
	private PatternFrame newAndBranch() {
		Task beforeSplit = process.newTask("Activity " + askNewName());
		Gateway split = process.newParallelGateway();
		Gateway join = process.newParallelGateway();
		for(int i = 0; i < 3; i++) {
			PatternFrame p = newInternalPattern();
			try {
				process.newSequence(split, p.getLeftBound());
				process.newSequence(p.getRightBound(), join);
			} catch (IllegalSequenceException e) {
				e.printStackTrace();
			}
		}
		Task afterJoin = process.newTask("Activity " + askNewName());
		try {
			process.newSequence(beforeSplit, split);
			process.newSequence(join, afterJoin);
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
		return new PatternFrame(beforeSplit, afterJoin);
	}
	
//	private PatternFrame newXorBranch(Process process) {
//		return process;
//	}
//	
//	private PatternFrame newLoopBranch(Process process) {
//		return process;
//	}
	
	/**
	 * This method asks for a new activity name
	 * 
	 * @return
	 */
	private String askNewName() {
		String newActivityName = askNewName(currentActivityName);
		currentActivityName = newActivityName;
		return newActivityName;
	}
	
	/**
	 * This method asks for a new activity name
	 *
	 * @param currentActivityName
	 * @return
	 */
	private String askNewName(String currentActivityName) {
		if (currentActivityName.isEmpty()) {
			return "A";
		}
		char c = currentActivityName.charAt(currentActivityName.length() - 1);
		if (c != 'Z') {
			return currentActivityName.substring(0, currentActivityName.length() - 1) + String.valueOf((char) (c + 1));
		} else {
			return askNewName(currentActivityName.substring(0, currentActivityName.length() - 1)) + "A";
		}
	}
}
