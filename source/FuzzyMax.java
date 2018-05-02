package flctk;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;

public class FuzzyMax extends MaxObject {
	
	FuzzySystem fuzzy;
	double[] ins;
	double[] outs;
	int[] insRule;
	int[] outsRule;
	JFileChooser fileChooser;
	float Num;
	int num=0;
	int num2=0;
	boolean test=true;
	
	public FuzzyMax(String filename)
	{		
		fuzzy= new FuzzySystem(filename);
		
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		Actualize();
	}
	
	private void Actualize()
	{

		declareInlets(new int[fuzzy.getNInputs()]);
		ins= new double[fuzzy.getNInputs()];
		declareOutlets(new int[fuzzy.getNOutputs()]);
		outs= new double[fuzzy.getNOutputs()];
		
		String[] inletAssist= new String[ins.length];
		for(int i=0; i<ins.length; i++)
				inletAssist[i]="double input between "+fuzzy.getInMinRange(i)+" and "+fuzzy.getInMaxRange(i);
		
		setInletAssist(inletAssist);
		
		String[] outletAssist= new String[outs.length];
		for(int i=0; i<outs.length; i++)
				outletAssist[i]="double output between "+fuzzy.getOutMinRange(i)+" and "+fuzzy.getOutMaxRange(i);
		
		setInletAssist(outletAssist);
		
	}
	
	public FuzzyMax()
	{	
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		fuzzy= new FuzzySystem();
		newInputs(3,0,1,"Default","trimf");
		newOutputs(3,0,1,"Default","trimf");
		Actualize();
		print();
	}
	
	public void bang() {
		outs=fuzzy.eval(ins);
	
		for(int i=0; i<outs.length;i++)
		{outlet(i,outs[i]);
		post(""+outs[i]);}
		
		post("new out eval");
		
	}
	
	public void SetName(String name)
	{
		fuzzy.setName(name);
		System();
	}
	
	public void SetInputName(String Name, int n){
		fuzzy.setInputName(Name, n-1);
		Ins();
		}
	
	public void SetOutputName(String Name, int n){
		fuzzy.setOutputName(Name, n-1);
		Outs();
		}
	
	public void inlet(double i)
	{
		int int_o=getInlet();
		ins[int_o]=i;
		if(int_o==0)
			bang();
	}
			
	public void inlet(float i)
	{
		int int_o=getInlet();
		ins[int_o]=i;
		if(int_o==0)
			bang();
	}
		
	public void Read()
	{
		read();
	}
	
