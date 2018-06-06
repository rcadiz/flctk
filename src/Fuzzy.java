/**
* Fuzzy
* Implements a MaxMSP/PD external containing a Fuzzy System
* @author: Marie Gonzalez and Rodrigo F. Cadiz
* @version: 2.0
* 2018
*/

package flctk;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxSystem;

/**
*
*/
public class Fuzzy extends MaxObject {

	FuzzySystem fuzzy;
	double[] ins;
	double[] outs;
	int[] desiredIns;
	int[] desiredOuts;
	int[] preparedIns;
	int[] preparedOuts;
	int preparedOper = 1;
	double preparedWeight = 1.0;
	int[] insRule;
	int[] outsRule;
	JFileChooser fileChooser;
	float Num;
	int num=0;
	int num2=0;
	boolean test=true;
	boolean trainMode = false;

	/**
	 * Fuzzy empty constructor
	 * Returns an error
	 */
	public Fuzzy() {
		bail("No matching arguments. Fuzzy system must be created with either [numInputs, numOutputs] or [filename]\n");
	}


	/**
	 * Fuzzy typical constructor
	 * @param nInputs  number of inputs
	 * @param nOutputs number of outputs
	 */
	public Fuzzy(int nInputs,int nOutputs) {

		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

		fuzzy = new FuzzySystem();
		for(int i = 0; i < nInputs; i++) {
			newInputs(5,0,1,"Input " + (i+1), "trimf");
		}
		for (int i = 0 ; i < nOutputs; i++) {
			newOutputs(5,0,1,"Output " + (i+1), "trimf");
		}

		update();
		print();
	}

	/**
	 * Fuzzy constructor with filename
	 * @param filename fis file to read
	 */
	public Fuzzy(String filename) {

		if (MaxSystem.locateFile(filename) != null) {

			String localfile = this.getParentPatcher().getPath() + "/" + filename;

			if (localfile.equals(MaxSystem.locateFile(filename))) {
				fuzzy = new FuzzySystem();
				fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				update();
			}
			else {
				error("File " + filename + " must be on the same folder as this patch.");
			}
		}
		else {
			error("File " + filename + " could not be found.");
		}
	}

	private void update() {

		String sins = "";
		for(int i=0;i<fuzzy.getNInputs();i++) {
			sins+="f";
		}

		String souts = "";
		for(int i=0;i<fuzzy.getNOutputs();i++) {
			souts+="f";
		}

		declareTypedIO(sins,souts);

		ins = new double[fuzzy.getNInputs()];
		desiredIns = new int[fuzzy.getNInputs()];
		preparedIns = new int[fuzzy.getNInputs()];
		for (int i=0; i < preparedIns.length; i ++)
			preparedIns[i] = 1;

		outs = new double[fuzzy.getNOutputs()];
		desiredOuts = new int[fuzzy.getNOutputs()];
		for(int i=0 ; i < fuzzy.getNOutputs(); i ++)
			desiredOuts[i] = 0;
		preparedOuts = new int[fuzzy.getNOutputs()];
		for (int i=0; i < preparedOuts.length; i ++)
			preparedOuts[i] = 1;

		String[] inletAssist = new String[ins.length];
		for(int i=0; i<ins.length; i++) {
			String ss = "double input between "+fuzzy.getInMinRange(i)+" and "+fuzzy.getInMaxRange(i);
			inletAssist[i] = ss;
			System.out.println(ss);

		}

		setInletAssist(inletAssist);

		String[] outletAssist = new String[outs.length];
		for(int i=0; i<outs.length; i++)
				outletAssist[i] = "double output between "+fuzzy.getOutMinRange(i)+" and "+fuzzy.getOutMaxRange(i);

		setInletAssist(outletAssist);

	}

	public void prepareRule(Atom[] args) {

		if (args.length == (fuzzy.getNInputs() + fuzzy.getNOutputs() + 2)) {

			for (int i=0; i < fuzzy.getNInputs(); i ++ )
				preparedIns[i] = args[i].toInt();

			for (int i=fuzzy.getNInputs(); i < fuzzy.getNInputs() + fuzzy.getNOutputs() ; i ++ )
				preparedOuts[i - fuzzy.getNInputs()] = args[i].toInt();

			String oper = args[args.length-2].toString();

			if("AND".equals(oper))
				preparedOper = 1;
			else if ("OR".equals(oper))
				preparedOper = 2;

			preparedWeight = args[args.length-1].toFloat();

		}
		else {
			bail("List should be of size number of inputs + number of outputs + 2");
		}

	}

