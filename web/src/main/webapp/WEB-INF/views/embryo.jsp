<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Embryo Landing Page</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>          



	<jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/slider.js?v=${version}'></script> 
        
        <style type="text/css">
        	#slider {
			  position: relative;
			  overflow: hidden;
			  margin: 20px auto 0 auto;
			  border-radius: 4px;
			  background:transparent;
			}
			
			#sliderHighlight{
			  position: relative;
			  margin: 0;
			  padding: 0;
			  list-style: none;
			}
			
			#sliderHighlight li {
			  position: relative;
			  display: block;
			  float: left;
			  margin: 0;
			  padding: 0;
			  width: 800px;
			  height: 500px;
			  background: transparent;
			}
						 
			 .slider img{
			 	max-width:100%;
			 	max-height:70%;
			 	margin-left: auto;
    			margin-right: auto;
    			display: block;
			 }
			 
			 .slider p{
			 	z-index: 1000;
			 	padding-left:8%;
			 	padding-right:8%;
			 	height: 20%;
			 }
			 			
			.sliderControl ul li {
				display:inline;
				width:100px;
				float:left;
				padding: 0.3em;
			}
			
			.sliderControl img{
				max-height:50px;
				margin-left: auto;
    			margin-right: auto;
    			display:block;
			}
			
			.sliderControl .caption{
				display:none;
			}
			
			.sliderTitle{
				display : bloc;
			}
			
			.minmargin p{
				padding:0.5em;
			}
			
			h2.sliderTitle{
				text-align:center;
				display : bloc;
			}
			
			#sliderOnDisplay p.sliderTitle{
				display : none;
			}
			
			#sliderControl{
				width: 100%;
    			height: 120px;
   				overflow-x: scroll;
   				margin: 0.7em;
			}
			
			#sliderControl ul{
				display:inline-block;
			}	
			
			li.sliderSelectedControl{
    			border: 1px solid black;
    			list-style-position:inside;
			}
			
			.control_prev, .control_next {
			  position: absolute;
			  top: 40%;
			  z-index: 999;
			  display: block;
			  padding: 4% 3%;
			  width: auto;
			  height: auto;
			  background: #2a2a2a;
			  color: #fff;
			  text-decoration: none;
			  font-weight: 600;
			  font-size: 18px;
			  opacity: 0.2;
			  cursor: pointer;
			}
			
			.control_prev:hover, .control_next:hover {
			  opacity: 0.8;
			  -webkit-transition: all 0.2s ease;
			}
			
			.control_prev {
			  border-radius: 0 2px 2px 0;
			}
			
			.control_next {
			  right: 0;
			  border-radius: 2px 0 0 2px;
			}
						
        </style>
        
    </jsp:attribute>

    <jsp:body>
        <div class="region region-content">
            <div class="block">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">IMPC Embryo Data </h1>

                        <div class="section">
                            <div class="inner">
                            	<h2>IMPC Viability</h2>
                            	<div id="viabilityChart" class="half right">
				            		<script type="text/javascript">${viabilityChart}</script>
								</div>
								<div id="viabilityChart" class="half right">
				            		<table> 
				            		<thead>				            		
				            			<tr> <th class="headerSort"> Category </th> <th> # Genes </th> </tr>
				            		</thead>
				            		<tbody>
				            		<c:forEach var="key" items="${viabilityTable.keySet()}">
					            		<tr>
					            			<td><h4 class="capitalize">${key}</h4></td>
					            			<td><h4>${viabilityTable.get(key)}</h4></td>			            					
					            		</tr>
									</c:forEach>
									<tr> 
										<td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/viabilityReport.csv" style="text-decoration:none;" download> <i class="fa fa-download" alt="Download"> Download</i></a></td>
										<td></td>	
									</tr>
				            		</tbody></table>
								</div>
								<div class="clear"> </div>								
	                           
	                           	<p>We estimate that approximately one third of genes are essential for the development and life of mammalian organisms. 
	                           	As part of IMPC, each gene knockout is assessed for its viability during weaning and below is a complete table of all 
	                           	currently known lethal (essential) genes that are known thus far for IMPC. For those lethal lines they will continue through 
	                           	the adult pipeline as heterozygous knockouts and this data can be explored on the portal  and the homozygous embryos will be 
	                           	phenotyped in the embryonic and perinatal lethal pipeline. We have included several interesting vignettes that highlight the 
	                           	utility of this pipeline in terms of gross morphology, embryonic lacz expression, and high resolution 3D imaging. Each of these 
	                           	types of examination are briefly explored further within this landing page with examples.</p>
								<p>We also have extensive documentation available on each aspect of the portal and a tutorial on how to access the data for 
									 independent researchers looking to make the best use of this encyclopedic resource. IMPC is a large-scale resource involving 
									 many researchers and phenotypes and as such we have developed IMPReSS that captures and stores how each procedure has been 
									 standardised and carried out at each IMPC center. This will eventually form an invaluable resource for developmental biologists 
									 and translation rare disease research and we have one such example where a knockout has been taken on for further research and 
									 funded through the <a href="http://grants.nih.gov/grants/guide/pa-files/PAR-13-231.html">NIH PAR-13-231</a> funding mechanism.</p>
	                           
                            </div>
                        </div>

        				<div class="section">

                            <h2 class="title"> Vignettes </h2>
                            <div class="inner">
								<div id="sliderDiv">
									<div id="slider">
										<div id="sliderHighlight" class="slider"> </div>
										<div> 
											<span class="control_next half left">></span>
											<span class="control_prev half right"><</span>
										</div>
									</div>
									<div class="clear"> </div>									
									<div id="sliderControl" class="sliderControl">
										<ul>
										    <li id="item0">  <img src="${baseUrl}/img/vignettes/Chtop.png" />
										    	<p class="caption"> Chtop has been shown to recruit the histone-methylating methylosome to genomic regions containing 5-Hydroxymethylcytosine, thus affecting gene expression.  Chtop mutants showed complete preweaning lethality with no homozygous pups observed.  High resolution episcopic microscopy (HREM) imaging, revealed decreased number of vertebrae, abnormal joint morphology and edema. <a href="${drupalBaseUrl}/vignettes">Read more >></a></p>
										    	<p class="sliderTitle"> Chtop -/-</p></li>
										    <li id="item1"> <img src="${baseUrl}/img/vignettes/Rab34.png" /> 
										    	<p class="caption"> Paralog of Rab23, a paralog of Rab23, which is a key component of hedgehog signalling. Homozygous E15.5 mutant embryos have the following phenotypes, consistent with a role in hedgehog signalling. <a href="${drupalBaseUrl}/vignettes">Read more >></a></p> 
										    	<p class="sliderTitle">Rab23</p></li>
										    <li id="item2"><img src="${baseUrl}/img/vignettes/Gyg.png" /> 
										    	<p class="caption">Glycogenin 1 is involved in glycogen biosynthesis. Recently a novel human mutation Gyg was shown to be associated with skeletal myopathy. <a href="${drupalBaseUrl}/vignettes">Read more >></a></p>
										    	<p class="sliderTitle"> Glycogenin 1</p></li>
										    <li id="item3"><img src="http://www.ebi.ac.uk/mi/media/omero/webgateway/render_image/140715/" />	
										    	<p class="sliderTitle"> Gene symbol</p> </li>
										    <li id="item4"><img src="http://www.ebi.ac.uk/mi/media/omero/webgateway/render_image/140711/" /> 
										    	<p class="caption"> This is interesting because see here and here and here and here </p> 
										    	<p class="sliderTitle"> Gene symbol</p></li>
										    <li id="item5"> <img src="${baseUrl}/img/vignettes/Rab34.png" /> 
										    	<p class="caption"> Paralog of Rab23, a paralog of Rab23, which is a key component of hedgehog signalling. Homozygous E15.5 mutant embryos have the following phenotypes, consistent with a role in hedgehog signalling. <a href="${drupalBaseUrl}/vignettes">Read more >></a></p> 
										    	<p class="sliderTitle">Rab23</p></li>
										    <li id="item6"><img src="${baseUrl}/img/vignettes/Gyg.png" /> 
										    	<p class="caption">Glycogenin 1 is involved in glycogen biosynthesis. Recently a novel human mutation Gyg was shown to be associated with skeletal myopathy. <a href="${drupalBaseUrl}/vignettes">Read more >></a></p>
										    	<p class="sliderTitle"> Glycogenin 1</p></li>
										    <li id="item7"><img src="http://www.ebi.ac.uk/mi/media/omero/webgateway/render_image/140715/" />	
										    	<p class="sliderTitle"> Gene symbol</p> </li>
										    <li id="item8"><img src="http://www.ebi.ac.uk/mi/media/omero/webgateway/render_image/140711/" /> 
										    	<p class="caption"> This is interesting because see here and here and here and here </p> 
										    	<p class="sliderTitle"> Gene symbol</p></li>
										  </ul> 
									</div>
	                           	</div>
                            </div>

                        </div>

                        <div class="section">

                            <h2 class="title"> 3D Imaging </h2>
                            <div class="inner">
                            	<img alt="IEV" src="${baseUrl}/img/IEV.png">
                            	<p> The embryonic and perinatal lethal pipeline comprises several 3D imaging modalities to quantify aberrant morphology that could not be determined by gross inspection. Images acquired by micro-CT and OPT are available via our Interactive Embryo Viewer (IEV). </p>
                            	<div>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer?mgi=MGI:2147810" style="margin: 10px">Tmem132a</a>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer?mgi=MGI:1916804" style="margin: 10px">Klhdc2</a>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer?mgi=MGI:1195985" style="margin: 10px">Cbx4</a>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer?mgi=MGI:102806" style="margin: 10px">Acvr2a</a>
                            		<a href="${baseUrl}/search/gene?kw=*&fq=(embryo_data_available:%22true%22)"> See all </a>
                            	</div>                              	
                            </div>

                        </div>
                        
                         <div class="section">

                            <h2 class="title"> 2D Imaging </h2>
                            <div class="inner">
                            	<div class="half minmargin">
                            		<h2>Embryo LacZ</h2>
                            		<img src="${baseUrl}/img/Tmem100_het.jpeg" height="200"/>
                            		<p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. 
                            			<a href="${drupalBaseUrl}/imagePicker/MGI:1915138/IMPC_ELZ_063_001">Tmem100</a> 
                            		</p>
                            		<p>See all genes with <a href='${drupalBaseUrl}/search/impc_images?kw=*&fq=(procedure_name:"Embryo%20LacZ")'>embryo LacZ images</a>.</p>
                            	</div>
								<div class="half minmargin">
									<h2>Embryo Gross Morphology</h2>
                            		<img src="${baseUrl}/img/Acvr2a_hom.jpeg" height="200"/>
                            		<p> Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat.
                            			<a href="${drupalBaseUrl}/imagePicker/MGI:102806/IMPC_GEO_050_001">Acvr2a</a> 
                            		</p>
                            		<p> See embryo gross morphology images for 
	                            		<a href='${drupalBaseUrl}/search/impc_images?kw=*#fq&fq=(procedure_name:"Gross Morphology Embryo E12.5")'>E12.5</a>,		
	                            		<a href='${drupalBaseUrl}/search/impc_images?kw=*#fq&fq=(procedure_name:"Gross Morphology Embryo E14.5-E15.5")'>E14.5-E15.5</a>,		
	                            		<a href='${drupalBaseUrl}/search/impc_images?kw=*#fq&fq=(procedure_name:"Gross Morphology Embryo E18.5")'>E18.5</a>,		
	                            		<a href='${drupalBaseUrl}/search/impc_images?kw=*#fq&fq=(procedure_name:"Gross Morphology Embryo E9.5")'>E9.5</a>.	
                            		</p>						
								</div>								
								<div class="clear"></div>
                            </div>

                        </div>
                        
                         <div class="section">
							<h2 class="title ">IMPC Embryonic Pipeline</h2>
                            <div class="inner">
	                        	<div><img src="${baseUrl}/img/embryo_impress.png"/></div>
                            </div>

                        </div>
                        
                      </div>
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>


	

      </jsp:body>

</t:genericpage>
