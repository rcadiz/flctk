package flctk;
import java.util.Random;

public class FuzzyVar {
	private String name;
	private double rangemin;
	private double rangemax;
	private int nmfunctions;
	private int size;
	private FuzzySet[] mfunctions;
	private double[] inputDegrees;
	private boolean empty; 
	private int defaultSize=100;
	private Random ran; 

	//constructores
	public	FuzzyVar(){
		size=defaultSize;
		name= new String("Default var");
		empty=true;
		rangemin=0;
		rangemax=0;
		nmfunctions=0;
		mfunctions= new FuzzySet[0];
		inputDegrees= new double[0];
		
	}

	public  FuzzyVar(int i){
		nmfunctions=i;
		name= new String("Default var");
		empty=true;
		size=defaultSize;
		rangemin=0;
		rangemax=0;
		mfunctions= new FuzzySet[0];
		inputDegrees= new double[0];
		}
	
	public  FuzzyVar(int i ,String Name){
		nmfunctions=i;
		name= Name;
		empty=true;
		size=defaultSize;
		rangemin=0;
		rangemax=0;
		mfunctions= new FuzzySet[0];
		inputDegrees= new double[0];
		}
	
	//setters
	
	 public void setName(String N) {
		 name = N;}
	 
	 public void setRangeMin(double x) {
		 rangemin = x;}
	 
	 public void setRangeMax( double x) {
		 rangemax = x;}
	 
	 public void setNMFunctions(int n) {
		 nmfunctions = n;}
	
	 /**
	  * 
	  * @param l = name of MF
	  * @param t = type of MemberShip FUnction
	  * @param values 
	  */
	 public void addMFunction(String l, String t, double[] values) {
		
		 int type=2;
		 if (t.equals("dsigmf")) type=FuzzySet.dsigmoidal;
		 else if (t.equals("gauss2mf")) type =FuzzySet.gaussian2;
		 else if (t.equals("gaussmf")) type = FuzzySet.gaussian;
		 else if (t.equals("gbellmf")) type = FuzzySet.gaussbell;
		 else if (t.equals("pimf")) type = FuzzySet.pi;
		 else if (t.equals("psigmf")) type = FuzzySet.psigmoidal;
		 else if (t.equals("sigmf")) type = FuzzySet.sigmoidal;
		 else if (t.equals("smf")) type = FuzzySet.s;
		 else if (t.equals("trapmf")) type = FuzzySet.trapezoidal;
		 else if (t.equals("trimf")) type = FuzzySet.triangular;
		 else if (t.equals("zmf")) type = FuzzySet.z;
		 else if (t.equals("linear")) type = FuzzySet.linearSugeno;
		 else if (t.equals("constant")) type = FuzzySet.constantSugeno;

		 
		 FuzzySet  fSet = new FuzzySet();
		 fSet.setSize(size);
		 fSet.setParameters(values);
		 fSet.setLabel(l);
		 fSet.setDomain(rangemin,rangemax);
		 fSet.setType(type);
		 fSet.refill();
		  
		 FuzzySet[] aux= new FuzzySet[mfunctions.length +1];
		 for(int i=0; i<mfunctions.length; i++)
			 aux[i]=mfunctions[i];
		 aux[mfunctions.length]=fSet;
		 mfunctions=aux;
		  	 
	 }

	 //Getters
	 public String getName() {
		 return name;}
	 
	 public String getMFLabelAt(int mf) {
		 return mfunctions[mf].getLabel();}
	 
	 public double getRangeMin() {
		 return rangemin;}
	 
	 public double getRangeMax() {
		 return rangemax;}
	 
	 public double getRange() {
		 return (rangemax - rangemin);}
	 
	 public double getRangeMiddle() {
		 return (rangemin + ((rangemax - rangemin)/2.0));}
	 
	 public double getRangeRandom() {
		 int random_integer;
		  random_integer = ran.nextInt(11);
		  double r = (double)random_integer/10.0;
		  return (r*getRange()+rangemin);
		  }
	 
	 public int getNMFunctions() {
		 return nmfunctions;}
	 
	 public int getSize() {
		 return size;}

	 public FuzzySet getMFunctionAt(int index) {
		 return mfunctions[index];}

	 /**
	  * Actualize inputs degrees from membership functions
	  * @param X
	  */
	 public void updateInputDegrees(double X)  {

		 inputDegrees=new double[nmfunctions];
		 for(int i=0;i<nmfunctions;i++)
			 inputDegrees[i] = mfunctions[i].sub(X);
	 }
	 
	 public double[] getDegreesFromScalar(double si)  {
		  //this->scalarInput = si;
		  updateInputDegrees(si);
		  return inputDegrees;
		}

	 public boolean valueInRange(double i) {
		 if (i>=rangemin || i<=rangemax)
		 return true;
		 return false;
		 }
	 
	 public boolean isEmpty() {
		 return empty;}
	 
	 public String toString(int flag) {

		 String ss;
		 ss ="Var: " +getName() + "(" +getNMFunctions() + ")";
		 ss= ss.concat( "[" +getRangeMin() + " " +getRangeMax()+"]");
		 ss=ss.concat( "\n Degrees : [ ");
		 for(int i=0;i<inputDegrees.length;i++)
			 ss =ss.concat(inputDegrees[i] +" ");
		 ss=ss.concat("]\n");
		 for(int i=0;i<mfunctions.length;i++)
			 ss=ss.concat("\n MF " + i+1 + ": " +mfunctions[i].toString(flag)+"\n");

		 return ss;
		}

	public double getWtaver(double[] weigth, double[] ins) {
		
		double total=0;
		double sum=0;
		for(int i=0; i<weigth.length;i++)
		total+=weigth[i];
		
		for(int i=0; i<weigth.length;i++)
			sum+=weigth[i]*mfunctions[i].getSugenoVal(ins);
		
		if(total==0)
		return 0;
		
		else
			return sum/total;
	}
	
public double getWtsum (double[] weigth, double[] ins) {
		
		double sum=0;
		
		for(int i=0; i<weigth.length;i++)
			sum+=weigth[i]*mfunctions[i].getSugenoVal(ins);
		
			return sum;
	}
	 	 

}
