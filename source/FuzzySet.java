package flctk;

public class FuzzySet {

	
	private double dmin;              
	private double dmax;                  
	private double domain;                
	private String label;                 
	private int type;                     
	private double[] parameters;     
	private double[] degrees;        
	private double gamma;            
	private int size=100;                
	private double resolution;       
	private boolean empty;                   
		  
	
	
		public FuzzySet()
	{
		initSet();
		empty = true;
	}
	
	public  FuzzySet(int s, double x,double y) {
		initSet();
		empty = true;
		resize(s);
		setDomain(x,y);
	}
		
	
	private void initSet() {
		setDomain(0.0,1.0);
		setSize(0);
		label = new String("Default var");
		type = 0;
		resolution = 1.0;
		parameters= new double[1];
		degrees= new double[1];
		gamma=0.0;
	}
	
	//setters
	
	public	void setLabel(String Label){
		label = Label;}
	
	public void setType(int i){
		if(type==0)
			{type=i; return;}
		if(type==i)
			return;
		double[] aux=null;
		
		if(i==z||i==s)
		{
			aux= new double[2];
			if(i==s)
			{
				if(type==triangular||type==trapezoidal||type==pi)
				{
					aux[0]=parameters[0];
					aux[1]=parameters[1];
				}
				
				else if(type==gaussbell)
				{
					aux[0]=parameters[0];
					aux[1]=parameters[3];
				}
				
				else if(type==gaussian||type==gaussian2)
				{
					aux[0]=parameters[1]-parameters[0]*2;
					aux[1]=parameters[1];
				}
				
				else if(type==sigmoidal || type==dsigmoidal || type==psigmoidal)
				{
					aux[0]=parameters[0];
					aux[1]=parameters[0]+(parameters[1]-parameters[0])*2;
				}
				
				else if(type==z)
				{
					aux[0]=parameters[1];
					aux[1]=parameters[0];
				}
			}
			
			else
			{
				if(type==triangular)
				{
					aux[0]=parameters[1];
					aux[1]=parameters[2];
				}

				else if (type==trapezoidal)
				{
					aux[0]=parameters[2];
					aux[1]=parameters[3];
				}
				
				else if(type==gaussbell)
				{
					aux[0]=parameters[2];
					aux[1]=parameters[2]+parameters[1];
				}
				
				else if(type==gaussian||type==gaussian2)
				{
					aux[0]=parameters[1]+parameters[0]*2;
					aux[1]=parameters[1];
				}
				
				else if(type==sigmoidal)
				{
					aux[0]=dmax+1;
					aux[1]=aux[0]+5;
				}
				
				else if( type==dsigmoidal || type==psigmoidal)
				{
					aux[0]=parameters[0];
					aux[1]=parameters[0]+(parameters[1]-parameters[0])*2;
				}
				
				else if (type==pi)
				{
					aux[0]=parameters[2];
					aux[1]=parameters[3];
				}
				
				else if(type==s)
				{
					aux[0]=parameters[1];
					aux[1]=parameters[0];
				}
			}
			
			
		}
		
		else if(i==triangular)
		{
			aux= new double[3];
			if(type==trapezoidal||type==pi)
			{
				aux[0]=parameters[0];
				aux[2]=parameters[3];
				aux[1]=aux[0]+(aux[2]-aux[0])/2;
			}
			
			else if(type==gaussbell)
			{
				aux[0]=parameters[0];
				aux[1]=parameters[2];
				aux[2]=aux[0]+aux[1];
			}
			
			else if(type==gaussian|| type==gaussian2)
			{
				aux[1]=parameters[1];
				aux[0]=aux[1]-parameters[0]*2;
				aux[2]=aux[1]+parameters[0]*2;
			}
			
			else if(type==sigmoidal||type==psigmoidal||type==dsigmoidal||type==s)
			{
				aux[0]=parameters[0];
				aux[1]=parameters[1];
				aux[2]=aux[1]+(aux[1]-aux[0]);
			}
			
			else if(type==z)
			{
				aux[1]=parameters[0];
				aux[2]=parameters[1];
				aux[0]=aux[1]-(aux[2]-aux[1]);
			}
		}
		
		if(i==trapezoidal)
		{
			aux= new double[4];
			if(type==triangular)
			{
				aux[0]=parameters[0];
				aux[3]=parameters[2];
				aux[1]=parameters[0]+(parameters[1]-parameters[0])/2;
				aux[2]=parameters[2]+(parameters[2]-parameters[1])/2;
			}
			
			else
				
			{
				setType(triangular);
				setType(trapezoidal);
			}	
		}
		
		else if(i==gaussbell)
		{
			aux= new double[3];
			if(type==triangular)
			{
				aux[0]=parameters[0];
				aux[2]=parameters[1];
				aux[1]=1;
			}
			
			else
				
			{
				setType(triangular);
				setType(gaussbell);
			}	
		}
		
		else if(i==gaussian)
		{
			aux= new double[2];
			if(type==triangular)
			{
				aux[1]=parameters[1];
				aux[0]=parameters[1]-parameters[0];
			}
			
			else
				
			{
				setType(triangular);
				setType(gaussian);
			}	
		}
		
		else if(i==gaussian2)
		{
			aux= new double[4];
			if(type==triangular)
			{
				aux[0]=parameters[1]-parameters[0];
				aux[2]=aux[0];
				aux[1]=parameters[0]-aux[0]/2;
				aux[3]=parameters[0]+aux[0]/2;
			}
			
			else
				
			{
				setType(triangular);
				setType(gaussian2);
			}	
		}
		
		else if(i==sigmoidal)
		{
			aux= new double[2];
			if(type==triangular)
			{
				aux[0]=parameters[0];
				aux[1]=(parameters[1]-parameters[0])/2;
			}
			
			else
				
			{
				setType(triangular);
				setType(sigmoidal);
			}	
		}
		
		else if(i==dsigmoidal)
		{
			aux= new double[4];
			if(type==triangular)
			{
				aux[0]=parameters[1]-parameters[0];
				aux[2]=aux[0];
				aux[1]=parameters[0]-aux[0]/2;
				aux[3]=parameters[0]+aux[0]/2;
			}
			
			else if(type==psigmoidal)
			{
				aux[0]=parameters[0];
				aux[1]=parameters[1];
				aux[2]=parameters[2];
				aux[3]=parameters[3];
			}
			else
				
			{
				setType(triangular);
				setType(dsigmoidal);
			}	
		}
		
		
		else if(i==psigmoidal)
		{
			aux= new double[4];
			if(type==triangular)
			{
				aux[0]=parameters[1]-parameters[0];
				aux[2]=aux[0];
				aux[1]=parameters[0]-aux[0]/2;
				aux[3]=parameters[0]+aux[0]/2;
			}
			
			else if(type==dsigmoidal)
			{
				aux[0]=parameters[0];
				aux[1]=parameters[1];
				aux[2]=parameters[2];
				aux[3]=parameters[3];
			}
			else
				
			{
				setType(triangular);
				setType(psigmoidal);
			}	
		}
		
		else if(i==pi)
		{
			aux= new double[4];
			if(type==triangular)
			{
				aux[0]=parameters[0];
				aux[3]=parameters[2];
				aux[1]=parameters[0]+(parameters[1]-parameters[0])/2;
				aux[2]=parameters[2]+(parameters[2]-parameters[1])/2;
			}
			
		
			else
				
			{
				setType(triangular);
				setType(pi);
			}	
		}
		parameters=aux;
		
		type=i;
		refill();
	
	}
	
