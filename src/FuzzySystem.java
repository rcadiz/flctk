/**
* FuzzySystem
* Defines a complete Fuzzy System
* @author: Marie Gonzalez and Rodrigo F. Cadiz
* @version: 2.0
* 2018
*/

package flctk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FuzzySystem {

	private FuzzyVar[] inputs;
	private FuzzyVar[] outputs;
	private FuzzyRule[] rules;
	private double[] DOF;
	private boolean[] fired;
	private FisReader fr;
	private String filename;
	private double version;
	private int ninputs;
	private int noutputs;
	private int nrules;
	private String name;
	private String type;
	private String andmethod;
	private String ormethod;
	private String impmethod;
	private String aggmethod;
	private String defuzzmethod;

	public FuzzySystem(){
		initSystem();
	}

	/**
	* Fuzzy System from File
	* @param fname
	* file name
	*/
	public FuzzySystem(String fname) {
		initSystem();
		filename = fname;
		readFis(filename);
		DOF = new double[nrules];
	}

	private void initSystem()	{
		name = "Empty system";
		version = 0.0;
		type = "mamdani";
		ninputs = 0;
		noutputs = 0;
		nrules = 0;
		andmethod = "min";
		ormethod = "max";
		impmethod = "min";
		aggmethod = "max";
		defuzzmethod = "centroid";
		filename = "";
		inputs = new FuzzyVar[0];
		outputs = new FuzzyVar[0];
		rules = new FuzzyRule[0];
		fired = new boolean[0];
	}

	public void addInput(FuzzyVar newinput) {
		FuzzyVar[] aux= new FuzzyVar[inputs.length+1];
		for(int i=0; i<ninputs;i++)
		{aux[i]=inputs[i];}
		aux[ninputs]=newinput;
		inputs=aux;

		for(int i=0; i<rules.length;i++)
		rules[i].addInput();
		ninputs++;
	}

	public void addOutput(FuzzyVar newoutput) {
		noutputs++;
		FuzzyVar[] aux= new FuzzyVar[outputs.length+1];
		for(int i=0; i<outputs.length;i++)
		{aux[i]=outputs[i];}
		aux[aux.length-1]=newoutput;
		outputs=aux;
		for(int i=0; i<rules.length;i++)
		rules[i].addOutput();
	}

	public void removeInput(int index) {
		if(index>=0 && index<inputs.length) {
			ninputs--;
			FuzzyVar[] aux = new FuzzyVar[inputs.length-1];

			for(int i=0; i<index;i++) {
				aux[i]=inputs[i];
			}
			for(int i=index; i<aux.length;i++) {
				aux[i]=inputs[i+1];
			}

			inputs=aux;
			for(int i=0; i<rules.length; i++)
			rules[i].removeInput(index);
		}

	}

	public void removeOutput(int index) {
		if(index>=0 && index<outputs.length) {
			noutputs--;
			FuzzyVar[] aux= new FuzzyVar[outputs.length-1];

			for(int i=0; i<index;i++)
			{aux[i]=outputs[i];}
			for(int i=index; i<aux.length;i++)
			{aux[i]=outputs[i+1];}

			outputs=aux;

			for(int i=0; i<rules.length; i++)
			rules[i].removeOutput(index);
		}

	}

	public void changeMFInput(int index, int MF, int newtype){
		if(index<0 || index>=ninputs){
			System.out.println("input out of range");
			return;
		}
		inputs[index].getMFunctionAt(MF).setType(newtype);
	}

	public void changeMFOutput(int index, int MF, int newtype) {
		if(index<0 || index>=noutputs){
			System.out.println("output out of range");
			return;
		}
		outputs[index].getMFunctionAt(MF).setType(newtype);
	}

	public void changeInputsMFparams(int index, int MF, double[] params){
		inputs[index].getMFunctionAt(MF).setParameters(params);
	}

	public void changeOutputsMFparams(int index, int MF, double[] params){
		outputs[index].getMFunctionAt(MF).setParameters(params);
	}

	public int getNMinput(int in){
		if(in<0|| in>=inputs.length)
		return 0;
		else
		return inputs[in].getNMFunctions();
	}

	public int getNMparamsInput(int in, int j){
		if(in<0|| in>=inputs.length)
		return 0;
		else
		return inputs[in].getMFunctionAt(j).GetNParams();
	}

	public int getNMoutput(int in){
		if(in<0|| in>outputs.length)
		return 0;
		else
		return outputs[in].getNMFunctions();
	}

	public int getNMparamsoutput(int in, int j){
		if(in<0|| in>=outputs.length)
		return 0;
		else
		return outputs[in].getMFunctionAt(j).GetNParams();
	}

	/**
	*
	* @param Generates DOF vector
	*/
	public void DOFvector(double[] ins) {

		double[] d;
		double max,prod;
		double min,probor;

		DOF = new double[rules.length];

		for (int i=0;i<rules.length;i++) {

			max = -10;
			prod = 1;
			min = 10;
			probor = 0;

			for(int j=0;j<inputs.length;j++) {
				d = inputs[j].getDegreesFromScalar(ins[j]);
				//for(int k=0;k<d.length;k++)
				//	System.out.println("d " + d[k] + " for input " + j + " rule " + i);

				int mf = rules[i].getMFAtInput(j);

				double deg;

				if(mf != 0) {
					deg = d[Math.abs(mf)-1];
					if(mf < 0)
					deg = 1-deg;
				}
				else
				deg = -1;

				//System.out.println("Degree at rule " + i + " and input " + j + ": " + deg);

				if (deg > max && deg != -1)
				max = deg;
				if (deg < min && deg != -1)
				min = deg;

				if (deg != -1) {
					prod *= deg;
					probor = probor+deg-deg*probor;
				}
			}

			if(rules[i].getOperator() == FuzzyRule.OpOR) {
				if(ormethod.equals("max"))
				DOF[i] = max;
				else
				DOF[i] = probor;
			}
			else {
				if(andmethod.equals("min"))
				DOF[i] = min;
				else
				DOF[i] = prod;
			}

			if (DOF[i] == 10 || DOF[i] == -10)
			DOF[i] = -1;

		}
	}

	// Check if there is at least one rule that fired
	public boolean checkFiring() {
		boolean result = false;
		for (int i=0; i<DOF.length; i++) {
			if (DOF[i] != 0 && DOF[i] != -1) {
				result = true;
				break;
			}
		}
		return result;
	}

	// Check if there is at least one rule that fired
	public int numFired() {
		int result = 0;
		for (int i=0; i<DOF.length; i++) {
			if (DOF[i] != 0 && DOF[i] != -1) {
				result++;
			}
		}
		return result;
	}

	public boolean outputFired(int out) {
		return fired[out];
	}

	// Check a given value is within a given input range
	public boolean valueInInputRange( double value,int index) {
		return inputs[index].valueInRange(value);
	}

	public FuzzySet fuzzyOutput(int out) {

		FuzzySet s = new FuzzySet(outputs[out].getSize(),outputs[out].getRangeMin(),outputs[out].getRangeMax());
		s.constant(0.0);

		boolean test = false;

		for(int i=0;i<rules.length;i++) {
			if (DOF[i] != 0 && DOF[i] != -1) {
				int MF = rules[i].getMFAtOutput(out);
				if (MF != 0) {
					FuzzySet t = outputs[out].getMFunctionAt(Math.abs(MF)-1).copy();
					if (MF<0)
					t=t.NOT();
					if(impmethod.equals("min"))
					t = t.limit(DOF[i]);
					else
					t.scale(DOF[i]);

					if (rules[i].getWeight()!=1)
					t.scale(rules[i].getWeight());

					if(aggmethod.equals("max"))
					s = s.OR(t);
					else if(aggmethod.equals("probor"))
					s = s.AlgebraicAdd(t);
					else if(aggmethod.equals("sum"))
					s = s.ADD(t);
					test = true;
				}
			}
		}

		fired[out] = test;

		//System.out.println(s.plot());

		return s;
	}

	public double fuzzyOutputSugeno(int out, double[] ins) {


		boolean test = false;

		double sum=0;
		double total=0;
		if(defuzzmethod.equals("wtaver"))
		for(int i=0; i<rules.length;i++)
		total+=DOF[i];
		else
		total=1;

		for(int i=0;i<rules.length;i++) {
			if (DOF[i] != 0 && DOF[i] != -1) {
				int MF = rules[i].getMFAtOutput(out);
				if (MF != 0) {
					sum+=DOF[i]*outputs[out].getMFunctionAt(MF-1).getSugenoVal(ins);
				}
			}
		}

		fired[out] = test;

		return sum/total;
	}

	public double[] eval( double[] ins) {

		fired = new boolean[noutputs];
		double[] outs = new double[noutputs];
		int n = noutputs-1;

		if(ins.length == ninputs) {

			//try range
			for(int i=0; i<ins.length;i++) {
				if(ins[i]>inputs[i].getRangeMax()) {
					ins[i]=inputs[i].getRangeMax();
					System.out.println("Input out of range");
				}

				if(ins[i]<inputs[i].getRangeMin()){
					ins[i]=inputs[i].getRangeMin();
					System.out.println("Input out of range");
				}
			}

			DOFvector(ins);

			//for(int i=0;i<DOF.length;i++)
			//		System.out.println("DOF " + i + " " + DOF[i]);

			for(int i=n;i>=0;--i) {

				int j=n-i;

				if(type.equals("mamdani")) {
					FuzzySet temp = fuzzyOutput(j);
					if (defuzzmethod.equals("centroid")) {
						outs[j] = temp.getCentroid();
					}
					else if (defuzzmethod.equals("bisector")) {
						outs[j] = temp.getBisector();
					}
					else if (defuzzmethod.equals("lom")) {
						outs[j] = temp.getLOM();
					}
					else if (defuzzmethod.equals("som")) {
						outs[j] = temp.getSOM();
					}
					else if (defuzzmethod.equals("mom")) {
						outs[j] = temp.getMOM();
					}

				}

				else if(type.equals("sugeno")) {
					outs[j]=fuzzyOutputSugeno(j,ins);
				}
			}

		}

		else
		System.out.println("Error: FuzzySystem '"+name +"' uses "+inputs.length+" inputs" );

		//System.out.println("N Fired : "  + numFired());
		return outs;

	}

	public float [] evalFloat(float[] ins) {

		double[] ins2= new double[ins.length];
		for(int i=0; i<ins.length;i++)
		ins2[i]=(double)ins[i];

		double[] outs= eval(ins2);

		float[] outs2= new float[outs.length];
		for(int i=0; i<outs.length;i++)
		outs2[i]=(float)outs[i];

		return outs2;
	}

	public FuzzyVar getInput(int index) {
		return inputs[index];
	}

	public FuzzyVar getOutput(int index) {
		return outputs[index];
	}

	public  int getNInputs(){
		return ninputs;
	}

	public int getNOutputs() {
		return noutputs;
	}

	public int getNRules() {
		return nrules;
	}

	public String getType(){
		return type;
	}

	public String rulesToString() {
		String ss = "[RULES]";
		for(int i=0;i<nrules;i++) {
			ss = ss.concat("\n" +(i+1) + " - " + rules[i].toString());
		}

		return ss;
	}

	public String toPost() {
		String ss;
		ss = "----------------------------------\n[FUZZY SYSTEM]\nName : "+name ;
		ss = ss.concat("\n Type : " +type +"\nVersion : " +version +"\nNumber of inputs : " +ninputs );
		ss = ss.concat("\n Number of outputs : " +noutputs +"\nNumber of rules : " +nrules );
		ss = ss.concat( "\n And method : " +andmethod );
		ss = ss.concat("\n Or method : " +ormethod + "\nImplication method : "+impmethod );
		ss = ss.concat("\n Aggregation method : " +aggmethod + "\nDefuzzification method : " +defuzzmethod);
		ss = ss.concat("\n----------------------------------" );
		return ss;
	}

	public String inputToString(int index) {
		if(index>=0 && index<inputs.length)
		return inputs[index].toString(1);

		else
		return "";
	}

	public String inputsToString() {
		String ss= "";
		ss=ss.concat("\n[INPUTS]");
		for(int i=0;i<ninputs;i++)
		ss=ss.concat("\n"+inputs[i].toString(1));
		ss=ss.concat("\n ----------------------------------" );
		return ss;
	}

	public String outputToString(int index){

		if(index>=0 && index<outputs.length)
		return outputs[index].toString(1);

		else
		return "";
	}

	public String outputsToString() {
		String ss= "";
		ss=ss.concat("\n[OUTPUTS]");
		for(int i=0;i<noutputs;i++)
		ss=ss.concat("\n"+outputs[i].toString(1));
		ss=ss.concat("\n ----------------------------------" );
		return ss;
	}

	public FuzzySystem Copy ( FuzzySystem arg) {
		this.name = arg.name;
		this.version = arg.version;
		this.type = arg.type;
		this.ninputs = arg.ninputs;
		this.noutputs = arg.noutputs;
		this.nrules = arg.nrules;
		this.andmethod = arg.andmethod;
		this.ormethod = arg.ormethod;
		this.impmethod = arg.impmethod;
		this.aggmethod = arg.aggmethod;
		this.defuzzmethod = arg.defuzzmethod;
		this.filename = arg.filename;
		this.inputs = arg.inputs;
		this.outputs = arg.outputs;
		this.rules = arg.rules;
		this.fired = arg.fired;

		return this;
	}

	private void readFis(String fname) {

		fr = new FisReader(fname);
		ninputs = fr.getNInputs();
		noutputs = fr.getNOutputs();
		nrules = fr.getNRules();
		inputs = fr.getInputs();
		outputs = fr.getOutputs();
		rules = fr.getRules();
		version = fr.getVersion();
		name = fr.getName();
		type = fr.getType();
		andmethod = fr.getAndMethod();
		ormethod = fr.getOrMethod();
		impmethod = fr.getImpMethod();
		aggmethod = fr.getAggMethod();
		defuzzmethod = fr.getDefuzzMethod();

	}

	public String toString() {
		String ss;
		ss = new String( "----------------------------------\n" );
		ss=ss.concat( "[FUZZY SYSTEM] \n");
		ss=ss.concat("Name : "+name);
		ss=ss.concat("\nType : " +type);
		ss=ss.concat("\nVersion : "+version);
		ss=ss.concat("\nNumber of inputs : "+ninputs);
		ss=ss.concat("\nNumber of outputs : " +noutputs);
		ss=ss.concat("\nNumber of rules : " +nrules );
		ss=ss.concat("\nAnd method : " +andmethod );
		ss=ss.concat("\nOr method : " +ormethod );
		ss=ss.concat("\nImplication method : " +impmethod );
		ss=ss.concat("\nAggregation method : " +aggmethod );
		ss=ss.concat("\nDefuzzification method : " +defuzzmethod );
		ss=ss.concat("\n----------------------------------");

		ss=ss.concat("\n[INPUTS]");
		for(int i=0;i<ninputs;i++)
		ss=ss.concat("\n"+inputs[i].toString(1));
		ss=ss.concat("\n ----------------------------------" );
		ss=ss.concat("\n[OUTPUTS]");
		for(int i=0;i<noutputs;i++)
		ss=ss.concat("\n"+outputs[i].toString(1));
		ss=ss.concat("\n----------------------------------" );
		ss=ss.concat("\n[RULES]");
		for(int i=0;i<nrules;i++)
		ss=ss.concat("\n"+rules[i].toString());

		return ss;
	}

	public void save() throws IOException{
		version++;
		if (filename == null) {
			filename = "" + name + ".fis";
		}

		File file = new File (filename);
		FileWriter FW = new FileWriter(file);
		String add = new String ("");
		add = "[System]\nName='"+name+"'\nType='"+type+"'\nVersion="+version+"\nNumInputs="+ninputs+"\n";
		add = add.concat("NumOutputs="+noutputs+"\nNumRules="+nrules+"\nAndMethod='"+andmethod+"'\n");
		add = add.concat("OrMethod='"+ormethod+"'\nImpMethod='"+impmethod+"'\nAggMethod='"+aggmethod+"'\nDefuzzMethod='"+defuzzmethod+"'");

		FW.write(add);
		for(int i=0; i<ninputs;i++) {
			add = "\n \n";
			add = add.concat("[Input"+(i+1)+"]\nName='"+inputs[i].getName()+"'\nRange=["+inputs[i].getRangeMin()+" "+inputs[i].getRangeMax()+"]\n");
			add = add.concat("NumMFs="+inputs[i].getNMFunctions()+"\n");
			for(int j=0; j<inputs[i].getNMFunctions();j++) {
				String tipo="trimf";
				if ( inputs[i].getMFunctionAt(j).getType()== FuzzySet.trapezoidal)
				tipo="trapmf";
				else if (  inputs[i].getMFunctionAt(j).getType()==FuzzySet.triangular)
				tipo="trimf";
				else if ( inputs[i].getMFunctionAt(j).getType()==  FuzzySet.gaussian)
				tipo="gaussmf";
				else if (inputs[i].getMFunctionAt(j).getType()==FuzzySet.gaussian2)
				tipo="gauss2mf";

				add=add.concat("MF"+(j+1)+"='"+inputs[i].getMFunctionAt(j).getLabel()+"':'"+tipo+"',[");

				for(int k=0; k<inputs[i].getMFunctionAt(j).getParameters().length;k++) {
					if(k!=0)
					add=add.concat(" ");
					add=add.concat(""+inputs[i].getMFunctionAt(j).getParameters()[k]);
				}
				add=add.concat("]\n");
			}
			FW.write(add);
		}

		for(int i=0; i<noutputs;i++) {
			add="\n \n";
			add=add.concat("[Output"+(i+1)+"]\nName='"+outputs[i].getName()+"'\nRange=["+outputs[i].getRangeMin()+" "+outputs[i].getRangeMax()+"]\n");
			add=add.concat("NumMFs="+outputs[i].getNMFunctions()+"\n");
			for(int j=0; j<outputs[i].getNMFunctions();j++) {
				String tipo="trimf";
				if ( outputs[i].getMFunctionAt(j).getType()== FuzzySet.trapezoidal)
				tipo="trapmf";
				else if (  outputs[i].getMFunctionAt(j).getType()==FuzzySet.triangular)
				tipo="trimf";
				else if ( outputs[i].getMFunctionAt(j).getType()==  FuzzySet.gaussian)
				tipo="gaussmf";
				else if (outputs[i].getMFunctionAt(j).getType()==FuzzySet.gaussian2)
				tipo="gauss2mf";

				add=add.concat("MF"+(j+1)+"='"+outputs[i].getMFunctionAt(j).getLabel()+"':'"+tipo+"',[");

				for(int k=0; k<outputs[i].getMFunctionAt(j).getParameters().length;k++) {
					if(k!=0)
					add=add.concat(" ");
					add=add.concat(""+outputs[i].getMFunctionAt(j).getParameters()[k]);
				}
				add=add.concat("]\n");
			}
			FW.write(add);
		}

		FW.write("\n\n[Rules]\n");
		for(int i=0; i<nrules;i++)
		{
			add = "";
			for(int j=0; j<rules[i].getInputs().length; j++)
			{
				if(j!=0)
				add = add.concat(" ");
				add = add.concat(""+rules[i].getInputs()[j]);
			}
			add = add.concat(", ");
			for(int j=0; j<rules[i].getOutputs().length; j++)
			{
				if(j!=0)
				add = add.concat(" ");
				add = add.concat(""+rules[i].getOutputs()[j]);
			}
			add =	add.concat(" ("+rules[i].getWeight()+") : "+rules[i].getOperator());


			FW.write(add+"\n");
		}

		FW.close();

		System.out.println("The system "+name+" was saved in "+ file.getPath());

	}

	public void save(String newfile) throws IOException{

		version++;

		if(!newfile.endsWith(".fis"));
		newfile=newfile.concat(".fis");
		File file= new File (newfile);
		FileWriter FW= new FileWriter(file);
		String add= new String ("");
		add="[System]\nName='"+name+"'\nType='"+type+"'\nVersion="+version+"\nNumInputs="+ninputs+"\n";
		add=add.concat("NumOutputs="+noutputs+"\nNumRules="+nrules+"\nAndMethod='"+andmethod+"'\n");
		add=add.concat("OrMethod='"+ormethod+"'\nImpMethod='"+impmethod+"'\nAggMethod='"+aggmethod+"'\nDefuzzMethod='"+defuzzmethod+"'");

		FW.write(add);
		for(int i=0; i<ninputs;i++)
		{
			add="\n \n";
			add=add.concat("[Input"+(i+1)+"]\nName='"+inputs[i].getName()+"'\nRange=["+inputs[i].getRangeMin()+" "+inputs[i].getRangeMax()+"]\n");
			add=add.concat("NumMFs="+inputs[i].getNMFunctions()+"\n");
			for(int j=0; j<inputs[i].getNMFunctions();j++)
			{
				String tipo="trimf";
				if ( inputs[i].getMFunctionAt(j).getType()== FuzzySet.trapezoidal)
				tipo="trapmf";
				else if (  inputs[i].getMFunctionAt(j).getType()==FuzzySet.triangular)
				tipo="trimf";
				else if ( inputs[i].getMFunctionAt(j).getType()==  FuzzySet.gaussian)
				tipo="gaussmf";
				else if (inputs[i].getMFunctionAt(j).getType()==FuzzySet.gaussian2)
				tipo="gauss2mf";

				add=add.concat("MF"+(j+1)+"='"+inputs[i].getMFunctionAt(j).getLabel()+"':'"+tipo+"',[");

				for(int k=0; k<inputs[i].getMFunctionAt(j).getParameters().length;k++)
				{if(k!=0)
					add=add.concat(" ");
					add=add.concat(""+inputs[i].getMFunctionAt(j).getParameters()[k]);}
					add=add.concat("]\n");
				}
				FW.write(add);
			}

			for(int i=0; i<noutputs;i++) {
				add="\n \n";
				add=add.concat("[Output"+(i+1)+"]\nName='"+outputs[i].getName()+"'\nRange=["+outputs[i].getRangeMin()+" "+outputs[i].getRangeMax()+"]\n");
				add=add.concat("NumMFs="+outputs[i].getNMFunctions()+"\n");
				for(int j=0; j<outputs[i].getNMFunctions();j++)
				{
					String tipo="trimf";
					if ( outputs[i].getMFunctionAt(j).getType()== FuzzySet.trapezoidal)
					tipo="trapmf";
					else if (  outputs[i].getMFunctionAt(j).getType()==FuzzySet.triangular)
					tipo="trimf";
					else if ( outputs[i].getMFunctionAt(j).getType()==  FuzzySet.gaussian)
					tipo="gaussmf";
					else if (outputs[i].getMFunctionAt(j).getType()==FuzzySet.gaussian2)
					tipo="gauss2mf";

					add=add.concat("MF"+(j+1)+"='"+outputs[i].getMFunctionAt(j).getLabel()+"':'"+tipo+"',[");

					for(int k=0; k<outputs[i].getMFunctionAt(j).getParameters().length;k++) {
						if(k!=0)
						add=add.concat(" ");
						add=add.concat(""+outputs[i].getMFunctionAt(j).getParameters()[k]);
					}
					add=add.concat("]\n");
				}
				FW.write(add);
			}

			FW.write("\n\n[Rules]\n");
			for(int i=0; i<nrules;i++) {
				add = "";
				for(int j=0; j<rules[i].getInputs().length; j++) {
					if (j!=0)
					add=add.concat(" ");
					add=add.concat(""+rules[i].getInputs()[j]);
				}
				add=add.concat(", ");
				for(int j=0; j<rules[i].getOutputs().length; j++) {
					if(j!=0)
					add = add.concat(" ");
					add = add.concat(""+rules[i].getOutputs()[j]);
				}
				add =	add.concat(" ("+rules[i].getWeight()+") : "+rules[i].getOperator());


				FW.write(add+"\n");
			}

			FW.close();

			System.out.println("The system "+name+" was save in "+ file.getPath());

		}


		public String fisToString() {
			String add = new String ("");
			add = "[System]\nName='"+name+"'\nType='"+type+"'\nVersion="+version+"\nNumInputs="+ninputs+"\n";
			add = add.concat("NumOutputs="+noutputs+"\nNumRules="+nrules+"\nAndMethod='"+andmethod+"'\n");
			add = add.concat("OrMethod='"+ormethod+"'\nImpMethod='"+impmethod+"'\nAggMethod='"+aggmethod+"'\nDefuzzMethod='"+defuzzmethod+"'");

			for(int i=0; i<ninputs;i++) {
				add = add.concat("\n \n");
				add = add.concat("[Input"+(i+1)+"]\nName='"+inputs[i].getName()+"'\nRange=["+inputs[i].getRangeMin()+" "+inputs[i].getRangeMax()+"]\n");
				add = add.concat("NumMFs="+inputs[i].getNMFunctions()+"\n");
				for(int j=0; j<inputs[i].getNMFunctions();j++) {
					String tipo="trimf";
					if ( inputs[i].getMFunctionAt(j).getType()== FuzzySet.trapezoidal)
					tipo="trapmf";
					else if (  inputs[i].getMFunctionAt(j).getType()==FuzzySet.triangular)
					tipo="trimf";
					else if ( inputs[i].getMFunctionAt(j).getType()==  FuzzySet.gaussian)
					tipo="gaussmf";
					else if (inputs[i].getMFunctionAt(j).getType()==FuzzySet.gaussian2)
					tipo="gauss2mf";

					add=add.concat("MF"+(j+1)+"='"+inputs[i].getMFunctionAt(j).getLabel()+"':'"+tipo+"',[");

					for(int k=0; k<inputs[i].getMFunctionAt(j).getParameters().length;k++) {
						if(k!=0)
						add=add.concat(" ");
						add=add.concat(""+inputs[i].getMFunctionAt(j).getParameters()[k]);
					}
					add=add.concat("]\n");
				}

			}

			for(int i=0; i<noutputs;i++) {
				add= add.concat("\n \n");
				add=add.concat("[Output"+(i+1)+"]\nName='"+outputs[i].getName()+"'\nRange=["+outputs[i].getRangeMin()+" "+outputs[i].getRangeMax()+"]\n");
				add=add.concat("NumMFs="+outputs[i].getNMFunctions()+"\n");
				for(int j=0; j<outputs[i].getNMFunctions();j++) {
					String tipo="trimf";
					if ( outputs[i].getMFunctionAt(j).getType()== FuzzySet.trapezoidal)
					tipo="trapmf";
					else if (  outputs[i].getMFunctionAt(j).getType()==FuzzySet.triangular)
					tipo="trimf";
					else if ( outputs[i].getMFunctionAt(j).getType()==  FuzzySet.gaussian)
					tipo="gaussmf";
					else if (outputs[i].getMFunctionAt(j).getType()==FuzzySet.gaussian2)
					tipo="gauss2mf";

					add=add.concat("MF"+(j+1)+"='"+outputs[i].getMFunctionAt(j).getLabel()+"':'"+tipo+"',[");

					for(int k=0; k<outputs[i].getMFunctionAt(j).getParameters().length;k++) {
						if(k!=0)
						add=add.concat(" ");
						add=add.concat(""+outputs[i].getMFunctionAt(j).getParameters()[k]);
					}
					add=add.concat("]\n");
				}

			}

			add = add.concat("\n\n[Rules]\n");
			for(int i=0; i<nrules;i++)
			{
				add = add.concat("");
				for(int j=0; j<rules[i].getInputs().length; j++)
				{
					if(j!=0)
					add = add.concat(" ");
					add = add.concat(""+rules[i].getInputs()[j]);
				}
				add = add.concat(", ");
				for(int j=0; j<rules[i].getOutputs().length; j++)
				{
					if(j!=0)
					add = add.concat(" ");
					add = add.concat(""+rules[i].getOutputs()[j]);
				}
				add =	add.concat(" ("+rules[i].getWeight()+") : "+rules[i].getOperator());


				add = add.concat("\n");
			}

		return add;

		}



		public void setName(String name2) {
			name=name2;
		}

		public void setType(String type_)
		{
			if(type_!="sugeno"&&type_!="mandami")
			{System.out.println("Invalid type");
			return;}
			type=type_;

		}

		public void addRule(int[] ins, int[] outs, int op, double weight){

			FuzzyRule newRule = new FuzzyRule();
			newRule.setInputs(ins);
			newRule.setOutputs(outs);
			newRule.setOperator(op);
			newRule.setWeight(weight);

			FuzzyRule[] aux = new FuzzyRule[rules.length+1];
			System.arraycopy(rules,0,aux,0,rules.length);
			aux[aux.length-1] = newRule;
			rules = new FuzzyRule[aux.length];
			System.arraycopy(aux,0,rules,0,aux.length);

			nrules++;

		}

		public void clearRules() {

			rules = new FuzzyRule[0];

		}

		public void removeRule(int index) {

			if(index>=0 && index<rules.length)
			{nrules--;
				FuzzyRule[] aux= new FuzzyRule[rules.length-1];

				for(int i=0; i<index;i++)
				{aux[i]=rules[i];}
				for(int i=index; i<aux.length;i++)
				{aux[i]=rules[i+1];}

				rules=aux;
			}

		}

		public void setInputMFName(int i, int j, String name2) {
			if(i<0|| i>=inputs.length)
			{
				System.out.println("input out of range");
				return;
			}

			if(j<0||j>=inputs[i].getNMFunctions())
			{
				System.out.println("MFinput out of range");
				return;
			}

			inputs[i].getMFunctionAt(j).setLabel(name2);

		}

		public void setOutputMFName(int i, int j, String name2) {
			if(i<0|| i>=outputs.length)
			{
				System.out.println("output out of range");
				return;
			}

			if(j<0||j>=outputs[i].getNMFunctions())
			{
				System.out.println("MFoutput out of range");
				return;
			}

			outputs[i].getMFunctionAt(j).setLabel(name2);

		}

		public void setAndMethod(String method) {
			if(method.equals("min")||method.equals("prod"))
			andmethod=method;
			else
			System.out.println("Not-defined method");
		}

		public void setOrMethod(String method) {
			if(method.equals("max")||method.equals("probor"))
			ormethod=method;
			else
			System.out.println("Not-defined method");
		}

		public void setImpMethod(String method) {
			if(method.equals("min")||method.equals("prod"))
			impmethod=method;
			else
			System.out.println("Not-defined method");
			if(type.equals("sugeno"))
			{impmethod="min";
			System.out.println("sugeno just accept min for implication method");}

		}

		public void setAggMethod(String method) {
			if(method.equals("max")||method.equals("sum"))
			aggmethod=method;
			else
			System.out.println("Not-defined method");
			if(type.equals("sugeno"))
			{aggmethod="max";
			System.out.println("sugeno just accept max for agregation method");}
		}

		public void setDefuzzMethod(String method) {
			if(type.equals("mamdani")){
				if(method.equals("centroid")||method.equals("bisector")||method.equals("mom")||method.equals("som")||method.equals("lom"))
				defuzzmethod=method;
				else
				System.out.println("Not-defined method");
			}
			else if(type.equals("sugeno")){
				if(method.equals("mtaver")||method.equals("wtsum"))
				defuzzmethod=method;
				else
				System.out.println("Not-defined method");
			}
		}

		public void setInputName(String name_, int i){
			if(i<0 || i>=inputs.length){
				System.out.println("Error: Number of input out of range");
			}

			else{
				inputs[i].setName(name_);
			}
		}

		public void setOutputName(String name_, int i){
			if(i<0 || i>=outputs.length){
				System.out.println("Error: Number of output out of range");
			}

			else{
				outputs[i].setName(name_);
			}
		}

		public void setInputMFParams(int i, int j, double[] params) {
			inputs[i].getMFunctionAt(j).setParameters(params);
		}

		public void setOutputMFParams(int i, int j, double[] params) {
			outputs[i].getMFunctionAt(j).setParameters(params);
		}

		public int parametersInMF(int i, int j){
			return inputs[i].getMFunctionAt(j).GetNParams();
		}

		public int parametersOutMF(int i, int j){
			return outputs[i].getMFunctionAt(j).GetNParams();
		}

		public double getInMinRange(int i) {
			if(i<0 || i>=inputs.length)
			return 0;
			return inputs[i].getRangeMin();
		}

		public double getInMaxRange(int i) {
			if(i<0 || i>=inputs.length)
			return 0;
			return inputs[i].getRangeMax();
		}

		public double getOutMinRange(int i) {
			if(i<0 || i>=outputs.length)
			return 0;
			return outputs[i].getRangeMin();
		}

		public double getOutMaxRange(int i) {
			if(i<0 || i>=outputs.length)
			return 0;
			return outputs[i].getRangeMax();
		}


		public String rulesToText() {

			String ss = "";
			for (int i=0;i<nrules;i++) {

				FuzzyRule r = rules[i];

				ss += "IF ";
				for(int j=0;j<ninputs;j++) {

          int nr = r.getInputs()[j];
					if (nr > 0) {
						ss += inputs[j].getName() + " IS ";
						ss += inputs[j].getMFLabelAt(nr - 1);
						if(j < (ninputs - 1)) {
							ss += " " + r.operToString() + " ";
						}
					}
					else if (nr == 0) {
						ss += inputs[j].getName() + " IS anything";
					}
					else {
						ss += inputs[j].getName() + " IS NOT ";
						ss += inputs[j].getMFLabelAt(nr - 1);
						if(j < (ninputs - 1)) {
							ss += " " + r.operToString() + " ";
						}
					}
				}

				ss += " THEN ";

				for(int j=0;j<noutputs;j++) {

          int nr = r.getOutputs()[j];
					if (nr > 0) {
						ss += outputs[j].getName() + " IS ";
						ss += outputs[j].getMFLabelAt(nr - 1);
						if(j < (noutputs - 1)) {
							ss += " " + r.operToString() + " ";
						}
					}
					else if (nr == 0) {
						ss += outputs[j].getName() + " IS anything";
					}
					else {
						ss += outputs[j].getName() + " IS NOT ";
						ss += outputs[j].getMFLabelAt(nr - 1);
						if(j < (noutputs - 1)) {
							ss += " " + r.operToString() + " ";
						}
					}

				}
				ss += "\n";
			}
		return ss;

		}

	}
