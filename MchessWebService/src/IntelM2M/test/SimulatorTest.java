package IntelM2M.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import IntelM2M.datastructure.AppNode;
import IntelM2M.datastructure.EnvStructure;
import IntelM2M.epcie.Epcie;
import IntelM2M.esdse.Esdse;
import IntelM2M.exp.ExpRecorder;
import IntelM2M.exp.ExpRecorderByDay;
import IntelM2M.exp.LocationBasedSaving;

/**
 * 
 * @author Mao (2012.06)
 */
public class SimulatorTest {

	// String testingDataPath="./_input_data/simulator/testing/all_test_data.txt";
	// String noiseTruthPath="./_input_data/simulator/testing/all_test_data_truth.txt";
	// String testingDataPath="./_input_data/simulator/testing/debug_test_data.txt";
	// String noiseTruthPath="./_input_data/simulator/testing/debug_test_data_truth.txt";
	String testingDataPath = "./_input_data/simulator/testing/parameter1/Set 1/simple_test_data.txt";
	String noiseTruthPath = "./_input_data/simulator/testing/parameter1/Set 1/simple_test_data_truth.txt";

	/* for debug */
	// String testingDataPath="./_input_data/simulator/debug/simple_test_data.txt";
	// String noiseTruthPath="./_input_data/simulator/debug/simple_test_data_truth.txt";
	/* for experiment */
	public static int rowNum = 0;
	public static int duration = 0;

	/* for mchess */
	static private String lastNoiseForMchess = null;
	static private ArrayList<AppNode> lastDecisionForMchess = null;
	static private Boolean readIsUpdateForMchess = false;

	public String updateReadForMchess(String read, String read2) {
		if (lastDecisionForMchess == null || lastNoiseForMchess == null) {
			// return;
			return read;
		}

		/*
		 * �ڤW��"�������q����"�A�p�G"�T�{�O�W����noise"�A�B�]�O"�o����noise"�A ���ڴN�������o����noise���R���o�ǹq���A�B�[�J�ڪ�turn off �}�C��
		 */
		/* noise */
		String[] split = lastNoiseForMchess.split(" ");

		/* context and activity */
		String[] split2 = read.split("#");
		String[] split3 = split2[0].split(" ");

		for (AppNode app : lastDecisionForMchess) {
			for (String str : split) {
				/* �o��i�঳���D */
				if (read2.contains(str) && app.appName.equals(str) && app.haveAPControlFromOn) {
					/* �N�o�ӹq���qread������ */
					/* �����o�ӹq���bread������m */
					Map<String, ArrayList<String>> sensorStatus = EnvStructure.sensorStatus;
					String[] sensorName = (String[]) sensorStatus.keySet().toArray(new String[0]);
					int index = 0;
					for (String str2 : sensorName) {
						if (str2.equals(str)) {
							break;
						}
						index++;
					}
					/* �����q�� */
					split3[index] = "off";

					/* �åBupdate��decision list�� */
					readIsUpdateForMchess = true;
				}
			}
		}
		/* �զX���쥻��read */
		String out = "";
		for (String str : split3) {
			out = out + str + " ";
		}
		out = out + "#" + split2[1];

		return out;

	}

	static public void setLastDataForMchess(String in1, ArrayList<AppNode> in2) {
		lastNoiseForMchess = in1;
		lastDecisionForMchess = in2;
	}

	static public void updateDecisionForMchess(ArrayList<AppNode> decisionList) {
		if (readIsUpdateForMchess == true) {
			String[] split = lastNoiseForMchess.split(" ");
			for (AppNode app : decisionList) {
				for (String str : split) {
					if (app.appName.equals(str)) {
						app.envContext = "off";
						app.haveAPControlFromOn = true;
					}
				}
			}

			readIsUpdateForMchess = false;
		}
	}

	static public String rawDataPreprocessing(String read) {
		String ans = null;
		try {
			String[] split = read.split("#");
			String[] sensorContext = split[0].split(" ");
			ans = "";
			for (String str : sensorContext) {
				String[] tmp = str.split("_");
				ans = ans + tmp[0] + " ";
			}
			ans = ans + "#" + split[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}

	private int getDuration(String in, int rowNum) {
		int duration = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testingDataPath));
			int i = 1;
			String read = null;
			while ((read = reader.readLine()) != null) {
				if (i < rowNum) {

				} else if (i >= rowNum) {
					if (in.equals(read)) {
						duration += 1;
					} else {
						break;
					}
				}
				i++;
			}
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return duration;

	}

	public void simulatorTesting(Epcie epcie, Esdse esdse) {
		try {

			BufferedReader reader = new BufferedReader(new FileReader(testingDataPath));
			BufferedReader reader2 = new BufferedReader(new FileReader(noiseTruthPath));
			String read = null, read2 = null, lastRead = null;

			int i = 0;

			ExpRecorderByDay.expByDay.initial();

			while ((read = reader.readLine()) != null && (read2 = reader2.readLine()) != null) {
				rowNum++;
				/* input data preprocessing */
				if (read.equals(lastRead)) {
					continue;
				} else {
					lastRead = read;
					duration = getDuration(read, rowNum);
					/* ��sREAD����� */
					/* todo1 :check if error */
					String updatedReadForMchess = updateReadForMchess(read, read2);

					// esdse.processForSimulator(epcie, lastRead,read2);
					esdse.processForSimulatorWithPR_new(epcie, read, read2, updatedReadForMchess);

					/* �`�Nlocation based�����խn��b esdse����A�]��esdse�|�]�w�ɶ��Ѽ� */
					/* location based */
					LocationBasedSaving lbs = new LocationBasedSaving();
					lbs.processForSimulator(epcie, lastRead, read2);
					System.out.println(i++);
				}

			}
			
			reader.close();
			reader2.close();
			System.out.println("Testing Finish");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		/* write out exp result */
		ExpRecorder.exp.wirteOutESResult();
		ExpRecorder.exp.writeOutARResult();
		ExpRecorder.exp.writeDebugInformation();

		/* writer out day exp result */
		ExpRecorderByDay.expByDay.writeOutESResult();
		System.out.println("Write Finish");

	}

}
