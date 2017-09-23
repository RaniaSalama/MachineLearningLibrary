package SemiSupervised.SelfLearning;

import java.util.HashMap;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Sample {

	private String name;
	private HashMap<String, Double> genesExperssion;
	private int type;
	
	
	public Sample() {
		name = "";
		genesExperssion = new HashMap<String, Double>();
		type = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Double> getGenesExperssion() {
		return genesExperssion;
	}

	public void setGenesExperssion(HashMap<String, Double> genesExperssion) {
		this.genesExperssion = genesExperssion;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
