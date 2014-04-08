package org.wso2.carbon.mediator.dblookup.multiple;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.db.AbstractDBMediator;
import org.apache.synapse.mediators.db.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: vladimirkl
 * Date: 10/17/13
 * Time: 10:53 AM
 */
public class MultipleDBLookupMediator extends AbstractDBMediator {

    protected void processStatement(Statement statement, MessageContext messageContext) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            PreparedStatement ps = getPreparedStatement(statement, messageContext);
            connection = ps.getConnection();
            resultSet = ps.executeQuery();
            OMFactory omFactory = messageContext.getEnvelope().getOMFactory();
            OMElement multipleDBLookup = omFactory.createOMElement(MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_TAG_NAME, MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_NS, MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_NS_PREFIX);
            while (resultSet.next()) {
                OMElement dbLookupResult = omFactory.createOMElement("row", MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_NS, MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_NS_PREFIX);
                for (String propertyName : statement.getResultsMap().keySet()) {
                    OMElement result = omFactory.createOMElement("column", MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_NS, MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_NS_PREFIX);
                    result.addAttribute(omFactory.createOMAttribute("name", null, propertyName));
                    result.addAttribute(omFactory.createOMAttribute("value", null, String.valueOf(resultSet.getObject(propertyName))));
                    dbLookupResult.addChild(result);
                }
                multipleDBLookup.addChild(dbLookupResult);
            }
            messageContext.setProperty(MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_PROPERTY_NAME, multipleDBLookup);
        } catch (Exception e) {
            log.error("Error occurred during SQL multiple lookup execution.", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log.warn("Error occurred while trying to close result set.", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.warn("Error occurred while trying to close connection.", e);
                }
            }
        }
    }
}