	/**
	 * Adds a fuzzy rule, based on prepareRule
	 */
	public void addRule() {

		if (trainMode) {
			for (int i=0; i < fuzzy.getNInputs(); i++) {
				FuzzyVar var  = fuzzy.getInput(i);
				double[] deg = var.getDegreesFromScalar(ins[i]);
				int maxIndex = 0;
				double maxValue = 0.0;
				for(int j=0; j < deg.length; j++) {
					if (deg[j] > maxValue) {
						maxValue = deg[j];
						maxIndex = j+1;
					}
				}
				desiredIns[i] = maxIndex;
			}

			for (int i = 0; i < desiredIns.length ; i ++) {
					desiredIns[i] *= preparedIns[i];
			}

			for (int i = 0; i < desiredOuts.length ; i ++) {
					desiredOuts[i] *= preparedOuts[i];
			}

			fuzzy.addRule(desiredIns, desiredOuts, preparedOper, preparedWeight);
			post(fuzzy.rulesToString());
		}
		else {
			post("Not in train mode");
		}
	}

	public void setDesiredOutputs(Atom[] args) {

		if (trainMode) {
			int nouts = fuzzy.getNOutputs();
			if (nouts == args.length) {
				for(int i=0; i < args.length; i++) {
					FuzzyVar out = fuzzy.getOutput(i);
					double[] deg = out.getDegreesFromScalar(args[i].toFloat());
					int maxIndex = 0;
					double maxValue = 0.0;
					for(int j=0; j<deg.length; j++) {

						if (deg[j] > maxValue) {
							maxValue = deg[j];
							maxIndex = j+1;
						}
					}
					desiredOuts[i] = maxIndex;
				}
			}
			else {
					post("List must match number of outputs\n");
			}
		}
		else {
			post("Not in train mode\n");
		}
	}

	public void bang() {

		if (!trainMode) {

			outs = fuzzy.eval(ins);
			//post("evaluating");

			for(int i=0; i < outs.length;i++) {
				outlet(i, outs[i]);
			}
		}

	}

	public void setSystemName(String name) {
		fuzzy.setName(name);
		//systemToPost();
	}

	public void setInputName(String Name, int n) {
		fuzzy.setInputName(Name, n-1);
		inputsToPost();
		}

	public void setOutputName(String Name, int n) {
		fuzzy.setOutputName(Name, n-1);
		outputsToPost();
		}

	public void setTrainMode(boolean mode) {
		trainMode = mode;
	}

	public void inlet(double i) {
		int int_o = getInlet();
		ins[int_o] = i;
		//if(int_o == 0)
		bang();
	}

	public void inlet(float i) {
		int int_o = getInlet();
		ins[int_o] = i;
		//if(int_o == 0)
		bang();
	}

