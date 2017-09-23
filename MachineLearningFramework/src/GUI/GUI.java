package GUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import SemiSupervised.CoTraining.CoTraining;
import SemiSupervised.SelfLearning.SelfLearner;
import Unsupervised.IncrementalKmeans.IncrementalKmeans;
import Unsupervised.Kmeans.Kmeans;
import Unsupervised.MeanShift.MeanShift;
import Unsupervised.MeasurementPerformer.MeasurementPerformer;
import Unsupervised.SHC.SHC;

/**
 * 
 * @author Rania Ibrahim
 * 
 */
@SuppressWarnings("serial")
public class GUI extends JFrame {

	private JPanel panel;
	private String inputFile;
	private String centroidFile;
	private String resultFile;
	private int isDraw;
	private ArrayList<JComponent> prevComponents;
	private ArrayList<JComponent> kmeansComponents;
	private ArrayList<JComponent> dbscanComponents;
	private ArrayList<JComponent> mitosisComponents;
	private ArrayList<JComponent> meanshiftComponents;
	private ArrayList<JComponent> SHCComponents;
	private ArrayList<JComponent> selflearningComponents;
	private ArrayList<JComponent> cotrainingComponents;
	private ArrayList<JComponent> measureComponents;
	private JLabel inputFileLabel;
	private JButton inputFileButton;
	private JLabel centroidFileLabel;
	private JButton centroidFileButton;
	private JLabel resultFileLabel;
	private JButton resultFileButton;
	private JCheckBox isDrawBox;
	private JButton clusterButton;
	private JLabel welcomeLabel;
	private JFileChooser inputFileChooser = new JFileChooser();
	private JFileChooser centroidFileChooser = new JFileChooser();
	private JFileChooser resultFileChooser = new JFileChooser();
	private JLabel minPointLabel;
	private JTextField minPointTextFeild;
	private JLabel epslionLabel;
	private JTextField epslionField;
	private String clusterType;
	private JLabel simLabel;
	@SuppressWarnings("rawtypes")
	private JComboBox simField;
	private JCheckBox isOutlierHandlingBox;
	private int isOutlierHandling;
	private JLabel patternNoLabel;
	private JTextField patternNoField;
	private JLabel differenceThresholdLabel;
	private JTextField differenceThresholdField;
	private JLabel labelsFileLabel;
	private JButton labelsFileButton;
	private JLabel selflearningresultFileLabel;
	private JButton selflearningresultfILEFileButton;
	private JLabel classifierFileLabel;
	private JButton classifierFileButton;
	private JLabel classifierTypeLabel;
	@SuppressWarnings("rawtypes")
	private JComboBox classifierTypeField;
	private String labelFile;
	private String selflearningResultFile;
	private String classifierFile;
	private JLabel alphaLabel;
	private JTextField alphaField;
	private JButton runButton;
	private JLabel labelsFileLabelSide2;
	private JButton labelsFileButtonSide2;
	private JLabel mappingFileLabelSide2;
	private JButton mappingFileButtonSide2;
	private JLabel classifierFileLabelSide2;
	private JButton classifierFileButtonSide2;
	private String labelFileSide2;
	private String mappingFileSide2;
	private String classifierFileSide2;
	private JLabel trainFileLabelSide2;
	private JButton trainFileButtonSide2;
	private JLabel testFileLabelSide2;
	private JButton testFileButtonSide2;
	private JLabel unlabeledFileLabelSide2;
	private JButton unlabeledFileButtonSide2;
	private String trainFileSide2;
	private String testFileSide2;
	private String unlabeledFileSide2;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addMLTypeSubTypes() {
		prevComponents = new ArrayList<JComponent>();
		kmeansComponents = new ArrayList<JComponent>();
		dbscanComponents = new ArrayList<JComponent>();
		mitosisComponents = new ArrayList<JComponent>();
		meanshiftComponents = new ArrayList<JComponent>();
		SHCComponents = new ArrayList<JComponent>();
		selflearningComponents = new ArrayList<JComponent>();
		cotrainingComponents = new ArrayList<JComponent>();
		measureComponents = new ArrayList<JComponent>();
		String[] mlTypeChoices = { "Unsupervised ML", "Semi-Supervised ML" };
		final String[] unsupervisedTypeChoices = { "K-means",
				"Incremental K-means", "DBSCAN", "Incremental DBSCAN",
				"Mitosis", "Incremental Mitosis", "Mean Shift", "SHC",
				"Measurement" };
		final String[] semisupervisedTypeChoices = { "Self-Learning",
				"Co-Training" };
		final JLabel mlChoiceLabel = new JLabel("ML Type:");
		final JLabel mlTypeLabel = new JLabel("Unsupverised ML Type");
		final JComboBox mlTypeBox = new JComboBox(mlTypeChoices);
		final JComboBox mlSubtypeBox = new JComboBox(unsupervisedTypeChoices);
		mlTypeBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int newChoice = mlTypeBox.getSelectedIndex();
				if (e.getStateChange() == 1) {
					if (newChoice == 0) {
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						for (JComponent component : kmeansComponents) {
							panel.add(component);
						}
						panel.repaint();
						prevComponents.addAll(kmeansComponents);
						mlTypeLabel.setText("Unsupverised ML Type");
						mlSubtypeBox.removeAllItems();
						for (int i = 0; i < unsupervisedTypeChoices.length; i++) {
							mlSubtypeBox.addItem(unsupervisedTypeChoices[i]);
						}
					} else {
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						mlTypeLabel.setText("Semi-Supervised ML Type");
						mlSubtypeBox.removeAllItems();
						for (int i = 0; i < semisupervisedTypeChoices.length; i++) {
							mlSubtypeBox.addItem(semisupervisedTypeChoices[i]);
						}
					}
				}
			}
		});
		clusterType = "k-means";

		mlSubtypeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int newChoice = mlSubtypeBox.getSelectedIndex();
				int mlChoice = mlTypeBox.getSelectedIndex();

				if (e.getStateChange() == 1 && mlChoice == 0) {
					count = 0;
					inputFileLabel.setText("Input File:");
					centroidFileLabel.setText("Centroid File:");
					resultFileLabel.setText("Result File:");
					inputFileButton.setText("Choose Input File");
					centroidFileButton.setText("Choose Centroid File");
					resultFileButton.setText("Choose Result File");
					if (newChoice == 0) { // k-means
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						welcomeLabel.setText("Welcom to k-means Clustering:");
						for (JComponent component : kmeansComponents) {
							panel.add(component);
						}
						panel.repaint();
						prevComponents.addAll(kmeansComponents);
						clusterType = "k-means";
					} else if (newChoice == 1) { // incremental k-means
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						welcomeLabel
								.setText("Welcom to Incremental k-means Clustering:");
						for (JComponent component : kmeansComponents) {
							panel.add(component);
						}
						panel.repaint();
						prevComponents.addAll(kmeansComponents);
						clusterType = "incremental k-means";
					} else if (newChoice == 2) { // dbscan
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						welcomeLabel.setText("Welcom to DBSCAN Clustering:");
						for (JComponent component : dbscanComponents) {
							panel.add(component);
						}
						minPointLabel.setText("Min Point:");
						epslionLabel.setText("Epslion:");
						panel.repaint();
						prevComponents.addAll(dbscanComponents);
						clusterType = "dbscan";
					} else if (newChoice == 3) { // incremental dbscan
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(dbscanComponents);
						welcomeLabel
								.setText("Welcom to Incremental DBSCAN Clustering:");
						for (JComponent component : dbscanComponents) {
							panel.add(component);
						}
						minPointLabel.setText("Min Point:");
						epslionLabel.setText("Epslion:");
						panel.repaint();
						clusterType = "incremental dbscan";
					} else if (newChoice == 4) { // mitosis
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(mitosisComponents);
						welcomeLabel.setText("Welcom to Mitosis Clustering:");
						patternNoLabel.setBounds(400, 320, 100, 10);
						patternNoField.setBounds(patternNoLabel.getX()
								+ patternNoLabel.getWidth() + 30,
								patternNoLabel.getY(), 200, 20);
						for (JComponent component : mitosisComponents) {
							panel.add(component);
						}
						minPointLabel.setText("F:");
						epslionLabel.setText("K:");
						panel.repaint();
						clusterType = "mitosis";
					} else if (newChoice == 5) { // incremental mitosis
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(mitosisComponents);
						patternNoLabel.setBounds(400, 320, 100, 10);
						patternNoField.setBounds(patternNoLabel.getX()
								+ patternNoLabel.getWidth() + 30,
								patternNoLabel.getY(), 200, 20);
						welcomeLabel
								.setText("Welcom to Incremental Mitosis Clustering:");
						for (JComponent component : mitosisComponents) {
							panel.add(component);
						}
						minPointLabel.setText("F:");
						epslionLabel.setText("K:");
						panel.repaint();
						clusterType = "incremental mitosis";
					} else if (newChoice == 6) { // mean shift
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(meanshiftComponents);
						patternNoLabel.setBounds(100, 320, 80, 10);
						patternNoField.setBounds(patternNoLabel.getX()
								+ patternNoLabel.getWidth() + 10,
								patternNoLabel.getY(), 200, 20);
						welcomeLabel
								.setText("Welcom to Mean Shift Clustering:");
						for (JComponent component : meanshiftComponents) {
							panel.add(component);
						}
						panel.repaint();
						clusterType = "mean shift";
					} else if (newChoice == 7) { // SHC
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(SHCComponents);
						welcomeLabel.setText("Welcom to SHC Clustering:");
						patternNoLabel.setBounds(400, 320, 100, 10);
						patternNoField.setBounds(patternNoLabel.getX()
								+ patternNoLabel.getWidth() + 10,
								patternNoLabel.getY() - 5, 200, 20);
						minPointLabel.setText("Bins No:");
						epslionLabel.setText("Threshold:");
						patternNoLabel.setText("Min Histogram:");

						for (JComponent component : SHCComponents) {
							panel.add(component);
						}
						panel.repaint();
						clusterType = "shc";
					} else if (newChoice == 8) {
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(measureComponents);
						welcomeLabel.setText("Welcom to Measurement:");
						inputFileLabel.setText("Ground Truth:");
						centroidFileLabel.setText("Solution File:");
						inputFileButton.setText("Choose Ground Truth File");
						centroidFileButton.setText("Choose Solution File");
						for (JComponent component : measureComponents) {
							panel.add(component);
						}
						panel.repaint();
						clusterType = "measure";
					}
				} else if (e.getStateChange() == 1 && mlChoice == 1) {
					count = 0;
					if (newChoice == 0) { // self-learning
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(selflearningComponents);
						patternNoLabel.setBounds(100, 320, 80, 10);
						patternNoField.setBounds(patternNoLabel.getX()
								+ patternNoLabel.getWidth() + 10,
								patternNoLabel.getY(), 200, 20);
						welcomeLabel.setText("Welcom to Self-Learning:");
						inputFileLabel.setText("Train File:");
						centroidFileLabel.setText("Test File:");
						resultFileLabel.setText("Unlabeled File:");
						inputFileButton.setText("Choose Train File");
						centroidFileButton.setText("Choose Test File");
						resultFileButton.setText("Choose Unlabeled File");
						labelsFileLabel.setText("Labels File:");
						labelsFileButton.setText("Choose Labels File");
						selflearningresultFileLabel.setText("Result File:");
						selflearningresultfILEFileButton
								.setText("Choose Result File");
						classifierFileLabel.setText("Classifier File:");
						classifierFileButton.setText("Choose Classifier File");
						classifierTypeLabel.setBounds(400, 250, 100, 50);
						classifierTypeField.setBounds(
								classifierTypeLabel.getX()
										+ classifierTypeLabel.getWidth() + 10,
								classifierTypeLabel.getY() + 10, 200, 30);

						alphaLabel.setBounds(400, 300, 100, 50);
						alphaField.setBounds(
								alphaLabel.getX() + alphaLabel.getWidth() + 10,
								alphaLabel.getY() + 10, 200, 30);

						for (JComponent component : selflearningComponents) {
							panel.add(component);
						}
						panel.repaint();
						clusterType = "self-learning";
					} else if (newChoice == 1) { // co-training
						for (JComponent component : prevComponents) {
							panel.remove(component);
						}
						panel.repaint();
						prevComponents.addAll(cotrainingComponents);
						patternNoLabel.setBounds(100, 320, 80, 10);
						patternNoField.setBounds(patternNoLabel.getX()
								+ patternNoLabel.getWidth() + 10,
								patternNoLabel.getY(), 200, 20);
						welcomeLabel.setText("Welcom to Co-Training:");
						inputFileLabel.setText("Train File 1:");
						centroidFileLabel.setText("Test File 1:");
						resultFileLabel.setText("Unlabeled 1:");
						inputFileButton.setText("Choose Train File 1");
						centroidFileButton.setText("Choose Test File 1");
						resultFileButton.setText("Choose Unlabeled File 1");
						labelsFileLabel.setText("Labels File 1:");
						labelsFileButton.setText("Choose Labels File 1");
						classifierFileLabel.setText("Classifier 1:");
						classifierFileButton
								.setText("Choose Classifier File 1");
						classifierTypeLabel.setBounds(700, 250, 100, 50);
						classifierTypeField.setBounds(
								classifierTypeLabel.getX()
										+ classifierTypeLabel.getWidth() + 10,
								classifierTypeLabel.getY() + 10, 200, 30);
						alphaLabel.setBounds(700, 300, 100, 50);
						alphaField.setBounds(
								alphaLabel.getX() + alphaLabel.getWidth() + 10,
								alphaLabel.getY() + 10, 200, 30);
						for (JComponent component : cotrainingComponents) {
							panel.add(component);
						}
						panel.repaint();
						clusterType = "cotraining";
					}
				}
			}

		});

		// set positions
		mlChoiceLabel.setBounds(100, 70, 50, 50);
		mlTypeBox.setBounds(mlChoiceLabel.getX() + mlChoiceLabel.getWidth()
				+ 20, mlChoiceLabel.getY(), 300, 50);
		mlTypeLabel.setBounds(mlChoiceLabel.getX(), mlChoiceLabel.getY()
				+ mlChoiceLabel.getHeight() + 20, 150, 50);
		mlSubtypeBox.setBounds(
				mlTypeLabel.getX() + mlTypeLabel.getWidth() + 20,
				mlChoiceLabel.getY() + mlChoiceLabel.getHeight() + 20, 200, 50);

		panel.add(mlChoiceLabel);
		panel.add(mlTypeBox);
		panel.add(mlTypeLabel);
		panel.add(mlSubtypeBox);

		// welcome label k-means initially
		welcomeLabel = new JLabel("Welcom to k-means" + " Clustering:");
		Font labelFont = welcomeLabel.getFont();
		welcomeLabel.setFont(new Font(labelFont.getName(), Font.BOLD, labelFont
				.getSize() + 2));
		panel.add(welcomeLabel);
		prevComponents.add(welcomeLabel);
		kmeansComponents.add(welcomeLabel);
		dbscanComponents.add(welcomeLabel);
		mitosisComponents.add(welcomeLabel);
		meanshiftComponents.add(welcomeLabel);
		SHCComponents.add(welcomeLabel);
		selflearningComponents.add(welcomeLabel);
		cotrainingComponents.add(welcomeLabel);
		measureComponents.add(welcomeLabel);
		// for k-means & incremental k-means
		// input file
		inputFileLabel = new JLabel("Input File:");
		inputFileButton = new JButton("Choose Input File");
		panel.add(inputFileLabel);
		panel.add(inputFileButton);
		prevComponents.add(inputFileLabel);
		prevComponents.add(inputFileButton);
		kmeansComponents.add(inputFileLabel);
		kmeansComponents.add(inputFileButton);
		dbscanComponents.add(inputFileButton);
		dbscanComponents.add(inputFileLabel);
		mitosisComponents.add(inputFileButton);
		mitosisComponents.add(inputFileLabel);
		meanshiftComponents.add(inputFileButton);
		meanshiftComponents.add(inputFileLabel);
		SHCComponents.add(inputFileButton);
		SHCComponents.add(inputFileLabel);
		selflearningComponents.add(inputFileLabel);
		selflearningComponents.add(inputFileButton);
		cotrainingComponents.add(inputFileLabel);
		cotrainingComponents.add(inputFileButton);
		measureComponents.add(inputFileLabel);
		measureComponents.add(inputFileButton);

		// centroid file
		centroidFileLabel = new JLabel("Centroid File:");
		centroidFileButton = new JButton("Choose Centroid File");
		panel.add(centroidFileLabel);
		panel.add(centroidFileButton);
		prevComponents.add(centroidFileLabel);
		prevComponents.add(centroidFileButton);
		kmeansComponents.add(centroidFileLabel);
		kmeansComponents.add(centroidFileButton);
		selflearningComponents.add(centroidFileLabel);
		selflearningComponents.add(centroidFileButton);
		cotrainingComponents.add(centroidFileLabel);
		cotrainingComponents.add(centroidFileButton);
		measureComponents.add(centroidFileLabel);
		measureComponents.add(centroidFileButton);

		// resultFile
		resultFileLabel = new JLabel("Result File:");
		resultFileButton = new JButton("Choose Result File");
		panel.add(resultFileLabel);
		panel.add(resultFileButton);
		prevComponents.add(resultFileLabel);
		prevComponents.add(resultFileButton);
		kmeansComponents.add(resultFileLabel);
		kmeansComponents.add(resultFileButton);
		dbscanComponents.add(resultFileLabel);
		dbscanComponents.add(resultFileButton);
		mitosisComponents.add(resultFileLabel);
		mitosisComponents.add(resultFileButton);
		meanshiftComponents.add(resultFileLabel);
		meanshiftComponents.add(resultFileButton);
		SHCComponents.add(resultFileLabel);
		SHCComponents.add(resultFileButton);
		selflearningComponents.add(resultFileLabel);
		selflearningComponents.add(resultFileButton);
		cotrainingComponents.add(resultFileLabel);
		cotrainingComponents.add(resultFileButton);
		measureComponents.add(resultFileLabel);
		measureComponents.add(resultFileButton);

		isDrawBox = new JCheckBox("Draw Clusters");
		panel.add(isDrawBox);
		prevComponents.add(isDrawBox);
		kmeansComponents.add(isDrawBox);
		dbscanComponents.add(isDrawBox);
		mitosisComponents.add(isDrawBox);
		meanshiftComponents.add(isDrawBox);
		SHCComponents.add(isDrawBox);

		clusterButton = new JButton("Cluster");
		panel.add(clusterButton);
		prevComponents.add(clusterButton);
		kmeansComponents.add(clusterButton);
		dbscanComponents.add(clusterButton);
		mitosisComponents.add(clusterButton);
		meanshiftComponents.add(clusterButton);
		SHCComponents.add(clusterButton);

		kmeansAndIncrementalKmeans();

		// for dbscan and incremental dbscan
		minPointLabel = new JLabel("Min Point:");
		minPointTextFeild = new JTextField();
		dbscanComponents.add(minPointLabel);
		dbscanComponents.add(minPointTextFeild);
		mitosisComponents.add(minPointLabel);
		mitosisComponents.add(minPointTextFeild);
		SHCComponents.add(minPointLabel);
		SHCComponents.add(minPointTextFeild);

		epslionLabel = new JLabel("Epslion:");
		epslionField = new JTextField();
		dbscanComponents.add(epslionLabel);
		dbscanComponents.add(epslionField);
		mitosisComponents.add(epslionLabel);
		mitosisComponents.add(epslionField);
		SHCComponents.add(epslionLabel);
		SHCComponents.add(epslionField);

		DBSCANAndIncrementalDBSCAN();

		// mitosis
		simLabel = new JLabel("Similarity\\Distance:");
		simField = new JComboBox(new String[] { "euclidean", "pearson" });
		mitosisComponents.add(simLabel);
		mitosisComponents.add(simField);

		patternNoLabel = new JLabel("Pattern No:");
		patternNoField = new JTextField();
		mitosisComponents.add(patternNoLabel);
		mitosisComponents.add(patternNoField);
		meanshiftComponents.add(patternNoLabel);
		meanshiftComponents.add(patternNoField);
		SHCComponents.add(patternNoLabel);
		SHCComponents.add(patternNoField);

		isOutlierHandlingBox = new JCheckBox("Outlier Handling");
		mitosisComponents.add(isOutlierHandlingBox);
		mitosisAndIncrementalMitosis();

		// shc
		differenceThresholdLabel = new JLabel("Diff. Threshold:");
		differenceThresholdField = new JTextField();
		SHCComponents.add(differenceThresholdLabel);
		SHCComponents.add(differenceThresholdField);
		differenceThresholdLabel.setBounds(400, 250, 100, 50);
		differenceThresholdField.setBounds(differenceThresholdLabel.getX()
				+ differenceThresholdLabel.getWidth() + 10,
				differenceThresholdLabel.getY() + 20, 200, 20);

		// self-learning
		labelsFileLabel = new JLabel("Labels File:");
		labelsFileButton = new JButton("Choose Labels File");
		selflearningresultFileLabel = new JLabel("Result File:");
		selflearningresultfILEFileButton = new JButton("Choose Result File");
		classifierFileLabel = new JLabel("Classifier File:");
		classifierFileButton = new JButton("Choose Classifier File");
		classifierTypeLabel = new JLabel("Classifier Type:");
		classifierTypeField = new JComboBox(new String[] { "randomforest",
				"svm" });
		alphaLabel = new JLabel("Alpha:");
		alphaField = new JTextField();
		runButton = new JButton("Run");

		selflearningComponents.add(labelsFileLabel);
		selflearningComponents.add(labelsFileButton);
		selflearningComponents.add(selflearningresultFileLabel);
		selflearningComponents.add(selflearningresultfILEFileButton);
		selflearningComponents.add(classifierFileLabel);
		selflearningComponents.add(classifierFileButton);
		selflearningComponents.add(classifierTypeLabel);
		selflearningComponents.add(classifierTypeField);
		selflearningComponents.add(alphaLabel);
		selflearningComponents.add(alphaField);
		selflearningComponents.add(runButton);
		cotrainingComponents.add(labelsFileLabel);
		cotrainingComponents.add(labelsFileButton);
		cotrainingComponents.add(selflearningresultFileLabel);
		cotrainingComponents.add(selflearningresultfILEFileButton);
		cotrainingComponents.add(classifierFileLabel);
		cotrainingComponents.add(classifierFileButton);
		cotrainingComponents.add(classifierTypeLabel);
		cotrainingComponents.add(classifierTypeField);
		cotrainingComponents.add(alphaLabel);
		cotrainingComponents.add(alphaField);
		cotrainingComponents.add(runButton);
		measureComponents.add(runButton);
		selflearning();

		trainFileLabelSide2 = new JLabel("Train File 2:");
		trainFileButtonSide2 = new JButton("Choose Train File 2");
		testFileLabelSide2 = new JLabel("Test File 2:");
		testFileButtonSide2 = new JButton("Choose Test File 2");
		unlabeledFileLabelSide2 = new JLabel("Unlabeled 2:");
		unlabeledFileButtonSide2 = new JButton("Choose Unlabeled File 2");
		labelsFileLabelSide2 = new JLabel("Labels File 2:");
		labelsFileButtonSide2 = new JButton("Choose Labels File 2");
		mappingFileLabelSide2 = new JLabel("Mapping File:");
		mappingFileButtonSide2 = new JButton("Choose Mapping File");
		classifierFileLabelSide2 = new JLabel("Classifier 2:");
		classifierFileButtonSide2 = new JButton("Choose Classifier File 2");
		cotrainingComponents.add(trainFileLabelSide2);
		cotrainingComponents.add(trainFileButtonSide2);
		cotrainingComponents.add(testFileLabelSide2);
		cotrainingComponents.add(testFileButtonSide2);
		cotrainingComponents.add(unlabeledFileLabelSide2);
		cotrainingComponents.add(unlabeledFileButtonSide2);
		cotrainingComponents.add(labelsFileLabelSide2);
		cotrainingComponents.add(labelsFileButtonSide2);
		cotrainingComponents.add(mappingFileLabelSide2);
		cotrainingComponents.add(mappingFileButtonSide2);
		cotrainingComponents.add(classifierFileLabelSide2);
		cotrainingComponents.add(classifierFileButtonSide2);
		cotraining();

		panel.repaint();
	}

	private int count;

	public void kmeansAndIncrementalKmeans() {
		welcomeLabel.setBounds(100, 200, 500, 50);
		inputFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == inputFileButton) {
					int returnVal = inputFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = inputFileChooser.getSelectedFile();
						inputFile = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		inputFileLabel.setBounds(100, 250, 80, 50);
		inputFileButton.setBounds(
				inputFileLabel.getX() + inputFileLabel.getWidth() + 10,
				inputFileLabel.getY() + 10, 200, 30);

		centroidFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == centroidFileButton) {
					int returnVal = centroidFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = centroidFileChooser.getSelectedFile();
						centroidFile = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		centroidFileLabel.setBounds(100, 300, 80, 50);
		centroidFileButton.setBounds(centroidFileLabel.getX()
				+ centroidFileLabel.getWidth() + 10,
				centroidFileLabel.getY() + 10, 200, 30);

		resultFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == resultFileButton) {
					int returnVal = resultFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = resultFileChooser.getSelectedFile();
						resultFile = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		resultFileLabel.setBounds(100, 350, 100, 50);
		resultFileButton.setBounds(
				resultFileLabel.getX() + resultFileLabel.getWidth() - 10,
				resultFileLabel.getY() + 10, 200, 30);

		isDrawBox.setSelected(true);
		isDraw = 1;
		isDrawBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == isDrawBox) {
					if (isDrawBox.isSelected())
						isDraw = 1;
					else
						isDraw = 0;
				}
			}
		});
		isDrawBox.setBounds(100, 400, 150, 50);

		clusterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// pressed button
				try {
					if (count != 0) {
						panel.remove(prevComponents.get(prevComponents.size() - 1));
						prevComponents.remove(prevComponents.size() - 1);
						panel.repaint();
					}
					count++;
					if (clusterType.equalsIgnoreCase("k-means")) {
						Kmeans kmeans = new Kmeans();
						kmeans.run(inputFile, centroidFile, resultFile, isDraw,
								true);
					} else if (clusterType
							.equalsIgnoreCase("incremental k-means")) {
						IncrementalKmeans incrementalKmeans = new IncrementalKmeans();
						boolean isDrawBoolean = (isDraw == 1) ? true : false;
						incrementalKmeans.run(centroidFile, inputFile,
								resultFile, isDrawBoolean, true);
					} else if (clusterType.equalsIgnoreCase("dbscan")) {
						Unsupervised.DBSCAN.Main dbscan = new Unsupervised.DBSCAN.Main();
						boolean isDrawBoolean = (isDraw == 1) ? true : false;
						dbscan.runDBSCAN(
								Integer.parseInt(minPointTextFeild.getText()),
								Float.parseFloat(epslionField.getText()),
								inputFile, resultFile, isDrawBoolean, true);
					} else if (clusterType
							.equalsIgnoreCase("incremental dbscan")) {
						Unsupervised.IncremenetalDBSCAN.Main incrementalDBSCAN = new Unsupervised.IncremenetalDBSCAN.Main();
						boolean isDrawBoolean = (isDraw == 1) ? true : false;
						incrementalDBSCAN.runIncrementalDBSCAN(
								Integer.parseInt(minPointTextFeild.getText()),
								Float.parseFloat(epslionField.getText()),
								inputFile, resultFile, isDrawBoolean, true);
					} else if (clusterType.equalsIgnoreCase("mitosis")) {
						Unsupervised.Mitosis.Phase3 mitosis = new Unsupervised.Mitosis.Phase3();
						mitosis.run(
								Double.parseDouble(minPointTextFeild.getText()),
								Double.parseDouble(epslionField.getText()),
								inputFile,
								Integer.parseInt(patternNoField.getText()),
								(String) simField.getSelectedItem(),
								isOutlierHandling, resultFile, isDraw, true);
					} else if (clusterType
							.equalsIgnoreCase("incremental mitosis")) {
						Unsupervised.IncrementalMitosis.Main incrementalMitosis = new Unsupervised.IncrementalMitosis.Main();
						incrementalMitosis.cluster(
								inputFile,
								Double.parseDouble(minPointTextFeild.getText()),
								Double.parseDouble(epslionField.getText()),
								Integer.parseInt(patternNoField.getText()),
								(String) simField.getSelectedItem(),
								isOutlierHandling, resultFile, isDraw, true);
					} else if (clusterType.equalsIgnoreCase("mean shift")) {
						MeanShift meanshift = new MeanShift();
						meanshift.run(inputFile,
								Integer.parseInt(patternNoField.getText()),
								resultFile, isDraw, true);
					} else if (clusterType.equalsIgnoreCase("shc")) {
						SHC shc = new SHC();
						shc.run(inputFile, Integer.parseInt(minPointTextFeild
								.getText()), Double.parseDouble(epslionField
								.getText()), Integer.parseInt(patternNoField
								.getText()),
								Double.parseDouble(differenceThresholdField
										.getText()), resultFile, isDraw, true);
					}
					BufferedImage clusterImage = ImageIO.read(new File(
							"image.jpeg"));
					JLabel picLabel = new JLabel(new ImageIcon(clusterImage));
					picLabel.setBounds(600, 100, 800, 500);
					panel.add(picLabel);
					prevComponents.add(picLabel);
					panel.repaint();
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Please check your parameters");
				}
			}
		});

		clusterButton.setBounds(100, 450, 290, 50);

		panel.repaint();
	}

	public void DBSCANAndIncrementalDBSCAN() {

		minPointLabel.setBounds(100, 280, 80, 50);
		minPointTextFeild.setBounds(
				minPointLabel.getX() + minPointLabel.getWidth() + 10,
				minPointLabel.getY() + 20, 200, 20);

		epslionLabel.setBounds(100, 310, 80, 50);
		epslionField.setBounds(epslionLabel.getX() + epslionLabel.getWidth()
				+ 10, epslionLabel.getY() + 20, 200, 20);

		panel.repaint();

	}

	public void mitosisAndIncrementalMitosis() {
		simLabel.setBounds(400, 350, 120, 50);
		simField.setBounds(simLabel.getX() + simLabel.getWidth() + 10,
				simLabel.getY() + 10, 200, 30);
		isOutlierHandlingBox.setSelected(true);
		isOutlierHandling = 1;
		isOutlierHandlingBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == isOutlierHandlingBox) {
					if (isOutlierHandlingBox.isSelected())
						isOutlierHandling = 1;
					else
						isOutlierHandling = 0;
				}
			}
		});
		isOutlierHandlingBox.setBounds(280, 400, 120, 50);
		patternNoLabel.setBounds(400, 320, 100, 10);
		patternNoField.setBounds(
				patternNoLabel.getX() + patternNoLabel.getWidth() + 30,
				patternNoLabel.getY(), 200, 20);
	}

	public void selflearning() {
		final JFileChooser labelFileChooser = new JFileChooser();
		labelsFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == labelsFileButton) {
					int returnVal = labelFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = labelFileChooser.getSelectedFile();
						labelFile = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		labelsFileLabel.setBounds(100, 400, 80, 50);
		labelsFileButton.setBounds(
				labelsFileLabel.getX() + labelsFileLabel.getWidth() + 10,
				labelsFileLabel.getY() + 10, 200, 30);
		final JFileChooser selflearningresultFileChooser = new JFileChooser();
		selflearningresultfILEFileButton
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (e.getSource() == selflearningresultfILEFileButton) {
							int returnVal = selflearningresultFileChooser
									.showOpenDialog(panel);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								File file = selflearningresultFileChooser
										.getSelectedFile();
								selflearningResultFile = file.getAbsolutePath();
							}
						}
					}
				});
		// set positions
		selflearningresultFileLabel.setBounds(100, 450, 80, 50);
		selflearningresultfILEFileButton.setBounds(
				selflearningresultFileLabel.getX()
						+ selflearningresultFileLabel.getWidth() + 10,
				selflearningresultFileLabel.getY() + 10, 200, 30);

		final JFileChooser classifierFileChooser = new JFileChooser();
		classifierFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == classifierFileButton) {
					int returnVal = classifierFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = classifierFileChooser.getSelectedFile();
						classifierFile = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		classifierFileLabel.setBounds(100, 500, 80, 50);
		classifierFileButton.setBounds(classifierFileLabel.getX()
				+ classifierFileLabel.getWidth() + 10,
				classifierFileLabel.getY() + 10, 200, 30);

		classifierTypeLabel.setBounds(400, 250, 100, 50);
		classifierTypeField.setBounds(classifierTypeLabel.getX()
				+ classifierTypeLabel.getWidth() + 10,
				classifierTypeLabel.getY() + 10, 200, 30);

		alphaLabel.setBounds(400, 300, 100, 50);
		alphaField.setBounds(alphaLabel.getX() + alphaLabel.getWidth() + 10,
				alphaLabel.getY() + 10, 200, 30);

		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// pressed button
				try {
					if (count != 0) {
						panel.remove(prevComponents.get(prevComponents.size() - 1));
						prevComponents.remove(prevComponents.size() - 1);
						panel.repaint();
					}
					count++;
					if (clusterType.equalsIgnoreCase("self-learning")) {
						SelfLearner selflearner = new SelfLearner();
						String[][] data = selflearner.run(inputFile,
								centroidFile, labelFile, resultFile,
								selflearningResultFile, classifierFile,
								Double.parseDouble(alphaField.getText()),
								(String) classifierTypeField.getSelectedItem());
						String[] columns = { "", "Precision", "Recall",
								"Fmeasure" };
						JTable resultTable = new JTable(data, columns);
						JScrollPane pane = new JScrollPane(resultTable);
						pane.setBounds(750, 250, 500, data.length * 50 + 20);
						for (int i = 0; i < data.length; i++) {
							resultTable.setRowHeight(i, 50);
						}
						resultTable.getColumnModel().getColumn(0)
								.setPreferredWidth(250);
						DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
						centerRenderer.setHorizontalAlignment(JLabel.CENTER);
						for (int j = 1; j < data[0].length; j++) {
							resultTable.getColumnModel().getColumn(j)
									.setPreferredWidth(100);
							resultTable.getColumnModel().getColumn(j)
									.setCellRenderer(centerRenderer);
						}
						panel.add(pane);
						prevComponents.add(pane);
						panel.repaint();
					} else if (clusterType.equalsIgnoreCase("cotraining")) {
						CoTraining cotrainer = new CoTraining();
						String[][] data = cotrainer.run(inputFile,
								centroidFile, labelFile, resultFile,
								trainFileSide2, testFileSide2, labelFileSide2,
								unlabeledFileSide2, mappingFileSide2,
								selflearningResultFile, classifierFile,
								classifierFileSide2,
								Double.parseDouble(alphaField.getText()),
								(String) classifierTypeField.getSelectedItem());
						String[] columns = { "", "Precision", "Recall",
								"Fmeasure" };
						JTable resultTable = new JTable(data, columns);
						JScrollPane pane = new JScrollPane(resultTable);
						pane.setBounds(700, 350, 580, data.length * 20 + 20);
						for (int i = 0; i < data.length; i++) {
							resultTable.setRowHeight(i, 20);
						}
						resultTable.getColumnModel().getColumn(0)
								.setPreferredWidth(280);
						DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
						centerRenderer.setHorizontalAlignment(JLabel.CENTER);
						for (int j = 1; j < data[0].length; j++) {
							resultTable.getColumnModel().getColumn(j)
									.setPreferredWidth(100);
							resultTable.getColumnModel().getColumn(j)
									.setCellRenderer(centerRenderer);
						}
						panel.add(pane);
						prevComponents.add(pane);
						panel.repaint();
					} else if (clusterType.equalsIgnoreCase("measure")) {
						MeasurementPerformer measure = new MeasurementPerformer();
						String[][] data = measure.compare(inputFile,
								centroidFile, resultFile);
						String[] columns = { "Fmeasure", "Jaccard", "Rand",
								"Rand Index" };
						JTable resultTable = new JTable(data, columns);
						JScrollPane pane = new JScrollPane(resultTable);
						pane.setBounds(100, 450, 580, data.length * 50 + 20);
						for (int i = 0; i < data.length; i++) {
							resultTable.setRowHeight(i, 50);
						}
						DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
						centerRenderer.setHorizontalAlignment(JLabel.CENTER);
						for (int j = 0; j < data[0].length; j++) {
							resultTable.getColumnModel().getColumn(j)
									.setPreferredWidth(100);
							resultTable.getColumnModel().getColumn(j)
									.setCellRenderer(centerRenderer);
						}
						panel.add(pane);
						prevComponents.add(pane);
						panel.repaint();
					}
				} catch (Exception ex) {
					System.out.println("Please check your parameters");
				}
			}
		});

		runButton.setBounds(100, 550, 290, 50);
	}

	public void cotraining() {
		final JFileChooser trainFileChooser = new JFileChooser();
		trainFileButtonSide2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == trainFileButtonSide2) {
					int returnVal = trainFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = trainFileChooser.getSelectedFile();
						trainFileSide2 = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		trainFileLabelSide2.setBounds(400, 250, 80, 50);
		trainFileButtonSide2.setBounds(trainFileLabelSide2.getX()
				+ trainFileLabelSide2.getWidth() + 10,
				trainFileLabelSide2.getY() + 10, 200, 30);

		final JFileChooser testFileChooser = new JFileChooser();
		testFileButtonSide2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == testFileButtonSide2) {
					int returnVal = testFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = testFileChooser.getSelectedFile();
						testFileSide2 = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		testFileLabelSide2.setBounds(400, 300, 80, 50);
		testFileButtonSide2.setBounds(testFileLabelSide2.getX()
				+ testFileLabelSide2.getWidth() + 10,
				testFileLabelSide2.getY() + 10, 200, 30);

		final JFileChooser unlabeledFileChooser = new JFileChooser();
		unlabeledFileButtonSide2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == unlabeledFileButtonSide2) {
					int returnVal = unlabeledFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = unlabeledFileChooser.getSelectedFile();
						unlabeledFileSide2 = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		unlabeledFileLabelSide2.setBounds(400, 350, 100, 50);
		unlabeledFileButtonSide2.setBounds(unlabeledFileLabelSide2.getX()
				+ unlabeledFileLabelSide2.getWidth() - 10,
				unlabeledFileLabelSide2.getY() + 10, 200, 30);

		final JFileChooser labelFileChooser = new JFileChooser();
		labelsFileButtonSide2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == labelsFileButtonSide2) {
					int returnVal = labelFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = labelFileChooser.getSelectedFile();
						labelFileSide2 = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		labelsFileLabelSide2.setBounds(400, 400, 80, 50);
		labelsFileButtonSide2.setBounds(labelsFileLabelSide2.getX()
				+ labelsFileLabelSide2.getWidth() + 10,
				labelsFileLabelSide2.getY() + 10, 200, 30);
		final JFileChooser selflearningresultFileChooser = new JFileChooser();
		mappingFileButtonSide2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mappingFileButtonSide2) {
					int returnVal = selflearningresultFileChooser
							.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = selflearningresultFileChooser
								.getSelectedFile();
						mappingFileSide2 = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		mappingFileLabelSide2.setBounds(400, 450, 80, 50);
		mappingFileButtonSide2.setBounds(mappingFileLabelSide2.getX()
				+ mappingFileLabelSide2.getWidth() + 10,
				mappingFileLabelSide2.getY() + 10, 200, 30);

		final JFileChooser classifierFileChooser = new JFileChooser();
		classifierFileButtonSide2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == classifierFileButtonSide2) {
					int returnVal = classifierFileChooser.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = classifierFileChooser.getSelectedFile();
						classifierFileSide2 = file.getAbsolutePath();
					}
				}
			}
		});
		// set positions
		classifierFileLabelSide2.setBounds(400, 500, 80, 50);
		classifierFileButtonSide2.setBounds(classifierFileLabelSide2.getX()
				+ classifierFileLabelSide2.getWidth() + 10,
				classifierFileLabelSide2.getY() + 10, 200, 30);

	}

	public void createAndShowGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		JLabel startLabel = new JLabel("Welcome to Machine Learning Library");
		Font labelFont = startLabel.getFont();
		startLabel.setFont(new Font(labelFont.getName(), Font.BOLD, labelFont
				.getSize() * 2));
		startLabel.setBounds(400, 0, 500, 50);
		panel = new JPanel();
		panel.setLayout(null);
		panel.setVisible(true);
		panel.add(startLabel);
		panel.setBounds(0, 0, 1300, 700);
		addMLTypeSubTypes();
		add(panel);
		// Display the window.
		setSize(1300, 700);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	public void run() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.run();
	}

}
