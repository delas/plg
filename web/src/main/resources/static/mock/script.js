var processGenerationCounter = 1;
var processes = {};
var selectedProcess = "";

// === FUNCTIONS ===

function msgSuccess(message) {
	toastr.success(message);
}

function msgError(message) {
	toastr.error(message);
}

function refreshProcessesList() {
	$("#list-processes").html("");
	for(var key in processes) {
		$("#list-processes").append('<li class="nav-item"><a class="nav-link select-process" href="#" data-process="' + key + '"><i class="fa fa-file"></i> ' + key + '</a></li>');
	}
}

function renderProcess(name, dot) {
	$("#process-title").text(name);
	d3.select("#process-graph").graphviz().fit(true).renderDot(dot, function() {
		$("#process-graph").removeClass("loading");
		$("#process-controllers").show();
		if (processes[name]["simulation"] == true) {
			$("#simulate").addClass("disabled");
		} else {
			$("#simulate").removeClass("disabled");
		}
	});
}


// === EVENTS ===

$(window).on("load", function() {
	
});

$(document).on("click", "#randomize-process", function() {
	$.ajax({
		url: "/api/v1/processes/randomize",
		success: function(data){
			var newProcessName = "Process " + processGenerationCounter;
			processes[newProcessName] = {};
			processes[newProcessName]["plg"] = String(data);
			processes[newProcessName]["simulation"] = false;
			processGenerationCounter += 1;
			refreshProcessesList();
			msgSuccess("New process (" +newProcessName + ") generated!");
		}
	});
});

$(document).on("click", ".select-process", function() {
	selectedProcess = $(this).data("process");
	
	$("#process-title").text(name);
	$("#process-graph").html("");
	$("#process-graph").addClass("loading");
	
	if (processes[selectedProcess]["dot"] === undefined) {
		process = processes[selectedProcess]["plg"];
		$.ajax({
			type: "POST",
			url: "/api/v1/processes/plg2dot",
			data: {
				'plg': process
			},
			success: function(data){
				processes[selectedProcess]["dot"] = data;
				renderProcess(selectedProcess, data);
			}
		});
	} else {
		renderProcess(selectedProcess, processes[selectedProcess]["dot"]);
	}
	
	if (processes[selectedProcess]["simulation"] == false) {
		$("#simulate-menu").removeClass("disabled");
	} else {
		$("#simulate-menu").addClass("disabled");
	}
});

$(document).on("click", ".select-process", function() {
	selectedProcess = $(this).data("process");
	
	$("#process-title").text(name);
	$("#process-graph").html("");
	$("#process-graph").addClass("loading");
	
	if (processes[selectedProcess]["bpmn"] === undefined) {
		process = processes[selectedProcess]["plg"];
		$.ajax({
			type: "POST",
			url: "/api/v1/processes/plg2bpmn",
			data: {
				'plg': process
			},
			success: function(data){
				processes[selectedProcess]["bpmn"] = data;
				renderProcess(selectedProcess, data);
			}
		});
	} else {
		renderProcess(selectedProcess, processes[selectedProcess]["bpmn"]);
	}
	
	if (processes[selectedProcess]["simulation"] == false) {
		$("#simulate-menu").removeClass("disabled");
	} else {
		$("#simulate-menu").addClass("disabled");
	}
});

$(document).on("click", "#simulate", function() {
	$('#streamConfigModal').modal("hide");
	if (processes[selectedProcess]["simulation"] == false) {
		$("#simulate-menu").addClass("disabled");
		$.ajax({
			type: "POST",
			url: "/api/v1/processes/stream/" + $('#modal-process-name').val(),
			data: {
				'plg': process,
				'broker': $('#modal-broker-name').val(),
				'topic': $('#modal-topic-name').val()
			},
			success: function(data){
				if (data) {
					processes[selectedProcess]["simulation"] = true;
					setTimeout(function() {
						$("#simulate-menu").removeClass("disabled");
						processes[selectedProcess]["simulation"] = false;
					}, 1000 * 60 * 5);
					msgSuccess("Stream started correctly");
				} else {
					msgError("This process is already being streamed");
				}
			}
		});
	} else {
		$("#simulate-menu").addClass("disabled");
		msgError("This process is already being streamed");
	}
});

$(document).on("click", "#export-plg", function(){
	var popup = window.open();
	popup.onload = function() {
		$(popup.document.body).text(processes[selectedProcess]["plg"]);
	};
});

$(document).on("click", "#export-svg", function(){
	var svg = $("#process-graph > svg")[0];
	var serializer = new XMLSerializer();
	var source = serializer.serializeToString(svg);
	//add name spaces.
	if(!source.match(/^<svg[^>]+xmlns="http\:\/\/www\.w3\.org\/2000\/svg"/)){
		source = source.replace(/^<svg/, '<svg xmlns="http://www.w3.org/2000/svg"');
	}
	if(!source.match(/^<svg[^>]+"http\:\/\/www\.w3\.org\/1999\/xlink"/)){
		source = source.replace(/^<svg/, '<svg xmlns:xlink="http://www.w3.org/1999/xlink"');
	}
	source = '<?xml version="1.0" standalone="no"?>\r\n' + source;
	//set url value to a element's href attribute.
	//window.open(url);
	var popup = window.open();
	popup.onload = function() {
		$(popup.document.body).html(source);
	};
});

$('#streamConfigModal').on('show.bs.modal', function (event) {
	$(this).find("#modal-process-name").val(selectedProcess);
});