	public void Read() {
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
		    changeFile(selectedFile.getAbsolutePath());
		}
	}

	public void open() {

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
		    changeFile(selectedFile.getAbsolutePath());
		}
	}

	public void newInputs(int NF, float limit1, float limit2, String name, String type){

		FuzzyVar FV = new FuzzyVar(NF,name);
		if (limit1 < limit2) {
			FV.setRangeMin(limit1);
			FV.setRangeMax(limit2);
		}
		else {
			FV.setRangeMin(limit2);
			FV.setRangeMax(limit1);
		}

		double dif = FV.getRange()/(NF-1);
		double min = FV.getRangeMin();

		for(int i=1; i<=NF; i++) {
			double[] values = new double[3];
			values[0]=min-dif;
			values[1]=min;
			values[2]=min+dif;
			FV.addMFunction("MF"+i, "trimf", values);
			min+=dif;
		}

		if(!type.equals("trimf")) {
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
			 else {
				 post("Invalid type of function");
			 return;
		 	}

			for(int i=0; i< NF ; i++)
				FV.getMFunctionAt(i).setType(t);

		}

		fuzzy.addInput(FV);

		update();
		//inputsToPost();

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

		if(!type.equals("trimf")) {
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
			 else {
				 post("Invalid type of function");
			 	return;
		 	}

			for(int i=0; i<NF;i++)
				FV.getMFunctionAt(i).setType(t);
		}

		fuzzy.addOutput(FV);

		//update();
		//outputsToPost();
	}

	public void changeInputType(float nI, float nMF, String type) {
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

		else {
			System.out.println(type+"it's not a defined type");
			return;
		}

		fuzzy.changeMFInput((int)(nI-1), (int)nMF, tipo);
		inputsToPost();
	}

	public void changeOutputType(int nO, int nMF, String type) {
		int tipo=0;
		if(fuzzy.getType()=="mamdani"){
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
		fuzzy.changeMFOutput((nO-1), nMF, tipo);

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
			fuzzy.changeMFOutput((nO-1), nMF, tipo);
			fuzzy.changeOutputsMFparams(nO, nMF, params);

		}

	}

	public void setInputMFName(int NI, int NMF, String name){
		fuzzy.setInputMFName(NI-1,NMF-1,name);

	}

	public void setOutputMFName(int NI, int NMF, String name){
		fuzzy.setOutputMFName(NI-1,NMF-1,name);
	}

	public void changeType(String namef){
		if(fuzzy.getType().equals(namef))
		{post("Select a different type");
		return;}

		else{
		fuzzy.setType(namef);
		if(!namef.equals(fuzzy.getType()))
			{post("Just mandami and sugeno type are accepted");
			return;}
		for(int i=0; i<fuzzy.getNOutputs();i++)
		fuzzy.removeOutput(0);
		post("All outs were remove. One compatible output was created.");
		if(namef.equals("mamdani"))
			newOutputs(3,0,1,"Default","trimf");
		else
			newOutputs(3,0,1,"Default","linear");

		}

		update();
		systemToPost();
	}

	public void removeInput(float nF) {
		if (nF<=0 || nF>fuzzy.getNInputs())
		{	post("Not defined input");
		return;}
		else
			fuzzy.removeInput((int)(nF-1));

		update();
		inputsToPost();

	}

	public void removeOutput(float nF) {
		if (nF<=0 || nF>fuzzy.getNOutputs())
			{post("Not defined output");
			return;}
		else
			fuzzy.removeOutput((int)(nF-1));

		update();
		outputsToPost();
	}

	public void removeRule(float nF) {
		if ((nF-1)<0 || (nF-1)>fuzzy.getNRules())
			System.out.println("Not defined rule");
		else
			fuzzy.removeRule((int)(nF-1));

		System.out.println(fuzzy.rulesToString());

	}

	public void changeFile(String file){
		fuzzy= new FuzzySystem(file);
		update();
		}

	public void setAndMethod(String method){
		fuzzy.setAndMethod(method);
		systemToPost();
	}

	public void setOrMethod(String method){
		fuzzy.setOrMethod(method);
		systemToPost();
	}

	public void setImpMethod(String method){
		fuzzy.setImpMethod(method);
		systemToPost();
	}

	public void setAggMethod(String method){
		fuzzy.setAggMethod(method);
		systemToPost();
	}

	public void setDefuzzMethod(String method){
		fuzzy.setDefuzzMethod(method);
		systemToPost();
	}

	public void changeParamsMFInput(Atom[] params) {
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

	public void changeParamsMFOutput(Atom[] params) {
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

	public void saveToFile() throws IOException{

		Atom[] a = new Atom[1];
		a[0] = Atom.newAtom("");
		MaxSystem.sendMessageToBoundObject("fismsg",fuzzy.fisToString(),a);

		//fuzzy.save();
		}

	public void saveToFile(String file) throws IOException{
		fuzzy.save(file);}

	public void Print(){
		print();
	}

	public void print(){
		System.out.println(fuzzy.toString());
	}

	public void systemToPost(){
		post(fuzzy.toPost());
	}

	private void inputsToPost() {
		System.out.println(fuzzy.inputsToString());
	}

	private void outputsToPost() {
		System.out.println(fuzzy.outputsToString());
	}

	public void newRule(Atom[] data) {

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
		fuzzy.addRule(insRule, outsRule, op, 1.0);
		System.out.println(fuzzy.rulesToString());
		test=true;
		}
		}

	}

	public void list(Atom[] data) {

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
		fuzzy.addRule(insRule, outsRule, op, 1.0);
		System.out.println(fuzzy.rulesToString());
		test=true;
		}
		}

	}

	public void showInputs() {
		inputsToPost();
	}

	public void showOutputs() {
		outputsToPost();
	}

	public void showRules() {
		System.out.println(fuzzy.rulesToString());
	}

}