	public void setDomain(double min,double max) {
		dmin = min;
		dmax = max;
		domain = max - min;
		if (size !=1)
		    resolution = domain/(size - 1.0);
		else
		    resolution = 1.0;
		}
	
	public void setParameters(double [] Parameters){
		parameters=Parameters;
		refill();
		}
	
	public double[] getParameters(){
		return parameters;
		}
	
	public void setGamma( double Gamma){
		gamma=Gamma;}
	
	public void setSize(int Size){
		resize(Size);}
		  
	public void resize( int s) {
		size = s;
		double[] aux= new double[s];
		  
		if(degrees==null)
		  degrees = new double[s];
		else
		  for(int i=0; i<degrees.length;i++)
			aux[i]=degrees[i];
		degrees=aux;
		  
		if (size !=1)
		    resolution = domain/(size - 1.0);
		else
		    resolution = 1.0;
		
	}
	
	//getters
	
	public String getLabel() {
		return label;}              
	
	public int getType(){
		return type;}
	
	public double getResolution(){
		return resolution;}
	
	public double getGamma(){
		return gamma;}
	
	public double[] getDegrees(){
		return degrees;}
	
	public void refill() {
		if (!isEmpty())
			constant(0.0);
		else if(type == triangular)
			triangle();
		else if(type == rectangular)
			rectangle();
		else if(type == trapezoidal)
			trapezoid();
		else if(type == gaussian)
			gauss();
		else if(type == gaussian2)
			gauss2();
		else if(type == sigmoidal)
			sigmoidal();
		else if (type == gaussbell)
			gbell();
		else if(type == pi)
			pi();
		else if(type == psigmoidal)
			psigmoidal();
		else if(type == dsigmoidal)
			dsigmoidal();
		else if(type == s)
			s();
		else if (type == z)
			z();
		
		else
			trapezoid();
		}
	
