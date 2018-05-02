/** 
* SPM.js           
* Rodrigo F. Cadiz 
* 2017             
*/

// GENERAL ======================================

inlets=3;
outlets=3;
autowatch=1;

var DATALENGTH = 6;
var EXTRAS = 2;

var debug = 0;
var draw = true;

// Geometry
var width = 180;
var height = 840;
var radius = 0.3;

// change for a longer or shorter tail
var maxRectangles = 50;
var maxCircles = 50;

var numRectangles = 0;
var currentRectangle = 0;

var numCircles = 0;
var currentCircle = 0;

var rectangles = [];
var circles = [];

// Check if these are used

var inRectangle = -1;
var inRectangleBang = false;


// =========================================================================== BLOBS 

var numBlobs = 10;
var blobs = [];

var outMatrix = new Array(0,0,0,0,0,0,0,0,0,0);


// Initialize graphics 
mgraphics.init();
mgraphics.relative_coords = 1;
mgraphics.autofill = 0;

// FUNCTIONS


setup();

/*function loadbang() {
	
	post("loadbang\n");
	setup();
}*/

function setup() {


	for(i=0;i<numBlobs;i++) {
		outMatrix[i] = new Array(i,0,0,0,0,0,0); 
	}

	for(i=0;i<numBlobs;i++) {
		blobs.push(new Blob(i,0,0,0,0,0));		
		//blobToMatrix(blobs[i]);
	}

	mgraphics.redraw();
}

function absToRelativeX(value,max) {

	return (value*2.0/max)-1;

}

function absToRelativeY(value,max) {

	return -((value*2.0/max)-1);

}

function normToRelativeX(value) {

	return absToRelativeX(value,1);
}

function normToRelativeY(value) {

	return absToRelativeY(value,1);
}


// Blob class definition
function Blob(id,x,y,z,maxz,area) {
	
	this.id = id;
	this.x = x;
	this.y = y;
	this.z = z;
	this.maxz = maxz;
	this.area = area;
	this.color = new Array(1.0,1.0,1.0);

	this.init_x = 0;
	this.init_y = 0;
	this.init_z = 0;
	this.init_maxz = 0;
	this.init_area = 0;

	this.circle = -1;
	this.rectangle = -1;

	this.note = -1;

	this.isActive = function() {
		return this.x && this.y && this.area;
	}

	if (debug)
		post ("Blob created " + id + " " + x + " " + y + " " + z + " " + maxz + " " + area + "\n");
	
}


// ======================================================================= RECTANGLES 

// Rectangle class definition
function Rectangle(x,y,w,h) {
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
	this.color = new Array(0.9,0.6,0.6);
	
	this.isActive = function(x,y) {
		if ( (x > this.x) && (x < (this.x+this.w)) && (y > this.y) && (y < (this.y+this.h)) ){
			this.color = new Array(0.3,1.0,0.5);
			return true;
		}
		else {
			this.color = new Array(0.9,0.6,0.6);
			return false;
		}
	};	
}


// Circle class definition
function Circle(x,y,r) {
	this.x = x;
	this.y = y;
	this.r = r;
	this.color = new Array(0.9,0.6,0.6);
	
	this.isActive = function(x,y) {
		if (x > (this.x-this.r) && x < (this.x+this.r) && y > (this.y-this.r) && y < (this.y+this.r)) {
			this.color = new Array(0.0,1.0,0.0);
			return true;
		}
		else {
			this.color = new Array(0.6,0.6,0.6);
			return false;
		}
	};	
}


////////////////////////////////// MAIN ///////////////////////////////////




// do the actual drawing
function paint() {

	var scrwidth = box.rect[2] - box.rect[0];
	var scrheight = box.rect[3] - box.rect[1];


	var aspect = scrwidth/scrheight;
	
	with (mgraphics) {

		set_source_rgb(0.,0.,0.);
		rectangle(-1*aspect,1,2,2);
		fill();

		if (draw == true) {

			for (i=0; i<numRectangles; i++) {
				var r = rectangles[i];
				set_source_rgb(r.color);

				var rx = absToRelativeX(r.x,width);
				var ry = absToRelativeY(r.y,height);
				var rw = (r.w/width)*2;
				var rh = (r.h/height)*2;
				rectangle(aspect*rx,ry,aspect*rw,rh);

				fill();			
			}

			for (i=0; i<numCircles; i++) {
				var c = circles[i];
				set_source_rgb(c.color);

				var cx = absToRelativeX(c.x,width);
				var cy = absToRelativeY(c.y,height);
				var cr = (c.r/width)*2;
				var cry = (c.r/height)*2;

				ellipse(aspect*cx,cy,cr,cry);
				fill();			
			}
		
			for (i=0; i<numBlobs; i++) {
				var b = blobs[i];
				if (b.isActive()) {
					
					set_source_rgb(b.color);

					var ex = normToRelativeX(b.x);
					var ey = normToRelativeY(b.y);
					var er = b.z*radius;
					var ery = b.z*radius;	

					ellipse((aspect*ex-er*0.5),(ey+ery*0.5),er,ery);
					fill();		
				}
			}

		}
	}
}