	public void read()
		{
		
		fileChooser.setFileFilter(new FileFilter() {
	        public boolean accept(File f) {
	            return f.getName().toLowerCase().endsWith(".fis")
	                || f.isDirectory();
	          }

	          public String getDescription() {
	            return "FIS Files";
	          }
	        });
		
		int result = fileChooser.showOpenDialog(fileChooser);
		
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    ChangeFile(selectedFile.getAbsolutePath());
		}
	}
	
	public void open()
	{
		
		fileChooser.setFileFilter(new FileFilter() {
	        public boolean accept(File f) {
	            return f.getName().toLowerCase().endsWith(".fis")
	                || f.isDirectory();
	          }

	          public String getDescription() {
	            return "FIS Files";
	          }
	        });
		
		int result = fileChooser.showOpenDialog(fileChooser);
		
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    ChangeFile(selectedFile.getAbsolutePath());
		}
	}
		
	public void newInputs(int NF, float limit1, float limit2, String name, String type){
		
		FuzzyVar FV= new FuzzyVar(NF,name);
		if(limit1<limit2)
		{	FV.setRangeMin(limit1);
		FV.setRangeMax(limit2);}
		else
		{FV.setRangeMin(limit2);
		FV.setRangeMax(limit1);}
		
		double dif=FV.getRange()/(NF-1);
		
		double min=FV.getRangeMin();
				
		for(int i=1; i<=NF;i++)
		{
			double[] values=new double[3];
			
				values[0]=min-dif;
				values[1]=min;
				values[2]=min+dif;				
			
			FV.addMFunction("MF"+i, "trimf", values);
			min+=dif;
		}
		
		if(!type.equals("trimf"))
		{
			int t=0;
			if (type.equals("dsigmf")) t=11;
			 else if (type.equals("gauss2mf")) t =6;
			 else if (type.equals("gaussmf")) t =5;
			 else if (type.equals("gbellmf")) t = 8;
			 else if (type.equals("pimf")) t = 9;
			 else if (type.equals("psigmf")) t = 10;
			 else if (type.equals("sigmf")) t = 7;
			 else if (type.equals("smf")) t = 12;
			 else if (type.equals("trapmf")) t = 4;
			 else if (type.equals("zmf")) t = 13;
			 else if (type.equals("linear")) t = 14;
			 else if (type.equals("constant")) t = 15;
			 else
			 {post("Invalid type of function");
			 return;}

			for(int i=0; i<NF;i++)
				FV.getMFunctionAt(i).setType(t);
			
		}
	
		fuzzy.AddInput(FV);
		Actualize();
		Ins();

	}
	
	public void newOutputs( int NF, float limit1, float limit2, String name, String type){
		FuzzyVar FV= new FuzzyVar(NF,name);
		if(limit1<limit2)
		{	FV.setRangeMin(limit1);
		FV.setRangeMax(limit2);}
		else
		{FV.setRangeMin(limit2);
		FV.setRangeMax(limit1);}
		
		double dif=FV.getRange()/(NF-1);
		
		double min=FV.getRangeMin();
				
		for(int i=1; i<=NF;i++)
		{
			double[] values=new double[3];
			
				values[0]=min-dif;
				values[1]=min;
				values[2]=min+dif;				
			
			FV.addMFunction("MF"+i, "trimf", values);
			min+=dif;
		}
		
		if(!type.equals("trimf"))
		{
			int t=0;
			if (type.equals("dsigmf")) t=11;
			 else if (type.equals("gauss2mf")) t =6;
			 else if (type.equals("gaussmf")) t =5;
			 else if (type.equals("gbellmf")) t = 8;
			 else if (type.equals("pimf")) t = 9;
			 else if (type.equals("psigmf")) t = 10;
			 else if (type.equals("sigmf")) t = 7;
			 else if (type.equals("smf")) t = 12;
			 else if (type.equals("trapmf")) t = 4;
			 else if (type.equals("zmf")) t = 13;
			 else if (type.equals("linear")) t = 14;
			 else if (type.equals("constant")) t = 15;
			 else
			 {post("Invalid type of function");
			 return;}

			for(int i=0; i<NF;i++)
				FV.getMFunctionAt(i).setType(t);	
		}
		
		fuzzy.AddOutput(FV);
		Actualize();
		Outs();		 
	}
	
	public void ChangeInputType(float nI, float nMF, String type)
	{	
		int tipo=0;
		if (type.equals("trapmf")) tipo = 4;
		else if (type.equals("trimf")) tipo = 2;
		else if (type.equals("gaussmf")) tipo = 5;
		else if (type.equals("gauss2mf")) tipo =6;
		else if (type.equals("sigmf")) tipo =7;
		else if (type.equals("gbellmf")) tipo =8;
		else if (type.equals("pimf")) tipo =9;
		else if (type.equals("psigmf")) tipo =10;
		else if (type.equals("dsigmf")) tipo =11;
		else if (type.equals("smf")) tipo =12;
		else if (type.equals("zmf")) tipo =13;
		
		else
		{	System.out.println(type+"it's not a defined type");
		return;}

		fuzzy.ChangeMFInput((int)(nI-1), (int)nMF, tipo);
		Ins();
	}
	
	public void ChangeOutputType(int nO, int nMF, String type)
	{
		int tipo=0;
		if(fuzzy.getType()=="mandani"){
		if (type.equals("trapmf")) tipo = 4;
		else if (type.equals("trimf")) tipo = 2;
		else if (type.equals("gaussmf")) tipo = 5;
		else if (type.equals("gauss2mf")) tipo =6;
		else if (type.equals("sigmf")) tipo =7;
		else if (type.equals("gbellmf")) tipo =8;
		else if (type.equals("pimf")) tipo =9;
		else if (type.equals("psigmf")) tipo =10;
		else if (type.equals("dsigmf")) tipo =11;
		else if (type.equals("smf")) tipo =12;
		else if (type.equals("zmf")) tipo =13;
		else
		{	System.out.println(type+"it's not a defined type");
		return;}
		fuzzy.ChangeMFOutput((nO-1), nMF, tipo);
		
		}
		//comprobar tipo de fuzzy
		else if(fuzzy.getType()=="sugeno"){
			double[] params= new double[1];
			if (type.equals("constant"))
			{tipo =14;
			params[0]=1;			
			}
			else if (type.equals("linear")) 
			{tipo =15;
			params= new double[ins.length+1];
			for(int i=ins.length; i>=0;i++)
				params[i]=i;
			}
			else
			{	System.out.println(type+"it's not a defined type");
			return;}
			fuzzy.ChangeMFOutput((nO-1), nMF, tipo);
			fuzzy.ChangeOutputsMFparams(nO, nMF, params);
		
		}
		
	}
	
	public void SetInputMFName(int NI, int NMF, String name){
		fuzzy.setInputMFName(NI-1,NMF-1,name);
		
	}
	
	public void SetOutputMFName(int NI, int NMF, String name){
		fuzzy.setOutputMFName(NI-1,NMF-1,name);
	}
	
	public void ChangeType(String namef){
		if(fuzzy.getType().equals(namef))
		{post("Select a different type");
		return;}
		
		else{
		fuzzy.setType(namef);
		if(!namef.equals(fuzzy.getType()))
			{post("Just mandami and sugeno type are accepted");
			return;}
		for(int i=0; i<fuzzy.getNOutputs();i++)
		fuzzy.RemoveOutput(0);
		post("All outs were remove. One compatible output was created.");
		if(namef.equals("mandani"))
			newOutputs(3,0,1,"Default","trimf");
		else
			newOutputs(3,0,1,"Default","linear");
				
		}
		
		Actualize();
		System();
	}
	
	public void RemoveInput(float nF)
	{
		if (nF<=0 || nF>fuzzy.getNInputs())
		{	post("Not defined input");
		return;}
		else
			fuzzy.RemoveInput((int)(nF-1));
		
		Actualize();
		Ins();

	}
	
	public void RemoveOutput(float nF)
	{
		if (nF<=0 || nF>fuzzy.getNOutputs())
			{post("Not defined output");
			return;}
		else
			fuzzy.RemoveOutput((int)(nF-1));
		
		Actualize();
		Outs();
	}
	
	public void RemoveRule(float nF)
	{
		if ((nF-1)<0 || (nF-1)>fuzzy.getNRules())
			System.out.println("Not defined rule");
		else
			fuzzy.RemoveRule((int)(nF-1));
		
		post(fuzzy.rulesToString());
		
	}
	
	public void ChangeFile(String file){
		fuzzy= new FuzzySystem(file);
		Actualize();
		}
	
	public void SetAndMethod(String method){
		fuzzy.setAndMethod(method);
		System();
	}
	
	public void SetOrMethod(String method){
		fuzzy.setOrMethod(method);
		System();
	}
	
	public void SetImpMethod(String method){
		fuzzy.setImpMethod(method);
		System();
	}
	
	public void SetAggMethod(String method){
		fuzzy.setAggMethod(method);
		System();
	}
	
	public void SetDefuzzMethod(String method){
		fuzzy.setDefuzzMethod(method);
		System();
	}
	
	public void ChangeParamsMFInput(Atom[] params)
	{
		int NI=params[0].getInt();
		int NMF=params[1].getInt();
		
		if(NI<=0 || NI >fuzzy.getNInputs())
		{
			post("Undefined input number");
			return;
		}
		else
		{post("number of input "+NI);}
		if(NMF<=0 || NMF>fuzzy.getNMinput(NI-1))
		{
			post("Undefined MF number");
			return;
		}
		else
		{post("number of MF "+ NMF);}
		if((params.length-2)!=fuzzy.getNMparamsInput(NI-1,NMF-1))
		{
			post("Error: number of params "+ fuzzy.getNMinput(NI-1));
			return;
		}
		
		double[] params2= new double[params.length -2];
		for(int i=0; i<params2.length;i++)
		params2[i]=params[i+2].toDouble();
		fuzzy.setInputMFParams(NI-1,NMF-1,params2);
		post(fuzzy.inputToString(NI-1));
	}

	
	
	public void ChangeParamsMFOutput(Atom[] params)
	{
		int NI=params[0].getInt();
		int NMF=params[1].getInt();
		
		if(NI<=0 || NI >fuzzy.getNOutputs())
		{
			post("Undefined output number");
			return;
		}
		else
		{post("number of output "+NI);}
		if(NMF<=0 || NMF>fuzzy.getNMoutput(NI-1))
		{
			post("Undefined MF number");
			return;
		}
		else
		{post("number of MF "+ NMF);}
		if((params.length-2)!=fuzzy.getNMparamsoutput(NI-1,NMF-1))
		{
			post("Error: number of params "+ fuzzy.getNMoutput(NI-1));
			return;
		}
		
		double[] params2= new double[params.length -2];
		for(int i=0; i<params2.length;i++)
		params2[i]=params[i+2].toDouble();
		fuzzy.setOutputMFParams(NI-1,NMF-1,params2);
		post(fuzzy.outputToString(NI-1));
	}
	
	public void Save() throws IOException{
		fuzzy.Save();
		}
	
	public void Save(String file) throws IOException{
		fuzzy.Save(file);}
	
	public void Print(){
		print();
	}
	
	public void print(){
		System.out.println(fuzzy.toString());
	}
	
	public void System(){
		post(fuzzy.toPost());
	}
	
	public void Ins(){
		post(fuzzy.inputsToString());
	}
	
	public void Outs(){
		System.out.println(fuzzy.outputsToString());
	}
	
	
	public void NewRule(Atom[] data)
	{
	num++;	
	
		if(num%2!=0){
			test=true;
			insRule=new int[data.length];
		
			if(insRule.length!=fuzzy.getNInputs())
			{System.out.print("You nedd "+fuzzy.getNInputs()+" inputs");
			test=false;
			return;}
		for(int i=0; i<insRule.length;i++)
		{
			insRule[i]=data[i].getInt();
			if(Math.abs(insRule[i])<0||Math.abs(insRule[i])>fuzzy.getNMinput(i))
			{System.out.print("Invalid value for input"+(i+1) +", remember it must be between 1 and "+fuzzy.getNMinput(i));
			test=false;
			return;}					
			
			}			
		post("ins ok");
		}
		
		else {
			
			outsRule=new int[data.length-1];
		
			if(outsRule.length!=fuzzy.getNOutputs())
			{System.out.print("You nedd "+fuzzy.getNOutputs()+" outputs");
			test=false;
			return;}
		for(int i=0; i<outsRule.length;i++)
		{
			outsRule[i]=data[i].getInt();
			if(Math.abs(outsRule[i])<0||Math.abs(outsRule[i])>fuzzy.getNMoutput(i))
			{System.out.print("Invalid output, remember it must be betwenn 1 and "+fuzzy.getNMoutput(i));
			test=false;
			return;}
			}
		post("out ok");
		
		
			num=0;
		int op=0;
		if("AND".equals(data[outsRule.length].toString()))
			op=1;
		else if ("OR".equals(data[outsRule.length].toString()))
			op=2;
		else
		{
			System.out.println("Write AND or OR for operator");
			test=false;
			return;
		}
		
		post("operador ok");
		
		if(test){
			post(""+test);
		fuzzy.AddRule(insRule, outsRule, op);
		System.out.println(fuzzy.rulesToString());
		test=true;
		}
		}
		 
	}
	
	
	
	public void list(Atom[] data)
	{

		num++;	
		
		if(num%2!=0){
			test=true;
			insRule=new int[data.length];
		
			if(insRule.length!=fuzzy.getNInputs())
			{System.out.print("You nedd "+fuzzy.getNInputs()+" inputs");
			test=false;
			return;}
		for(int i=0; i<insRule.length;i++)
		{
			insRule[i]=data[i].getInt();
			if(Math.abs(insRule[i])<0||Math.abs(insRule[i])>fuzzy.getNMinput(i))
			{System.out.print("Invalid value for input"+(i+1) +", remember it must be between 1 and "+fuzzy.getNMinput(i));
			test=false;
			return;}					
			
			}			
		post("ins ok");
		}
		
		else {
			
			outsRule=new int[data.length-1];
		
			if(outsRule.length!=fuzzy.getNOutputs())
			{System.out.print("You nedd "+fuzzy.getNOutputs()+" outputs");
			test=false;
			return;}
		for(int i=0; i<outsRule.length;i++)
		{
			outsRule[i]=data[i].getInt();
			if(Math.abs(outsRule[i])<0||Math.abs(outsRule[i])>fuzzy.getNMoutput(i))
			{System.out.print("Invalid output, remember it must be betwenn 1 and "+fuzzy.getNMoutput(i));
			test=false;
			return;}
			}
		post("out ok");
		
		
			num=0;
		int op=0;
		if("AND".equals(data[outsRule.length].toString()))
			op=1;
		else if ("OR".equals(data[outsRule.length].toString()))
			op=2;
		else
		{
			System.out.println("Write AND or OR for operator");
			test=false;
			return;
		}
		
		post("operador ok");
		
		if(test){
			post(""+test);
		fuzzy.AddRule(insRule, outsRule, op);
		System.out.println(fuzzy.rulesToString());
		test=true;
		}
		}

	}
	

}