	public void large() {
		double slope = (double)1/domain;
		double intercept = -slope*dmin;
		double dx = domain/(size - 1.0);
		double x = dmin;
		for(int i=0; i<size; i++) {
			degrees[i] = slope*x+intercept;
			x += dx;
			}
		}
	
	public void small() {
		large ();
		for(int i=0; i<size; i++)
			degrees[i] = 1.0 - degrees[i];
		}
	
	public void constant(double val) {
		if(size!=degrees.length)
			degrees= new double[size];
		for(int i=0;i<size;i++)
			degrees[i] = val;
		}
	
	public FuzzySet hedge( double hedgeExp)  {
		FuzzySet result= new FuzzySet(size, dmin, dmax);
		for(int i=0; i<size; i++)
			result.degrees[i] = Math.pow(degrees[i],hedgeExp);
		return result;
		}
	
	public FuzzySet enhanceContrast() {
		FuzzySet result= new FuzzySet(size,dmin,dmax);
		for(int i=0; i<size; i++) {
		double deg = degrees[i];
		if(deg < 0.5) result.degrees[i] = 2.0+Math.pow(deg,2);
		else result.degrees[i] = 1.0-2+Math.pow(1-deg,2);
		}
		return result;
		}
	
	public int rectangle() {

		double l,r,dx,x;

		if (parameters.length != 2)
			return 0;
		else {
			l = parameters[0];
		    r = parameters[1];
		    dx = domain/(size - 1.0);
		    x = dmin;
		    for(int i=0; i<size; i++) {
		    	if((x <= l) || (x >= r)) 
		    		degrees[i] = 0.0;
		    	else
		    		degrees[i] = 1.0;
		    	x += dx;
		    }
		    return 1;
		  }
		}
	
	public int triangle() {
		  double l,c,r;
		  double dx,x;
		  double temp;

		 constant(0.0);
		  if (parameters.length !=3)
		    return 0;
		  else {
			  l = parameters[0];
			    c = parameters[1];
			    r = parameters[2];
			    dx =domain/(size - 1.0);
			    x = dmin;
			    for(int i=0; i<size; i++) {
			      if((x <= l) || (x >= r)) 
				degrees[i] = 0.0;
			      else if(x < c) {
				  temp = linInt(l,0,c,1,x);
				  degrees[i] = temp;
				}
				else {
				  temp = linInt(c,1,r,0,x);
				  degrees[i] =  temp;
				}
			      x += dx;
			    
		    }
		    return 1;  
		  }
		}
	
	public int trapezoid() {
		  
		  double a,b,c,d;
		  double dx,x;

		  if(parameters!=null && parameters.length == 4)
		    {
		    a = parameters[0];
		    b = parameters[1];
		    c = parameters[2];
		    d = parameters[3];
		    dx = domain/(size-1.0);
		    x = dmin;
		    for(int i=0; i<size; i++) {
		      if(x <= a)
			degrees[i] = 0.0;
		      else if (x <=b)
			  degrees[i] = (x-a)/(b-a);
			else if(x <= c)
			  degrees[i] = 1.0;
			  else if(x<=d)
			  degrees[i] = (d-x)/(d-c);
			  else
				  degrees[i]=0;
		      x += dx;
		    }
		    return 1;
		  }
		  
		  else
			  return 0;
		}

