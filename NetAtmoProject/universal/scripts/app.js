// DOM Elements

var mainEl = document.getElementById('main'),
		navEl = document.getElementById('navbar'),
		navBtnEl = document.getElementById('navBtn'),
		navListEl = document.getElementById('navList'),
		settingBtnEl = document.getElementById('settingsBtn');

var sectionsEls = [],
		currentEl;

var settings = {},
		json = {},
		barCharts = {},
		lineCharts = {};


// Initialize Functions
update();


// Eventlisteners

// Nav Toggle
navEl.addEventListener('click', function(evt){
	var el = evt.target;

	while(el.parentElement){
		if(el.nodeName === 'LI'){

			// Remove active from prev El
			navToggle(el);

			break;
		} else if(el.nodeName === 'INPUT'){
			editSettings(el);
		}else if(el === navBtnEl){
			//navEl.classList.remove('settings');
			navEl.classList.toggle('active');
			break;
		} else if(el === settingBtnEl){
			toggleSettings();
		}
		el = el.parentElement;
	}
}, false);

function navToggle (element) {
	var activeElement = navEl.querySelector('.active');
	if(activeElement) activeElement.classList.remove('active');
	element.classList.add('active');
}

// Get NetAtmo Data
function update() {

	// AJAX Request
	var	request = new XMLHttpRequest();
		request.open('GET', 'update');
		//request.open('GET', 'data.json');

	// ErrorHandling
	request.onerror = function() {
		console.log('Fehler beim Senden der Daten');
	};

	// Status Handling
	request.onload = function() {
		if (this.status >= 200 && this.status < 400){
			json = JSON.parse(this.response);

			build();
			loadSettings();
		} else {
			console.log('Fehler: ' + this);
		}
	}

	request.send();
}

function build(){

	for(item in json){
		buildNavItem(item);
		buildSection(item, json[item]);
	}

	initBarCharts();
	initLineCharts();

	console.log(sectionsEls);
	currentEl = navListEl.querySelector('li:not(.hide)');
}

// Navigation
function buildNavItem(key){

	var li = document.createElement('li'),
			a = document.createElement('a');
	a.href = '#' +	key;

	var img = document.createElement('img');
	img.src = 'images/' + key + '.svg';

	var h = document.createElement('h3');
	h.innerHTML = key.replace('_', ' ');

	var checkbox = document.createElement('input');
	checkbox.type = 'checkbox';
	checkbox.name = key;

	a.appendChild(img);
	a.appendChild(h);
	li.appendChild(a);
	li.appendChild(checkbox);

	navListEl.appendChild(li);
}

// Sections
function buildSection(key, data){
	var section = document.createElement('section');
	section.id = item;

	section.appendChild(addSectionHeader(item));
	section.appendChild(addSectionContent(item, data));

	mainEl.appendChild(section);
	sectionsEls.push(section);
}

function addSectionHeader(key){
	var header = document.createElement('header');

	var h = document.createElement('h1');
	h.innerHTML = item.replace('_', ' ');

	var img = document.createElement('img');
	img.src = 'images/' + item + '.svg';

	header.appendChild(img);
	header.appendChild(h);

	return header;
}

function addSectionContent(item, data){

	var main = undefined;

	if(item === 'forecast' || item === 'device'){
		// Cusom FORECAST || DEVICE
		main = addCustom(item, data);
	} else	if(typeof data === 'object' && Array.isArray(data) ){
		// Food ||	Pollen
		main = addArray(item, data);
	} else {
		// REST = airquality || barometer || co2 || humidity || noise || stresslevel || temperature
		main = addObject(item, data);
	}

	return main;
}

// Custom Sections
function addCustom(item, data){

	var main = document.createElement('main');

	if(item === 'device'){
		main.appendChild(buildDevice(item, data));
		main.appendChild(buildMap(item, data));
	} else if(item === 'forecast'){
		data.forEach(function(day){
			main.appendChild(buildForecast(day));
		});
	}

	return main;
}

function buildForecast(data){

	var section = document.createElement('section'),
			h = document.createElement('h2'),
			figure = document.createElement('figure'),
			img = document.createElement('img'),
			figcaption = document.createElement('figcaption'),
			hTemp = document.createElement('h3'),
			hDescr = document.createElement('h3'),
			hRain = document.createElement('h3'),
			date = date = new Date(data.dt * 1000);

	h.innerHTML = date.toLocaleDateString();
	img.src = 'images/weather/' + data.weather.icon + '.svg';
	hTemp.innerHTML = Math.floor(data.temp.max) + '°C / ' + Math.floor(data.temp.min) + '°C';
	hDescr.innerHTML = data.weather.description;
	hRain.innerHTML = Math.floor(data.rain) + '%';
	hRain.classList.add('rain');

	figure.appendChild(h);
	figcaption.appendChild(hTemp);
	figcaption.appendChild(hDescr);
	figcaption.appendChild(hRain);
	figure.appendChild(img);
	figure.appendChild(figcaption);
	section.appendChild(h);
	section.appendChild(figure);

	return section
}

