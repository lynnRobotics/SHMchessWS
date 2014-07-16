package IntelM2M.agent.ap;

import java.util.ArrayList;

import IntelM2M.datastructure.AppNode;
import IntelM2M.epcie.GAinference;
import IntelM2M.esdse.Optimizer;
import IntelM2M.exp.ExpRecorder;

public class APAgent {

	
	public void setPriorityForEUS(ArrayList<AppNode> eusList){
		ArrayList<Double> priorityList= new ArrayList<Double>();
		for(AppNode eus:eusList){
			double priority=FuzzyInference.getPriority_test(eus);
			eus.priority=priority;	
		}
	}

	
	/* input: decision array, eusList, inferResult */
	public double getComfortCost(ArrayList<AppNode> decisionList,ArrayList<AppNode> apRawList){
		
		// cal cost form priority array and decision array(apDecisionList)
		double cost=0;
		for(int i=0;i<decisionList.size();i++){
			AppNode dApp=decisionList.get(i);
			AppNode rApp=apRawList.get(i);
			if(!dApp.envContext.equals( rApp.envContext)){
				cost+=Math.pow(dApp.priority, 2);
			}
		}

		return cost;		
	}
	
	/* optimization */
	public ArrayList <AppNode> getOptApList(ArrayList<AppNode> eusList,GAinference gaInference){
		// get ap List
		ArrayList<AppNode> apAppList= new ArrayList<AppNode>();
		ArrayList<AppNode> apRawList= new ArrayList<AppNode> ();
		for(AppNode app:eusList){
			if(app.agentName.equals("ap")){
				AppNode app2= app.copyAppNode(app);
				apAppList.add(app2);
				AppNode app3= app.copyAppNode(app);
				apRawList.add(app3);
			}
		}
				
		/*�o��n�Q��k
		 * �����������i�hã�|
		 * �n�ΨǤ�q
		 * 1.�n����apAppList���ǫe�B�z�A�Y�p�d��
		 * 2.�ھڨC��apAppList���p��fuzzy
		 * */
		setPriorityForEUS(apAppList);

		//ArrayList<AppNode> bestAnswer=null;	
		apAppList= apIterate(apAppList,apRawList);
		
//		/*for experiment record visual save*/
//		double rawAmp= Optimizer.calEnergyConsumption(apRawList);
//		double newAmp=Optimizer.calEnergyConsumption(apAppList);
//		ExpRecorder.exp.setAPSave(rawAmp-newAmp);
//		/*exp record end*/
		
		return apAppList;
		
	}
	
	private ArrayList<AppNode> updateState(ArrayList<AppNode> apAppList){
		for(AppNode app:apAppList){
			/* not finish �o��O�g����*/
			if(app.priority<2){
				if(app.envContext.contains("on")){
					app.haveAPControlFromOn=true;
				}
				app.envContext="off";		
			}
		}
		return apAppList;
	}
	
	private ArrayList<AppNode>apIterate(ArrayList<AppNode> apAppList,ArrayList<AppNode> apRawList){
		/* iterate */
		/* ��������priority <? �����ǹq�� */
		apAppList=updateState(apAppList);		
		double cost=getComfortCost(apAppList,apRawList);
		double amp=Optimizer.calEnergyConsumption(apAppList);
	
		return apAppList;
	}
}