	public int gauss() {
		  
		  double mu,sigma;
		  double dx,x;

		  if(parameters.length != 2)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    mu = parameters[1];
		    sigma = parameters[0];
		    x = dmin;
		    for(int i=0; i<size; i++) {
		      degrees[i] = Math.exp((-(Math.pow(x-mu, 2)))/(2*Math.pow(sigma, 2)));
		      x += dx;
		    }

		    return 1;
		  }
		}
	
	public int gauss2() {
		  
		  double mu1,sigma1;
		  double mu2,sigma2;
		  double dx,x;
		  double tmp1,tmp2;


		  if(parameters.length != 4)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    mu1 = parameters[1];
		    sigma1 = parameters[0];
		    mu2 = parameters[3];
		    sigma2 = parameters[2];

		    x = dmin;
		    for(int i=0; i<size; i++) {
		    

			int Index1=0;
			if(x<=mu1) Index1=1;
			int Index2=0;
			if(x>=mu2) Index2=1;

		    	tmp1 = (Math.exp(-(Math.pow(x-mu1, 2))/(2*Math.pow(sigma1, 2))))*Index1 + (1-Index1);
		    	tmp2 = (Math.exp(-(Math.pow(x-mu2, 2))/(2*Math.pow(sigma2, 2))))*Index2 + (1-Index2);
		      degrees[i] = tmp1*tmp2;
		      x += dx;
		    }
		    return 1;
		  }
		}
	
	public int dsigmoidal(){
		
		  double a1,a2,c1,c2;
		  double dx,x;
		  double tmp1,tmp2;


		  if(parameters.length != 4)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a1 = parameters[0];
		    c1 = parameters[1];
		    a2 = parameters[2];
		    c2 = parameters[3];

		    x = dmin;
		    for(int i=0; i<size; i++) {
		      tmp1 =1/(1+Math.exp(-a1*(x-c1))) ;
		      tmp2 =1/(1+Math.exp(-a2*(x-c2))) ;
		      degrees[i] = tmp1-tmp2;
		      x += dx;
		    }
		    return 1;
		  }
	}
	
	public int gbell(){
		
		  double a,b,c;
		  double dx,x;
		  double tmp;


		  if(parameters.length != 3 || parameters[1]<0)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a = parameters[0];
		    b = parameters[1];
		    c = parameters[2];
		    
		    x = dmin;
		    for(int i=0; i<size; i++) {
		      tmp=1/(1+Math.abs(Math.pow(((x-c)/a), 2*b))) ;
		      degrees[i] = tmp;
		      x += dx;
		    }
		    return 1;
		  }
	}
	
	public int psigmoidal(){
		
		  double a1,c1,a2,c2;
		  double dx,x;
		  double tmp1,tmp2;


		  if(parameters.length != 4)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a1 = parameters[0];
		    c1 = parameters[1];
		    a2 = parameters[2];
		    c2 = parameters[3];
		    		    
		    x = dmin;
		    for(int i=0; i<size; i++) {
		    	
		      tmp1=1/(1+Math.exp(-a1*(x-c1)));
		      tmp2=1/(1+Math.exp(-a2*(x-c2)));
		      degrees[i] = tmp1*tmp2;
		      x += dx;
		    }
		    return 1;
		  }
	}
	
	public int sigmoidal(){
		
		  double a,c;
		  double dx,x;
		  double tmp;


		  if(parameters.length != 2)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a = parameters[0];
		    c = parameters[1];
		    		    
		    x = dmin;
		    for(int i=0; i<size; i++) {
		      tmp=1/(1+Math.exp(-a*(x-c)));
		      degrees[i] = tmp;
		      x += dx;
		    }
		    return 1;
		  }
	}

	public int s(){
		
		  double a,b;
		  double dx,x;
		  double tmp;


		  if(parameters.length != 2)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a = parameters[0];
		    b = parameters[1];
		    		    
		    x = dmin;
		    for(int i=0; i<size; i++) {
		    	if(x<=a)
		    		tmp=0;
		    	else if(x<=(a+b)/2)
		    		tmp=2*(Math.pow((x-a)/(b-a),2));
		    		
		    	else if(x<=b)
		    	tmp=1-2*(Math.pow((x-b)/(b-a),2));
		    	
		    	else
		    		tmp=1;
		      degrees[i] = tmp;
		      
		      x += dx;
		    }
		    return 1;
		  }
	}
	
	public int z(){
		
		  double a,b;
		  double dx,x;
		  double tmp;


		  if(parameters.length != 2)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a = parameters[0];
		    b = parameters[1];
		    		    
		    x = dmin;
		    for(int i=0; i<size; i++) {
		    	if(x<=a)
		    		tmp=1;
		    	else if(x<=(a+b)/2)
		    		tmp=1-2*(Math.pow((x-a)/(b-a),2));
		    	else if(x<=b)
		    		tmp=2*(Math.pow((x-b)/(b-a),2));
		    	else
		    		tmp=0;
		      degrees[i] = tmp;
		      x += dx;
		    }
		    return 1;
		  }
	}
	public int pi(){
		
		  double a,b,c,d;
		  double dx,x;
		  double tmp;


		  if(parameters.length != 4)
		    return 0;
		  else {
		    dx = domain/(size-1.0);
		    a = parameters[0];
		    b = parameters[1];
		    c = parameters[2];
		    d = parameters[3];
		    
		    x = dmin;
		    for(int i=0; i<size; i++) {
		    	tmp=0;
		     if(x<=a)
		    	 tmp=0;
		     else if(x<=((a+b)/2))
		    	 tmp=2*(Math.pow((x-a)/(b-a), 2));
		     else if(x<=b)
		    	 tmp=1-2*(Math.pow((x-b)/(b-a), 2));
		     else if(x<=c)
		    	 tmp=1;
		     else if(x<=(c+d)/2)
		    	 tmp=1-2*(Math.pow((x-c)/(d-c), 2));
		     else if(x<=d)
		    	 tmp=2*(Math.pow((x-d)/(d-c), 2));
		     else 
		    	 tmp=0;
		    	 
		      degrees[i] = tmp;
		      x += dx;
		    }
		    return 1;
		  }
	}
	
	public double getDomainMin() {
		return dmin;}
	
	public double getDomainMax() {
		return dmax;}
	
	public  double getDomain() {
		return domain;}
	 
	public int getMinDegreeIndex() {
		double min  = 1.0;
		int minIndex = 0;
		for(int i=0; i<size; i++)
			if(min > degrees[i]) {
				min = degrees[i];
				minIndex = i;
				}
		return minIndex;
		}
	
	public int getMaxDegreeIndex() {
		double max  = 0.0;
		int maxIndex = 0;
		for(int i=0; i<size; i++)
			if(max < degrees[i]) {
				max = degrees[i];
				maxIndex = i;
				}
		return maxIndex;
		}
	
	public double getMinDegree() {		
		return degrees[getMinDegreeIndex()];}
	
	public double getMaxDegree() {
		return degrees[getMaxDegreeIndex()];}
	
	public double getSupportMaxDegree() {
		return getMinDegreeIndex()*resolution+dmin;}
	
	public double getSupportMinDegree() {
		return getMaxDegreeIndex()*resolution+dmin;}

	public double getCentroid() {
		double num = (dmin+degrees[0]+dmax*degrees[size-1])/2.0;
		double den = (degrees[0]+degrees[size-1])/2.0;
		double x = dmin;
		  
		for(int i=1; i<=size-2; i++) {
			x += resolution;
		    num += x*degrees[i];
		    den += degrees[i];
		    }
		  
		if (den == 0)
			return (domain/2.0 + dmin);
		else
		    return num/den;  
		}
	


	public double getBisector() {
		double area = (degrees[0]+degrees[size-1])/2.0;
		double areatemp = (degrees[0]+degrees[size-1])/2.0;
		double x = dmin;
		
		
		for(int i=1; i<=size-2; i++) {
			area += degrees[i];
		    }
		
		if (area == 0)
			return (domain/2.0 + dmin);
		
		for(int i=1; i<=size-2; i++) {
			areatemp+= degrees[i];
			x += resolution;
			if	(areatemp>=area/2)
			break;
		}
		
		    return x;  
		
	}
	
	public double getSOM() {
		double x = dmin;
		double max=0;
		double val=0;
		
		for(int i=1; i<=size-2; i++) {
			x+=resolution;
			if(degrees[i]>max)
			{val=x;
			max=degrees[i];}
		}
		
		    return val;  
		
	}
	
	public double getSugenoVal(double[] ins) {
		if(type==constantSugeno)
			return parameters[0];
		else if(type==linearSugeno)
		{	double result=0;
		for(int i=0; i<ins.length;i++)
		result+=ins[i]*parameters[i];
		result+=parameters[parameters.length-1];
		return result;
		}	
		return 0;
	}

	
	public double getLOM() {
		double x = dmin;
		double max=0;
		double val=0;
		
		for(int i=1; i<=size-2; i++) {
			x+=resolution;
			if(degrees[i]>=max)
			{val=x;
			max=degrees[i];}
		}
		
		    return val;  
		
	}
	
	public double getMOM() {
		
		double x = dmin;
		double sum=0;
		double max=0;
		int M=0;
				
		for(int i=1; i<size-2; i++) {
			
			if(degrees[i]>max)
			{sum=x;
			max=degrees[i];
			M=1;}
			if(degrees[i]==max)
			{sum+=x;
			M++;}
			x+=resolution;
		}
	
		if(M==0)
			return 0;
		else
			return sum/M;
		/*
		double x = dmin;
		double ini=0;
		double end=0;
		double max=0;
		
				
		for(int i=1; i<=size-2; i++) {
			
			if(degrees[i]>max)
			{ini=x;}
			if(degrees[i]==max)
			{end=x;}
			x+=resolution;
		}
	
		
			return (end-ini)/2;
			*/
	}

	
	
	public  void normalize() { 
		double max = getMaxDegree();
		if (max != 0.0) 
		  for(int i=0; i<size; i++)
		    degrees[i] /= max;
	}
	
	public void scale( double factor) { 
		  for(int i=0; i<size; i++)
		    degrees[i] *= factor;
		}
	
	public double getCardinality() {
		  double cd = 0.0;
		  for(int i=0; i<size; i++)
		    cd += degrees[i];
		  return cd;
		}
	
	public double getRelativeCardinality() {
		return getCardinality()/domain; 
	}

	 public FuzzySet limit(double ceiling) {
		  FuzzySet result=this.copy();
		  for(int i=0; i<size; i++)
		    if(degrees[i] > ceiling)
		      result.degrees[i] = ceiling;
		  return result;
		}
	 
	 public FuzzySet alphaCut(double alpha) {
		  FuzzySet result= new FuzzySet(size,dmin,dmax);
		  for(int i=0; i<size; i++)
		  { if(degrees[i] <= alpha) 
		    	result.degrees[i] = 0.0;}
		  return result;
		}
	 
	public FuzzySet betaCut(int beta) {
		double grades[] = new double[size];
		int indexes[] = new int [size];
		for(int i=0; i<size; i++) {
			grades[i] = degrees[i];
		    indexes[i] = i;
		    }
		  
		int exchanged=0;
		int nFound = 0;
			
		do {
			exchanged = 0;
		    for(int n=0; n<size-nFound-1; n++) {
		    	if(grades[n] > grades[n+1]) {
			double ddum =  grades[n+1];
			grades[n+1] = grades[n];
			grades[n] = ddum;
			int idum =  indexes[n+1];
			indexes[n] = idum;
			exchanged++;
			}
		    	}
		    
		    nFound++;
		    }
		  
		while((exchanged!=0) && (nFound<=beta));// ¿? 
		  
		  FuzzySet result= new FuzzySet(size,dmin,dmax);
		  result.constant(0);
		  
		  for(int nb=1; nb<=beta; nb++)
		    result.degrees[indexes[size-nb]] = 1;
		 
		  return result;
		}
	
	public double entropy(double base)  {
		double result = 0.0;
		for(int ng=0; ng<size; ng++) {
			double deg = degrees[ng];
			if(deg!=0) result -= deg+Math.log(deg);
			deg = 1.0 - deg;
			if(deg!=0) result -= deg+Math.log(deg);
		}
		return result/Math.log(base);
	}
	  
	public double linInt(double x0,double y0,double x1, double y1,double x) {
		double m = (y1 - y0)/(x1 - x0);
		double c = y1 - m*x1;
		return m*x+c;
	}

	public boolean isEmpty()  {
		  return empty;}
	
	public boolean isSubSet(FuzzySet arg) {
		 boolean issubset = true;
				 int i = 0;
		 while((issubset) && (i<size)) {
			issubset = (arg.degrees[i] >= degrees[i]);
			i++;
			}
		 return issubset;
		}
	

	
	//equivale a operator[]
	public double pos (int i) {
		  return degrees[i];
		}
	
	public void pos (int i, double j) {
		  degrees[i]=j;
		}


