<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">${phenotype.getMpId()} (${phenotype.getMpTerm()}) | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/mp?kw=*">Phenotypes</a> &raquo; ${phenotype.getMpTerm()}</jsp:attribute>

	<jsp:attribute name="header">

	<!-- CSS Local Imports -->
        <link rel="stylesheet" href="${baseUrl}/css/treeStyle.css">

		<script type="text/javascript">
			var phenotypeId = '${phenotype.getMpId()}';
			var drupalBaseUrl = '${drupalBaseUrl}';
		</script>

		<script type='text/javascript' src="${baseUrl}/js/general/dropDownPhenPage.js?v=${version}"></script>
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>

	</jsp:attribute>


	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

	<jsp:attribute name="addToFooter">

		<script type="text/javascript">
			// Stuff dor parent-child. Will be used in parentChildTree.js.
			var ont_id = '${phenotype.getMpId()}';
			var ontPrefix = "mp";
			var page = "phenotypes";
			var hasChildren = ${hasChildren};
			var hasParents = ${hasParents};
		</script>
    	<script type="text/javascript" src="${baseUrl}/js/parentChildTree.js"></script>

		<div class="region region-pinned">
	        <div id="flyingnavi" class="block smoothScroll">

				<a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

		        <ul>
		 	        <li><a href="#top">Phenotype</a></li>
		            <c:if test="${genePercentage.getDisplay()}">
		                		<li><a href="#data-summary">Phenotype Association Stats</a></li>
		            </c:if>
		            <c:if test="${hasData}">
		                <li><a href="#gene-variants">Gene Variants</a></li><!-- message comes up in this section so dont' check here -->
		            </c:if>
		            <c:if test="${not empty images && fn:length(images) !=0}">
		                <li><a href="#imagesSection">Images</a></li>
		            </c:if>
		        </ul>

		        <div class="clear"></div>

	        </div>

	</div>

	</jsp:attribute>
	<jsp:body>

	<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">Phenotype: ${phenotype.getMpTerm()}
							<span class="documentation"><a href='' id='summarySection' class="fa fa-question-circle pull-right"></a></span>
						</h1>

					<div class="section">
						<div class="inner">

							<div class="half">
								<c:if test="${not empty phenotype.getMpDefinition()}">
									<p id="definition" class="with-label"> <span class="label"> Definition</span> ${phenotype.getMpDefinition()} </p>
								</c:if>

                                <c:if test="${not empty phenotype.getMpTermSynonym()}">
                                    <div id="synonyms" class="with-label"> <span class="label">Synonyms</span>
                                        <c:if test='${phenotype.getMpTermSynonym().size() == 1}'>

                                            <c:forEach var="synonym" items="${phenotype.getMpTermSynonym()}" varStatus="loop">
                                                ${synonym}
                                                <%--<c:if test="${!loop.last}">,&nbsp;</c:if>--%>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test='${phenotype.getMpTermSynonym().size() gt 1}'>
                                            <c:set var="count" value="0" scope="page"/>
                                            <ul>
                                                <c:forEach var="synonym" items="${phenotype.getMpTermSynonym()}" varStatus="loop">
                                                    <c:set var="count" value="${count + 1}" scope="page"/>
                                                    <c:if test='${count lt 3}'>
                                                        <li class='defaultList'>${synonym}</li>
                                                    </c:if>
                                                    <li class='fullList'>${synonym}</li>
                                                </c:forEach>
                                            </ul>
                                            <c:if test='${count gt 2}'>
                                                <span class='synToggle'>Show more</span>
                                            </c:if>
                                        </c:if>
                                    </div>
                                </c:if>

								<c:if test="${not empty phenotype.getMpNarrowSynonym()}">
                                    <div id="narrowSynonyms" class="with-label"> <span class="label">Related<br>Synonyms <i class="fa fa-question-circle fa-1x relatedSyn"></i></span>

                                        <c:if test='${phenotype.getMpNarrowSynonym().size() == 1}'>
                                            <c:forEach var="nsynonym" items="${phenotype.getMpNarrowSynonym()}" varStatus="loop">
                                                ${nsynonym}
                                                <%--<c:if test="${!loop.last}">,&nbsp;</c:if>--%>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test='${phenotype.getMpNarrowSynonym().size() gt 1}'>
                                            <c:set var="count" value="0" scope="page"/>
                                            <ul>
                                                <c:forEach var="nsynonym" items="${phenotype.getMpNarrowSynonym()}" varStatus="loop">
                                                    <c:set var="count" value="${count + 1}" scope="page"/>
                                                    <c:if test='${count lt 3}'>
                                                      <li class='defaultList'>${nsynonym}</li>
                                                    </c:if>
                                                      <li class='fullList'>${nsynonym}</li>
                                                </c:forEach>
                                            </ul>
                                            <c:if test='${count gt 2}'>
                                                <span class='synToggle'>Show more</span>
                                            </c:if>
                                        </c:if>
                                    </div>
								</c:if>
								<%--<c:if test="${not empty phenotype.getHpTerm()}">--%>
									<%--<div id="mappedHpTerms" class="with-label"> <span class="label">Computationally mapped HP term</span>--%>
										<%--<ul>--%>
											<%--<c:forEach var="hpTerm" items="${phenotype.getHpTerm()}" varStatus="loop">--%>
												<%--<li>${hpTerm}</li>--%>
												<%--<c:if test="${loop.last}">&nbsp;</c:if>--%>
											<%--</c:forEach>--%>
										<%--</ul>--%>
									<%--</div>--%>
								<%--</c:if>--%>


								<c:if test="${not empty procedures}">
									<div id="procedures" class="with-label"> <span class="label">Procedure</span>
										<ul>
										<c:set var="count" value="0" scope="page"/>
											<c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
		 										<c:set var="count" value="${count+1}" />
		 										<c:set var="hrefVar" value="${drupalBaseUrl}/impress/protocol/${procedure.procedureStableKey}"/>
		 										<c:if test="${fn:contains(procedure.procedureStableId,'M-G-P')}">
		 											<c:set var="hrefVar" value="${drupalBaseUrl}/impress/parameters/${procedure.procedureStableKey}/4"/>
		 										</c:if>
		  										<li><a href="${hrefVar}">
		  											${procedure.procedureName} (${procedure.procedureStableId.split("_")[0]},
		  											v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())})
		  										</a></li>
			 									<c:if test="${count==3 && !firstLoop.last}"><p ><a id='show_other_procedures'><i class="fa fa-caret-right"></i><span id="procedureToogleLink">Show more</span></a></p> <div id="other_procedures"></c:if>
												<c:if test="${firstLoop.last && fn:length(procedures) > 3}"></c:if>
											</c:forEach>
										</ul>
									</div>
								</c:if>

								<p id="mpId" class="with-label"><span class="label">MP browser</span><a href="${baseUrl}/ontologyBrowser?termId=${phenotype.getMpId()}">${phenotype.getMpId()}</a></p>
								<c:if test="${!hasData}">
									<p>This MP term has not been considered for annotation in <a href="https://www.mousephenotype.org/impress">IMPReSS</a>. However, you can search and retrieve all MP terms currently associated to the Knock-out mutant lines from the <a href="${baseUrl}/search">IMPC Search</a> page. You can also look at all the MP terms used to annotate the IMPReSS SOPs from the <a href="https://www.mousephenotype.org/impress/ontologysearch">IMPReSS ontology search</a> page.</p>
								</c:if>
								<c:choose>
		                        	<c:when test="${registerButtonAnchor!=''}">
		                            	<p> <a class="btn" href='${registerButtonAnchor}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a></p>
		                            </c:when>
			                        <c:otherwise>
			                            <p> <a class="btn interest" id='${registerButtonId}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a></p>
			                        </c:otherwise>
		                        </c:choose>
		                        <c:if test="${orderPossible}">
		                          	<p> <a class="btn" href="#order2"> <i class="fa fa-shopping-cart"></i> Order </a> </p>
		                        </c:if>
							</div>

							<div id="parentChild" class="half">
									<c:if test="${hasChildren && hasParents}">
		                            	<div class="half" id="parentDiv"></div>
										<div class="half" id="childDiv"></div>
									</c:if>
									<c:if test="${hasChildren && !hasParents}">
										<div id="childDiv"></div>
									</c:if>
									<c:if test="${!hasChildren && hasParents}">
		                            	<div id="parentDiv"></div>
									</c:if>
							</div>

							<div class="clear"></div>
						</div><!--  closing off inner here - but does this look correct in all situations- because of complicated looping rules above? jW -->
				</div>


				<c:if test="${genePercentage.getDisplay()}">
					<div class="section">
						<h2 class="title" id="data-summary">Phenotype associations stats <span class="documentation" ><a href='' id='phenotypeStatsSection' class="fa fa-question-circle pull-right"></a></span> </h2>
						<div class="inner">
							<!-- Phenotype Assoc. summary -->


							<c:if test="${parametersAssociated.size() == 0}">

									<c:if test="${genePercentage.getTotalGenesTested() > 0}">
										<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.getMpTerm()}
										(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
									</c:if>
									<p>
									<c:if test="${genePercentage.getFemaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
									</c:if>
									<c:if test="${genePercentage.getMaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span>
									</c:if>
									</p>


							</c:if>
							<c:if test="${parametersAssociated.size() > 0}">

									<c:if test="${genePercentage.getTotalGenesTested() > 0}">
										<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.getMpTerm()}
										(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
									</c:if>
									<p>
									<c:if test="${genePercentage.getFemaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
									</c:if>
									<c:if test="${genePercentage.getMaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span>
									</c:if>
									</p>



							</c:if>


							<!-- baseline charts -->
							<c:if test="${parametersAssociated.size() > 0}">
								<c:if test="${parametersAssociated.size() > 1}">
										<p> Select a parameter <i class="fa fa-bar-chart-o" ></i>&nbsp; &nbsp;
											<select class="overviewSelect" onchange="ajaxToBe('${phenotype.getMpId()}', this.options[this.selectedIndex].value);">
												<c:forEach var="assocParam" items="${parametersAssociated}" varStatus="loop">
													<option value="${assocParam.getStableId()}">${assocParam.getName()} (${assocParam.getStableId()})</option>
												</c:forEach>
											</select>
										</p>
									</c:if>
									<div id="baselineChart"></div>
										<c:if test="${parametersAssociated.size() > 0}">
												<div id="chartsHalfBaseline" class="half">
												<%-- <c:if test="${parametersAssociated.size() > 1}">
													<p> Select a parameter <i class="fa fa-bar-chart-o" ></i>&nbsp; &nbsp;
														<select class="overviewSelect" onchange="ajaxToBeBaseline('${phenotype.getMpId()}', this.options[this.selectedIndex].value);">
															<c:forEach var="assocParam" items="${parametersAssociated}" varStatus="loop">
																<option value="${assocParam.getStableId()}">${assocParam.getName()} (${assocParam.getStableId()})</option>
															</c:forEach>
														</select>
													</p>
												</c:if> --%>
												<br/>

												<div id="baseline-chart-container">
													<div id="baseline-chart-div" class="baselineChart" parameter="${parametersAssociated.get(0).getStableId()}" mp="${phenotype.getMpId()}">
													</div>
													<div id="spinner-baseline-charts"><i class="fa fa-refresh fa-spin"></i></div>
												</div>

												<div id='baseline-chartFilters'></div>

											</div>
										</c:if>








								<!-- Overview Graphs -->
								<c:if test="${parametersAssociated.size() > 0}">
								<div id="chartsHalf" class="half">

									<br/>

									<div id="chart-container">
										<div id="single-chart-div" class="oChart" parameter="${parametersAssociated.get(0).getStableId()}" mp="${phenotype.getMpId()}">
										</div>
										<div id="spinner-overview-charts"><i class="fa fa-refresh fa-spin"></i></div>
									</div>

									<div id='chartFilters'></div>

								</div>
							</c:if>
						</c:if>
							<div class="clear"></div>
					</div>
				</div>
			</c:if>

			<c:if test="${hasData}">
				<div class="section">

			    <h2 class="title" id="gene-variants"><a name="hasGeneVariants">Gene variants with ${phenotype.getMpTerm()}</a>
			    <span class="documentation" ><a href='' id='geneVariantSection' class="fa fa-question-circle pull-right"></a></span>
			    </h2>

					<div class="inner">

						<c:if test="${errorMessage != null}">
							<div class="alert alert-info"><p>${errorMessage}</p></div>
						</c:if>

						<div id="phenotypesDiv">
							<div class="container span12">
								<c:forEach var="filterParameters" items="${paramValues.fq}">
									${filterParameters}
								</c:forEach>
								<c:if test="${not empty phenotypes}">
									<form class="tablefiltering no-style" id="target" action="">
											<c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
													<select id="${phenoFacet.key}" class="impcdropdown"	multiple="multiple" title="Filter on ${phenoFacet.key}">
														<c:forEach var="facet" items="${phenoFacet.value}">
															<option>${facet.key}</option>
														</c:forEach>
													</select>
											</c:forEach>

											<div class="clear"></div>
									</form>

									<jsp:include page="geneVariantsWithPhenotypeTable.jsp"/>

									<br/>
									<div id="export">
										<p class="textright">
											Download data as:
											<a id="tsvDownload" href="${baseUrl}/phenotypes/export/${phenotype.getMpId()}?fileType=tsv&fileName=${phenotype.getMpTerm()}" target="_blank" class="button fa fa-download">TSV</a>
											<a id="xlsDownload" href="${baseUrl}/phenotypes/export/${phenotype.getMpId()}?fileType=xls&fileName=${phenotype.getMpTerm()}" target="_blank" class="button fa fa-download">XLS</a>
										</p>
									</div>
							</c:if>
							</div>
							<c:if test="${empty phenotypes}">
								<div class="alert alert-info"> Phenotype associations to genes and alleles will be available once data has completed quality control.</div>
							</c:if>
						</div>
					</div>
				</div><!-- end of section -->

			</c:if>

			<!-- example for images on phenotypes page: http://localhost:8080/phenotype-archive/phenotypes/MP:0000572 -->
			<!--  old sanger images section should be replaced by PHIS route soon so we could delete this soon!!!??? -->
			<c:if test="${not empty images && fn:length(images) !=0}">
				<div class="section" id="imagesSection">
						<h2 class="title" id="section">Images <i
									class="fa fa-question-circle pull-right"></i>
						</h2>
							<div class="inner">
								<%--<div class="accordion-body" style="display: block">--%>
										<%--<div class="accordion-heading">--%>
												<%--Phenotype Associated Images--%>
										<%--</div>--%>
									<div class="accordion-body" style="display: block">
										<div id="grid">
											<ul>
												<c:forEach var="doc" items="${images}">
		                                            <li class="span2">
														<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
		                                            </li>
		    	  								</c:forEach>
											</ul>
									</div>
											<div class="clear"></div>
												<c:if test="${entry.count>5}">
												<p class="textright">
													<a href="${baseUrl}/images?phenotype_id=${phenotype.getMpId()}">show all  ${images.getNumFound()} images</a>
												</p>
												</c:if>
										</div>
									</div>
								<!--  end of accordion -->
								<%--</div>--%>
					</div>	<!--  end of images section -->
			</c:if>

			<%--IMPC images--%>
			<c:if test="${not empty impcImageGroups}">
				<div class="section" id="imagesSection">
					<h2 class="title">Associated Images </h2>
						<div class="inner">
							<jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
						</div>
				</div>
			</c:if>
		</div>
	</div>
</div>
</div>
    <script type="text/javascript">
        $('document').ready(function(){

            var whatIsRelatedSyn = "Related synonyms are mostly terms of the Human Phenotype Ontology that are mapped to an mammalian phenotype (MP) term. Occasionally, they may be children of the current MP term.";

            // what is related synonym
            $('i.relatedSyn').qtip({
                content: {
                    text: whatIsRelatedSyn
                },
                style: {
                    classes: 'qtipimpc'
                }
            });


            // show more/less for related synonyms
            $('span.synToggle').click(function(){
               var partList = $(this).siblings('ul').find('li.defaultList');
               var fullList = $(this).siblings('ul').find('li.fullList');

               if ($(this).siblings('ul').find('li.defaultList').is(':visible')){
                   partList.hide();
                   fullList.show();
                   $(this).text('Show less');
               }
               else {
                   partList.show();
                   fullList.hide();
                   $(this).text('Show more');
               }
            });
        });
    </script>

</jsp:body>

</t:genericpage>


