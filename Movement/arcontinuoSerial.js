/**
* arcontinuoSerial.js 
* Rodrigo F. Cadiz 
* 2017 
*/


inlets=1;
outlets=4;
autowatch=1;


var data = [];

var activeBlobs = [];


var DATALENGTH = 9;
var MAXBLOBS = 10;
var EXTRAS = 2;

var maxX = 16;
var maxY = 80;
var maxavgZ = 25;
var maxZ = 100;
var maxA = 110;

var firstData = true;
var countData = 0;

var debug = false;

var through = true;

for (i=0;i<MAXBLOBS;i++) activeBlobs[i] = 0;


function initData() {

	for(i=0;i<DATALENGTH*MAXBLOBS+EXTRAS; i++)
		data[i] = 0;

}

function msg_int(value) {

	if (value == 255) {

		// Not first data to arrive
		if (!firstData) {

			if (countData == DATALENGTH*MAXBLOBS+EXTRAS) {

				processData();
			}

			else {

				if (debug)
					post("Error reading data ...\n");
				if (through)
					outlet(3,"bang");
			}

			countData = 0;
			initData();
		}

		// First time ever, do nothing
		else {

			firstData = false;
		}

	}

	else {

		data[countData] = value;

		if (countData < DATALENGTH*MAXBLOBS+EXTRAS) {
			countData++;
		}

	}

}


function processData() {

	var th = data[0];
	var numBlobs = data[1];
	var dataOut = [];

	for(i=0;i<MAXBLOBS;i++) {

		var dataBlob = [];

		var id = data[i*DATALENGTH+EXTRAS];
			
		dataBlob.push(id);

		var x1 = data[i*DATALENGTH+EXTRAS+1];
		var x2 = data[i*DATALENGTH+EXTRAS+2];
		var x = (x1 + x2*0.00390625)/maxX;
		dataBlob.push(x);

		var y1 = data[i*DATALENGTH+EXTRAS+3];
		var y2 = data[i*DATALENGTH+EXTRAS+4];
		var y = (y1 + y2*0.00390625)/maxY;
		dataBlob.push(y);

		var z1 = data[i*DATALENGTH+EXTRAS+5];
		var z2 = data[i*DATALENGTH+EXTRAS+6];
		var z = (z1 + z2*0.00390625)/maxavgZ;

		dataBlob.push(z);

		var zmax = data[i*DATALENGTH+EXTRAS+7];
		dataBlob.push(zmax/maxZ);

		var area =  data[i*DATALENGTH+EXTRAS+8];
		dataBlob.push(area/maxA);

		//if(through) messnamed("blob" + (i+1),dataBlob);

		dataOut = dataOut.concat(dataBlob);
		
	}
	
	var header = [numBlobs,th];

	if (through)	{
		outlet(0,header.concat(dataOut));
		outlet(1,numBlobs);
		outlet(2,th);
	}

}


function setMaxArea(value) {
	maxA = value;
}

function setMaxX(value) {
	maxX = value;
}

function setMaxY(value) {
	maxY = value;
}

function setMaxZ(value) {
	maxZ = value;
}

function setMaxAvgZ(value) {
	maxavgZ = value;
}

function on() {
	through = true;
	post("Outputting data...\n");
}

function off() {
	through = false;
	post("No output\n");
}