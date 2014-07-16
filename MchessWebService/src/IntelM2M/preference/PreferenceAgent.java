package IntelM2M.preference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import IntelM2M.datastructure.AppNode;
import IntelM2M.epcie.GAinference;
import IntelM2M.epcie.GaGenerator;


public class PreferenceAgent {
	
	
	public class PreferenceModel{
		 ArrayList<String> prFeature= new ArrayList<String>();
		 ArrayList<AppNode> decisionList= new ArrayList<AppNode>();
		 Set<String> actInferResultSet = new HashSet<String>();
		 ArrayList<String> gaInferResultList=new ArrayList<String>();
		
		public Boolean inferUpdate=false;
		public Boolean controlUpdate=false;
		/*raw decision */
		public ArrayList<AppNode> rawDecisionList= new ArrayList<AppNode>();
		/*raw infer result*/
		public Set<String> rawSingleInferResult =null;
	}
	
	private int controlWrongCounter=0;
	private ArrayList<PreferenceModel> prModelList= new ArrayList<PreferenceModel>();

//	 public void userFeedback(GAinference gaInference,String read,String read2){
//		 /*update ���ؼ�model�O�̫ᴡ�J�����@��model*/
//		 PreferenceModel model= inferPRmodel(gaInference);
//		 		 		 
//		 /*check �O�_��inference���~*/
//		 Boolean inferWrong=false;
//		 if(read!=null){
//			 String split[]=read.split("#");
//			 String split2[]=split[1].split(" ");
//			 /*check infer�����G�O�_���T*/
//			 for(String str:model.singleInferResult){
//				 Boolean contain=false;
//				 for(String str2:split2){
//					if(str.equals(str2)){
//						contain=true;
//					}
//				 }
//				 /*infer�����G���A ground trugh ��*/
//				 if(contain==false){
//					 inferWrong=true;
//					 break;
//				 }
//			 }
//			 /*���� infer wrong �ɪ�����*/
//			 if(inferWrong==true){
//				 model.inferUpdate=true;
//				 model.rawSingleInferResult= new HashSet<String>();
//				 /*���ƻs�������infer���G�� rawSingleInferResult*/
//				 for(String str:model.singleInferResult){
//					 model.rawSingleInferResult.add(str);
//				 }
//				 
//				 /*�Ninfer �����G��אּ�ϥΪ̧�s�����e*/
//				 model.singleInferResult.clear();
//				 for(String str:split2){
//					 model.singleInferResult.add(str);
//				 }
//			 }
//			 
//		 }
//		 
//		 /*check ap agent�O�_���� �åB update decision list*/
//		
//		 if(!read2.equals("")){
//			 	String split[]=read2.split(" ");
//				/*���ƻs�����decisionList�� rawDecisino��*/
//			 	for(AppNode app:model.decisionList){
//			 		AppNode app2=app.copyAppNode(app);
//			 		model.rawDecisionList.add(app2);
//			 	}
//			 	
//			 	/*�P�_�O�_���L����*/
//				for(AppNode app:model.decisionList){
//					if(app.haveAPControlFromOn){
//						for(String str:split){
//							if(app.appName.equals(str)){
//								/*����F*/
//							}else{
//								model.controlUpdate=true;
//								/*�����������}*/
//								app.haveAPControlFromOn=false;
//								app.envContext="on";
//								controlWrongCounter++;
//							}
//						}
//					}else{
//						/*��S��������*/
//						for(String str:split){
//							if(app.appName.equals(str) && app.envContext.equals("on")){
//								app.haveAPControlFromOn=true;
//								app.envContext="off";
//							}
//						}
//					}
//			   }
//		}
//		 
//		 
//	 }
	 
	 
	 public void userFeedback_new(GAinference gaInference,String read,String read2){
		 /*update ���ؼ�model�O�̫ᴡ�J�����@��model*/
		 PreferenceModel model= inferPRmodel(gaInference);
		 		 		 
		 /*check �O�_��inference���~*/
		 Boolean inferWrong=false;
		 if(read!=null && model!=null){
			 String split[]=read.split("#");
			 String split2[]=split[1].split(" ");
			 /*check infer�����G�O�_���T*/
			 if(model.actInferResultSet==null){
				 /*for debug*/
			 }else{
				 for(String str:model.actInferResultSet){
					 Boolean contain=false;
					 for(String str2:split2){
						if(str.equals(str2)){
							contain=true;
						}
					 }
					 /*infer�����G���A ground trugh ��*/
					 if(contain==false){
						 inferWrong=true;
						 break;
					 }
				 }
			 }

			 /*���� infer wrong �ɪ�����*/
			 if(inferWrong==true){
				 model.inferUpdate=true;
				 model.rawSingleInferResult= new HashSet<String>();
				 /*���ƻs�������infer���G�� rawSingleInferResult*/
				 for(String str:model.actInferResultSet){
					 model.rawSingleInferResult.add(str);
				 }
				 
				 /*�Ninfer �����G��אּ�ϥΪ̧�s�����e*/
				 model.actInferResultSet.clear();
				 for(String str:split2){
					 model.actInferResultSet.add(str);
				 }
				 
				 model.gaInferResultList.clear();
				 /*�ھ�single act result �Ө��oga set*/
				 GaGenerator gaG=gaInference.GaGeneratorList.get(0);
				 for(String str:split2){
					 ArrayList<String> gaName=gaG.getGID(str);
					 model.gaInferResultList.add(gaName.get(0));
				 }
			 }
			 
		 }
	 		 
	 }
	 
	 public void updateInferResult(GAinference gaInference, PreferenceModel prM){
		 gaInference.actInferResultSet= prM.actInferResultSet;
		 gaInference.gaInferResultList=prM.gaInferResultList;
	 }

	 public   PreferenceModel inferPRmodel(GAinference gaInference){
			int index=-1;
			
			
			for( int i=0;i<prModelList.size();i++ ){
				PreferenceModel model=prModelList.get(i);
				Boolean featureSame=true;
				if(model.prFeature.size()!= gaInference.rawGAinferResultList.size()){
					featureSame=false;
				}else{
					
					for(String str: model.prFeature){
						Boolean contain=false;
						for(String str2: gaInference.rawGAinferResultList){
							if(str.equals(str2)){
								contain=true;
								break;
							}
						}
						if(contain==false){
							featureSame=false;
							break;
						}
					}
				}

				
				if(featureSame==true){
					/*�o��ndebug �i��|����*/
					index=i;
					break;
				}
			}
			
			if(index<0){
				return null;
			}
			
			else{
				/*infer preference model*/
				PreferenceModel model=prModelList.get(index);
				/*return */
				return model;
			}
	
	 }
	 

	 
	 public void buildPRModel(ArrayList<AppNode> decisionList,GAinference gaInference){
		
		 
		 PreferenceModel model= new PreferenceModel();
		for(String str: gaInference.rawGAinferResultList){
			model.prFeature.add(str);
		}
		
		for(AppNode app:decisionList){
			AppNode app2=app.copyAppNode(app);
			model.decisionList.add(app2);
		}
		
		 for(String str:gaInference.actInferResultSet){
				model.actInferResultSet.add(str);
		}
		 
		 for(String str:gaInference.gaInferResultList){
			 model.gaInferResultList.add(str);
		 }
		
		prModelList.add(model);
		
	}
}