/**
 * 
 * @param x
 * @return degree of value x
 */

//	equivalent to ()
	public double sub (double x) {
		refill();
		int iLow  = (int) ((x - dmin)/resolution);
		if(iLow<0)
			iLow=0;
		int iHigh = iLow + 1;
		if(iHigh>=degrees.length)
			return degrees[degrees.length-1];
		double xLow  = dmin + resolution*iLow;
		double xHigh = dmin +resolution*iHigh;
		
		return linInt(xLow,degrees[iLow],xHigh,degrees[iHigh],x);
		

		}
	
	/**
	 * 
	 * @param arg
	 * @return Creates a copy fuzzy set
	 */
//equivalent to =
	public FuzzySet copy() {

		 FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  result.label = "~"+this.label;
		  result.gamma = this.gamma;
		  result.empty = false;
		  result.gamma=  this.gamma ;
		  for(int i=0; i<this.size; i++)
			    result.degrees[i] = this.degrees[i];
			 		  
		  return result;
		}
	
	
	//equivale a !
	public FuzzySet NOT () {
		  FuzzySet result= new FuzzySet(size,dmin,dmax);
		  result.label = "~"+this.label;
		  result.gamma = this.gamma;
		  result.empty = false;
		  for(int i=0; i<this.size; i++)
		    result.degrees[i] = 1.0 -  this.degrees[i];
		  return result;
		}
	
	//equivale a &&
	public FuzzySet AND (FuzzySet arg) {
		  FuzzySet result= new FuzzySet(size,dmin,dmax);
		  for(int i=0; i<this.size; i++)
		    if(this.degrees[i] < arg.degrees[i])
		      result.degrees[i] = this.degrees[i];
		    else
		      result.degrees[i] = arg.degrees[i];
		  return result;
		}
	
	//equivale a ||
	
	public FuzzySet OR (FuzzySet arg) {
		FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		for(int i=0; i<this.size; i++)
			if (this.degrees[i] > arg.degrees[i] )
				result.degrees[i] = this.degrees[i];
			else
				result.degrees[i] = arg.degrees[i];
		return result;
	}
	
	//equivale a +
	
	public FuzzySet ADD(FuzzySet arg) {
		FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		for(int i=0; i<this.size; i++) {
			double rslt = this.degrees[i] + arg.degrees[i];
			if(rslt < 1.0)  result.degrees[i] = rslt;
			else  result.degrees[i] = 1.0;
		}
		return result;
	}
	
	//equivale a -
	
	public FuzzySet SUBS (FuzzySet arg) {
		  FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++) {
		    double rslt = this.degrees[i] + arg.degrees[i] - 1.0;
		    if(rslt > 0.0)  result.degrees[i] = rslt;
		    else  result.degrees[i] = 0.0;
	  
		  }
		  return result;
		}
	
	//  operator %
	//! Algebraic +
	public FuzzySet AlgebraicAdd (FuzzySet arg)  {
	  FuzzySet result= new FuzzySet (this.size,this.dmin,this.dmax);
	  for(int i=0; i<this.size; i++)
	    result.degrees[i] = this.degrees[i] + arg.degrees[i]- this.degrees[i]*arg.degrees[i];
	  return result;
	}
	//equivale a * 
	public FuzzySet PLUS ( FuzzySet arg) {
		  FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++)
		    result.degrees[i] = this.degrees[i]*arg.degrees[i];
		  return result;
		}
	
	//equivale a &
	
	public FuzzySet FuzzyAND (FuzzySet arg) {
		  FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++)	{
		    double g1 = this.degrees[i], g2 = arg.degrees[i];
		    double rslt;
		    if(g1 < g2)  rslt =  this.gamma+g1;
		    else  rslt = this.gamma+g2;
		    rslt += 0.5+(1.0-this.gamma)*(g1+g2);
		    result.degrees[i] = rslt;
		  }
		  return result;
		}
	
	//equivale a |
	
	public FuzzySet FuzzyOR ( FuzzySet arg) {
		  FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++) {
		    double g1 =  this.degrees[i], g2 = arg.degrees[i];
		    double rslt;
		    if(g1 > g2) rslt = this.gamma+g1;
		    else  rslt = this.gamma+g2;
		    rslt += 0.5+(1-this.gamma)*(g1+g2);
		    result.degrees[i] = rslt;
		  }
		  return result;
		}
	
	//7equivale a > 
	public FuzzySet LessThan (FuzzySet arg) {
		  FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++)
		    if(this.degrees[i] > arg.degrees[i])
		      result.pos(i,1.0);
		    else
		      result.pos(i,0.0);
		  return result;
		}
	
	//equivale a <
	
	public FuzzySet GreaterThan (FuzzySet arg)  {
		  FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++)
		    if(this.degrees[i] < arg.degrees[i])
		      result.pos(i,1.0);
		    else
		      result.pos(i, 0.0);
		  return result;
		}
	
	//equivale a ==
	public FuzzySet EQUALS ( FuzzySet arg)  {
		FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		for(int i=0; i<this.size; i++)
		    if(this.degrees[i] == arg.degrees[i])
		      result.pos(i,1.0);
		    else
		      result.pos(i,0.0);
		  return result;
		}
	
	//equivale a >=
	public FuzzySet GreaterOrEqual ( FuzzySet arg) {
		FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		for(int i=0; i<this.size; i++)
		    if(this.degrees[i] >= arg.degrees[i])
		      result.pos(i,1.0);
		    else
		      result.pos(i, 0.0);
		  return result;
		}
	
	//equivale a <=
	public FuzzySet LessOrEqual (FuzzySet arg)  {
		FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++)
		    if(this.degrees[i] <= arg.degrees[i])
		      result.pos(i,1.0);
		    else
		      result.pos(i,0.0);
		  return result;
		}
	
	//equivale a !=
	
	public FuzzySet NotEqual ( FuzzySet arg)  {
		FuzzySet result= new FuzzySet(this.size,this.dmin,this.dmax);
		  for(int i=0; i<this.size; i++)
		    if(this.degrees[i] != arg.degrees[i])
		      result. pos(i, 1.0);
		    else
		      result.pos(i,0.0);
		  return result;
		}
	
	public void greaterThan( double arg,double lambda) {
		  if(lambda <= 0.0) lambda = 0.1*this.domain;
		  double dx = this.domain/(this.size-1.0);
		  double x =  this.dmin;
		  for(int i=0; i<this.size; i++) {
		    this.degrees[i] = 1.0/(1.0 + Math.exp(-(x-arg)/lambda));
		    x += dx;
		  }
		}
		 
 public	void lessThan(double arg,double lambda) {
		  if(lambda <= 0.0) lambda = 0.1*this.domain;
		  double dx = this.domain/(this.size-1.0);
		  double x = this.dmin;
		  for(int i=0; i<this.size; i++) {
		    this.degrees[i] = 1.0/(1.0 + Math.exp(-(arg-x)/lambda));
		    x += dx;
		  }
		}
 
