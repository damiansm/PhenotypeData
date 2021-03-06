/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.db.dao;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.ReferenceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Repository
@Transactional
public class ReferenceDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public final String heading =
            "MGI allele symbol"
        + "\tMGI allele id"
        + "\tIMPC gene link"
        + "\tMGI allele name"
        + "\tTitle"
        + "\tjournal"
        + "\tPMID"
        + "\tDate of publication"
        + "\tGrant id"
        + "\tGrant agency"
        + "\tPaper link"
        + "\tMesh term"
        + "\tConsortium paper"
        + "\tabstract"
        + "\tcited_by";

    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    public ReferenceDAO() {

    }

    /**
     * Given a list of <code>ReferenceDTO</code> and a <code>pubMedId</code>,
     * returns the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     *
     * @param references a list of <code>ReferenceDTO</code>
     *
     * @param pubMedId pub med ID
     *
     * @return the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     *
     * @throws SQLException
     */
    public ReferenceDTO getReferenceByPmid(List<ReferenceDTO> references, Integer pubMedId
    ) throws SQLException {
        for (ReferenceDTO reference : references) {
            if (reference.getPmid() == pubMedId) {
                return reference;
            }
        }

        return null;
    }

    /**
     * Fetch all reference rows.
     *
     * @return all reference rows.
     *
     * @throws SQLException
     */
    public List<ReferenceDTO> getReferenceRows() throws SQLException {
        return getReferenceRows();
    }

    /**
     * Fetch the reference rows, optionally filtered.
     *
     * @param filter Filter string, which may be null or empty, indicating no
     * filtering is desired. If supplied, a WHERE clause of the form "LIKE
     * '%<i>filter</i>%' is used in the query to query all fields for
     * <code>filter</code>.
     *
     * @return the reference rows, optionally filtered.
     *
     * @throws SQLException
     *
     */

    public List<ReferenceDTO> getReferenceRowsAgencyPaper(String searchKw, String filter, String orderBy) throws SQLException {
        Connection connection = admintoolsDataSource.getConnection();

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";

        List<String> srchCols = new ArrayList<>();
        String searchClause = "";

        if (filter == null){
            filter = "";
        }

        if (! filter.isEmpty()){
            srchCols = new ArrayList<>(Arrays.asList("title", "mesh", "abstract", "cited_by", "author", "journal", "symbol"));

            int occurrence = findOccurrenceOfSubstr(filter, "|");
            int loop = occurrence + 1;

            List<String> titleLikes = new ArrayList<>();
            List<String> meshLikes = new ArrayList<>();
            List<String> abstractLikes = new ArrayList<>();
            List<String> citedByLikes = new ArrayList<>();
            List<String> authorByLikes = new ArrayList<>();
            List<String> agencyByLikes = new ArrayList<>();
            List<String> journalByLikes = new ArrayList<>();
            List<String> alleleSymbolByLikes = new ArrayList<>();

            for (int oc = 0; oc < loop; oc++) {
                for (String col : srchCols) {
                    if (col.equals("title")) {
                        titleLikes.add(col + " LIKE ? ");
                    } else if (col.equals("mesh")) {
                        meshLikes.add(col + " LIKE ? ");
                    } else if (col.equals("abstract")) {
                        abstractLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("cited_by")) {
                        citedByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("author")) {
                        authorByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("journal")) {
                        journalByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("symbol")) {
                        alleleSymbolByLikes.add(col + " LIKE ? ");
                    }
                }
            }

            searchClause =
                    "  AND (\n"
                            + "(" + StringUtils.join(titleLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(meshLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(abstractLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(citedByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(authorByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(journalByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(alleleSymbolByLikes, " OR ") + "))\n";
        }


        String sql =  "SELECT\n"
                + "  symbol AS alleleSymbols\n"
                + ", acc AS alleleAccessionIds\n"
                + ", gacc AS geneAccessionIds\n"
                + ", title\n"
                + ", journal\n"
                + ", pmid\n"
                + ", date_of_publication\n"
                + ", agency AS grantAgencies\n"
                + ", paper_url AS paperUrls\n"
                + ", mesh\n"
                + ", author\n"
                + ", consortium_paper\n"
                + ", abstract\n"
                + ", cited_by\n"
                + " FROM allele_ref\n"
                + " WHERE agency LIKE '%" + searchKw + "%'\n"
                + searchClause + "\n"
                + " AND falsepositive='no'\n"
                + " AND reviewed='yes'\n"
                + " ORDER BY " + orderBy + "\n";

        System.out.println("alleleRef agency paper query: " + sql);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the parameter holder ? with the values.

                List<String> fltrs = Arrays.asList(StringUtils.split(filter,"|"));

                int colCount = 0;
                for (int i=0; i<fltrs.size(); i++){
                    for (int j=0; j<srchCols.size(); j++) {
                        colCount++;
                        ps.setString(colCount, "%" + fltrs.get(i) + "%");
                    }
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));
                referenceRow.setAuthor(resultSet.getString("author"));
                referenceRow.setConsortiumPaper(resultSet.getString("consortium_paper"));
                referenceRow.setAbstractTxt(resultSet.getString("abstract"));
                referenceRow.setCitedBy(resultSet.getString("cited_by"));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("Fetching agency related papers failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        System.out.println("Found " + results.size() + " papers");
        return results;
    }

    /**
     * Fetch the reference rows, optionally filtered.
     *
     * @param filter Filter string, which may be null or empty, indicating no
     * filtering is desired. If supplied, a WHERE clause of the form "LIKE
     * '%<i>filter</i>%' is used in the query to query all fields for
     * <code>filter</code>.
     *
     * @return the reference rows, optionally filtered.
     *
     * @throws SQLException
     *
     */

    public List<ReferenceDTO> getReferenceRows(Boolean agencyOnly, String agencySrch, String filter) throws SQLException {
        Connection connection = admintoolsDataSource.getConnection();

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";

        String whereClause = "WHERE agency LIKE '%" + agencySrch + "%'\nAND falsepositive = 'no' AND reviewed = 'yes'";
        int colCount = 0;
        String filterClause = "";

        if (filter != null){
            colCount = 12;
            filterClause =
                    "  AND (\n"
                            + "     title               LIKE ?\n"
                            + " OR journal             LIKE ?\n"
                            + " OR acc                 LIKE ?\n"
                            + " OR symbol              LIKE ?\n"
                            + " OR pmid                LIKE ?\n"
                            + " OR date_of_publication LIKE ?\n"
                            + " OR grant_id            LIKE ?\n"
                            + " OR agency              LIKE ?\n"
                            + " OR acronym             LIKE ?\n"
                            + " OR author              LIKE ?\n"
                            + " OR mesh                LIKE ?\n"
                            + " OR abstract            LIKE ?)\n";

            whereClause += filterClause;
        }


        String query =
                "SELECT\n"
                        + "  symbol AS alleleSymbols\n"
                        + ", acc AS alleleAccessionIds\n"
                        + ", gacc AS geneAccessionIds\n"
                        + ", name AS alleleNames\n"
                        + ", title\n"
                        + ", journal\n"
                        + ", pmid\n"
                        + ", date_of_publication\n"
                        + ", grant_id AS grantIds\n"
                        + ", agency AS grantAgencies\n"
                        + ", paper_url AS paperUrls\n"
                        + ", mesh\n"
                        + ", meshtree\n"
                        + ", author\n"
                        + ", consortium_paper\n"
                        + ", abstract"
                        + ", cited_by\n"
                        + "FROM allele_ref\n"
                        + whereClause
                        + " ORDER BY date_of_publication DESC\n";

        System.out.println("alleleRef query 3: " + query);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (! filterClause.isEmpty()) {
                // Replace the parameter holder ? with the values.
                String like = "%" + filter + "%";
                for (int i = 0; i < colCount; i++) {                                   // If a search clause was specified, load the parameters.
                    ps.setString(i + 1, like);
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames(Arrays.asList(resultSet.getString("alleleNames").split(delimeter)));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds(Arrays.asList(resultSet.getString("grantIds").split(delimeter)));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshJsonStr(resultSet.getString("meshtree"));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));
                referenceRow.setAuthor(resultSet.getString("author"));
                referenceRow.setConsortiumPaper(resultSet.getString("consortium_paper"));
                referenceRow.setAbstractTxt(resultSet.getString("abstract"));
                referenceRow.setCitedBy(resultSet.getString("cited_by"));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;
    }


    public List<ReferenceDTO> getReferenceRowsForBiologicalSystemPapers(String searchKw, String filter, String orderBy) throws SQLException {
        Connection connection = admintoolsDataSource.getConnection();
        // need to set max length for group_concat() otherwise some values would get chopped off !!
//    	String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
//
//    	PreparedStatement pst = connection.prepareStatement(gcsql);
//    	pst.executeQuery();

        if (searchKw == null) {
            searchKw = "";
        }
        if (filter == null) {
            filter = "";
        }

        System.out.println("Kw: " + searchKw + ", filter: " + filter);

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
        String pmidsToOmit = getPmidsToOmit();
        String notInClause = (pmidsToOmit.isEmpty() ? "" : "  AND pmid NOT IN (" + pmidsToOmit + ")\n");
        String searchClause = "";
        List<String> srchCols = new ArrayList<>(Arrays.asList("title", "mesh", "abstract"));

        List<String> filters = new ArrayList<>();
        if (! searchKw.isEmpty()){
            filters.add(searchKw);
        }
        if (! filter.isEmpty()){
            filters.add(filter);
        }
        String filterStr = StringUtils.join(filters, "|");

        for(String flt : filters ) {
            System.out.println("filter now: " + flt);
            int occurrence = findOccurrenceOfSubstr(flt, "|");
            int loop = occurrence + 1;
            System.out.println("loop: " + loop);
            List<String> titleLikes = new ArrayList<>();
            List<String> meshLikes = new ArrayList<>();
            List<String> abstractLikes = new ArrayList<>();


            for (int oc = 0; oc < loop; oc++) {
                for (String col : srchCols) {
                    if (col.equals("title")) {
                        titleLikes.add(col + " LIKE ? ");
                    } else if (col.equals("mesh")) {
                        meshLikes.add(col + " LIKE ? ");
                    } else if (col.equals("abstract")) {
                        abstractLikes.add(col + " LIKE ? ");
                    }
                }
            }

            searchClause +=
                    "  AND (\n"
                            + "(" + StringUtils.join(titleLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(meshLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(abstractLikes, " OR ") + "))\n";
        }

        String whereClause = "WHERE\n"
                    + " reviewed = 'yes'\n"
                    + " AND falsepositive = 'no'"
                    // + " AND symbol != '' "
                    + notInClause
                    + searchClause;

        String query =
                "SELECT\n"
                        + "  symbol AS alleleSymbols\n"
                        + ", acc AS alleleAccessionIds\n"
                        + ", gacc AS geneAccessionIds\n"
                        + ", name AS alleleNames\n"
//              + "  GROUP_CONCAT( symbol    SEPARATOR \"|||\") AS alleleSymbols\n"
//              + ", GROUP_CONCAT( acc       SEPARATOR \"|||\") AS alleleAccessionIds\n"
//              + ", GROUP_CONCAT( gacc      SEPARATOR \"|||\") AS geneAccessionIds\n"
//              + ", GROUP_CONCAT( name      SEPARATOR \"|||\") AS alleleNames\n"
                        + ", title\n"
                        + ", journal\n"
                        + ", pmid\n"
                        + ", date_of_publication\n"
                        + ", grant_id AS grantIds\n"
                        + ", agency AS grantAgencies\n"
                        + ", paper_url AS paperUrls\n"
                        + ", mesh\n"
                        + ", meshtree\n"
                        + ", author\n"
                        + ", consortium_paper\n"
                        + ", abstract"
                        + ", cited_by\n"
                        + "FROM allele_ref AS ar\n"
                        + whereClause
                        + " ORDER BY " + orderBy + "\n";

        System.out.println("alleleRef query 2: " + query);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the parameter holder ? with the values.

                List<String> fltrs = Arrays.asList(StringUtils.split(filterStr,"|"));

                int colCount = 0;
                for (int i=0; i<fltrs.size(); i++){
                    for (int j=0; j<srchCols.size(); j++) {
                        colCount++;
                       // System.out.println("CHECK: "+ srchCols.get(j) + " : "+  fltrs.get(i));
                        ps.setString(colCount, "%" + fltrs.get(i) + "%");
                    }
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames(Arrays.asList(resultSet.getString("alleleNames").split(delimeter)));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds(Arrays.asList(resultSet.getString("grantIds").split(delimeter)));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshJsonStr(resultSet.getString("meshtree"));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));
                referenceRow.setAuthor(resultSet.getString("author"));
                referenceRow.setConsortiumPaper(resultSet.getString("consortium_paper"));
                referenceRow.setAbstractTxt(resultSet.getString("abstract"));
                referenceRow.setCitedBy(resultSet.getString("cited_by"));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        System.out.println("Found " + results.size() + " papers");

        return results;
    }



    /**
     * Fetch the reference rows, optionally filtered.
     *
     * @param filter Filter string, which may be null or empty, indicating no
     * filtering is desired. If supplied, a WHERE clause of the form "LIKE
     * '%<i>filter</i>%' is used in the query to query all fields for
     * <code>filter</code>.
     *
     * @return the reference rows, optionally filtered.
     *
     * @throws SQLException
     *
     */

    public List<ReferenceDTO> getReferenceRows(String searchKw, String filter, String orderBy, Boolean consortium) throws SQLException {
        Connection connection = admintoolsDataSource.getConnection();
        // need to set max length for group_concat() otherwise some values would get chopped off !!
//    	String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
//
//    	PreparedStatement pst = connection.prepareStatement(gcsql);
//    	pst.executeQuery();

        if (searchKw == null) {
            searchKw = "";
        }
        if (filter == null) {
            filter = "";
        }

        System.out.println("Kw: " + searchKw + ", filter: " + filter);

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
        String pmidsToOmit = getPmidsToOmit();
        String notInClause = (pmidsToOmit.isEmpty() ? "" : "  AND pmid NOT IN (" + pmidsToOmit + ")\n");
        String searchClause = "";
        List<String> srchCols = new ArrayList<>(Arrays.asList("title", "mesh", "abstract", "cited_by", "author", "agency", "journal", "symbol"));

        List<String> filters = new ArrayList<>();
        if (! searchKw.isEmpty()){
            filters.add(searchKw);
        }
        if (! filter.isEmpty()){
            filters.add(filter);
        }
        String filterStr = StringUtils.join(filters, "|");

        for(String flt : filters ) {
            System.out.println("filter now: " + flt);
            int occurrence = findOccurrenceOfSubstr(flt, "|");
            int loop = occurrence + 1;
            System.out.println("loop: " + loop);
            List<String> titleLikes = new ArrayList<>();
            List<String> meshLikes = new ArrayList<>();
            List<String> abstractLikes = new ArrayList<>();
            List<String> citedByLikes = new ArrayList<>();
            List<String> authorByLikes = new ArrayList<>();
            List<String> agencyByLikes = new ArrayList<>();
            List<String> journalByLikes = new ArrayList<>();
            List<String> alleleSymbolByLikes = new ArrayList<>();

            for (int oc = 0; oc < loop; oc++) {
                for (String col : srchCols) {
                    if (col.equals("title")) {
                        titleLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("mesh")) {
                        meshLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("abstract")) {
                        abstractLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("cited_by")) {
                        citedByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("author")) {
                        authorByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("agency")) {
                        agencyByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("journal")) {
                        journalByLikes.add(col + " LIKE ? ");
                    }
                    else if (col.equals("symbol")) {
                        alleleSymbolByLikes.add(col + " LIKE ? ");
                    }
                }
            }

            searchClause =
                    "  AND (\n"
                            + "(" + StringUtils.join(titleLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(meshLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(abstractLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(citedByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(authorByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(agencyByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(journalByLikes, " OR ") + ")\n"
                            + " OR (" + StringUtils.join(alleleSymbolByLikes, " OR ") + "))\n";

        }


        String whereClause = "";

        if (consortium){
            whereClause = "WHERE consortium_paper='yes'\n";
            if (! filter.isEmpty()) {
                whereClause += searchClause;
            }

        }
        else {
            whereClause = "WHERE\n"
                    + " reviewed = 'yes'\n"
                    + " AND falsepositive = 'no'"
                   // + " AND symbol != '' "
                    + notInClause
                    + searchClause;
        }

        String query =
            "SELECT\n"
                    + "  symbol AS alleleSymbols\n"
                    + ", acc AS alleleAccessionIds\n"
                    + ", gacc AS geneAccessionIds\n"
                    + ", name AS alleleNames\n"
//              + "  GROUP_CONCAT( symbol    SEPARATOR \"|||\") AS alleleSymbols\n"
//              + ", GROUP_CONCAT( acc       SEPARATOR \"|||\") AS alleleAccessionIds\n"
//              + ", GROUP_CONCAT( gacc      SEPARATOR \"|||\") AS geneAccessionIds\n"
//              + ", GROUP_CONCAT( name      SEPARATOR \"|||\") AS alleleNames\n"
                    + ", title\n"
                    + ", journal\n"
                    + ", pmid\n"
                    + ", date_of_publication\n"
                    + ", grant_id AS grantIds\n"
                    + ", agency AS grantAgencies\n"
                    + ", paper_url AS paperUrls\n"
                    + ", mesh\n"
                    + ", meshtree\n"
                    + ", author\n"
                    + ", consortium_paper\n"
                    + ", abstract"
                    + ", cited_by\n"
                    + "FROM allele_ref AS ar\n"
                    + whereClause
                    + " ORDER BY " + orderBy + "\n";

        System.out.println("alleleRef query generic: " + query);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the parameter holder ? with the values.

                List<String> fltrs = Arrays.asList(StringUtils.split(filterStr,"|"));

                int colCount = 0;
                for (int i=0; i<fltrs.size(); i++){
                    for (int j=0; j<srchCols.size(); j++) {
                        colCount++;
                        System.out.println("CHECK: "+ srchCols.get(j) + " : "+  fltrs.get(i));
                        ps.setString(colCount, "%" + fltrs.get(i) + "%");
                    }
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames(Arrays.asList(resultSet.getString("alleleNames").split(delimeter)));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds(Arrays.asList(resultSet.getString("grantIds").split(delimeter)));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshJsonStr(resultSet.getString("meshtree"));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));
                referenceRow.setAuthor(resultSet.getString("author"));
                referenceRow.setConsortiumPaper(resultSet.getString("consortium_paper"));
                referenceRow.setAbstractTxt(resultSet.getString("abstract"));
                referenceRow.setCitedBy(resultSet.getString("cited_by"));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        System.out.println("Found " + results.size() + " papers");

        return results;
    }

    private int findOccurrenceOfSubstr (String str, String substr){
        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){

            lastIndex = str.indexOf(substr,lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += substr.length();
            }
        }
        return count;
    }

    public List<ReferenceDTO> getReferenceRows(String srchKw, Boolean consortium) throws SQLException {

    	Connection connection = admintoolsDataSource.getConnection();
    	// need to set max length for group_concat() otherwise some values would get chopped off !!
//    	String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
//
//    	PreparedStatement pst = connection.prepareStatement(gcsql);
//    	pst.executeQuery();

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
        String pmidsToOmit = getPmidsToOmit();
        String notInClause = (pmidsToOmit.isEmpty() ? "" : "  AND pmid NOT IN (" + pmidsToOmit + ")\n");
        String searchClause = "";

        int colCount = 0;

        if (! srchKw.isEmpty()) {
            if ( srchKw.contains("|")){
                searchClause =
                        "  AND (\n"
                                + "(title LIKE ? or title LIKE ?)\n"
                                + " OR (journal LIKE ? OR journal LIKE ?)\n"
                                + " OR (acc LIKE ? OR acc LIKE ?)\n"
                                + " OR (symbol LIKE ? OR symbol LIKE ?)\n"
                                + " OR (pmid LIKE ? OR pmid LIKE ?)\n"
                                + " OR (date_of_publication LIKE ? OR date_of_publication LIKE ?)\n"
                                + " OR (grant_id LIKE ? OR grant_id LIKE ?)\n"
                                + " OR (agency LIKE ? OR agency LIKE ?)\n"
                                + " OR (acronym LIKE ? OR acronym LIKE ?)\n"
                                + " OR (author LIKE ? OR author LIKE ?)\n"
                                + " OR (abstract LIKE ? OR abstract LIKE ?)\n"
                                + " OR (mesh LIKE ? OR mesh LIKE ?))\n";
                colCount = 24;
            }
            else {
                colCount = 12;
                searchClause =
                        "  AND (\n"
                                + "     title               LIKE ?\n"
                                + " OR journal             LIKE ?\n"
                                + " OR acc                 LIKE ?\n"
                                + " OR symbol              LIKE ?\n"
                                + " OR pmid                LIKE ?\n"
                                + " OR date_of_publication LIKE ?\n"
                                + " OR grant_id            LIKE ?\n"
                                + " OR agency              LIKE ?\n"
                                + " OR acronym             LIKE ?\n"
                                + " OR author              LIKE ?\n"
                                + " OR abstract            LIKE ?\n"
                                + " OR mesh                LIKE ?)\n";
            }
        }

        String whereClause = "";

        if (consortium) {
            whereClause = "WHERE consortium_paper='yes' " + notInClause + searchClause;
        }
        else {
            whereClause = "WHERE\n"
                    + " reviewed = 'yes'\n"
                    + " AND falsepositive = 'no'"
                   // + " AND symbol != ''\n"

                    // some paper are forced to be reviewed although no gacc and acc is known, but symbol will have been set as "Not available"
                    // + " AND gacc != ''\n"
                    // + " AND acc != ''\n"
                    + notInClause
                    + searchClause;
        }

                    //    + " AND pmid=24652767 "; // for test
        String query =
                "SELECT\n"
              + "  symbol AS alleleSymbols\n"
              + ", acc AS alleleAccessionIds\n"
              + ", gacc AS geneAccessionIds\n"
              + ", name AS alleleNames\n"
//              + "  GROUP_CONCAT( symbol    SEPARATOR \"|||\") AS alleleSymbols\n"
//              + ", GROUP_CONCAT( acc       SEPARATOR \"|||\") AS alleleAccessionIds\n"
//              + ", GROUP_CONCAT( gacc      SEPARATOR \"|||\") AS geneAccessionIds\n"
//              + ", GROUP_CONCAT( name      SEPARATOR \"|||\") AS alleleNames\n"
              + ", title\n"
              + ", journal\n"
              + ", pmid\n"
              + ", date_of_publication\n"
              + ", grant_id AS grantIds\n"
              + ", agency AS grantAgencies\n"
              + ", paper_url AS paperUrls\n"
              + ", mesh\n"
              + ", meshtree\n"
              + ", consortium_paper\n"
              + ", abstract"
              + ", cited_by\n"
              + "FROM allele_ref \n"
              + whereClause
              //+ "GROUP BY pmid\n"
              + " ORDER BY date_of_publication DESC\n";

        System.out.println("alleleRef query 1: " + query);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the parameter holder ? with the values.

                String like1, like2 = null;
                if (srchKw.contains("|")){
                    String[] fltr = StringUtils.split(srchKw,"|");
                    like1 = "%" + fltr[0] + "%";
                    like2 = "%" + fltr[1] + "%";

                    for (int i = 0; i < colCount; i=i+2) {                                   // If a search clause was specified, load the parameters.
                        ps.setString(i + 1, like1);
                        ps.setString(i + 2, like2);
                    }
                }
                else {
                    like1 = "%" + srchKw + "%";
                    for (int i = 0; i < colCount; i++) {                                   // If a search clause was specified, load the parameters.
                        ps.setString(i + 1, like1);
                    }
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames(Arrays.asList(resultSet.getString("alleleNames").split(delimeter)));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds(Arrays.asList(resultSet.getString("grantIds").split(delimeter)));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));
                referenceRow.setMeshJsonStr(resultSet.getString("meshtree"));
                referenceRow.setConsortiumPaper(resultSet.getString("consortium_paper"));
                referenceRow.setAbstractTxt(resultSet.getString("abstract"));
                referenceRow.setCitedBy(resultSet.getString("cited_by"));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Returns a comma-separated, single-quoted list of pmid values to omit.
     * This is meant to be a filter to omit any common distinct papers. Rows
     * with more than MAX_ROWS distinct paper references are excluded from the
     * download row set.
     *
     * @return a comma-separated list of pmid values to omit.
     */
    public String getPmidsToOmit() throws SQLException {
        StringBuilder retVal = new StringBuilder();

        // Filter to omit any common distinct papers. Rows with more than MAX_ROWS distinct papers are excluded.
        final int MAX_PAPERS = 140;

        Connection connection = admintoolsDataSource.getConnection();
        String query = "SELECT pmid FROM allele_ref ar GROUP BY pmid HAVING (SELECT COUNT(symbol) FROM allele_ref ar2 WHERE ar2.pmid = ar.pmid) > " + MAX_PAPERS;

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (retVal.length() > 0) {
                    retVal.append(", ");
                }
                retVal.append("'").append(rs.getInt("pmid")).append("'");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            log.error("getPmidsToOmit() call failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return retVal.toString();
    }
}
