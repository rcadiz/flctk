/* blobMidi.js           */
/* Rodrigo F. Cadiz */
/* 2017             */

// GENERAL ======================================

inlets=1;
outlets=13;
autowatch=1;


var numBlobs = 10;
var maxRectangles = 10;
var maxCircles = 10;

// Arrays
var currentRectanglePitchIndex = -1;
var lastRectangleTime = -1;
var currentRectangleChord = [];

var rectanglePitches = [];
var circlePitches = [];

var rectangleLoops = [];
var circleLoops = [];

var rectanglePitchesIndex = [];
var circlePitchesIndex = [];

var blobPitches = [];


resetIndexes();


function list() {


	//post("List " + arguments.length + "\n");

	for(i=0;i<numBlobs;i++) {

		var id = arguments[i*7+0];
		var x = arguments[i*7+1];
		var y = arguments[i*7+2];
		var z = arguments[i*7+3];
		var r = arguments[i*7+4];
		var c = arguments[i*7+5];
		var m = arguments[i*7+6];

		//post("before\n");
		analyzeBlob(id,x,y,z,r,c,m);
	}
		//post("after\n");

}

function analyzeBlob(id,x,y,z,r,c,m) {


	//post("analyze blob " + id + " " + x + " " + y + " " + z + " " + r + " " + c + " " + m + "\n");

	if (m != -1) {

		// Blob starting
		if (m == 1) {

			if(r != -1 || c != -1) {
				//post("Calling note on on id" + id +"\n");
				noteOn(id,r,c);

			}


		}

		// Blob continuing
		else if (m == 2) {

			if(r != -1 || c != -1) {
				//post("Calling note change on id" + id +"\n");
				noteChange(id,r,c,x,y,z);
				//post("after note change\n");

			}



		}

		// Blob ending
		else if (m == 0) {

			if(r != -1 || c != -1) {
				//post("Calling note off on id " + id + " " + r + " " + c +"\n");
				noteOff(id,r,c);

			}


		}

		// Something creepy happened
		else {
			post("Creepy\n");

		}


	}

	


}

function noteOn(id,r,c) {

	//post ("beggining note on " + id + "\n");

	var pitches = [];
	var channel = 0;
	if (r != -1) {
		pitches = getRectanglePitches(r);
		channel = r;
	}
	else if (c != 1) {
		pitches = getCirclePitches(c);
		channel = c;
	}

	//post ("before blobpitches id " + id + " length " + pitches.length + "\n");

	if (typeof pitches.length != "undefined") {

		blobPitches[id] = pitches;

	//post ("after blobpitches\n");
    
	

		for(i=0;i<pitches.length;i++) {
	//	post("Note on " + pitches[i] + " id " + id + "\n")

			outlet(channel, new Array(parseInt(pitches[i]),100));

		//var midiname = "midiList" + channel;
		//messnamed(midiname,new Array(parseInt(pitches[i]),100));
		}

	}



}


function noteOff(id,r,c) {

	if (r != -1) {
		channel = r;
	}
	else if (c != 1) {
		channel = c;
	}


	if (typeof blobPitches[id].length != "undefined") {
	
	// post("channel " + channel + " id " + id + " length " + blobPitches[id].length + "\n");

		for(i=0;i<blobPitches[id].length;i++) {
	
		//post("Note off " + blobPitches[id][i] + " id " + id + "\n");

			outlet(channel, new Array(parseInt(blobPitches[id][i]),0));

		//var midiname = "midiList" + channel;
		//messnamed(midiname,new Array(parseInt(blobPitches[id][i]),0));
		}
	
	
	blobPitches[id] = [];
	}
}


function noteChange(id,r,c,x,y,z) {

	if (r != -1) {
		channel = r;
	}
	else if (c != 1) {
		channel = c;
	}

	outlet(12,"list",new Array(channel,x,y,z));


}

function addRectanglePitches(id,values) {

	var parray = [];
	var arr = values.split(',');

	for(i=0;i<arr.length;i++) {

		var arr2 = arr[i].split(' ');
		parray[i] = arr2;

	}

	rectanglePitches[id] = parray;

}

function addCirclePitches() {

	var parray = [];
	var arr = values.split(',');

	for(i=0;i<arr.length;i++) {

		var arr2 = arr[i].split(' ');
		parray[i] = arr2;

	}

	circlePitches[id] = parray;
}


function getRectanglePitches(r) {

	var pitches = [];

	if (rectanglePitchesIndex[r] < (rectanglePitches[r].length)) {
		pitches = rectanglePitches[r][rectanglePitchesIndex[r]];
		rectanglePitchesIndex[r] = rectanglePitchesIndex[r]+1;
	}

	return pitches;

}

function getCirclePitches(c) {


	var pitches =  [];


	if (circlePitchesIndex[c] < (circlePitches[c].length)) {
		pitches = circlePitches[c][rectanglePitchesIndex[c]];
		circlePitchesIndex[c] = circlePitchesIndex[c]+1;
	}

	return pitches;

}

function resetIndexes() {
	
	for(i=0;i<maxRectangles;i++) { rectanglePitches[i] = []; rectanglePitchesIndex[i] = 0; rectangleLoops[i] = 0;}
	for(i=0;i<maxCircles;i++) { circlePitches[i] = []; circlePitchesIndex[i] = 0; circleLoops[i] = 0;}
	for(i=0;i<numBlobs;i++) blobPitches[i] = 0;

}


function rewind() {
	
	for(i=0;i<maxRectangles;i++) { rectanglePitchesIndex[i] = 0;}
	for(i=0;i<maxCircles;i++) { circlePitchesIndex[i] = 0; }

}

function setRectangleLoop(r,value) {

	rectangleLoops[r] = value;

}

function setCircleLoop(c,value) {

	circleLoops[c] = value;

}


function resetReadingMidi() {

		currentRectanglePitchIndex = -1;
}

function addPitches() {

	if (arguments.length == 4) {

		var id = arguments[0];
		var time = arguments[2];
		var pitch = arguments[3];

		if (time != lastRectangleTime) {

			rectanglePitches[id][currentRectanglePitchIndex] = currentRectangleChord;
			currentRectangleChord = [];
			currentRectangleChord.push(pitch);
			currentRectanglePitchIndex++;

		}

		else {
			currentRectangleChord.push(pitch);
		}

		lastRectangleTime = time;
		post("pitch added\n");
	}

}

function printRectanglePitches() {

	for (k = 0; k < 2 ; k++) {

		for(i=0;i<rectanglePitches[k].length;i++) {
			var arr = rectanglePitches[k][i];
			for(j=0;j<arr.length;j++){
				post(arr[j] + " ");
			}
			post("\n");
		}

	}

}

