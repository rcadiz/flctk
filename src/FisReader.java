/**
* FisReader
* Reads a fuzzy system from a fis file
* @author: Marie Gonzalez and Rodrigo F. Cadiz
* @version: 2.0
* 2018
*/

package flctk;
import java.io.*;
import java.util.Scanner;

public class FisReader {

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

	private FuzzyVar[] inputs= new FuzzyVar[0];
	private FuzzyVar[] outputs= new FuzzyVar[0];
	private FuzzyRule[] rules= new FuzzyRule[0];

	static int NONE=0;
	static int SYSTEM=1;
	static int INPUTS=2;
	static int OUTPUTS=3;
	static int RULES=4;
	static int ARRAY_SIZE=255;


	/**
	* Object that reads a fis file
	* @param fname name of the fis file
	*/
	public FisReader(String fname) {
		filename = fname;
		readFis();
	}

	/**
	* Read the content of a fis file
	* @return returns 1 if sucessful 0 if not
	*/
	private int readFis() {

		try {
			File file = new File (filename);
			Scanner scan;
			scan = new Scanner(file);
			String line= "";

			while(scan.hasNext()){
				line = scan.nextLine();

				if (line.contains("[System")) {
					int j=0;
					String[] sys= new String[11];

					while(!line.equals("") && j<11) {
						line = scan.nextLine();
						sys[j] = line;
						j++;
					}
					readSystemSection(sys);
				}

				else if(line.contains("[Input")) {
					int enviados =0;
					String[] inp= new String[1];
					line= scan.nextLine();
					inp[0]=line;

					while(!line.equals("")) {
						line = scan.nextLine();

						if (line.startsWith("Name") || line.equals("")) {
							readInputsOutputs(inp,true);
							enviados++;
							inp= new String[1];
							if(enviados==ninputs)
							break;
						}

						String[] aux= new String[inp.length+1];
						for(int i=0; i<inp.length; i++)
						aux[i]=inp[i];
						aux[inp.length]=line;
						inp=aux;
					}
				}

				else if(line.contains("[Output")) {
					int enviados =0;
					String[] inp= new String[1];
					line= scan.nextLine();
					inp[0]=line;

					while(!line.equals(""))
					{
						line= scan.nextLine();
						if(line.startsWith("Name") || line.equals("")){
							readInputsOutputs(inp,false);
							enviados++;
							inp= new String[1];
							if(enviados==noutputs)
							break;
						}

						String[] aux= new String[inp.length+1];
						for(int i=0; i<inp.length; i++)
						aux[i]=inp[i];

						aux[inp.length]=line;
						inp=aux;
					}
				}

				else if (line.contains("[Rules]")) {
					int j=0;
					while(scan.hasNext() && !line.equals("") && j < 11){
						line = scan.nextLine();
						readRules(line);
					}
				}
			}

			scan.close();
			System.out.println(toString());

			return 1;
		}

		catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not read FIS file.");
		}

