package SemiSupervised.CoTraining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Debug;
import weka.core.Instances;

/**
 * 
 * @author Rania Ibrahim
 * 
 */
public class CoTraining {

	ArrayList<Sample> samplesSide1;
	HashSet<String> featuresSide1;
	ArrayList<Sample> samplesSide2;
	HashSet<String> featuresSide2;
	HashSet<Integer> classLabels;

	public Sample getSampleByName(String sampleName, int side) {
		ArrayList<Sample> samples = (side == 1) ? samplesSide1 : samplesSide2;
		for (Sample sample : samples) {
			if (sample.getName().equalsIgnoreCase(sampleName))
				return sample;
		}
		return new Sample();
	}

	public void loadLabels(String filename, int side) {
		try {
			classLabels = new HashSet<Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] splits = line.split("\t");
				Sample sample = getSampleByName(splits[0], side);
				sample.setType(Integer.parseInt(splits[1]));
				classLabels.add(Integer.parseInt(splits[1]));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Sample> loadData(String filename, int side,
			boolean addFeatures) {
		try {
			HashSet<String> features = (side == 1) ? featuresSide1
					: featuresSide2;
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine(); // header
			ArrayList<Sample> samples = new ArrayList<Sample>();
			String[] splits = line.split("\t");
			for (int i = 1; i < splits.length; i++) {
				Sample sample = new Sample();
				sample.setName(splits[i]);
				samples.add(sample);
			}
			while ((line = reader.readLine()) != null) {
				splits = line.split("\t");
				if (!features.contains(splits[0])) {
					if (splits[0].startsWith("\"")) {
						splits[0] = splits[0].substring(1);
						splits[0] = splits[0].substring(0,
								splits[0].length() - 1);
					}
					if (addFeatures)
						features.add(splits[0]);
				}
				for (int i = 1; i < splits.length; i++) {
					Sample sample = samples.get(i - 1);
					sample.getGenesExperssion().put(splits[0],
							Double.parseDouble(splits[i]));
				}
			}
			reader.close();
			return samples;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void generateArff(String filename, ArrayList<Sample> samples,
			int side) {
		try {
			HashSet<String> features = (side == 1) ? featuresSide1
					: featuresSide2;
			PrintWriter out = new PrintWriter(new File(filename));
			out.println("@RELATION\tSelfLearning");
			out.println();
			for (String feature : features) {
				out.println("@ATTRIBUTE\t\"" + feature + "\"\tNUMERIC");
			}
			out.print("@ATTRIBUTE\tclass\t{");
			int count = 1;
			for (Integer label : classLabels) {
				if (count == classLabels.size()) {
					out.println(label + "}");
				} else {
					out.print(label + ",");
				}
				count++;
			}
			out.println("@DATA");
			for (Sample sample : samples) {
				for (String feature : features) {
					if (sample.getGenesExperssion().containsKey(feature))
						out.print(sample.getGenesExperssion().get(feature)
								+ ",");
					else
						// missing value
						out.print("0.0,");
				}
				out.println(sample.getType());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Classifier buildClassifier(String trainData, String testData,
			PrintWriter resultWriter, String type, int side) {
		try {
			// train
			BufferedReader reader = new BufferedReader(
					new FileReader(trainData));
			Instances trainInstances = new Instances(reader);
			reader.close();
			trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
			Classifier classifier = null;
			if (type.equalsIgnoreCase("randomforest")) {
				classifier = new RandomForest();
				String[] options = new String[2];
				options[0] = "-I";
				options[1] = "10";
				((RandomForest) classifier).setOptions(options);
			} else if (type.equalsIgnoreCase("svm")) {
				classifier = new SMO();
				((SMO) classifier).setBuildLogisticModels(true);
			}
			classifier.buildClassifier(trainInstances);
			Evaluation eval = new Evaluation(trainInstances);
			eval.crossValidateModel(classifier, trainInstances, 10, new Random(
					1));
			resultWriter.println("Side = " + side);
			resultWriter.println(eval.toSummaryString(
					"\nCross Validation 10-fold Results\n======\n", false));
			resultWriter.println("Confusion Matrix:");
			double[][] confusionMatrix = eval.confusionMatrix();
			for (int i = 0; i < confusionMatrix.length; i++) {
				for (int j = 0; j < confusionMatrix[i].length; j++) {
					resultWriter.print(confusionMatrix[i][j] + " ");
				}
				resultWriter.println();
			}
			resultWriter.println("Percision = "
					+ (eval.weightedPrecision() * 100.0) + "%");
			resultWriter.println("Recall = " + (eval.weightedRecall() * 100.0)
					+ "%");
			resultWriter
					.println("F measure = "
							+ ((2 * eval.weightedPrecision() * eval
									.weightedRecall())
									/ (eval.weightedPrecision() + eval
											.weightedRecall()) * 100.0) + "%");

			// test
			reader = new BufferedReader(new FileReader(testData));
			Instances testInstances = new Instances(reader);
			reader.close();
			testInstances.setClassIndex(testInstances.numAttributes() - 1);
			eval = new Evaluation(testInstances);
			eval.evaluateModel(classifier, testInstances);
			resultWriter.println(eval.toSummaryString(
					"\nTest Results\n======\n", false));
			resultWriter.println("Confusion Matrix:");
			confusionMatrix = eval.confusionMatrix();
			for (int i = 0; i < confusionMatrix.length; i++) {
				for (int j = 0; j < confusionMatrix[i].length; j++) {
					resultWriter.print(confusionMatrix[i][j] + " ");
				}
				resultWriter.println();
			}

			resultWriter.println("Percision = "
					+ (eval.weightedPrecision() * 100.0) + "%");
			resultWriter.println("Recall = " + (eval.weightedRecall() * 100.0)
					+ "%");
			resultWriter
					.println("F measure = "
							+ ((2 * eval.weightedPrecision() * eval
									.weightedRecall())
									/ (eval.weightedPrecision() + eval
											.weightedRecall()) * 100.0) + "%");
			resultWriter.flush();
			return classifier;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// side1 side2
	public HashMap<String, ArrayList<String>> getMapping(String mappingFile,
			int side) throws IOException {
		HashMap<String, ArrayList<String>> mapping = new HashMap<String, ArrayList<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(mappingFile));
		String line = null;
		HashSet<String> features = (side == 1) ? featuresSide1 : featuresSide2;
		HashSet<String> othersideFeatures = (side == 1) ? featuresSide2
				: featuresSide1;

		while ((line = reader.readLine()) != null) {
			String[] splits = line.split("\t");
			if (features.contains(splits[side - 1])
					&& othersideFeatures.contains(splits[side % 2])) {
				ArrayList<String> otherside = new ArrayList<String>();
				if (mapping.containsKey(splits[side - 1])) {
					otherside = mapping.get(splits[side - 1]);
				}
				otherside.add(splits[side % 2]);
				mapping.put(splits[side - 1], otherside);
			}
		}
		reader.close();
		return mapping;
	}

	public Sample convertSample(Sample sample,
			HashMap<String, ArrayList<String>> mapping) {
		Sample newSample = new Sample();
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		HashMap<String, Double> newExpressions = new HashMap<String, Double>();
		for (String feature : sample.getGenesExperssion().keySet()) {
			if (mapping.containsKey(feature)) {
				ArrayList<String> newFeatures = mapping.get(feature);
				for (String newFeature : newFeatures) {
					double value = 0;
					int count = 0;
					if (newExpressions.containsKey(newFeature)) {
						count = counter.get(newFeature);
						value = newExpressions.get(newFeature) * count;
					}
					newExpressions.put(newFeature, (value + sample
							.getGenesExperssion().get(feature)) / (count + 1));
					counter.put(newFeature, count + 1);
				}
			}
		}
		newSample.setGenesExperssion(newExpressions);
		newSample.setName(sample.getName());
		newSample.setType(sample.getType());
		return newSample;
	}

	public void generateUnlabeledArff(String inputFilename,
			String outputFilename, Classifier classifier,
			double classificationConfidentAlpha, int side,
			HashMap<String, ArrayList<String>> mapping) {
		try {
			ArrayList<Sample> unlabeledSamples = loadData(inputFilename, side,
					false);
			generateArff(outputFilename, unlabeledSamples, side);
			// classify
			ArrayList<Sample> samples = (side == 1) ? samplesSide2
					: samplesSide1;
			BufferedReader reader = new BufferedReader(new FileReader(
					outputFilename));
			Instances testInstances = new Instances(reader);
			reader.close();
			testInstances.setClassIndex(testInstances.numAttributes() - 1);
			for (int i = 0; i < testInstances.size(); i++) {
				double predictionIndex = classifier
						.classifyInstance(testInstances.instance(i));
				String predictedClassLabel = testInstances.classAttribute()
						.value((int) predictionIndex);
				double[] predictionDistribution = classifier
						.distributionForInstance(testInstances.instance(i));
				if (predictionDistribution[(int) predictionIndex] >= classificationConfidentAlpha) {
					unlabeledSamples.get(i).setType(
							Integer.parseInt(predictedClassLabel));
					// add to the opposite side + do mapping
					Sample newSample = convertSample(unlabeledSamples.get(i),
							mapping);
					samples.add(newSample);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String[][] getTableData(String filename) throws IOException {
		String[][] result = new String[8][4];
		result[0][0] = "Side 1 Initial 10-fold Cross Validation";
		result[1][0] = "Side 1 Initial Test";
		result[2][0] = "Side 2 Initial 10-fold Cross Validation";
		result[3][0] = "Side 2 Initial Test";
		result[4][0] = "Side 2 CoTraining 10-fold Cross Validation";
		result[5][0] = "Side 2 CoTraining Test";
		result[6][0] = "Side 1 CoTraining 10-fold Cross Validation";
		result[7][0] = "Side 1 CoTraining Test";

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
		int row = 0;
		int column = 1;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("Percision = ") || line.startsWith("Recall = ")
					|| line.startsWith("F measure = ")) {
				String[] splits = line.split("=");
				String modifiedNum = "";
				boolean start = false;
				int count = 0;
				for (int i = 0; i < splits[1].length(); i++) {
					if (start)
						count++;
					if (splits[1].charAt(i) == '.') {
						start = true;
					}
					modifiedNum += splits[1].charAt(i);
					if (count == 2) {
						modifiedNum += "%";
						break;
					}
				}
				result[row][column++] = modifiedNum.trim();
				if (column == 4) {
					row++;
					column = 1;
				}
			}
		}
		reader.close();
		// swap row 5 with row 7
		for (int i = 0; i < result[i].length; i++) {
			String temp = result[5][i];
			result[5][i] = result[7][i];
			result[7][i] = temp;
		}
		// swap row 4 with row 6
		for (int i = 0; i < result[i].length; i++) {
			String temp = result[4][i];
			result[4][i] = result[6][i];
			result[6][i] = temp;
		}
		return result;
	}

	public String[][] run(String trainFileSide1, String testFileSide1,
			String labelSide1, String unlabeledFileSide1,
			String trainFileSide2, String testFileSide2, String labelSide2,
			String unlabeledFileSide2, String mappingFile, String resultFile,
			String ClassifierModelFileSide1, String ClassifierModelFileSide2,
			double classificationConfidentAlpha, String type)
			throws IOException {
		PrintWriter resultWriter = new PrintWriter(new File(resultFile));
		// side 1
		featuresSide1 = new HashSet<String>();
		samplesSide1 = loadData(testFileSide1, 1, true);
		loadLabels(labelSide1, 1);
		generateArff("test-side1.arff", samplesSide1, 1);
		samplesSide1 = loadData(trainFileSide1, 1, true);
		loadLabels(labelSide1, 1);
		generateArff("train-side1.arff", samplesSide1, 1);
		Classifier classifierSide1 = buildClassifier("train-side1.arff",
				"test-side1.arff", resultWriter, type, 1);

		// side 2
		featuresSide2 = new HashSet<String>();
		samplesSide2 = loadData(testFileSide2, 2, true);
		loadLabels(labelSide2, 2);
		generateArff("test-side2.arff", samplesSide2, 2);
		samplesSide2 = loadData(trainFileSide2, 2, true);
		loadLabels(labelSide2, 2);
		generateArff("train-side2.arff", samplesSide2, 2);
		Classifier classifierSide2 = buildClassifier("train-side2.arff",
				"test-side2.arff", resultWriter, type, 2);

		// cotraining side 1
		HashMap<String, ArrayList<String>> mappingside1 = getMapping(
				mappingFile, 1);
		generateUnlabeledArff(unlabeledFileSide1, "unlabeled-side1.arff",
				classifierSide1, classificationConfidentAlpha, 1, mappingside1);
		// test new side 2
		generateArff("train+unlabeled-side2.arff", samplesSide2, 2);
		samplesSide2 = new ArrayList<Sample>();
		samplesSide2 = loadData(testFileSide2, 2, true);
		loadLabels(labelSide2, 2);
		generateArff("test-unlabeled-side2.arff", samplesSide2, 2);
		Classifier side2AfterCoTraining = buildClassifier(
				"train+unlabeled-side2.arff", "test-unlabeled-side2.arff",
				resultWriter, type, 2);
		mappingside1 = new HashMap<String, ArrayList<String>>();

		// cotraining side 2
		featuresSide2 = new HashSet<String>();
		samplesSide2 = loadData(trainFileSide2, 2, true);
		loadLabels(labelSide2, 2);
		HashMap<String, ArrayList<String>> mappingside2 = getMapping(
				mappingFile, 2);
		generateUnlabeledArff(unlabeledFileSide2, "unlabeled-side2.arff",
				classifierSide2, classificationConfidentAlpha, 2, mappingside2);
		// test new side 1
		generateArff("train+unlabeled-side1.arff", samplesSide1, 1);
		samplesSide1 = new ArrayList<Sample>();
		samplesSide1 = loadData(testFileSide1, 1, true);
		loadLabels(labelSide1, 1);
		generateArff("test-unlabeled-side1.arff", samplesSide1, 1);
		Classifier side1AfterCoTraining = buildClassifier(
				"train+unlabeled-side1.arff", "test-unlabeled-side1.arff",
				resultWriter, type, 1);

		resultWriter.close();
		// save classifiers model
		Debug.saveToFile(ClassifierModelFileSide1, side1AfterCoTraining);
		Debug.saveToFile(ClassifierModelFileSide2, side2AfterCoTraining);

		return getTableData(resultFile);
	}

	public static void main(String[] args) {

		if (args.length < 14) {
			System.out
					.println("CoTraining [trainFile-Side1] [testFile-Side1] [labelFile-Side1] [unlabeledFile-Side1] [trainFile-Side2] [testFile-Side2] [labelFile-Side2] [unlabeledFile-Side2] [MappingFile] [Alpha] [ClassifierType(randomforest,svm)] [resultFile] [ClassifierModelFileSide1] [ClassifierModelFileSide2]");
			return;
		}
		try {
			String trainFileSide1 = args[0];
			String testFileSide1 = args[1];
			String labelFileSide1 = args[2];
			String unlabeledFileSide1 = args[3];
			String trainFileSide2 = args[4];
			String testFileSide2 = args[5];
			String labelFileSide2 = args[6];
			String unlabeledFileSide2 = args[7];
			String mappingFile = args[8];
			double alpha = Double.parseDouble(args[9]);
			String classifierType = args[10];
			String resultFile = args[11];
			String classifierModelSide1 = args[12];
			String classifierModelSide2 = args[13];
			CoTraining learner = new CoTraining();
			learner.run(trainFileSide1, testFileSide1, labelFileSide1,
					unlabeledFileSide1, trainFileSide2, testFileSide2,
					labelFileSide2, unlabeledFileSide2, mappingFile,
					resultFile, classifierModelSide1, classifierModelSide2,
					alpha, classifierType);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}

	}
}
