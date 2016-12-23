// obst_gemuese.js
// ========
module.exports = {
  getMarketData: function () {
  	var obst, pollen,salat,gemuese;
  	var food = new Array();
	var handler_obst = function(error, content){
	   
	    if (error){
	      console.log(error);
	    }
	    else{
	      csv.parse(content,{delimiter: ';'}, function(err, output){
	 		 obst = output;
			});
	    } 
	  }
	 var handler_gemuese = function(error, content){
	   
	    if (error){
	      console.log(error);
	    }
	    else{
	     csv.parse(content, {delimiter: ';',comment: '#'}, function(err, output){
	 		 gemuese = output;
	 		 
			});
	    }
	  }
	 var handler_salat = function(error, content){
	   
	    if (error){
	      console.log(error);
	    }
	    else{
	      csv.parse(content, {delimiter: ';',comment: '#'}, function(err, output){
	 		 salat = output;

			});
	    }
	  }
	var handler_pollen = function(error, content){
	   
	    if (error){
	      console.log(error);
	    }
	    else{
	      csv.parse(content, {delimiter: ';',comment: '#'}, function(err, output){
	 		 pollen = output;

			});
	    }
	  }
	fs.readFile('Server/obst_gemuese/obst.csv','utf-8', handler_obst);
	fs.readFile('Server/obst_gemuese/gemuese.csv','utf-8', handler_gemuese);
	fs.readFile('Server/obst_gemuese/salat.csv','utf-8', handler_salat);
	fs.readFile('Server/pollen/pollen.csv','utf-8', handler_pollen);

	var d = new Date();
	var n = d.getMonth();
	n = n+1;
	console.log("month: "+n);
	for (var obstIndex = 1; obstIndex < obst.length-1; obstIndex++){
		//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
		if(obst[obstIndex][n] == 'ja' ||obst[obstIndex][n] == '(ja)' ){
			var currentObst = obst[obstIndex][0];
			var pushObject = new Object();

			pushObject[currentObst] = "http://www.chefkoch.de/rs/s0/"+obst[obstIndex][0]+"/Rezepte.html";
			food.push(pushObject);
		}
	}
	for (var gemueseIndex = 1; gemueseIndex < gemuese.length-1; gemueseIndex++){
		//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
		if(gemuese[gemueseIndex][n] == 'ja' ||gemuese[gemueseIndex][n] == '(ja)' ){
			var currentGemuese = gemuese[gemueseIndex][0];
			var pushObject = new Object();
			pushObject[currentGemuese] = "http://www.chefkoch.de/rs/s0/"+gemuese[gemueseIndex][0]+"/Rezepte.html" ;
			food.push(pushObject);
		}
	}
	for (var salatIndex = 1; salatIndex < salat.length-1; salatIndex++){
		//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
		if(salat[salatIndex][n] == 'ja' ||salat[salatIndex][n] == '(ja)' ){
			var currentSalat = salat[salatIndex][0];
			var pushObject = new Object()
			pushObject[currentSalat] =  "http://www.chefkoch.de/rs/s0/"+salat[salatIndex][0]+"/Rezepte.html";
			food.push(pushObject);
		}
	}
	return food;
  }
};
