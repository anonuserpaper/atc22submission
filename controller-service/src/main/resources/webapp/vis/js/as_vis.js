// Add keypress listeners
// uncomment the line below to enable keyboard functionality
//addListeners();

// SETUP
var toggle = 0; // for node selection/highlighting

// Start with a clear graph to allow loading in the background
var node_opacity_clear = 0.05;
var link_opacity_clear = 0.05;

// use these values for nodes/links that have activity
var node_opacity = 1;
var link_opacity = 0.8;

// victim stats
var victim_radius = 60;
var victim_text = 45; 
// Create request for .json file and load into javascript object
// This is a synchronous request and will cause the browser to complain.
var request = new XMLHttpRequest();
request.open("GET", "js/as_info.json", false);
request.send(null);
var graphData = JSON.parse(request.responseText);
var graph_links = graphData.links;
var graph_nodes = graphData.nodes;

// Initialize SVG
var svg = d3.select("svg"),
width = +svg.attr("width"),
height = +svg.attr("height");

// Enable pan and zoom
svg.append("rect")
.attr("width", width)
.attr("height", height)
.style("fill", "none")
.style("pointer-events", "all")
.call(d3.zoom()
.scaleExtent([0.1,10])
.on("zoom", zoomed));

// if a separate zoom var is not defined, the animation could be very choppy because
// the zoom and the interaction are all part of the same process flow (race condition)
// since we separate the zoom and "pipe" it into the interaction, the animation is much smoother
var gr = svg.append("g");

// add arrows
// this should still be improved, by making the arrows flush with the node boundaries
var arrowsize = 10;
var asHalf = arrowsize / 2;

// Arrow definition and calculations.
gr.append("defs").selectAll("marker")
.data(["attack"])
.enter().append("marker")
.attr("id", function(d) { return d; })
.attr("viewBox", "0 -5 " + arrowsize + " " + arrowsize)
.attr("refX", arrowsize)
.attr("refY", 0)
.attr("markerWidth", 6)
.attr("markerHeight", 6)
.attr("orient", "auto")
.attr("class","arrowhead-light")
.append("path")
.attr("d", "M 0," + (asHalf * -1.0) + " L " + arrowsize + ",0 L 0," + asHalf)
.style("fill", "red");

// Arrow definition and calculations.
gr.append("defs").selectAll("marker")
.data(["defend"])
.enter().append("marker")
.attr("id", function(d) { return d; })
.attr("viewBox", "0 -5 " + arrowsize + " " + arrowsize)
.attr("refX", arrowsize)
.attr("refY", 0)
.attr("markerWidth", 6)
.attr("markerHeight", 6)
.attr("orient", "auto")
.attr("class","arrowhead-light")
.append("path")
.attr("d", "M 0," + (asHalf * -1.0) + " L " + arrowsize + ",0 L 0," + asHalf)
.style("fill", "green");


// Color Scheme
var color = d3.scaleOrdinal(d3.schemeCategory20);


// Loading will always take a while due to calculation, but dragging has been turned off
// so after the graph is finished loading, no movement will occur
var simulation = d3.forceSimulation()
.force("link", d3.forceLink().id(function(d) { return d.id; }))
.force("charge", d3.forceManyBody().strength(-100))
.force("center", d3.forceCenter(width / 2, height / 2));

// Adjust these values to account for animation speed and decay
simulation.alphaDecay(0.2);
simulation.velocityDecay(0.3);

// Set up link characteristics
var link = gr.append("g")
.attr("class", "links")
.selectAll("line")
.data(graph_links)
.enter().append("line")
.style("stroke", 'black')
.style("stroke-opacity", link_opacity)
.attr('id', function(d){ return "name" + d.source + '-' + d.target; })
.attr("stroke-width", 1); /* Uncomment this function to restore proportion of width to # of connections function(d) { return Math.sqrt(d.value); } */

// Set up node characteristics
// Set up node group
var node = gr.selectAll(".node")
.data(graph_nodes)
.enter().append("g")
.attr("class", "node")
.attr('id', function(d){ return "name" + d.id; });

