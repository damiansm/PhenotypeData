/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.dataimport.dcc.support;

import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by mrelac on 02/03/2016.
 */
public class DccSqlUtils {

    private       CommonUtils commonUtils = new CommonUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    private NamedParameterJdbcTemplate npJdbcTemplate;


    @Inject
    public DccSqlUtils(NamedParameterJdbcTemplate npJdbcTemplate) {
        Assert.notNull(npJdbcTemplate, "Named parameter npJdbcTemplate cannot be null");
        this.npJdbcTemplate = npJdbcTemplate;
    }


    public String dumpSpecimen(long centerPk, long specimenPk) {
        String retVal = "";

        String query =
                "SELECT\n"
                        + "  cs.pk AS cs_pk\n"
                        + ", c.pk AS c_pk\n"
                        + ", s.pk AS s_pk\n"
                        + ", s.statuscode_pk AS s_statuscode_pk\n"
                        + ", c.centerId\n"
                        + ", c.pipeline\n"
                        + ", c.project\n"
                        + ", s.colonyId\n"
                        + ", s.gender\n"
                        + ", s.isBaseline\n"
                        + ", s.litterId\n"
                        + ", s.phenotypingCenter\n"
                        + ", s.pipeline\n"
                        + ", s.productionCenter\n"
                        + ", s.specimenId\n"
                        + ", s.strainId\n"
                        + ", s.zygosity\n"
                        + ", sc.dateOfStatuscode\n"
                        + ", sc.value\n"
                        + ", m.DOB\n"
                        + ", e.stage\n"
                        + ", e.stageUnit\n"
                        + "FROM center c\n"
                        + "JOIN center_specimen cs ON cs.center_pk = c.pk\n"
                        + "JOIN specimen s ON cs.specimen_pk = s.pk\n"
                        + "LEFT OUTER JOIN mouse m ON m.specimen_pk = cs.specimen_pk\n"
                        + "LEFT OUTER JOIN embryo e ON e.specimen_pk = cs.specimen_pk\n"
                        + "LEFT OUTER JOIN statuscode sc ON sc.pk = s.statuscode_pk\n"
                        + "WHERE c.pk = :centerPk AND s.pk = :specimenPk;";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("centerPk", centerPk);
        parameterMap.put("specimenPk", specimenPk);
        SqlRowSet rs = npJdbcTemplate.queryForRowSet(query, parameterMap);

        if (rs.next()) {
            retVal += "{"
                    + "cs.pk=" + rs.getLong("cs_pk")
                    + ",c.pk=" + rs.getLong("c_pk")
                    + ",s.pk=" + rs.getLong("s_pk")
                    + ",s.statuscode_pk=" + (rs.getLong("s_statuscode_pk") == 0 ? "<null>" : rs.getLong("s_statuscode_pk"))
                    + ",centerId=" + rs.getString("c.centerId")
                    + ",pipeline=" + rs.getString("c.pipeline")
                    + ",project=" + rs.getString("c.project")
                    + ",colonyId=" + rs.getString("s.colonyId")
                    + ",gender=" + rs.getString("s.gender")
                    + ",isBaseline=" + rs.getInt("s.isBaseline")
                    + ",litterId=" + rs.getString("s.litterId")
                    + ",phenotypingCenter=" + rs.getString("s.phenotypingCenter")
                    + ",productionCenter=" + (rs.getString("s.productionCenter") == null ? "<null>" : rs.getString("s.productionCenter"))
                    + ",specimenId=" + rs.getString("s.specimenId")
                    + ",strainId=" + rs.getString("s.strainId")
                    + ",zygosity=" + rs.getString("s.zygosity");
            if (rs.getLong("s_statuscode_pk") != 0) {
                retVal += ",sc.dateOfStatuscode=" + (rs.getDate("sc.dateOfStatuscode") == null ? "<null>" : rs.getDate("sc.dateOfStatuscode"))
                        + ",sc.value=" + rs.getString("sc.value");
            }
            retVal += (rs.getDate("m.DOB") == null ? " (EMBRYO)" : " (MOUSE)");
        }

        return retVal;
    }

