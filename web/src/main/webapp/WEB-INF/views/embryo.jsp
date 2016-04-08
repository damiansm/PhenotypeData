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
        <link rel="stylesheet" href='${baseUrl}/css/slider.css?v=${version}'/>        
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
				            		<c:forEach var="row" items="${viabilityTable}">
					            		<tr>
					            			<td><h4 class="capitalize">${row.category}</h4></td>
					            			<c:if test="${row.mpId != null}">    				
					            				<td><h4><a href="${baseUrl}/phenotypes/${row.mpId}">${row.count}</a></h4></td>
					            			</c:if>
					            			<c:if test="${row.mpId == null}">    				
					            				<td><h4>${row.count}</h4></td>
					            			</c:if>	        	
					            		</tr>
									</c:forEach>
									<tr> 
										<td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/viabilityReport.csv" style="text-decoration:none;" download> <i class="fa fa-download" alt="Download"> Download</i></a></td>
										<td></td>	
									</tr>
				            		</tbody></table>
								</div>
								<div class="clear"> </div>								
	                           
	                           <p>Each IMPC gene knockout strain is assessed for viability by examination of litters produced from mating heterozygous animals. A strain is declared lethal if no homozygous nulls pups are detected at weaning age. A strain is declared subviable if homozygous null pups constitute less than 12.5% of the litter. For lethal strains, embryos are phenotyped in the embryonic and perinatal lethal pipeline. For embryonic lethal and subviable strains, heterozygotes are phenotyped in the IMPC adult phenotyping pipeline.
                         </p>
	                           
	                           
	                           
                            </div>
                        </div>

        				

						<div class="section">
						<h2>IMPC Embryo Phenotyping - Goals and Procedures</h2>
                            <div class="inner">
                            
                            <p>With up to one third of knockout strains being embryonic lethal, a systematic unbaised phenotyping pipeline was established to perform morphologic and imaging evaluation of mutant embryos to define the primary perturbations that cause their death. From this important insights are gained into gene function.
                            </p>
                            <p>
                            IMPC centers funded by the NIH Common fund mechanism are delivering the following
							for <b>All Lines</b>:
							<ul>
 							<li>Viability</li>
 							<li>
 							Heterozygote E12.5 Embryonic LacZ staining ( 2 mutant animals, wt reference images)</li>
							</ul>
							<p>For <b>All Embryonic Lethal Lines</b>, gross morphology is assessed at E12.5 to determine if defects occur earlier or later in development. A comprehensive imaging platform is then used to assess dysmorphology at the <b>most</b> appropriate stage:
                            </p>
                            <table>
                            <tr><th>Procedure</th><th>Number</th><th>Note</tr>
                            <tr><td>E9.5 Gross morphology</td><td>at least 2 homs,2 wt</td><td>images optional</td></tr>
                            <tr><td>E9.5 OPT screening</td><td>at least 2 homs</td><td>reconstructions available</td></tr>
                            <tr><td>E14.5-E15.5 Gross morphology</td><td>at least 2 homs, 2 wt</td><td>images optional</td></tr>
                            <tr><td>E14.5-E15.5 microCT screening</td><td>at least 2 homs</td><td>reconstructions available</td></tr>
                            <tr><td>E18.5 Gross morphology</td><td>at least 2 homs</td><td>images optional</td></tr>
                            <tr><td>E18.5 microCT</td><td>at least 2 homs, 2 wt</td><td>reconstructions available</td></tr>
                            </table>
                            <p>
                            In addition, the NIH is supporting in-depth phenotyping of embryonic lethal lines with two current awardees.
                            </p>
                            <p><a href="http://www.ucdenver.edu/academics/colleges/medicalschool/programs/Molbio/faculty/WilliamsT/Pages/WilliamsT.aspx">Trevor William, University of Colorado School of Medicine</a>
                            </p>
                            <p><a href="https://www.umass.edu/m2m/people/jesse-mager">Jesse Mager, University of Massachusetts Amherst</a>
                            </p>
                            
                            
                            	  </div>
                       	</div>
                       	
                       	 <div class="section">

                            <h2 class="title"> 2D Imaging </h2>
                            <div class="inner">
                            	<div class="half">
                            		<h2>Embryo LacZ</h2>
                            		<img src="${baseUrl}/img/Tmem100_het.jpeg" height="200" style="width:auto" class="twothird"/>
                            		<a class="onethird" href="${baseUrl}/imagePicker/MGI:1915138/IMPC_ELZ_063_001">Tmem100</a>
                            		<div class="clear"></div> 
                            		<p class="minpadding"> The majority of IMPC knockout strains replace a critical protein coding exon with a LacZ gene expression 
                            		reporter element. Heterozygote E12.5 embryos from IMPC strains are treated to determine in situ expression of the targeted gene.
                            		</p>
                            		<p class="minpadding">See all genes with <a href='${baseUrl}/search/impc_images?kw=*&fq=(procedure_name:"Embryo%20LacZ")'>embryo LacZ images</a>.</p>
                            	</div>
								<div class="half ">
									<h2>Embryo Gross Morphology</h2>
                            		<img class="twothird"src="${baseUrl}/img/Acvr2a_hom.jpeg" height="200" style="width:auto"/>
                            		<p class="onethird ">&nbsp;&nbsp;WT / <a href="${baseUrl}/imagePicker/MGI:102806/IMPC_GEO_050_001">Acvr2a</a> </p>
                            		<div class="clear"></div> 
                            		<p class="minpadding">  Gross morphology of embryos from lethal and subviable strains highlights which biological systems are impacted when the 
                            		function of a gene is turned off. The developmental stage selected is determined by an initial assessment.
                            		</p>
                            		<p class="minpadding"> See embryo gross morphology images for 		
	                            		<a href='${baseUrl}/search/impc_images?kw=*&fq=(procedure_name:"Gross Morphology Embryo E9.5")'>E9.5</a>,	
	                            		<a href='${baseUrl}/search/impc_images?kw=*&fq=(procedure_name:"Gross Morphology Embryo E12.5")'>E12.5</a>,		
	                            		<a href='${baseUrl}/search/impc_images?kw=*&fq=(procedure_name:"Gross Morphology Embryo E14.5-E15.5")'>E14.5-E15.5</a>,		
	                            		<a href='${baseUrl}/search/impc_images?kw=*&fq=(procedure_name:"Gross Morphology Embryo E18.5")'>E18.5</a>.
                            		</p>						
								</div>								
								<div class="clear"></div>
                            </div>

                        </div>
                        
                        <div class="section">

                            <h2 class="title"> 3D Imaging </h2>
                            <div class="inner">
                            	<img alt="IEV" src="${baseUrl}/img/IEV.png">
                            	<p> The embryonic and perinatal lethal pipeline comprises several 3D imaging modalities to quantify aberrant morphology that could not be determined by gross inspection. Images acquired by micro-CT and OPT are available via our Interactive Embryo Viewer (IEV). </p>
                            	<div>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer/?mgi=MGI:2147810&pid=203&h=undefined&s=on&c=off&a=off&o=vertical&zoom=0&sb=600&wn=146521&wx=54&wy=66&wz=68&wl=0&wu=254&mn=129313&mx=52&my=68&mz=108&ml=0&mu=205" style="margin: 10px">
                            		Tmem132a</a>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer/?mgi=MGI:1916804&mod=203&h=624&wt=klf7-tm1b-ic/16.5b_5553715&mut=klhdc2-tm1b-ic/21.1f_5578050&s=on&c=off&a=off&wx=64&wy=117&wz=178&mx=44&my=107&mz=154&wl=0&wu=255&ml=0&mu=255&o=vertical&zoom=0" style="margin: 10px">
                            		Klhdc2</a>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer/?mgi=MGI:1195985&mod=203&h=561&wt=Population%20average&mut=AAPN_K1026-1-e15.5&s=off&c=off&a=on&wx=94&wy=64&wz=177&mx=94&my=70&mz=137&wl=0&wu=255&ml=0&mu=254&o=vertical&zoom=0&wto=jacobian&muto=none" style="margin: 10px">
                            		Cbx4</a>
                            		<a class="btn" href="${drupalBaseUrl}/embryoviewer/?mgi=MGI:102806&pid=203&h=569&s=on&c=off&a=off&o=vertical&zoom=0&sb=600&wn=ABIF_K1339-10-e15.5&wx=79&wy=107&wz=141&wl=0&wu=255&mn=ABIF_K1267-19-e15.5&mx=79&my=107&mz=142&ml=0&mu=255" style="margin: 10px">
                            		Acvr2a</a>
                            		<a href="${baseUrl}/search/gene?kw=*&fq=(embryo_data_available:%22true%22)"> See all </a>
                            	</div>                              	
                            </div>

                        </div>
                        
             <c:set var="vignettesLink" value='<a class="btn" style="float: right" href="${baseUrl}/embryo/vignettes">Full Analysis</a>'/>
                        <div class="section">
                            <h2 class="title"> Vignettes </h2>
                            <div class="inner">
								<div id="sliderDiv">
									<div id="slider">
										<div id="sliderHighlight" class="slider" imgUrl="${baseUrl}/embryo/vignettes"> </div>
										<div> 
											<span class="control_next half left">></span>
											<span class="control_prev half right"><</span>
										</div>
									</div>
									<div class="clear"> </div>									
									<div id="sliderControl" class="sliderControl" >
										<ul>
										    <li id="item0">  <img src="${baseUrl}/img/vignettes/chtopPink.jpg" />
										    	<p class="embryo-caption"> Chtop has been shown to recruit the histone-methylating methylosome to genomic regions containing 
										    		5-Hydroxymethylcytosine, thus affecting gene expression.  Chtop mutants showed complete preweaning lethality with 
										    		no homozygous pups observed.  High resolution episcopic microscopy (HREM) imaging, revealed decreased number of 
										    		vertebrae, abnormal joint morphology and edema. <t:vignetteLink geneId="MGI:1913761"></t:vignetteLink></p>
										    	<p class="sliderTitle"><t:formatAllele>Chtop<tm1a(EUCOMM)Wtsi></t:formatAllele></p></li>
										    <li id="item1"> <img src="${baseUrl}/img/vignettes/Kldhc2.png" /> 
										    	<p class="embryo-caption"> The Kldhc2 gene is located within a locus linked to an automsomal dominant disease that leads to fibro-fatty replacement of right ventricle myocardium leading to arrythmias (ARVD3 ; OMIM) <t:vignetteLink geneId="MGI:1916804"></t:vignetteLink></p> 
										    	<p class="sliderTitle"><t:formatAllele>Klhdc2<tm1b(EUCOMM)Hmgu></t:formatAllele></p></li>
										    <li id="item2"><img src="${baseUrl}/img/vignettes/Acvr2aMicroCT.png" /> 
										    	<p class="embryo-caption">Activin receptor IIA is a receptor for activins, which are members of the TGF-beta superfamily involved in diverse biological processes. Acvr2a mutants are subviable with most pups dying before postnatal day 7. <t:vignetteLink geneId="MGI:102806"></t:vignetteLink></p>
										    	<p class="sliderTitle"> <t:formatAllele>Acvr2a<tm1.1(KOMP)Vlcg></t:formatAllele></p></li>
										    <li id="item3"><img src="${baseUrl}/img/vignettes/cbx4.png" />
										    	<p class="embryo-caption">Chromobox 4 is in the polycomb protein family that are key regulators of transcription and is reported to be upregulated in lung bud formation and required for thymus development <t:vignetteLink geneId="MGI:1195985"></t:vignetteLink></p>	
										    	<p class="sliderTitle"><t:formatAllele>Cbx4<tm1.1(KOMP)Vlcg></t:formatAllele></p> </li>
										    <li id="item4"><img src="${baseUrl}/img/vignettes/tmem100.png" /> 
										    	<p class="embryo-caption">Transmembrane Protein 100 functions downstream of the BMP/ALK1 signaling pathway. Tmem100 mutants showed complete preweaning lethality and were also lethal at E12.5. <t:vignetteLink geneId="MGI:1915138"></t:vignetteLink></p> 
										    	<p class="sliderTitle"><t:formatAllele>Tmem100<tm1e.1(KOMP)Wtsi></t:formatAllele></p></li>
										    <li id="item5"> <img src="${baseUrl}/img/vignettes/eye4.png" /> 
										    	<p class="embryo-caption"> Eyes absent transcriptional coactivator and phosphatase 4 is associated with a variety of developmental defects including hearing loss. Eya4 mutants showed complete preweaning lethality with no homozygous pups observed. <t:vignetteLink geneId="MGI:1337104"></t:vignetteLink></p> 
										    	<p class="sliderTitle"><t:formatAllele>Eya4<tm1b(KOMP)Wtsi></t:formatAllele></p></li>
										    <li id="item6"><img src="${baseUrl}/img/vignettes/tox3MRI.png" /> 
										    	<p class="embryo-caption">Tox High Mobility Group Box Family Member 3 is a member of the HMG-box family involved in bending and unwinding DNA. Tox3 mutants have partial preweaning lethality with 1/3 of the pups dying before P7. <t:vignetteLink geneId="MGI:3039593"></t:vignetteLink></p>
										    	<p class="sliderTitle"><t:formatAllele>Tox3<tm1b(KOMP)Mbp></t:formatAllele></p></li>
										    <li id="item7"><img src="${baseUrl}/img/vignettes/Rsph9Slides.png" />
										    	<p class="embryo-caption">Radial spoke head protein 9 is a component of the radial spoke head in motile cilia and flagella. Rsph9 mutants showed partial pre-weaning lethality but viable to P7. <t:vignetteLink geneId="MGI:1922814"></t:vignetteLink></p>	
										    	<p class="sliderTitle"><t:formatAllele>Rsph9<tm1.1(KOMP)Vlcg></t:formatAllele></p> </li>
										    <li id="item8"><img src="${baseUrl}/img/vignettes/Pax7.png" /> 
										    	<p class="embryo-caption">Pax 7 is a nuclear transcription factor with DNA-binding activity via its paired domain. It is involved in specification of the neural crest and is an upstream regulator of myogenesis during post-natal growth and muscle regeneration in the adult. <t:vignetteLink geneId="MGI:97491"></t:vignetteLink> </p> 
										    	<p class="sliderTitle"><t:formatAllele>Pax7<tm1.1(KOMP)Vlcg></t:formatAllele></p></li>
										    <li id="item9"><img src="${baseUrl}/img/vignettes/Svep1.jpg" /> 
										    	<p class="embryo-caption">Svep1 .... <t:vignetteLink geneId="MGI:1928849"></t:vignetteLink> </p> 
										    	<p class="sliderTitle"><t:formatAllele>Svep1<tm1b(EUCOMM)Hmgu/J></t:formatAllele></p></li>	
										    <li id="item10"><img src="${baseUrl}/img/vignettes/Strn3.jpg" /> 
										    	<p class="embryo-caption">Strn3 .... <t:vignetteLink geneId="MGI:2151064"></t:vignetteLink> </p> 
										    	<p class="sliderTitle"><t:formatAllele>Strn3<tm1b(KOMP)Wtsi/J></t:formatAllele></p></li>	
										    <li id="item11"><img src="${baseUrl}/img/vignettes/Rab34.jpg" /> 
										    	<p class="embryo-caption">Rab34 .... <t:vignetteLink geneId="MGI:104606"></t:vignetteLink> </p> 
										    	<p class="sliderTitle"><t:formatAllele>Rab34<tm1b(EUCOMM)Hmgu/J></t:formatAllele></p></li>	
										    <li id="item12"><img src="${baseUrl}/img/vignettes/Cox7c.jpg" /> 
										    	<p class="embryo-caption">Cox7c .... <t:vignetteLink geneId="MGI:103226"></t:vignetteLink> </p> 
										    	<p class="sliderTitle"><t:formatAllele>Cox7c<tm1b(KOMP)Mbp></t:formatAllele></p></li>	
										    <li id="item13"><img src="${baseUrl}/img/vignettes/Bloc1s2.jpg" /> 
										    	<p class="embryo-caption">Bloc1s2 .... <t:vignetteLink geneId="MGI:1920939"></t:vignetteLink> </p> 
										    	<p class="sliderTitle"><t:formatAllele>Bloc1s2<tm1.1(KOMP)Mbp></t:formatAllele></p></li>	
										  </ul> 
									</div>
	                           	</div>
	                           	<br/>
	                           	<p> These vignettes highlight the utility of embryo phenotyping pipeline and demonstrate how gross morphology, embryonic 
	                           	lacz expression, and high resolution 3D imaging provide insights into developmental biology. Clicking on an image will provide 
	                           	more information. </p>
                            </div>
                        </div>

                         <div class="section" id="pipeline">
							<h2 class="title ">IMPC Embryonic Pipeline</h2>
                            <div class="inner">
	                        	<div><a href="${drupalBaseUrl}/impress" ><img src="${baseUrl}/img/embryo_impress.png"/> </a></div>
                            </div>

                        </div>
                        
                      
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>
        
        </div>


	

      </jsp:body>

</t:genericpage>