// Add circle shape to node group
node.append("circle")
.attr("r", 5)
.style("stroke-opacity", node_opacity)
//.on("click", connectedNodes)
.style("fill", function(d) { return color(d.group); });

// Add text to node group
node.append("text")
.attr("dx", 3.5)
.attr("dy", 3.5)
.attr("font-family", "sans-serif")
.attr("font-size", 10)
.text(function(d) { return d.id });


// Node / Link simulation
simulation.nodes(graph_nodes)
.on("tick", ticked);

simulation.force("link")
.links(graph_links);

initOff();


// Immediately turn everything off
function initOff(){
  node.style("stroke-opacity", function (o) {
    this.setAttribute("fill-opacity", node_opacity_clear);
  });

  link.attr("marker-end", "");

  link.style("stroke-opacity", link_opacity_clear);
}



// Create index for all links in graph
var linkedByIndex = {};

// Add self to index
for (i = 0; i < graph_nodes.length; i++) {
  linkedByIndex[i + "," + i] = 1;
};

// Load index of all source and destination nodes
graph_links.forEach(function (d) {
  linkedByIndex[d.source.index + "," + d.target.index] = 1;
});

function neighboring(a, b) {
  return linkedByIndex[a.index + "," + b.index];
}

// This is the function that handles the click toggling
function connectedNodes() {

  if (toggle == 0) {

    var d = d3.select(this).node().__data__;

    node.style("stroke-opacity", function (o) {
      var op = neighboring(d, o) | neighboring(o, d) ? node_opacity : 0.05;
      this.setAttribute("fill-opacity", op);
      return op;
    });

    link.attr("marker-end", function (o) {
      return o.source === d | o.target === d ? "url(#attack)" : "";
    });

    link.style("stroke-opacity", function (o) {
      return o.source === d | o.target === d ? link_opacity : 0.05;
    });

    toggle = 1;
    } else {

    node.style("stroke-opacity", function (o) {
    this.setAttribute("fill-opacity", node_opacity);
    });

    link.attr("marker-end", "");

    link.style("stroke-opacity", link_opacity);
    toggle = 0;
  }

}

function ticked() {

  link
  .attr("x1", function(d) { return d.source.x; })
  .attr("y1", function(d) { return d.source.y; })
  .attr("x2", function(d) { return d.target.x; })
  .attr("y2", function(d) { return d.target.y; });

  node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
}

//});

function nclick() {
  d3.select(this).attr("r", 12);
}

function ndblclick() {
  d3.select(this).attr("r", 5);
}

function zoomed() {
  gr.attr("transform", d3.event.transform);
}

function everythingOff(){
  node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity_clear);
    });

  link.attr("marker-end", "");

  link.style("stroke-opacity", link_opacity_clear);
}

function everythingOn(){
  node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

    link.attr("marker-end", "");

    link.style("stroke-opacity", link_opacity);
}

function focusDefender(selected){
  var s_node = d3.select("#name" + selected);

  // set source attributes & opacity
    s_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

    s_node.select("circle").attr("r", 12);
    s_node.select("circle").style("fill", "orange");
    s_node.select("text").attr("font-size", 15);

}

function focusVictim(target){
var d_node = d3.select("#name" + target);
d_node.select("circle").style("fill", "purple");
    d_node.style("stroke-opacity", function (o) {
          this.setAttribute("fill-opacity", node_opacity);
        });

    d_node.select("circle").attr("r", victim_radius);
    d_node.select("text").attr("font-size", victim_text);

}

function focusBad(source,target){

  // Grab the corresponding nodes and links
    var s_node = d3.select("#name" + source);
    var d_node = d3.select("#name" + target);
    var link = d3.select("#name" + source+ '-' + target);

    // set source attributes & opacity
    s_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

    s_node.select("circle").attr("r", 12);
    s_node.select("text").attr("font-size", 15);


    if(target === 23028){
    d_node.select("circle").style("fill", "purple");
    d_node.style("stroke-opacity", function (o) {
          this.setAttribute("fill-opacity", node_opacity);
        });

    d_node.select("circle").attr("r", victim_radius);
    d_node.select("text").attr("font-size", victim_text);
    } else {
    // set target attributes & opacity
    d_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

    d_node.select("circle").attr("r", 12);
    d_node.select("text").attr("font-size", 15)
    }

    // set link attributes & opacity
    link.attr("stroke-width", 10);
    link.style("stroke", 'red')
    link.attr("marker-end", "url(#attack)");
    link.style("stroke-opacity", link_opacity);
}

