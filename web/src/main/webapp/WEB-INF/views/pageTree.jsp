<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">Some page</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
    
    <jsp:attribute name="header">
                    
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.js"></script>		
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>	
    	
        <link rel="stylesheet" href="${baseUrl}/css/treeStyle.css">
    	                
    </jsp:attribute>
    
    <jsp:attribute name="addToFooter">
        <script type="text/javascript">

        var m = [20, 120, 20, 120],
        w = 380 - m[1] - m[3],
        h = 200 - m[0] - m[2],
        i = 0,
        root;
        
        
        var tree = d3.layout.tree()
        .size([h, w]);
        
        
        var diagonal = d3.svg.diagonal()
        .projection(function(d) { return [d.y, d.x]; });

   		var vis = d3.select("#body").append("svg:svg")
        .attr("width", w + m[1] + m[3])
        .attr("height", h + m[0] + m[2])
      	.append("svg:g")
        .attr("transform", "translate(" + m[3] + "," + m[0] + ")");
    
   		d3.json("json", function(json) {
   		  root = json;
   		  root.x0 = h / 2;
   		  root.y0 = 0;

   		  function toggleAll(d) {
   		    if (d.children) {
   		      d.children.forEach(toggleAll);
   		      toggle(d);
   		    }
   		  }

   		  // Initialize the display to show a few nodes.
   		  root.children.forEach(toggleAll);
   		//  toggle(root.children[1]);

   		  update(root);
   		});
    

   		function update(source) {
   		  var duration = d3.event && d3.event.altKey ? 5000 : 500;

   		  // Compute the new tree layout.
   		  var nodes = tree.nodes(root).reverse();

   		  // Normalize for fixed-depth.
   		  nodes.forEach(function(d) { d.y = d.depth * 180; });

   		  // Update the nodes…
   		  var node = vis.selectAll("g.node")
   		      .data(nodes, function(d) { return d.id || (d.id = ++i); });

   		  // Enter any new nodes at the parent's previous position.
   		  var nodeEnter = node.enter().append("svg:g")
   		      .attr("class", "node")
   		      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
   		      .on("click", function(d) { toggle(d); update(d); });

   		  nodeEnter.append("svg:circle")
   		      .attr("r", 1e-6)
   		      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

   		  nodeEnter.append("svg:foreignObject")
   		      .attr("x", function(d) { return d.children || d._children ? -150 : 10; })
   		      .attr("dy", ".35em") 
   		      .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
   		      .text(function(d) { return d.name; })
   		      .style("fill-opacity", 1e-6)
   		      .style("width", "150px"); // width of the node labels

   		  // Transition nodes to their new position.
   		  var nodeUpdate = node.transition()
   		      .duration(duration)
   		      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

   		  nodeUpdate.select("circle")
   		      .attr("r", 4.5)
   		      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

   		  nodeUpdate.select("text")
   		      .style("fill-opacity", 1);

   		  // Transition exiting nodes to the parent's new position. 
   		  var nodeExit = node.exit().transition()
   		      .duration(duration)
   		      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
   		      .remove();

   		  nodeExit.select("circle")
   		      .attr("r", 1e-6);

   		  nodeExit.select("text")
   		      .style("fill-opacity", 1e-6);

   		  // Update the links…
   		  var link = vis.selectAll("path.link")
   		      .data(tree.links(nodes), function(d) { return d.target.id; });

   		  // Enter any new links at the parent's previous position.
   		  link.enter().insert("svg:path", "g")
   		      .attr("class", "link")
   		      .attr("d", function(d) {
   		        var o = {x: source.x0, y: source.y0};
   		        return diagonal({source: o, target: o});
   		      })
   		    .transition()
   		      .duration(duration)
   		      .attr("d", diagonal);

   		  // Transition links to their new position.
   		  link.transition()
   		      .duration(duration)
   		      .attr("d", diagonal);

   		  // Transition exiting nodes to the parent's new position.
   		  link.exit().transition()
   		      .duration(duration)
   		      .attr("d", function(d) {
   		        var o = {x: source.x, y: source.y};
   		        return diagonal({source: o, target: o});
   		      })
   		      .remove();

   		  // Stash the old positions for transition.
   		  nodes.forEach(function(d) {
   		    d.x0 = d.x;
   		    d.y0 = d.y;
   		  });
   		}

   		// Toggle children.
   		function toggle(d) {
   		  if (d.children) {
   		    d._children = d.children;
   		    d.children = null;
   		  } else {
   		    d.children = d._children;
   		    d._children = null;
   		  }
   		}

   		
    	</script>

    </jsp:attribute>
    
    <jsp:body>
       

    <div id="body">
      <div id="footer">
      </div>
    </div>
    
        
    </jsp:body>
    
    
    
    </t:genericpage>