// on a click, update and paint
function onclick(x,y,but,cmd,shift,capslock,option,ctrl)
{
	
	if (shift) {
		addRectangle(x,y);
	}
	else if (option) {
		addCircle(x,y);
	}
	else {
		//checkInRectangles(x,y);
	}
}

// on a click-draw, update and paint
function ondrag(x,y,but,cmd,shift,capslock,option,ctrl) {
	
	if (shift) {
		if (but == 0) {
			currentRectangle = -1;
			outlet(0,numRectangles+numCircles);
		}
		else 
			updateRectangle(x,y);
	}
    else if (option) {
		if (but == 0) {
			currentCircle = -1;
			outlet(0,numRectangles+numCircles);
		}
		else 
			updateCircle(x,y);
	}
}

// on idle, update the current location
function onidle(x,y,but,cmd,shift,capslock,option,ctrl)
{
	
}

// on idle outside the box, update the current location
function onidleout(x,y,but,cmd,shift,capslock,option,ctrl)
{

}

// on a tick, update and paint
function ontick() {
	post('tick');
}

// add a rectangle
function addRectangle(x,y,w,h) {

	var width = 1;
 	var height = 1;
	if(typeof w !== "undefined") {width = w;}
	if(typeof h !== "undefined") {height = h;}	
	
	rectangles[numRectangles] = new Rectangle(x,y,width,height);
	currentRectangle = numRectangles;
	numRectangles++;
	outlet(0,numRectangles+numCircles);
	mgraphics.redraw();
}

// update a rectangle
function updateRectangle(mousex, mousey) {
	
	var x = rectangles[currentRectangle].x;
	var y = rectangles[currentRectangle].y;	
	rectangles[currentRectangle].w = mousex-x;
	rectangles[currentRectangle].h = mousey-y;
	//post("drag "+xx+" "+yy+"\n");
	mgraphics.redraw();
}

// add a rectangle
function addCircle(x, y, r)
{
	var rr = 1;
	if(typeof r !== "undefined") {rr = r;}
	circles[numCircles] = new Circle(x,y,rr);
	currentCircle = numCircles;
	numCircles++;
	outlet(0,numRectangles+numCircles);
	mgraphics.redraw();
}


// update a rectangle
function updateCircle(mousex, mousey) {
	
	var x = circles[currentCircle].x;
	var y = circles[currentCircle].y;	
	circles[currentCircle].r = mousex-x;
	mgraphics.redraw();
}

function list() {

	if (arguments.length == DATALENGTH*numBlobs+EXTRAS) {

		var activeBlobs = arguments[0];
		var threshold = arguments[1]; 

		for(i=0;i<numBlobs;i++) {

			var id = (arguments[i*DATALENGTH+EXTRAS]-1);
			var x = arguments[i*DATALENGTH+EXTRAS+1]; 	
			var y = arguments[i*DATALENGTH+EXTRAS+2]; 	
			var z = arguments[i*DATALENGTH+EXTRAS+3]; 	
			var maxz = arguments[i*DATALENGTH+EXTRAS+4]; 
			var area = arguments[i*DATALENGTH+EXTRAS+5]; 	

			//post("calling update " + id + "\n");
			updateBlob(id,x,y,z,maxz,area,getBlobColor(id));


		}


	}


}

// ================================== UPDATE BLOB ===================================