    @Deprecated
    public void truncateExperimentTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[] {
                  "experiment"
                , "experiment_statuscode"
                , "experiment_specimen"
                , "housing"
                , "line"
                , "procedure_"
                , "center_procedure"
                , "line_statuscode"
                , "simpleParameter"
                , "ontologyParameter"
                , "seriesParameter"
                , "mediaParameter"
                , "ontologyParameterTerm"
                , "seriesParameterValue"
                , "mediaParameter_parameterAssociation"
                , "mediaParameter_procedureMetadata"
                , "parameterAssociation"
                , "procedureMetadata"
                , "dimension"
                , "mediaSampleParameter"
                , "mediaSample"
                , "mediaSection"
                , "mediaFile"
                , "mediaFile_parameterAssociation"
                , "mediaFile_procedureMetadata"
                , "seriesMediaParameter"
                , "procedure_procedureMetadata"
                , "seriesMediaParameterValue"
                , "seriesMediaParameterValue_parameterAssociation"
                , "seriesMediaParameterValue_procedureMetadata"
        };

        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.execute();
        for (String tableName : tables) {
            query = "TRUNCATE " + tableName + ";";

            try {
                ps = connection.prepareStatement(query);
                ps.execute();
            } catch (SQLException e) {
                logger.error("Unable to truncate table " + tableName);
                throw e;
            }
        }
        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
        ps.execute();
    }

    @Deprecated
    public void truncateSpecimenTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[] {
                  "center"
                , "center_specimen"
                , "embryo"
                , "genotype"
                , "mouse"
                , "parentalStrain"
                , "relatedSpecimen"
                , "specimen"
                , "statuscode"
        };

        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.execute();
        for (String tableName : tables) {
            query = "TRUNCATE " + tableName + ";";

            try {
                ps = connection.prepareStatement(query);
                ps.execute();
            } catch (SQLException e) {
                logger.error("Unable to truncate table " + tableName);
                throw e;
            }
        }
        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
        ps.execute();
    }


    // GETS


    /**
     * Returns the primary key for the given centerId, pipeline, and project, if found; 0 otherwise
     *
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     *
     * @return The center primary key if found; 0 otherwise.
     */
    public long getCenterPk(String centerId, String pipeline, String project) {
        long centerPk = 0L;
        String query = "SELECT pk FROM center WHERE centerId = :centerId AND pipeline = :pipeline AND project = :project";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("centerId", centerId);
        parameterMap.put("pipeline", pipeline);
        parameterMap.put("project", project);

        try {
            centerPk = npJdbcTemplate.queryForObject(query, new HashMap<>(), Long.class);
        } catch (Exception e) {

        }

        return centerPk;
    }

    public long getCenter_specimenPk(long centerPk, long specimenPk) {
        long center_specimenPk = 0L;
        String query = "SELECT pk FROM center_specimen WHERE center_pk = :centerPk AND specimen_pk = :specimenPk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("centerPk", centerPk);
        parameterMap.put("specimenPk", specimenPk);

        try {
            center_specimenPk = npJdbcTemplate.queryForObject(query, new HashMap<>(), Long.class);
        } catch (Exception e) {

        }

        return center_specimenPk;
    }

    /**
     * Returns a {@link List} of {@link Dimension} instances associated with parameter association primary key. If
     * there are no associations, an empty list is returned.
     *
     * @param parameterAssociationPk the parameter association primary key
     * @return
     */
    public List<Dimension> getDimensions(long parameterAssociationPk) {
        List<Dimension> dimensions = new ArrayList<>();

        String query = "SELECT * FROM dimension WHERE parameterAssociation_pk = :parameterAssociation_pk";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("parameterAssociation_pk", parameterAssociationPk);
        dimensions = npJdbcTemplate.query(query, parameterMap, new DimensionRowMapper());

        return dimensions;
    }

    /**
     * Returns the line primary key matching {@code colonyId} and {@code center_procedurePk}, if found; 0 otherwise.
     *
     * @param colonyId The {@link StatusCode} value to search for
     * @param center_procedurePk The center_procedure primary key value to search for
     * @return The line primary key matching {@code colonyId} and {@code center_procedurePk}, if found; 0 otherwise.
     */
    public long getLinePk(String colonyId, long center_procedurePk) {
        long pk = 0L;

        if (colonyId == null)
            return 0L;

        final String query = "SELECT pk FROM line WHERE colonyId = :colonyId AND center_procedure_pk = :center_procedurePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("colonyId", colonyId);
        parameterMap.put("center_procedurePk", center_procedurePk);

        try {
            pk = npJdbcTemplate.queryForObject(query, new HashMap<>(), Long.class);
        } catch (Exception e) {

        }

        return pk;
    }

    /**
     * Returns the <code>ParameterAssociation</code> matching <code>parameterId</code> and <code>sequenceId</code>, and
     * the {@link Dimension} collection, if found; null otherwise.
     *
     * @param parameterId The parameterId value to search for
     * @param sequenceId An optional sequence Id to search for. May be null.
     * @return The <code>ParameterAssociation</code> matching <code>parameterId</code> and, if not null,
     * <code>sequenceId</code>, if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     * <i>NOTE: The <code>Dimension</code> collection, if any, is returned as well.</i>
     */
    public ParameterAssociation getParameterAssociation(String parameterId, Integer sequenceId) {
        ParameterAssociation parameterAssociation = null;

        if (parameterId == null)
            return parameterAssociation;

        String query;
        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("parameterId", parameterId);
        if (sequenceId != null) {
            query = "SELECT * FROM parameterAssociation WHERE parameterId = :parameterId";
        } else {
            query = "SELECT * FROM parameterAssociation WHERE parameterId = :parameterId AND sequenceId = :sequenceId";
            parameterMap.put("sequenceId", sequenceId);
        }

        List<ParameterAssociation> parameterAssociations =
                npJdbcTemplate.query(query, parameterMap, new ParameterAssociationRowMapper());

        if ( ! parameterAssociations.isEmpty()) {
            parameterAssociation = parameterAssociations.get(0);
            if (parameterAssociation.getDim() != null) {
                parameterAssociation.setDim(getDimensions(parameterAssociation.getHjid()));
            }
        }

        for (ParameterAssociation pa : parameterAssociations) {
            pa.setDim(getDimensions(pa.getHjid()));
        }

        return parameterAssociation;
    }

    /**
     * Looks for the procedure for the given procedureId.
     * Retuns the {@link Procedure} instance if found; null otherwise.
     *
     * @param procedureId The procedure id
     * @return The {@link Procedure} instance if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public Procedure getProcedure(String procedureId) {
        Procedure procedure = null;
        final String query =
                "SELECT * FROM procedure_ WHERE procedureId = :procedureId";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("procedureId", procedureId);

        List<Procedure> procedures = npJdbcTemplate.query(query, parameterMap, new ProcedureRowMapper());
        if ( ! procedures.isEmpty()) {
            procedure = procedures.get(0);
        }

        return procedure;
    }

    /**
     * Returns the {@code ProcedureMetadata} matching {@code parameterId} and {@code sequenceId} if found; null
     * otherwise.
     *
     * @param parameterId The parameterId value to search for
     * @param sequenceId An optional sequence Id to search for. May be null.
     * @return The <code>ProcedureMetadata</code> matching <code>parameterId</code> and, if not null, <code>sequenceId</code>,
     * if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public ProcedureMetadata getProcedureMetadata(String parameterId, Integer sequenceId) {
        ProcedureMetadata procedureMetadata = null;

        if (parameterId == null)
            return procedureMetadata;

        String query;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("parameterId", parameterId);
        if (sequenceId == null) {
            query = "SELECT * FROM procedureMetadata WHERE parameterId = :parameterId";
        } else {
            query = "SELECT * FROM procedureMetadata WHERE parameterId = :parameterId AND sequenceId = :sequenceId";
            parameterMap.put("sequenceId", sequenceId);
        }

        List<ProcedureMetadata> procedureMetadataList =
                npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataRowMapper());

        if ( ! procedureMetadataList.isEmpty()) {
            procedureMetadata = procedureMetadataList.get(0);
        }

        return procedureMetadata;
    }

    /**
     * Looks for the {@code specimen} for the given specimenId and centerId.
     * Retuns the {@link Specimen} instance if found; null otherwise.
     *
     * @param specimenId The specimen id
     * @param centerId   The center id
     * @return The <code>Specimen</code> instance if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public Specimen getSpecimen(String specimenId, String centerId) {
        Specimen specimen = null;
        final String query =
                "SELECT s.*, sc.value AS status\n" +
                "FROM specimen s\n" +
                "JOIN            center_specimen cs ON cs.specimen_pk =  s .pk\n" +
                "JOIN            center          c  ON c .pk          =  cs.center_pk\n" +
                "LEFT OUTER JOIN statuscode      sc ON sc.pk          =  s .statuscode_pk\n" +
                "WHERE s.specimenId = :specimenId AND c.centerId = :centerId";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("specimenId", specimenId);
        parameterMap.put("centerId", centerId);

        List<Specimen> specimens = npJdbcTemplate.query(query, parameterMap, new SpecimenRowMapper());
        if ( ! specimens.isEmpty()) {
            specimen = specimens.get(0);
            specimen.setStatusCode(selectOrInsertStatuscode(specimen.getStatusCode()));
        }

        return specimen;
    }

    /**
     * Returns the status code matching {@code value}, if found; null otherwise.
     *
     * @param value The {@link StatusCode} value to search for
     * @return The {@link StatusCode} matching {@code value}, if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public StatusCode getStatuscode(String value) {
        StatusCode statuscode = null;

        if (value == null)
            return statuscode;

        final String query = "SELECT * FROM statuscode WHERE value = :value";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("value", value);

        List<StatusCode> statuscodes = npJdbcTemplate.query(query, parameterMap, new StatusCodeRowMapper());
        if ( ! statuscodes.isEmpty()) {
            statuscode = statuscodes.get(0);
        }

        return statuscode;
    }


    // INSERTS


    /**
     * Inserts the given {@code }centerId}, {@code }pipeline}, and {@code }project} into the center table. Duplicates
     * are ignored. The center primary key is returned.
     *
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     *
     * @return the primary key matching the newly inserted values
     */
    public long insertCenter(String centerId, String pipeline, String project) {
        long pk;
        String insert = "INSERT INTO center (centerId, pipeline, project) VALUES (:centerId, :pipeline, :project)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("centerId", centerId);
            parameterMap.put("pipeline", pipeline);
            parameterMap.put("project", project);

            npJdbcTemplate.update(insert, parameterMap);
            pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);

        } catch (DuplicateKeyException e) {
            pk = getCenterPk(centerId, pipeline, project);
        }

        return pk;
    }

    /**
     * Inserts the given {@code centerPk} and {@code specimenPk}, into the center_specimen table. Duplicates are ignored.
     * The center_specimen primary key is returned.
     *
     * @param centerPk   The center primary key
     * @param specimenPk The specimen primary key
     * @return the the center_specimen primary key
     */
    public long insertCenter_specimen(long centerPk, long specimenPk) {
        long pk;
        String insert = "INSERT INTO center_specimen (center_pk, specimen_pk) VALUES (:center_pk, :specimen_pk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("center_pk", centerPk);
            parameterMap.put("specimen_pk", specimenPk);

            npJdbcTemplate.update(insert, parameterMap);
            pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);

        } catch (DuplicateKeyException e) {
            pk = getCenter_specimenPk(centerPk, specimenPk);
        }

        return pk;
    }

    /**
     * Inserts the given embryo into the embryo table. Duplicates are ignored.
     *
     * @param embryo the embryo to be inserted
     *
     * @return the embryo, with primary key loaded
     */
    public Embryo insertEmbryo(Embryo embryo, long specimen_pk) {
        String insert = "INSERT INTO embryo (stage, stageUnit, specimen_pk) VALUES (:stage, :stageUnit, :specimen_pk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("stage", embryo.getStage());
            parameterMap.put("stageUnit", embryo.getStageUnit().value());
            parameterMap.put("specimen_pk", specimen_pk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                long pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
                embryo.setHjid(pk);
            }

        } catch (DuplicateKeyException e) {

        }

        return embryo;
    }

    /**
     * Inserts the given genotype into the genotype table. Duplicates are ignored.
     *
     * @param genotype the genotype to be inserted
     *
     * @return the genotype, with primary key loaded
     */
    public Genotype insertGenotype(Genotype genotype, long specimen_pk) {
        long pk = 0L;
        String insert = "INSERT INTO genotype (geneSymbol, mgiAlleleId, mgiGeneId, fatherZygosity, motherZygosity, specimen_pk) "
                     + " VALUES (:geneSymbol, :mgiAlleleId, :mgiGeneId, :fatherZygosity, :motherZygosity, :specimen_pk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("geneSymbol", genotype.getGeneSymbol());
            parameterMap.put("mgiAlleleId", genotype.getMGIAlleleId());
            parameterMap.put("mgiGeneId", genotype.getMGIGeneId());
            parameterMap.put("fatherZygosity", (genotype.getFatherZygosity() == null ? null : genotype.getFatherZygosity()));
            parameterMap.put("motherZygosity", (genotype.getMotherZygosity() == null ? null : genotype.getMotherZygosity()));
            parameterMap.put("specimen_pk", specimen_pk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
                genotype.setHjid(pk);
            }

        } catch (DuplicateKeyException e) {

        }

        return genotype;
    }

    /**
     * Inserts the given {@link Housing} into the {@code housing} table. Duplicates are ignored.
     *
     * @param housingList;   The list of {@link Housing} instances to be inserted
     * @param center_procedurePk   The center_procedure primary key
     */
    public void insertHousing(List<Housing> housingList, long center_procedurePk) {

        String insert = "INSERT INTO housing (fromLims, lastUpdated, center_procedure_pk) VALUES (:fromLims, :lastUpdated, :center_procedurePk)";

        for (Housing housing : housingList) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("fromLims", (housing.isFromLIMS() ? 1 : 0));
                parameterMap.put("lastUpdated", (housing.getLastUpdated() == null ? null : new Date(housing.getLastUpdated().getTime().getTime())));
                parameterMap.put("center_procedurePk", center_procedurePk);

                npJdbcTemplate.update(insert, parameterMap);

            } catch (DuplicateKeyException e) {

            }
        }
    }

    /**
     * Inserts the given {@link Line} into the {@code line} table. Duplicates are ignored.
     *
     * @param line;   The {@link Line} instance to be inserted
     * @param center_procedurePk   The center_procedure primary key
     *
     * @return returns the line primary key
     */
    public long insertLine(Line line, long center_procedurePk) {
        long pk = 0L;
        String insert = "INSERT INTO line (colonyId, center_procedure_pk) VALUES (:colonyId, :center_procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("colonyId", line.getColonyID());
            parameterMap.put("center_procedurePk", center_procedurePk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
            }

        } catch (DuplicateKeyException e) {

        }

        return pk;
    }

    /**
     * Inserts a row into the line_statuscode table.
     *
     * @param linePk;   The line primary key
     * @param statuscodePk   The statuscode primary key
     *
     * @return returns the line_statuscode primary key
     */
    public long insertLine_statuscode(long linePk, long statuscodePk) {
        long pk = 0L;
        String insert = "INSERT INTO line_statuscode (line_pk, statuscode_pk) VALUES (:linePk, :statuscodePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("linePk", linePk);
            parameterMap.put("statuscodePk", statuscodePk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
            }

        } catch (DuplicateKeyException e) {

        }

        return pk;
    }

    /**
     * Inserts the given mouse into the mouse table. Duplicates are ignored.
     *
     * @param mouse the mouse to be inserted
     *
     * @return the mouse, with primary key loaded
     */
    public Mouse insertMouse(Mouse mouse, long specimen_pk) {
        String insert = "INSERT INTO mouse (DOB, specimen_pk) VALUES (:DOB, :specimen_pk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("DOB", new java.sql.Date(mouse.getDOB().getTime().getTime()));
            parameterMap.put("specimen_pk", specimen_pk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                long pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
                mouse.setHjid(pk);
            }

        } catch (DuplicateKeyException e) {

        }

        return mouse;
    }
    
    /**
     * Inserts the given parentalStrain into the parentalStrain table. Duplicates are ignored.
     *
     * @param parentalStrain the parentalStrain to be inserted
     *
     * @return the parentalStrain, with primary key loaded
     */
    public ParentalStrain insertParentalStrain(ParentalStrain parentalStrain, long specimen_pk) {
        String insert = "INSERT INTO parentalStrain (percentage, mgiStrainId, gender, level, specimen_pk) "
                      + "VALUES (:percentage, :mgiStrainId, :gender, :level, :specimen_pk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("percentage", parentalStrain.getPercentage());
            parameterMap.put("mgiStrainId", parentalStrain.getMGIStrainID());
            parameterMap.put("gender", parentalStrain.getGender().value());
            parameterMap.put("level", parentalStrain.getLevel());
            parameterMap.put("specimen_pk", specimen_pk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                long pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
                parentalStrain.setHjid(pk);
            }

        } catch (DuplicateKeyException e) {

        }

        return parentalStrain;
    }

    /**
     * Inserts the given procedureId into the procedure_ table. Duplicates are ignored.
     *
     * @param procedureId the procedure id to be inserted
     *
     * @return the procedure, with primary key loaded
     */
    public Procedure insertProcedure(String procedureId) {
        Procedure procedure;
        String insert = "INSERT INTO procedure_ (procedureId) VALUES (:procedureId)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("procedureId", procedureId);

            npJdbcTemplate.update(insert, parameterMap);
            procedure = getProcedure(procedureId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("INSERT of procedure(" + procedureId + " FAILED: " + e.getLocalizedMessage());
        }

        return procedure;
    }

    /**
     * Inserts the given {@code procedurePk} and {@code procedureMetadataPk} into the procedure_procedureMetadata
     * table. Duplicates are ignored.
     *
     * @param procedurePk the procedure primary key to be inserted
     * @param procedureMetadataPk the procedureMetadata primary key to be inserted
     *
     * @return the procedure_procedureMetadata primary key
     */
    public long insertProcedure_procedureMetadata(long procedurePk, long procedureMetadataPk) {
        long pk = 0L;
        String insert = "INSERT INTO procedure_procedureMetadata(procedure_pk, procedureMetadata_pk) VALUES (:procedurePk, :procedureMetadataPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("procedurePk", procedurePk);
            parameterMap.put("procedureMetadataPk", procedureMetadataPk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
            }

        } catch (DuplicateKeyException e) {

        }

        return pk;
    }

    /**
     * Inserts the given relatedSpecimen into the relatedSpecimen table. Duplicates are ignored.
     *
     * @param relatedSpecimen the relatedSpecimen to be inserted
     *
     * @return the relatedSpecimen, with primary key loaded
     */
    public RelatedSpecimen insertRelatedSpecimen(RelatedSpecimen relatedSpecimen, long specimen_pk) {
        String insert = "INSERT INTO relatedSpecimen (relationship, specimenIdMine, specimen_theirs_pk) "
                      + "VALUES (:relationship, :specimenIdMine, :specimen_theirs_pk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("relationship", relatedSpecimen.getRelationship().value());
            parameterMap.put("specimenIdMine", relatedSpecimen.getSpecimenID());
            parameterMap.put("specimen_pk", specimen_pk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                long pk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
                relatedSpecimen.setHjid(pk);
            }

        } catch (DuplicateKeyException e) {

        }

        return relatedSpecimen;
    }

    public Specimen insertSpecimen(Specimen specimen) throws DataImportException {
        final String insert = "INSERT INTO specimen (" +
                    "colonyId, gender, isBaseline, litterId, phenotypingCenter, pipeline, productionCenter, project, specimenId, strainId, zygosity, statuscode_pk) VALUES " +
                    "(:colonyId, :gender, :isBaseline, :litterId, :phenotypingCenter, :pipeline, :productionCenter, :project, :specimenId, :strainId, :zygosity, :statuscode_pk);";

        Map<String, Object> parameterMap = new HashMap();

        try {
            parameterMap.put("colonyId", specimen.getColonyID());
            parameterMap.put("gender", specimen.getGender().value());
            parameterMap.put("isBaseline", specimen.isIsBaseline() ? 1 : 0);
            parameterMap.put("litterId", specimen.getLitterId());
            parameterMap.put("phenotypingCenter", specimen.getPhenotypingCentre().value());
            parameterMap.put("pipeline", specimen.getPipeline());
            parameterMap.put("productionCenter", specimen.getProductionCentre().value());
            parameterMap.put("project", specimen.getProject());
            parameterMap.put("specimenId", specimen.getSpecimenID());
            parameterMap.put("strainId", specimen.getStrainID());
            parameterMap.put("zygosity", specimen.getZygosity().value());
            parameterMap.put("statuscode_pk", (specimen.getStatusCode() == null ? null : specimen.getStatusCode().getHjid()));

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count > 0) {
                long specimenPk = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Long.class);
                specimen.setHjid(specimenPk);
            }
        } catch (Exception e) {
            throw new DataImportException(commonUtils.mapToString(parameterMap, "parameterMap"), e);
        }
        
        return specimen;
    }


    // SELECT OR INSERTS


    /**
     * Returns the center_procedure primary key matching the given centerPk and procedurePk. The data is first inserted
     * into the center_procedure table if it does not yet exist.
     *
     * @param centerPk The center primary key
     * @param procedurePk The procedure primary key
     *
     * @return the center_procedure primary key matching the given centerPk and procedurePk. The data is first inserted
          * into the center_procedure table if it does not yet exist.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public long selectOrInsertCenter_procedure(long centerPk, long procedurePk) {
        Map<String, Object> parameterMap = new HashMap<>();
        Long pk;
        final String query = "SELECT pk FROM center_procedure WHERE center_pk = :centerPk and procedure_pk = :procedurePk";

        parameterMap.put("centerPk", centerPk);
        parameterMap.put("procedurePk", procedurePk);
        pk = npJdbcTemplate.queryForObject(query, parameterMap, Long.class);
        if ((pk == null) || (pk == 0)) {
            String insert = "INSERT INTO center_procedure (center_pk, procedure_pk) VALUES (:centerPk, :procedurePk)";
            try {
                int count = npJdbcTemplate.update(insert, parameterMap);
                if (count > 0) {
                    pk = npJdbcTemplate.queryForObject(query, parameterMap, Long.class);
                }

            } catch (DuplicateKeyException e) {

            }
        }

        return pk;
    }

    /**
     * Given a parameterId value, attempts to fetch the matching <code>ParameterAssociation</code> instance. If there is
     * none, the parameterId and sequenceId are first inserted. The <code>ParameterAssociation</code> instance is then
     * returned.
     *
     * <i>NOTE: if <code>parameterId</code> is null, a null <code>ParameterAssociation</code> is returned.</i>
     * <i>NOTE: Any <code>Dimension</code></i> instances in the parameterAssociation are added to the dimension table
     *          and returned in the returned <code>ParameterAssociation</code> instance.
     *
     * @param parameterAssociation a valid <code>ParameterAssociation</code> instance
     *
     * @return The <code>ParameterAssociation</code> instance matching <code>parameterId</code> (and <code>sequenceId</code>,
     * if specified), inserted first if necessary.
     */
    public ParameterAssociation selectOrInsertParameterAssociation(ParameterAssociation parameterAssociation) {
        String parameterId = parameterAssociation.getParameterID();
        Integer sequenceId = (parameterAssociation.getSequenceID() == null ? null : parameterAssociation.getSequenceID().intValue());
        final String insertPa = "INSERT INTO parameterAssociation (parameterId, sequenceId) VALUES (:parameterId, :sequenceId)";
        final String insertDimension = "INSERT INTO dimension (id, origin, unit, value, parameterAssociation_pk) "
                                     + "VALUES(:id, :origin, :unit, :value, :parameterAssociation_pk)";

        ParameterAssociation retVal = getParameterAssociation(parameterId, sequenceId);

        if (retVal == null) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("parameterId", parameterId);
                parameterMap.put("sequenceId", sequenceId);

                npJdbcTemplate.update(insertPa, parameterMap);
                retVal = getParameterAssociation(parameterId, sequenceId);
                if (retVal.getDim() != null) {
                    parameterMap.clear();
                    for (Dimension dimension : retVal.getDim()) {
                        parameterMap.put("id", dimension.getId());
                        parameterMap.put("origin", dimension.getOrigin());
                        parameterMap.put("unit", dimension.getUnit());
                        parameterMap.put("value", dimension.getValue());
                        parameterMap.put("parameterAssociation_pk", retVal.getHjid());

                        npJdbcTemplate.update(insertDimension, parameterMap);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("INSERT of retVal(" + parameterId + ", " + sequenceId + " FAILED: " + e.getLocalizedMessage());
            }
        }

        return retVal;
    }

    /**
     * Given a parameterId value, attempts to fetch the matching <code>ProcedureMetadata</code> instance. If there is
     * none, the parameterId and sequenceId are first inserted. The <code>ProcedureMetadata</code> instance is then
     * returned.
     *
     * <i>NOTE: if <code>parameterId</code> is null, a null <code>ProcedureMetadata</code> is returned.</i>
     *
     * @param parameterId the parameterId to search for
     * @param sequenceId the sequence id to search for (may be null)
     *
     * @return The <code>ProcedureMetadata</code> instance matching <code>parameterId</code> (and <code>sequenceId</code>,
     * if specified), inserted first if necessary.
     */
    public ProcedureMetadata selectOrInsertProcedureMetadata(String parameterId, Integer sequenceId) {
        ProcedureMetadata procedureMetadata = getProcedureMetadata(parameterId, sequenceId);

        if (procedureMetadata == null) {
            String insert = "INSERT INTO procedureMetadata (parameterId, sequenceId) VALUES (:parameterId, :sequenceId)";
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("parameterId", parameterId);
                parameterMap.put("sequenceId", sequenceId);

                npJdbcTemplate.update(insert, parameterMap);
                procedureMetadata = getProcedureMetadata(parameterId, sequenceId);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("INSERT of parameterAssociation(" + parameterId + ", " + sequenceId + " FAILED: " + e.getLocalizedMessage());
            }
        }

        return procedureMetadata;
    }

    /**
     * Given a non-null statuscode value, attempts to insert the statuscode, ignoring any duplicates. Returns the
     * statuscode fetched from the database. A null {@code value} returns a null {@link StatusCode}.
     *
     * @param value The status code value
     * @param dateOfStatuscode statuscode date (may be null)  (Not used in SELECT)
     *
     * @return The {@link StatusCode} instance matching {@code value}
     */
    public StatusCode selectOrInsertStatuscode(String value, Calendar dateOfStatuscode) {
        if (value == null)
            return null;

        StatusCode statuscode = getStatuscode(value);

        if (statuscode == null) {
            String insert = "INSERT INTO statuscode (value, dateOfStatuscode) VALUES (:value, :dateOfStatuscode)\n";

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("value", value);
            parameterMap.put("dateOfStatuscode", dateOfStatuscode);

            npJdbcTemplate.update(insert, parameterMap);
            statuscode = getStatuscode(value);
        }

        return statuscode;
    }

    /**
     * Given a statuscode instance, attempts to fetch the matching object. If there is none, the value and (nullable)
     * dateOfStatuscode are first inserted. The <code>StatusCode</code> instance is then returned.
     *
     * @param statuscode The <code>StatusCode</code> instance
     *
     * @return The <code>StatusCode</code> instance matching <code>value</code>, inserted first if necessary.
     */
    public StatusCode selectOrInsertStatuscode(StatusCode statuscode) {
        if (statuscode == null)
            return null;

        return selectOrInsertStatuscode(statuscode.getValue(), statuscode.getDate());
    }


    // UPDATES


    public int updateRelatedSpecimenMinePk() {
        final String update = "UPDATE relatedSpecimen SET specimen_mine_pk = (SELECT pk FROM specimen WHERE relatedSpecimen.specimenIdMine = specimen.specimenId)";
        int count = npJdbcTemplate.update(update, new HashMap<String, Object>());

        return count;
    }


    // ROWMAPPERS


    public class DimensionRowMapper implements RowMapper<Dimension> {

        @Override
        public Dimension mapRow(ResultSet rs, int rowNum) throws SQLException {
            Dimension dimension = new Dimension();

            dimension.setHjid(rs.getLong("pk"));
            dimension.setId(rs.getString("id"));
            dimension.setOrigin(rs.getString("origin"));
            dimension.setUnit(rs.getString("unit"));
            BigDecimal value = rs.getBigDecimal("value");
            dimension.setValue( (rs.wasNull() ? null : value));

            return dimension;
        }
    }

    public class ParameterAssociationRowMapper implements RowMapper<ParameterAssociation> {

        @Override
        public ParameterAssociation mapRow(ResultSet rs, int rowNum) throws SQLException {
            ParameterAssociation parameterAssociation = new ParameterAssociation();

            parameterAssociation.setHjid(rs.getLong("pk"));
            parameterAssociation.setParameterID(rs.getString("parameterId"));
            parameterAssociation.setSequenceID(BigInteger.valueOf(rs.getLong("sequenceId")));

            return parameterAssociation;
        }
    }

    public class ProcedureMetadataRowMapper implements RowMapper<ProcedureMetadata> {

        @Override
        public ProcedureMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProcedureMetadata procedureMetadata = new ProcedureMetadata();

            procedureMetadata.setHjid(rs.getLong("pk"));
            procedureMetadata.setParameterID(rs.getString("parameterId"));
            procedureMetadata.setParameterStatus(rs.getString("parameterStatus"));
            procedureMetadata.setSequenceID(BigInteger.valueOf(rs.getLong("sequenceId")));
            procedureMetadata.setValue(rs.getString("value"));

            return procedureMetadata;
        }
    }

    public class ProcedureRowMapper implements RowMapper<Procedure> {

        @Override
        public Procedure mapRow(ResultSet rs, int rowNum) throws SQLException {
            Procedure procedure = new Procedure();

            procedure.setHjid(rs.getLong("pk"));
            procedure.setProcedureID(rs.getString("procedureId"));

            return procedure;
        }
    }

    public class SpecimenRowMapper implements RowMapper<Specimen> {

        @Override
        public Specimen mapRow(ResultSet rs, int rowNum) throws SQLException {
            Specimen specimen = new SpecimenCda();

            specimen.setHjid(rs.getLong("s.pk"));
            String colonyId = rs.getString("colonyId");
            if ( ! rs.wasNull()) {
                specimen.setColonyID(colonyId);
            }
            specimen.setGender(Gender.fromValue(rs.getString("gender")));
            specimen.setIsBaseline((rs.getInt("isBaseline") == 1 ? true : false));
            specimen.setLitterId(rs.getString("litterId"));
            specimen.setPhenotypingCentre(CentreILARcode.fromValue(rs.getString("phenotypingCenter")));
            specimen.setPipeline(rs.getString("pipeline"));
            String productionCenter = rs.getString("productionCenter");
            if ( ! rs.wasNull()) {
                specimen.setProductionCentre(CentreILARcode.fromValue(productionCenter));
            }
            specimen.setProject(rs.getString("project"));
            specimen.setSpecimenID(rs.getString("specimenId"));
            String strainId = rs.getString("strainId");
            if ( ! rs.wasNull()) {
                specimen.setStrainID(strainId);
            }
            specimen.setZygosity(Zygosity.fromValue(rs.getString("zygosity")));
            StatusCode statusCode = new StatusCode();
            statusCode.setValue(rs.getString("status"));
            specimen.setStatusCode(statusCode);

            return specimen;
        }
    }

    public class StatusCodeRowMapper implements RowMapper<StatusCode> {

        @Override
        public StatusCode mapRow(ResultSet rs, int rowNum) throws SQLException {
            StatusCode statuscode = new StatusCode();

            long pk = rs.getLong("pk");
            if (pk > 0) {
                statuscode.setHjid(pk);
            }

            statuscode.setValue(rs.getString("value"));
            Date dateOfStatuscode = rs.getDate("dateOfStatuscode");
            if ( ! rs.wasNull()) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(dateOfStatuscode);
                statuscode.setDate(gc);
            }

            return statuscode;
        }
    }
}