		return 0;
	}

	/**
	* Retrieves numbers from a string
	* @param  {String} str string to be read
	* @return {Array} array of doubles containing the numbers
	*/
	private double[] getNumbersFromString(String str) {

		String[] vecString = str.split(" ");
		double[] vecNumbersIt = new double[vecString.length];
		int converted = 0;

		//for each string in the vector convert it to a number
		for(int i=0; i<vecString.length; i++) {
			if(vecString[i].contains("["))
			vecString[i]=vecString[i].substring(1);
			else if(vecString[i].contains("]"))
			vecString[i]=vecString[i].substring(0,vecString[i].length()-1);

			double j=Double.parseDouble(vecString[i]);
			vecNumbersIt[converted] = j;
			converted++;
		}

		double[] aux = new double[converted];
		for(int j=0; j<aux.length; j++)
		{
			aux[j] = vecNumbersIt[j];
		}

		return aux;

	}

	/**
	* Retrieves numbers from a string
	* @param  {String} str string to be read
	* @return {Array} array of ints containing the numbers
	*/

	private int[] getNumbersFromString2(String str) {

		String[] vecString= str.split(" ");
		int[] vecNumbersIt=new int[vecString.length];
		int converted=0;

		//for each string in the vector convert it to a number
		for(int i=0; i<vecString.length; i++) {
			int j=Integer.parseInt(vecString[i]);
			vecNumbersIt[converted]=j;
			converted++;
		}

		int[] aux=new int[converted];

		for(int j=0; j<converted; j++) {
			aux[j]=vecNumbersIt[j];
		}

		return aux;

	}

	private void readSystemSection(String [] system) {

		for(int i=0; i<system.length; i++){

			if(system[i].equals(null)) {
				continue;
			}
			int corte = system[i].indexOf("=");
			if(corte!=-1 && !system[i].equals("")){
				String dato = system[i].substring(corte+1);
				if(dato.charAt(0)=='\''){//property
					if (system[i].startsWith("Name="))
					name= dato.substring(1,dato.length()-1);
					else if(system[i].startsWith("Type="))
					type= dato.substring(1,dato.length()-1);
					else if (system[i].startsWith("AndMethod="))
					andmethod = dato.substring(1,dato.length()-1);
					else if (system[i].startsWith("OrMethod="))
					ormethod = dato.substring(1,dato.length()-1);
					else if (system[i].startsWith("ImpMethod="))
					impmethod = dato.substring(1,dato.length()-1);
					else if (system[i].startsWith("AggMethod="))
					aggmethod = dato.substring(1,dato.length()-1);
					else if (system[i].startsWith("DefuzzMethod="))
					defuzzmethod = dato.substring(1,dato.length()-1);
				}

				else{//number
					if (system[i].startsWith("Version"))
					version = Double.parseDouble(dato);
					else if (system[i].startsWith("NumInputs"))
					ninputs = Integer.parseInt(dato);
					else if (system[i].startsWith("NumOutputs"))
					noutputs = Integer.parseInt(dato);
					else if (system[i].startsWith("NumRules"))
					nrules = Integer.parseInt(dato);
				}
			}
		}
	}


	private void readInputsOutputs(String[] in, boolean input) {
		FuzzyVar fVar;
		int MFcounter = 0;
		double minR=0;
		double maxR=0;
		String FVname= new String("");
		String[] MFlabels = new String[0];
		String[] MFfunctions = new String[0];
		String[] MFvalues = new String[0];
		double[] values= new double[0];
		int numMF = 0;

		for(int j=0;j<in.length; j++)
		{
			if(in[j].startsWith("Name="))
			FVname = in[j].substring(6,in[j].length()-1);

			else if(in[j].startsWith("Range=")){
				int corte= in[j].indexOf(" ");
				minR=Double.parseDouble(in[j].substring(7, corte));
				maxR=Double.parseDouble(in[j].substring(corte, in[j].length()-1));
			}

			else if(in[j].startsWith("NumMFs=")){
				int corte= in[j].indexOf("=");
				numMF=Integer.parseInt(in[j].substring(corte+1));
			}

			else if(!in[j].equals(""))
			{
				int corte=in[j].indexOf("=");
				int corte2= in[j].indexOf(":");
				String label=in[j].substring(corte+2, corte2-1);

				MFlabels= Merge(MFlabels,label);


				int corte3= in[j].indexOf(",");
				String functions=in[j].substring(corte2+2, corte3-1);
				MFfunctions=Merge( MFfunctions,functions);

				MFvalues=Merge(MFvalues,in[j].substring(corte3+1,in[j].length()-1));
				MFcounter++;
			}


		}


		fVar = new FuzzyVar();
		fVar.setName(FVname);
		fVar.setNMFunctions(numMF);
		fVar.setRangeMin(minR);
		fVar.setRangeMax(maxR);
		for (int i=0;i<MFlabels.length;i++)
		{
			values= getNumbersFromString(MFvalues[i]);
			fVar.addMFunction(MFlabels[i],MFfunctions[i],values);
		}

		FuzzyVar[] aux;

		if(input)
		{
			aux= new FuzzyVar[inputs.length+1];
			for(int i=0; i<inputs.length; i++)
			aux[i]=inputs[i];
			aux[inputs.length]=fVar;
			inputs=aux;
		}

		else
		{
			aux= new FuzzyVar[outputs.length+1];
			for(int i=0; i<outputs.length; i++)
			aux[i]=outputs[i];
			aux[outputs.length]=fVar;
			outputs=aux;
		}

	}


	private String[] Merge(String[] mFlabels, String label) {
		String[] aux= new String[mFlabels.length+1];
		for(int i=0; i<mFlabels.length;i++)
		aux[i]=mFlabels[i];
		aux[mFlabels.length]=label;
		return aux;
	}

	private int readRules (String rule) {

		int op = 0;
		double w = 0;
		int[] inputs = new int[0];
		int[] outputs = new int[0];

		FuzzyRule fRule;

		if(rule.equals("") || rule.contains("Rule")) {
			return 0;
		}

		String In = rule.substring(0,rule.indexOf(","));
		inputs = getNumbersFromString2(In);
		String Out = rule.substring(rule.indexOf(",")+2,rule.indexOf("(")-1);
		outputs = getNumbersFromString2(Out);
		w = Double.parseDouble(rule.substring(rule.indexOf("(")+1, rule.indexOf(")")));
		op = Integer.parseInt(rule.substring(rule.indexOf(":")+2));

		fRule = new FuzzyRule();
		fRule.setInputs(inputs);
		fRule.setOutputs(outputs);
		fRule.setWeight(w);
		fRule.setOperator(op);

		FuzzyRule[] aux = new FuzzyRule [rules.length+1];
		for(int i=0; i<rules.length; i++)
		aux[i]=rules[i];
		aux[rules.length]=fRule;
		rules=aux;

		return 1;
	}



	/**
	* Substring involve from param
	* Ex.: string=" we 3 have 3 apples", param= '3' return " have "
	* @param string
	* @param param
	* @return
	*/
	private String getString(String string, char param) {


		String result= new String("");

		int i =string.indexOf(param);
		int j= string.indexOf(i+1,param);
		result= string.substring(i,j);

		return result;
	}

	public String getName()  {
		return name;
	}

		public FuzzyVar[] getInputs()  {
			return inputs;
		}

			public FuzzyVar[] getOutputs() {
				return outputs;
			}

				public FuzzyRule[] getRules() {
					return rules;
				}

					public double getVersion() {
						return version;
					}

						public int getNInputs()  {
							return ninputs;
						}

							public int getNOutputs()  {
								return noutputs;
							}

								public int getNRules()  {
									return nrules;
								}

									public String getType()  {
										return type;
									}

										public String getAndMethod() {
											return andmethod;
										}

											public String getOrMethod()  {
												return ormethod;
											}

												public String getImpMethod() {
													return impmethod;
												}

													public String getAggMethod()  {
														return aggmethod;
													}

														public String getDefuzzMethod() {
															return defuzzmethod;
														}

															public  String toString(){

																String fis= new String("");
																fis=fis.concat( "----------------------------------");
																fis=fis.concat("\n [FUZZY SYSTEM]");
																fis=fis.concat("\n Name : "+name);
																fis=fis.concat("\n Type : "+ type );
																fis=fis.concat("\n Version : " +version );
																fis=fis.concat("\n Number of inputs : "+ninputs );
																fis=fis.concat("\n Number of outputs : " + noutputs);
																fis=fis.concat("\n Number of rules : " + nrules );
																fis=fis.concat("\n And method : " + andmethod );
																fis=fis.concat("\n Or method : " + ormethod);
																fis=fis.concat("\n Implication method : " + impmethod );
																fis=fis.concat("\n Aggregation method : " +aggmethod );
																fis=fis.concat("\n Defuzzification method : " +defuzzmethod );
																fis=fis.concat("\n ----------------------------------");

																fis=fis.concat("\n[INPUTS]\n" );
																for(int i=0;i<ninputs;i++)
																fis=fis.concat("\n "+inputs[i].toString(1));
																fis=fis.concat("\n----------------------------------");

																fis=fis.concat("\n[OUTPUTS]\n");
																for(int i=0;i<noutputs;i++)
																fis=fis.concat("\n "+outputs[i].toString(1));
																fis=fis.concat("\n ----------------------------------" );

																fis=fis.concat("\n [RULES]");
																for(int i=0;i<nrules;i++)
																fis=fis.concat("\n"+rules[i].toString());

																return fis;

															}


														}