function buildDevice(item, data){

	var section = document.createElement('section');
	var baseRow = document.createElement('div');
	var stationRow = document.createElement('div');

	var base = 	document.createElement('img');
	base.src = 'images/netatmo_base.svg';
	base.classList.add('left');

	var station = 	document.createElement('img');
	station.src = 'images/netatmo_station.svg';
	station.classList.add('left');

	var wifi = document.createElement('img');
	wifi.src = 'images/wifi_' + data.wifiState + '.svg';

	var fstate = document.createElement('img');
	fstate.src = 'images/fstate_' + data.fstate + '.svg';

	var battery = document.createElement('figure'),
			batteryimg = document.createElement('img'),
			batteryfc = document.createElement('figcaption'),
			batteryvalue = document.createElement('data');

	batteryimg.src = 'images/battery.svg';
	batteryvalue.innerHTML  = data.battery + '%';

	batteryfc.appendChild(batteryvalue);
	battery.appendChild(batteryimg);
	battery.appendChild(batteryfc);

	baseRow.appendChild(base)
	baseRow.appendChild(wifi);
	stationRow.appendChild(station);
	stationRow.appendChild(fstate);
	stationRow.appendChild(battery);
	section.appendChild(baseRow);
	section.appendChild(stationRow)

	return section;
}

function buildMap(item, data){

	var section = document.createElement('section');
	section.classList.add('mapWrapper');

	var map = document.createElement('div');
	map.classList.add('map');


	var alt = document.createElement('data');
	alt.innerHTML = 'Alt: ' + data.alt + 'm';
	var lat = document.createElement('data');
	lat.innerHTML = 'Lat: ' + data.lat.toFixed(3);
	var lng = document.createElement('data');
	lng.innerHTML = 'Lng: ' + data.lng.toFixed(3);

	section.appendChild(map);
	section.appendChild(alt);
	section.appendChild(lat);
	section.appendChild(lng);

	initMap(map, data.lat, data.lng);

	return section;
}

function initMap(element, lat, lng){
	var map;

	var mapOptions = {
		zoom: 16,
		mapTypeControl: false,
		scrollwheel: false
	};

	map = new google.maps.Map(element, mapOptions);

	var pos = new google.maps.LatLng(lat, lng);

	var marker = new google.maps.Marker({
		position: pos,
		map: map
	});

	map.setCenter(pos);
}

// Array Sections
function addArray(item, data){

	var main = document.createElement('main');
	main.classList.add('list');

	data.forEach(function(obj){
		for (value in obj){
			if(typeof obj[value] === 'string'){
				main.appendChild(buildFigure(obj));
			} else if(typeof obj[value] === 'number'){
				main.appendChild(buildProgressBar(obj[value], value));
			}
		}

	});

	return main;
}

function buildFigure(value){
	for (key in value){
		var figure = document.createElement('figure');

		var img = document.createElement('img');
		var path = 'images/food/' + key.replace(' ', '-').replace('ä', 'ae').replace('ö', 'oe').replace('ü', 'ue') + '.jpg';
		img.src = path;

		var figcaption = document.createElement('figcaption');
		var h = document.createElement('h2');
		h.innerHTML = key;

		var a = document.createElement('a');
		a.href = value[key];
		a.target = "_blank";

		figcaption.appendChild(h);
		figcaption.appendChild(a);
		figure.appendChild(img);
		figure.appendChild(figcaption);
	}

	return figure;
}

//Object Sections
function addObject(item, data){

	var main = document.createElement('main');

	if(data.indoor && data.unit){
		main.appendChild(buildValue(data.indoor, data.outdoor, data.unit));
	}

	if(data.percentage){
		main.appendChild(buildProgressBar(data.percentage))
	}

	if(data.flow_indoor){
		main.appendChild(buildFlow(item, data.flow_indoor, data.flow_outdoor, data.unit, data.flow_time_indoor));
	}

	if(data.origin){
		main.appendChild(buildOrigin(item, data.origin));
	}

	return main;
}

function buildValue(indoor, outdoor, unit){

	console.log(indoor, outdoor, unit);

	if(outdoor && outdoor != undefined){
		var data = document.createElement('data');
		var unit = unit ? unit : '';
		var text = indoor + unit + ' | ' + outdoor + unit;
		data.innerHTML = text;
		return data;
	} else {
		var data = document.createElement('data');
		var text = indoor;
		if(unit) text += unit;
		data.innerHTML = text;
		return data;
	}
}


