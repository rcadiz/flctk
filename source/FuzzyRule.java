package flctk;

public class FuzzyRule {

private int[] inputs;
private int[] outputs;
private  int oper;
private  double weight;

static int OpAND = 1;
static int OpOR = 2;

//constructor
public FuzzyRule(){
	inputs= new int[0];
	outputs= new int[0];
	oper = 1;
	weight = 1;
}
	
public   void setInputs(int[] in){
	inputs = in;}
	
public  void setOutputs(int[] out) {
	outputs = out;}
	
public void setOperator(int op) {
	oper = op;}
	
public void setWeight( double w) {
	weight = w;}

public  int[] getInputs(){
	return inputs ;}
	
public  int[] getOutputs() {
	return outputs;}
		
public double getWeight() {
	return weight;}
	
public int getOperator() {
	return oper;}
	
public int getMFAtInput(int in) {
	return inputs[in];}
	
public int getMFAtOutput(int out)  {
	return outputs[out];}

public void RemoveInput(int i){
	int[] aux= new int[inputs.length-1];
	for(int j=0; j<i;j++)
	{
		aux[j]=inputs[j];
	}
	
	for(int j=i; j<aux.length;j++)
	{
		aux[j]=inputs[j+1];
	}
	
	inputs=aux;
}

public void RemoveOutput(int i){
	int[] aux= new int[outputs.length-1];
	for(int j=0; j<i;j++)
	{
		aux[j]=outputs[j];
	}
	
	for(int j=i; j<aux.length;j++)
	{
		aux[j]=outputs[j+1];
	}
	
	outputs=aux;
}
	
public String toString() {
	String result= new String( "Rule: [ ");
	for ( int i=0;i<inputs.length;i++)
		result= result+inputs[i] +"  ";

	result= result+ "] [ ";
	
	for (int i=0;i<outputs.length;i++)
		result= result + outputs[i]+" ";
	
	result=result+"] ";
	result=result+"( " + oper + " ) " + "[ " + weight + " ]";
		
	return result;
		
	}

public void addInput() {
	int[] aux= new int[inputs.length+1];
	for(int j=0;  j<aux.length-1;j++)
		aux[j]=inputs[j];
	aux[inputs.length]=0;	
	
	inputs=aux;
	
}

public void addOutput() {
	int[] aux= new int[outputs.length+1];
	for(int j=0;  j<aux.length-1;j++)
		aux[j]=outputs[j];
	aux[outputs.length]=0;	
	
	outputs=aux;
	
}

}