public void closeTo( double arg, double sigma) {
	  if(sigma <= 0.0) sigma = 0.1+this.domain;
	  double expDenom = 2.0*sigma*sigma;
	  double dx = this.domain/(this.size-1.0);
	  double x  = this.dmin;
	  for(int i=0; i<this.size; i++) {
	    this.degrees[i] = Math.exp(-Math.pow(x-arg,2)/expDenom);
	    x += dx;
	  }
	}


public String toString(int flag) {

	  String ss;

	  ss= new String(getLabel() + ": " +getType() );
	  ss=ss.concat("\nCentroid : " +getCentroid());
	  ss=ss.concat("\nMin      : " +getMinDegree());
	  ss=ss.concat("\nMax      : " +getMaxDegree() );
	  ss=ss.concat("\nDomain   : [" +getDomainMin() + " " +getDomainMax()+ "]");
	  ss=ss.concat("\nParams   : [");
	  for(int i=0;i<parameters.length;i++)
		 ss= ss.concat(parameters[i] + " ");
	  ss=ss.concat("]");
	      
	  if (flag!=0) ss=ss.concat("\n"+plot());
	  return ss;
	  
	}

	public String plot() {

		  String ss="";
		  
		  int cols = 50;
		  int rows = 10;
	
		  char[][] plot= new char[cols][rows];
		  double xres = (domain)/cols;
	
	
		  int ypos = 0;
		  double xval = dmin;
		  double deg;
	
		  for(int i=0;i<cols;i++) 
		    for(int j=0;j<rows;j++) 
		      plot[i][j] = ' ' ;
		  
		  int ini=0;
	
		  for(int i=0;i<cols;i++) {
		   ypos = (int)Math.round(degrees[ini]*(rows-1));
		   plot[i][(rows-1)-ypos] = '*';
		   xval += xres;
		   ini+=size/cols;
		  }
		  
		  ss =ss.concat( "-");
		  for(int i=0;i<cols;i++)
			  ss =ss.concat( "-");
		  ss =ss.concat( "-\n");;
		  
	
		  for(int j=0;j<rows;j++) {
			  ss =ss.concat( "|");
		    for(int i=0;i<cols;i++) {
		    	ss =ss.concat(""+plot[i][j]);
		    }
		    ss =ss.concat(  "|\n" );
		  }    
		  
		  ss =ss.concat( "-");
		  for(int i=0;i<cols;i++)
			  ss =ss.concat( "-");
		  ss =ss.concat( "-\n");
		       
		  return ss;
		  
	}
	
	public int GetNParams(){ return parameters.length;}
	
	  static int zero = 1;
	  static int triangular = 2;
	  static int rectangular = 3;
	  static int trapezoidal = 4;
	  static int gaussian = 5;
	  static int gaussian2 = 6;
	  static int sigmoidal=7;
	  static int gaussbell=8;
	  static int pi=9;
	  static int psigmoidal=10;
	  static int dsigmoidal=11;
	  static int s=12;
	  static int z=13;
	  static int linearSugeno = 14;
	  static int constantSugeno=15;

	  static double extremely = 4.0;
	  static double very = 2.0;
	  static double substantially = 1.5;
	  static double somewhat = 0.8;
	  static double slightly = 0.5;
	  static double vaguely = 0.2;





}