function buildProgressBar(value, label){

	var div = document.createElement('div');

	if(label){
		var headline = document.createElement('h3');
		headline.innerHTML = label;
		div.appendChild(headline);
	}

	var progress = document.createElement('progress');
	progress.min = 0;
	progress.max = 1;
	progress.value = value;

	div.appendChild(progress)

	return div;
}

function buildOrigin(item, value){

	var container = document.createElement('div');
	container.id = item + '_bar';


	if(Array.isArray(value)){

		var data = {
			chart: {
				renderTo: item + '_bar',
				type: 'column'
			},
			title: {
				text: item,
				x: -20 //center
			},
			xAxis: {
				categories: []
			},
			yAxis: {
				title: {
					text: item
				},
				plotLines: [{
					value: 0,
					width: 1,
					color: '#808080'
				}]
			},
			legend: {
				layout: 'vertical',
				align: 'right',
				verticalAlign: 'middle',
				borderWidth: 0
			},
			series: [
				{
					name: 'Origins',
					data: []
				}
			]
		}

		value.forEach(function(origin){
			for(key in origin){
				data.xAxis.categories.push(key.charAt(0).toUpperCase() + key.slice(1));
				data.series[0].data.push(origin[key]);
			}
		});

		barCharts[item + '_bar'] = data;

	}

	return container;
}

function buildFlow(item, indoor, outdoor, unit, time){

	var container = document.createElement('div');
	container.id = item + '_flow';

	if(Array.isArray(indoor)){

		var data = {
			chart: {
				renderTo: item + '_flow'
			},
			title: {
				text: item,
				x: -20 //center
			},
			xAxis: {
				categories: [],
				crosshair: true
			},
			yAxis: {
				title: {
					text: (item + ' (' + unit + ')')
				},
				plotLines: [{
					value: 0,
					width: 1,
					color: '#808080'
				}]
			},
			tooltip: {
				valueSuffix: unit,
        crosshairs: true,
        shared: true
			},
			legend: {
				layout: 'vertical',
				align: 'right',
				verticalAlign: 'middle',
				borderWidth: 0
			},
			series: [
				{
					name: 'Indoor',
					data: []
				}
			]
		}

		if(outdoor){
			data.series.push({name: 'Outdoor', data: []});
		}

		for (var i = indoor.length - 1; i >= 0; i--) {
			var date = new Date(time * 1000 - 30 * 60 * 1000 * i);
			data.xAxis.categories.push(date.getHours() + ':' + date.getMinutes());

			data.series[0].data.push(indoor[i]);

			if(outdoor){
				data.series[1].data.push(outdoor[i]);
			}
		};

		lineCharts[item + '_flow'] = data;
	}

	return container;
}

function initBarCharts(options){
	for (barChart in barCharts){
		var chart = new Highcharts.Chart(barCharts[barChart]);
	}
}

function initLineCharts(options){
	for (lineChart in lineCharts){
		var chart = new Highcharts.Chart(lineCharts[lineChart]);
	}
}

// Settings
// Load Settings
function loadSettings(){

	settings = JSON.parse(localStorage.getItem('settings'));

	if(settings === null){
		settings = {};
		for (key in json){
			settings[key] = true;
		}
	}

	for(setting in settings){
		if(settings[setting] === true){
			navEl.querySelector('input[name=' + setting + ']').checked = true;
		} else {
			navEl.querySelector('input[name=' + setting + ']').parentElement.classList.add('hide');
			document.getElementById(setting).classList.add('hide');
		}
	}
}

function editSettings(el){
	settings[el.name] = el.checked;
	localStorage.setItem('settings', JSON.stringify(settings));
	navList.querySelector('input[name=' + el.name + ']').parentElement.classList.toggle('hide');
	document.getElementById(el.name).classList.toggle('hide');
}

// Toggle Settings
function toggleSettings(){
	navEl.classList.toggle('settings');
}

window.addEventListener('scroll', function(evt){
	var currPos = window.scrollY + window.innerHeight * 0.33;

	for (var i = 0; i < sectionsEls.length ; i++) {

		var el = sectionsEls[i],
				parent = el;

		var top = parent.offsetTop,
				bottom = 0;

		// Here not needed >> parents have no padding or margin
		/*while(parent = parent.offsetParent){
			top += parent.offsetTop;
		}*/

		bottom = top + sectionsEls[i].offsetHeight;

		if (currPos >= top && currPos <= bottom && currentEl.id != el.id) {
			navToggle(navEl.querySelector('[href="#'+el.id+'"]').parentElement);
			currentEl = el;
		}
	};
}, false);