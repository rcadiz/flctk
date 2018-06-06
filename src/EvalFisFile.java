/**
* EvalFisFile
* Evaulates a fis file for debugging purposes
* @author: Marie Gonzalez and Rodrigo F. Cadiz
* @version: 2.0
* 2018
*/

package flctk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class EvalFisFile {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		FuzzySystem fuzzy = null;
		double[] errores= new double[33];
		Scanner n= new Scanner(System.in);
		int k=3;
	while(k!=0)
	//	for(int k=1; k<=29;k++)
			{
			k=n.nextInt();
		String filename="C:\\Users\\Marie\\Documents\\PUC\\MUC\\test"+k+".fis";
		//String filename="NewEcualizador2.fis";

		fuzzy= new FuzzySystem(filename);

		double error=0;

		   Scanner scan;
		   try {
			   File file= new File ("C:\\Users\\Marie\\Documents\\PUC\\MUC\\results-test"+k+".txt");
			   scan = new Scanner(file);


			   String line = "";
			   double value=0;
			   int j=0;
			   for(j=0; j<=100; j++)
			   {
				   double i=(double)j/100;
				   if(j==22)
				   {
					   System.out.println();
				   }
				   double[] eval=new double[3];
				   eval[0]=i;
				   eval[1]=1-i;
				   eval[2]=1-i/2;
				   double[] result= fuzzy.eval(eval);
				   if(!scan.hasNext())
				   {System.out.println("se acabaron");
				   break;}
				   line=scan.nextLine();
				   value=Double.parseDouble(line);
				   if(Math.abs(value-result[0])>0.20)
				   {
					   System.out.println("Error de "+(value-result[0])+" en sistema "+k+" ins: "+eval[0]+" "+eval[1]+" "+eval[2]);
				   }
				   error+=Math.abs(value-result[0]);
				   if(!scan.hasNext())
				   {System.out.println("se acabaron");
				   break;}
				   line=scan.nextLine();
				   value=Double.parseDouble(line);
				   error+=Math.abs(value-result[1]);


				   if(Math.abs(value-result[1])>0.20)
				   {
					   System.out.println("Error de "+(value-result[1])+" en sistema "+k+" ins: "+eval[0]+" "+eval[1]+" "+eval[2]);
				   }
			   }
			   if(j<100)
			   {System.out.println("j arrived at "+j);}

			   error=error/200;

			   errores[k-1]=error;

			   System.out.println("Error obtenido "+error);
			   scan.close();
		   }

		   catch (FileNotFoundException e)
		   {System.out.println("Error");}

			//}


		   /*
	for(int u=1; u<=29;u++)
	{
		System.out.println("Error "+u+" = "+errores[u-1]);
	}

*/
	Scanner scan2 = new Scanner(System.in);

	double[] ins=new double[3];
	for(int m=0; m<5; m++)
	{
		System.out.println("in 1");
		ins[0]=scan2.nextDouble();
		System.out.println("in 2");
		ins[1]=scan2.nextDouble();
		System.out.println("in 3");
		ins[2]=scan2.nextDouble();

		double[] sal= fuzzy.eval(ins);

		for(int t=0; t<sal.length; t++)
				System.out.println(sal[t]);


	}

}

}
}
