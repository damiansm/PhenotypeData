<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	
	<jsp:attribute name="title">Invalid identifier ${gene.name}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>	
	
	
	<jsp:body>
		<div class="region region-content">              
			<div class="block block-system">
				<div class="content">
					<div class="node">
						<h1>Oops! IMPC lists only protein-coding genes.</h1>
						<c:choose>
						<c:when test="${fn:containsIgnoreCase(acc , 'MGI:')  }">
							<p>Try <a href="http://www.informatics.jax.org/marker/${acc}">http://www.informatics.jax.org/marker/${acc}</a> to find out if this is the case.</p>
						</c:when>
						<c:otherwise>
							<p>A gene identifier should start with MGI: to be valid</p>	
						</c:otherwise>
						</c:choose>
						<p class="lead">Example of a valid page: <a href="${baseUrl}${exampleURI}">${exampleURI}</a> </p>
						<p><a href="${pageContext.request.contextPath}">Click here to search IMPC.</a></p>
						<div class="clear"> </div>
					</div>
				</div>
			</div>
		</div>
	</jsp:body>
	
</t:genericpage>