function focusGood(source,target){

  // Grab the corresponding nodes and links
    var s_node = d3.select("#name" + source);
    var d_node = d3.select("#name" + target);
    var link = d3.select("#name" + source+ '-' + target);

    // set source attributes & opacity
    s_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

    s_node.select("circle").attr("r", 12);
    s_node.select("text").attr("font-size", 15);

    if(target === 23028){
    d_node.select("circle").style("fill", "purple");
    d_node.style("stroke-opacity", function (o) {
          this.setAttribute("fill-opacity", node_opacity);
        });

    d_node.select("circle").attr("r", victim_radius);
    d_node.select("text").attr("font-size", victim_text);

    } else {
    // set target attributes & opacity
    d_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

    d_node.select("circle").attr("r", 12);
    d_node.select("text").attr("font-size", 15)
    }
    // set link attributes & opacity
    link.attr("stroke-width", 10);
    link.style("stroke", 'blue')
    //link.attr("marker-end", "url(#defend)"); uncomment if we want green arrows
    link.style("stroke-opacity", link_opacity);
}

function removePath(source,target){
    var s_node = d3.select("#name" + source);
    var d_node = d3.select("#name" + target);
    var link = d3.select("#name" + source + '-' + target);

    // set source attributes & opacity
    s_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity_clear);
    });

    s_node.select("circle").attr("r", 5);
    s_node.select("text").attr("font-size", 10);
	
    if(target === 23028){
    d_node.select("circle").style("fill", "purple");
    d_node.style("stroke-opacity", function (o) {
          this.setAttribute("fill-opacity", node_opacity);
        });

    d_node.select("circle").attr("r", victim_radius);
    d_node.select("text").attr("font-size", victim_text);

    } else {

    // set target attributes & opacity
    d_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity_clear);
    });

    d_node.select("circle").attr("r", 5);
    d_node.select("text").attr("font-size", 10)
	}
    // set link attributes & opacity
    //link.attr("stroke-width", 1); leave past paths enlarged
    link.style("stroke", 'black')
    link.attr("marker-end", "");
    link.style("stroke-opacity", link_opacity_clear);
}

function removeDef(selected){
  var s_node = d3.select("#name" + selected);
  // set source attributes & opacity
    s_node.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity_clear);
    });

    s_node.select("circle").attr("r", 5);
    s_node.style("fill", function(d) { return color(d.group); });

    s_node.select("text").attr("font-size", 10);
}



function highlight(source,target){
  var node_color = "green";
  var link_color = "green";
  var d_node = d3.select("#name" + source);
    d_node.select("circle").style("fill", node_color);

    var d_node = d3.select("#name" + target);
    d_node.select("circle").style("fill", node_color);

    var d_link = d3.select("#name" + source + "-" + target);
    d_link.style("stroke", link_color);
}

function unhighlight(source,target){
  var d_node = d3.select("#name" + source);
    d_node.select("circle").style("fill", function(d) { return color(d.group); });

    var d_node = d3.select("#name" + target);
    d_node.select("circle").style("fill", function(d) { return color(d.group); });

    var d_link = d3.select("#name" + source + "-" + target);
    d_link.style("stroke", 'red');
}



