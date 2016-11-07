<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="section">
    <div class="inner">
        <p> ${shortDescription} </p>
        <br/> <br/>

        <c:if test="${genePercentage.getDisplay()}">

            <div class="half">
                <div id="pieChart">
                    <script type="text/javascript">${genePercentage.getPieChartCode()}</script>
                </div>

                <c:if test="${genePercentage.getTotalGenesTested() > 0}">
                    <p><span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of the
                        tested genes with null mutations on a B6N genetic background have related phenotype
                        associations
                        (${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()})
                    </p>
                </c:if>
                <c:if test="${genePercentage.getFemaleGenesTested() > 0}">
                    <p>
                        <span class="padleft"><span class="bigger"> ${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
                    </p>
                </c:if>
                <c:if test="${genePercentage.getMaleGenesTested() > 0}">
                    <p> <span class="padleft"><span  class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span> </p>
                </c:if>
            </div>


            <div class="half">
                <table>
                    <thead>
                        <tr> <th class="headerSort"> Phenotype </th> <th> # Genes </th> </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${phenotypes}"  varStatus="loop">
                        <tr <c:if test="${loop.index >= 10}"> class="hidden hideable" </c:if> >
                            <td class="capitalize">${row.getCategory()}</td>
                            <c:if test="${row.getMpId() != null}">
                                <td><a href="${baseUrl}/phenotypes/${row.getMpId()}">${row.getCount()} </a></td>
                            </c:if>
                            <c:if test="${row.getMpId() == null}">
                                <td><h4>${row.getCount()}</h4></td>
                            </c:if>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td>
                            <c:if test="${phenotypes.size() > 10}"> <a id="showMore">Show more</a> </c:if> </td>
                        </td>
                        <td>
                            <a id="tsvDownload" href="${baseUrl}/phenotypes/export/${mpId}?fileType=tsv&fileName=IMPC_${pageTitle}" target="_blank" class="button fa fa-download">Download</a>
                        </td>
                    </tr>
                    </tbody>
                </table>

            </div>

            <div class="clear both"></div>
        </c:if>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function() {
        $('#showMore').click(function () {
            $(".hideable").toggleClass("hidden");
        });
    });
</script>