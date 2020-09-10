package plg.generator.process;

import plg.utils.Random;

public class EvolutionConfiguration extends RandomizationConfiguration {

	private double activityEvolutionProbability = 0.1;
	
	public EvolutionConfiguration(double activityEvolutionProbability, RandomizationConfiguration random) {
		super(random.getAndBranches(), random.getXorBranches(),
				random.getLoopWeight(), random.getSingleActivityWeight(),
				random.getSkipWeight(), random.getSequenceWeight(),
				random.getANDWeight(), random.getXORWeight(),
				random.getMaximumDepth(), random.getDataObjectProbability());
		setActivityEvolutionProbability(activityEvolutionProbability);
	}
	
	public double getActivityEvolutionProbability() {
		return activityEvolutionProbability;
	}
	
	public void setActivityEvolutionProbability(double activityEvolutionProbability) {
		this.activityEvolutionProbability = activityEvolutionProbability;
	}
	
	/**
	 * This method is used for the definition of the presence of a loop
	 * 
	 * @return true if a loop must be inserted, false otherwise
	 */
	public boolean getEvolutionPresence() {
		return Random.randomFromWeight(getActivityEvolutionProbability());
	}
}