//var json_pull_1 = '{"timestr":"1186261165","defenders":[],"attackPaths":["45891,60725,174,23028","16276,1299,174,23028","8075,174,23028","3301,1299,174,23028","37053,3741,174,23028","23693,7713,174,23028","16276,1299,174,23028"]}';
//var json_pull_2 = '{"timestr":"1186261199","defenders":[],"attackPaths":["45891,60725,174,23028","16276,1299,174,23028","209,174,23028","2559,7473,12989,23028","8075,174,23028","3301,1299,174,23028","2559,7473,12989,23028","2559,7473,12989,23028","37053,3741,174,23028","23693,7713,174,23028","16276,1299,174,23028"]}';
//var json_pull_3 = '{"timestr":"1186261219","defenders":[],"attackPaths":["45891,60725,174,23028","3301,1299,174,23028","4694,2914,174,23028","17676,6939,23028","17676,6939,23028","3320,174,23028","8075,174,23028","8075,174,23028","31399,3320,174,23028","37053,3741,174,23028","23693,7713,174,23028"]}';
//var json_updated = '{"timestr": "1186261165","defenders": [60725,174, 1299], "goodSeg": ["60725,174","1299,174","174,23028"], "badSeg": ["45891,60725","16276,1299","8075,174","3301,1299","37053,3741","3741,174","23693,7713","7713,174","16276,1299"]}';

setInterval(function(){getSimulationInfoAndUpdate();},2000);
setInterval(function(){focusVictim(23028);},1);


// Highlight victim 23028
var victim = d3.select("#name" + "23028");
victim.select("circle").style("fill", "purple");
victim.style("stroke-opacity", function (o) {
      this.setAttribute("fill-opacity", node_opacity);
    });

victim.select("circle").attr("r", 24);
victim.select("text").attr("font-size", 28);


// for storing previous paths
// The main idea:
// 1. Check for new paths compared to the current. When starting out, all paths will be new
// 2. Ignore existing paths, don't re-draw them
// 3. Remove all inactive paths
// 4. Only draw brand-new paths
// 5. brand-new paths now become existing paths
var goodPathsCache = {};
var badPathsCache = {};
var defendersCache = {};

function getSimulationInfoAndUpdate(){
  var simulationTrafficURL = "http://0.0.0.0:8081/Anon/visualization/topology";
  $.getJSON(simulationTrafficURL, function(receivedJSON) {

  //var receivedJSON = JSON.parse(data);

  var time = receivedJSON["timestr"];

  var defCount = receivedJSON["defenders"].length;
  var goodCount = receivedJSON["goodSeg"].length;
  var badCount = receivedJSON["badSeg"].length;

  document.getElementById("timestep").innerHTML = "Current Timestep: " + time;

  var infoForDisplay = "Graph Stats: Defender Count: " + defCount + " | Good Segment Count: " + goodCount + " | Bad Segment Count: " + badCount;
  document.getElementById("graphInfoDisplay").innerHTML = infoForDisplay;

  var defenders = receivedJSON["defenders"];
  var goodPaths = receivedJSON["goodSeg"];
  var badPaths = receivedJSON["badSeg"];

  // return paths for update, remove inactive paths
  var newDefenders = defenderCheck(defenders);
  var newGoodPaths = goodPathCheck(goodPaths);
  var newBadPaths = badPathCheck(badPaths);

  // only update the links that are actually new, don't redraw existing paths
  updateGraph(newDefenders, newGoodPaths, newBadPaths);

  });

}

// check functions
var checkDefenders = function(value){ return defendersCache[value] === true;};
var checkGoodExist = function(value){ return goodPathsCache[value] === true;};
var checkBadExist = function(value){ return badPathsCache[value] === true;};

// check for defender existence, remove non-defenders
function defenderCheck(defend){
  var exists = []; // this array will contain all existing defenders
  var brandnew = []; // this array will contain all the brand new defenders

  for(var i = 0 ; i < defend.length ; i++){ // iterate through the current array of good paths
    var def = defend[i];

    if(checkDefenders(def) === true){ // if the path exists
      exists.push(def); // add the path to the existing array
      delete defendersCache[def]; // remove path from the current cache
    } else { // if the path doesn't exist
      brandnew.push(def); // add the path to the brand new array
    }
  }

  // At this point, the defendersCache should contain all the paths that are no longer active in the current path check
  // Remove all inactive defenders ( change back to default)
  for(var key in defendersCache){
    removeDef(key);
  }

  defendersCache = {}; // set defendersCache to a new empty object

  // Load all existing data back into defendersCache
  for(var i = 0 ; i < exists.length ; i++){
    defendersCache[exists[i]] = true;
  }

  // Load all brand new data back into defendersCache
  for(var i = 0 ; i < brandnew.length ; i++){
    defendersCache[brandnew[i]] = true;
  }

  // Finally, return all brand new data to be drawn
  return brandnew;

}