function updateBlob(id,x,y,z,maxz,area,color) {
	
	//post("update " + id + "\n");
	//post("update " + id + " " + x + " "+ y + " "+ z + " "+ maxz + " "+ area + " " + color + "\n");


	var blob = blobs[id];
	
	if (!blob.x && !blob.y && !blob.area) {  // Blob was zero
	
		
		if (x && y && area) { // Blob is not zero, blob starting
			blob.init_x = x;
			blob.init_y = y;
			blob.init_z = z;
			blob.init_maxz = maxz;
			blob.init_area = area;

			blob.x = x;
			blob.y = y;
			blob.z = z;
			blob.maxz = maxz;
			blob.area = area;

			blob.color = color;

			blob.note = 1;

			r = blobInRectangle(x,y);
			if (r != -1) {
				if (debug)
					post("Blob " + id + " in rectangle " + r + "\n");
				blob.rectangle = r;

			}
			c = blobInCircle(x,y);
			if (c != -1) {
				if(debug)
					post("Blob " + id + " in circle " + c + "\n");
				blob.circle = c;
			}


			if (debug)
				post("Blob started "+ blob.init_x + " " + blob.init_y + " " + blob.init_z + " " + blob.init_area + "\n");
		}
		
		else { // Blob continues to be zero

			blob.rectangle = -1;
			blob.circle = -1;
			blob.note = -1;
			
			//if (debug)
			//	post("No blob " + id + "\n");
			
		}
		
		
	}
	
	else { // Blob was not zero, blob is active

		
		if (x && y && area) { // Blob is not zero, blob continuing
			
			blob.x = x;
			blob.y = y;
			blob.z = z;
			blob.area = area;
			
			blob.note = 2;

			if (debug)
				post("Blob going on "+ id + "\n");
		
		}
		
		else { // Blob is ending, now is zero 
				
			blob.x = 0;
			blob.y = 0;
			blob.z = 0;
			blob.area = 0;
			blob.note = 0;
			
			
			if (debug)
				post("Blob gone "+ id + "\n");
			
		}
	}

	
	blobToMatrix(blob);

	if (blob.id == 9) {
		outlet(2,"bang");
	
		mgraphics.redraw();
	}

	

}


function blobInRectangle(x,y) {

	var inRectangle = -1;
	
	for(i=0;i<numRectangles;i++) {
		var r = rectangles[i];
		if (r.isActive(x*width,y*height)) {
	
			inRectangle = i;
			break;
		}
	}
	return inRectangle;		
}



function blobInCircle(x,y) {	
	
	var inCircle = -1;
	
	for(i=0;i<numCircles;i++) {
		var c = circles[i];
		if (c.isActive(x*width,y*height)) {
			
			inCircle = i; 
			break;
		}
	}
	return inCircle;
}


function blobToMatrix(blob) {

	//post("blobToMatrix\n");
	setMatrixCell(blob.id,0,blob.id);
	setMatrixCell(blob.id,1,blob.x - blob.init_x);
	setMatrixCell(blob.id,2,blob.y - blob.init_y);
	setMatrixCell(blob.id,3,blob.z);
	setMatrixCell(blob.id,4,blob.rectangle);
	setMatrixCell(blob.id,5,blob.circle);
	setMatrixCell(blob.id,6,blob.note);


	var arrOut = [];

	if(blob.id == 9) {
		for(i=0;i<numBlobs;i++) {
			for(j=0;j<7;j++)
				arrOut.push(outMatrix[i][j]);
		}
		outlet(1,"list",arrOut);
	}
}

function setMatrixCell(i,j,value) {

	outMatrix[i][j] = value;

}


function clear() {
	numRectangles = 0;
	currentRectangle = 0;

	numCircles = 0;
	currentCircle = 0;

	rectangles = [];
	circles = [];
	
	for(var i=0;i<numBlobs;i++) {
		blobs[i] = new Blob(i,0,0,0,0,0);
		blobToMatrix(blobs[i]);
	}
	outlet(0,numRectangles+numCircles);
	//outlet(1,"jit_matrix",outMatrix.name);
	
	mgraphics.redraw();
}

function verbose(value) {

	debug = value;

}

function getBlobColor(id) {


	var c = [];

	switch(id) {

		case 1:
			c = new Array(1.0,0.0,0.0);
			break;

		case 2:
			c = new Array(0.0,1.0,0.0);
			break;

		case 3:
			c = new Array(0.0,0.0,1.0);
			break;

		case 4:
			c = new Array(1.0,1.0,0.0);
			break;			

		case 5:
			c = new Array(0.0,1.0,1.0);
			break;

		case 6:
			c = new Array(1.0,0.0,1.0);
			break;

		case 7:
			c = new Array(1.0,1.0,1.0);
			break;

		case 8:
			c = new Array(1.0,0.5,0.5);
			break;			

		case 9:
			c = new Array(0.5,1.0,0.5);
			break;

		case 0:
			c = new Array(0.5,0.5,1.0);
			break;	

	}

	return c;

}

function setDraw(value) {
	draw = value;
}




