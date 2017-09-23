package SemiSupervised.SelfLearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
public class SelfLearner {

	ArrayList<Sample> samples;
	HashSet<String> features;
	HashSet<Integer> classLabels;

	public Sample getSampleByName(String sampleName) {
		for (Sample sample : samples) {
			if (sample.getName().equalsIgnoreCase(sampleName))
				return sample;
		}
		return new Sample();
	}

	public void loadLabels(String filename) {
		try {
			classLabels = new HashSet<Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] splits = line.split("\t");
				Sample sample = getSampleByName(splits[0]);
				sample.setType(Integer.parseInt(splits[1]));
				classLabels.add(Integer.parseInt(splits[1]));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Sample> loadData(String filename, boolean addFeature) {
		try {
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
					if (addFeature)
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

	public void generateArff(String filename, ArrayList<Sample> samples) {
		try {
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
						out.print("0.0,");
				}
				out.println(sample.getType());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateArffAppend(String inputFilename, String filename,
			ArrayList<Sample> samples) {
		try {
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
			BufferedReader reader = new BufferedReader(new FileReader(
					inputFilename));
			String line = null;
			boolean skip = true;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("@DATA")) {
					skip = false;
					continue;
				}
				if (skip)
					continue;
				out.println(line);
			}
			reader.close();

			for (Sample sample : samples) {
				for (String feature : features) {
					if (sample.getGenesExperssion().containsKey(feature))
						out.print(sample.getGenesExperssion().get(feature)
								+ ",");
					else
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
			PrintWriter resultWriter, String type) {
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

			return classifier;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void generateUnlabeledArff(String inputFilename,
			String outputFilename, Classifier classifier,
			double classificationConfidentAlpha) {
		try {
			ArrayList<Sample> unlabeledSamples = loadData(inputFilename, false);
			generateArff(outputFilename, unlabeledSamples);
			// classify
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
					samples.add(unlabeledSamples.get(i));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String[][] getTableData(String filename) throws IOException {
		String[][] result = new String[4][4];
		result[0][0] = "Initial 10-fold Cross Validation";
		result[1][0] = "Initial Test";
		result[2][0] = "SelfLearning 10-fold Cross Validation";
		result[3][0] = "SelfLearning Test";

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

		return result;
	}

	public String[][] run(String trainFile, String testFile, String labels,
			String unlabeledFile, String resultFile,
			String ClassifierModelFile, double classificationConfidentAlpha,
			String type) throws IOException {
		features = new HashSet<String>();
		PrintWriter resultWriter = new PrintWriter(new File(resultFile));
		samples = loadData(testFile, true);
		loadLabels(labels);
		generateArff("test.arff", samples);
		samples = loadData(trainFile, true);
		loadLabels(labels);
		generateArff("train.arff", samples);
		Classifier randomforest = buildClassifier("train.arff", "test.arff",
				resultWriter, type);
		generateUnlabeledArff(unlabeledFile, "unlabeled.arff", randomforest,
				classificationConfidentAlpha);
		generateArff("train+unlabeled.arff", samples);
		samples = new ArrayList<Sample>();
		samples = loadData(testFile, true);
		loadLabels(labels);
		generateArff("test-unlabeled.arff", samples);
		Classifier randomforestAfterSelfLearning = buildClassifier(
				"train+unlabeled.arff", "test-unlabeled.arff", resultWriter,
				type);
		resultWriter.close();
		// save classifier model
		Debug.saveToFile(ClassifierModelFile, randomforestAfterSelfLearning);
		return getTableData(resultFile);
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 8) {
			System.out
					.println("SelfLearning [trainFile] [testFile] [labelFile] [unlabeledFile] [Alpha] [ClassifierType(randomforest,svm)] [resultFile] [ClassifierModelFile]");
			return;
		}
		try {
			String trainFile = args[0];
			String testFile = args[1];
			String labelFile = args[2];
			String unlabeledFile = args[3];
			double alpha = Double.parseDouble(args[4]);
			String classifierType = args[5];
			String resultFile = args[6];
			String classifierModel = args[7];
			SelfLearner learner = new SelfLearner();
			learner.run(trainFile, testFile, labelFile, unlabeledFile,
					resultFile, classifierModel, alpha, classifierType);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}

	}
}