// check for good path existence, remove inactive paths
function goodPathCheck(good){

  var exists = []; // this array will contain all existing paths
  var brandnew = []; // this array will contain all the brand new paths

  for(var i = 0 ; i < good.length ; i++){ // iterate through the current array of good paths
    var path = good[i];

    if(checkGoodExist(path) === true){ // if the path exists
      exists.push(path); // add the path to the existing array
      delete goodPathsCache[path]; // remove path from the current cache
    } else { // if the path doesn't exist
      brandnew.push(path); // add the path to the brand new array
    }
  }

  // At this point, the goodPathsCache should contain all the paths that are no longer active in the current path check
  // Remove all inactive paths ( change back to default)
  for(var key in goodPathsCache){
    var seg = key.split(","); // each key will be a segment entry. (source,target)
    removePath(seg[0],seg[1]);
  }

  goodPathsCache = {}; // set goodPathsCache to a new empty object

  // Load all existing data back into goodPathsCache
  for(var i = 0 ; i < exists.length ; i++){
    goodPathsCache[exists[i]] = true;
  }

  // Load all brand new data back into goodPathsCache
  for(var i = 0 ; i < brandnew.length ; i++){
    goodPathsCache[brandnew[i]] = true;
  }

  // Finally, return all brand new data to be drawn
  return brandnew;
}

function badPathCheck(bad){
  var exists = []; // this array will contain all existing paths
  var brandnew = []; // this array will contain all the brand new paths

  for(var i = 0 ; i < bad.length ; i++){ // iterate through the current array of bad paths
    var path = bad[i];

    if(checkBadExist(path) === true){ // if the path exists
      exists.push(path); // add the path to the existing array
      delete badPathsCache[path]; // remove path from the current cache
    } else { // if the path doesn't exist
      brandnew.push(path); // add the path to the brand new array
    }
  }

  // At this point, the badPathsCache should contain all the paths that are no longer active in the current path check
  // Remove all inactive paths ( change back to default)
  for(var key in badPathsCache){
    var seg = key.split(","); // each key will be a segment entry. (source,target)
    removePath(seg[0],seg[1]);
  }

  badPathsCache = {}; // set badPathsCache to a new empty object

  // Load all existing data back into badPathsCache
  for(var i = 0 ; i < exists.length ; i++){
    badPathsCache[exists[i]] = true;
  }

  // Load all brand new data back into badPathsCache
  for(var i = 0 ; i < brandnew.length ; i++){
    badPathsCache[brandnew[i]] = true;
  }

  // Finally, return all brand new data to be drawn
  return brandnew;
}

// Update the graph with the current path
function updateGraph(def, good, bad){
  for(var i = 0 ; i < def.length ; i++){
    focusDefender(def[i]);
  }

  for(var i = 0 ; i < good.length ; i++){
    //var seg = good[i].split(",");
    //focusGood(seg[0],seg[1]);
    focusGood(good[i][0],good[i][1]);
  }

  for(var i = 0 ; i < bad.length ; i++){
    //var seg = bad[i].split(",");
    //focusBad(seg[0],seg[1]);
    focusBad(bad[i][0],bad[i][1]);
  }

}

function addListeners() {
  window.addEventListener("keydown", checkKeyPressed, false);
}

function checkKeyPressed(e) {

  // Turn everything off
  if (e.keyCode == "49") { // '1' was pressed
    everythingOff();

    // Turn everything on
    } else if (e.keyCode == "50") { // '2' was pressed
      everythingOn();
    } else if (e.keyCode == "51") { // '3' was pressed
      highlight(174, 23028);
    } else if (e.keyCode == "52") { // '4' was pressed

      unhighlight(174,23028);

    } else if (e.keyCode == "53") { // '5' was pressed
    alert("5 was pressed.");
  }
}
