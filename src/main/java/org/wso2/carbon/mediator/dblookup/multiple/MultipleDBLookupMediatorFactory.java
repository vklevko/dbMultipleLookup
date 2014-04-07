package org.wso2.carbon.mediator.dblookup.multiple;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.commons.datasource.DataSourceInformation;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.config.xml.SynapseXPathFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.apache.synapse.mediators.db.Statement;
import org.apache.synapse.securevault.secret.SecretInformation;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;

import javax.naming.Context;
import javax.xml.namespace.QName;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Properties;

/**
 * User: vladimirkl
 * Date: 10/17/13
 * Time: 10:53 AM
 */
public class MultipleDBLookupMediatorFactory extends AbstractMediatorFactory {

    private static final QName DB_MULTIPLE_LOOKUP_QNAME = new QName(SynapseConstants.SYNAPSE_NAMESPACE, "dbMultipleLookup");

    public static final QName URL_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "url");
    static final QName DRIVER_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "driver");
    static final QName USER_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "user");
    static final QName PASS_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "password");

    public static final QName DSNAME_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "dsName");
    static final QName ICCLASS_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "icClass");

    static final QName STMNT_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "statement");
    static final QName SQL_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "sql");
    static final QName PARAM_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "parameter");
    static final QName RESULT_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "result");

    static final QName ATT_COLUMN = new QName("column");
    static final QName ATT_TYPE = new QName("type");

    protected static final QName ATT_NAME    = new QName("name");
    protected static final QName ATT_VALUE   = new QName("value");
    protected static final QName ATT_XPATH   = new QName("xpath");
    protected static final QName ATT_REGEX   = new QName("regex");
    protected static final QName ATT_SEQUENCE = new QName("sequence");
    protected static final QName ATT_EXPRN   = new QName("expression");
    protected static final QName ATT_KEY     = new QName("key");
    protected static final QName ATT_SOURCE  = new QName("source");
    protected static final QName ATT_TARGET  = new QName("target");
    protected static final QName ATT_ONERROR = new QName("onError");
    protected static final QName ATT_STATS
            = new QName(XMLConfigConstants.STATISTICS_ATTRIB_NAME);
    protected static final QName PROP_Q
            = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "property");
    protected static final QName FEATURE_Q
            = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "feature");
    protected static final QName TARGET_Q
            = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "target");
    protected static final QName DESCRIPTION_Q
            = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "description");

    @Override
    protected Mediator createSpecificMediator(OMElement element, Properties properties) {
        MultipleDBLookupMediator mediator = new MultipleDBLookupMediator();
        buildDataSource(element, mediator);
        processStatements(element, mediator);
        return mediator;
    }

    /**
     * Reads the data source configuration for all mediators based on the <code>AbstractDBMediator</code>
     * and stores the configuration in the mediator for datasource initialization and de-serialization.
     *
     * @param elem the configuration element of the mediator
     * @param mediator the mediator on which the configuration shall be stored
     */
    protected void buildDataSource(OMElement elem, MultipleDBLookupMediator mediator) {

        OMElement pool;

        try {
            // get the 'pool' element and determine if we need to create a DataSource or
            // lookup using JNDI
            SynapseXPath xpath = new SynapseXPath("self::node()/syn:connection/syn:pool");
            xpath.addNamespace("syn", XMLConfigConstants.SYNAPSE_NAMESPACE);
            pool = (OMElement) xpath.selectSingleNode(elem);
            if (pool.getFirstChildWithName(DSNAME_Q) != null) {
                readLookupConfig(mediator, pool);
            } else if (pool.getFirstChildWithName(DRIVER_Q) != null) {
                readCustomDataSourceConfig(pool, mediator);
            } else {
                handleException("The DataSource connection information must be specified for " +
                        "using a custom DataSource connection pool or for a JNDI lookup");
            }
        } catch (JaxenException e) {
            handleException("Error looking up DataSource connection information", e);
        }
    }

    private void readLookupConfig(MultipleDBLookupMediator mediator, OMElement pool) {
        String dataSourceName = getValue(pool, DSNAME_Q);
        mediator.setDataSourceName(dataSourceName);
        saveElementConfig(pool, DSNAME_Q, mediator);

        if (pool.getFirstChildWithName(ICCLASS_Q) != null) {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, getValue(pool, ICCLASS_Q));
            props.put(Context.PROVIDER_URL, getValue(pool, URL_Q));
            props.put(Context.SECURITY_PRINCIPAL, getValue(pool, USER_Q));
            props.put(Context.SECURITY_CREDENTIALS, getValue(pool, PASS_Q));
            mediator.setJndiProperties(props);

            // Save element configuration for later de-serialization
            saveElementConfig(pool, ICCLASS_Q, mediator);
            saveElementConfig(pool, URL_Q, mediator);
            saveElementConfig(pool, USER_Q, mediator);
            saveElementConfig(pool, PASS_Q, mediator);
        }
    }

    private void readCustomDataSourceConfig(OMElement pool, MultipleDBLookupMediator mediator) {

        DataSourceInformation dataSourceInformation = new DataSourceInformation();

        dataSourceInformation.setDriver(getValue(pool, DRIVER_Q));
        dataSourceInformation.setUrl(getValue(pool, URL_Q));

        SecretInformation secretInformation = new SecretInformation();
        secretInformation.setUser(getValue(pool, USER_Q));
        secretInformation.setAliasSecret(getValue(pool, PASS_Q));
        dataSourceInformation.setSecretInformation(secretInformation);

        // Save element configuration for later de-serialization
        saveElementConfig(pool, DRIVER_Q, mediator);
        saveElementConfig(pool, URL_Q, mediator);
        saveElementConfig(pool, USER_Q, mediator);
        saveElementConfig(pool, PASS_Q, mediator);

        Iterator poolPropIter = pool.getChildrenWithName(PROP_Q);
        while (poolPropIter.hasNext()) {
            OMElement poolProp = (OMElement) poolPropIter.next();
            readPoolProperty(mediator, dataSourceInformation, poolProp);
        }

        mediator.setDataSourceInformation(dataSourceInformation);
    }

    private void readPoolProperty(MultipleDBLookupMediator mediator, DataSourceInformation dataSourceInformation,
                                  OMElement prop) {
        String name = prop.getAttribute(ATT_NAME).getAttributeValue();
        String value = prop.getAttribute(ATT_VALUE).getAttributeValue();
        mediator.addDataSourceProperty(name, value);

        if ("autocommit".equals(name)) {
            if ("true".equals(value)) {
                dataSourceInformation.setDefaultAutoCommit(true);
            } else if ("false".equals(value)) {
                dataSourceInformation.setDefaultAutoCommit(false);
            }
        } else if ("isolation".equals(name)) {
            try {
                if ("Connection.TRANSACTION_NONE".equals(value)) {
                    dataSourceInformation.setDefaultTransactionIsolation(Connection.TRANSACTION_NONE);
                } else if ("Connection.TRANSACTION_READ_COMMITTED".equals(value)) {
                    dataSourceInformation.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                } else if ("Connection.TRANSACTION_READ_UNCOMMITTED".equals(value)) {
                    dataSourceInformation.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                } else if ("Connection.TRANSACTION_REPEATABLE_READ".equals(value)) {
                    dataSourceInformation.setDefaultTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                } else if ("Connection.TRANSACTION_SERIALIZABLE".equals(value)) {
                    dataSourceInformation.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                }
            } catch (NumberFormatException ignore) {
            }
        } else if ("initialsize".equals(name)) {
            try {
                dataSourceInformation.setInitialSize(Integer.parseInt(value));
            } catch (NumberFormatException ignore) {
            }
        } else if ("maxactive".equals(name)) {
            try {
                dataSourceInformation.setMaxActive(Integer.parseInt(value));
            } catch (NumberFormatException ignore) {
            }
        } else if ("maxidle".equals(name)) {
            try {
                dataSourceInformation.setMaxIdle(Integer.parseInt(value));
            } catch (NumberFormatException ignore) {
            }
        } else if ("maxopenstatements".equals(name)) {
            try {
                dataSourceInformation.setMaxOpenPreparedStatements(Integer.parseInt(value));
            } catch (NumberFormatException ignore) {
            }
        } else if ("maxwait".equals(name)) {
            try {
                dataSourceInformation.setMaxWait(Long.parseLong(value));
            } catch (NumberFormatException ignore) {
            }
        } else if ("minidle".equals(name)) {
            try {
                dataSourceInformation.setMinIdle(Integer.parseInt(value));
            } catch (NumberFormatException ignore) {
            }
        } else if ("poolstatements".equals(name)) {
            if ("true".equals(value)) {
                dataSourceInformation.setPoolPreparedStatements(true);
            } else if ("false".equals(value)) {
                dataSourceInformation.setPoolPreparedStatements(false);
            }
        } else if ("testonborrow".equals(name)) {
            if ("true".equals(value)) {
                dataSourceInformation.setTestOnBorrow(true);
            } else if ("false".equals(value)) {
                dataSourceInformation.setTestOnBorrow(false);
            }
        } else if ("testonreturn".equals(name)) {
            if ("true".equals(value)) {
                dataSourceInformation.setTestOnReturn(true);
            } else if ("false".equals(value)) {
                dataSourceInformation.setTestOnReturn(false);
            }
        } else if ("testwhileidle".equals(name)) {
            if ("true".equals(value)) {
                dataSourceInformation.setTestWhileIdle(true);
            } else if ("false".equals(value)) {
                dataSourceInformation.setTestWhileIdle(false);
            }
        } else if ("validationquery".equals(name)) {
            dataSourceInformation.setValidationQuery(value);
        }
    }

    protected void processStatements(OMElement elem, MultipleDBLookupMediator mediator) {

        Iterator iter = elem.getChildrenWithName(STMNT_Q);
        while (iter.hasNext()) {

            OMElement stmntElt = (OMElement) iter.next();
            Statement statement = new Statement(getValue(stmntElt, SQL_Q));

            Iterator paramIter = stmntElt.getChildrenWithName(PARAM_Q);
            while (paramIter.hasNext()) {

                OMElement paramElt = (OMElement) paramIter.next();
                String xpath = getAttribute(paramElt, ATT_EXPRN);
                String value = getAttribute(paramElt, ATT_VALUE);

                if (xpath != null || value != null) {

                    SynapseXPath xp = null;
                    if (xpath != null) {
                        try {
                            xp = SynapseXPathFactory.getSynapseXPath(paramElt, ATT_EXPRN);

                        } catch (JaxenException e) {
                            handleException("Invalid XPath specified for the source attribute : " +
                                    xpath);
                        }
                    }
                    statement.addParameter(
                            value,
                            xp,
                            getAttribute(paramElt, ATT_TYPE));
                }
            }

            Iterator resultIter = stmntElt.getChildrenWithName(RESULT_Q);
            while (resultIter.hasNext()) {

                OMElement resultElt = (OMElement) resultIter.next();
                statement.addResult(
                        getAttribute(resultElt, ATT_NAME),
                        getAttribute(resultElt, ATT_COLUMN));
            }

            mediator.addStatement(statement);
        }
    }

    protected String getValue(OMElement elt, QName qName) {
        OMElement e = elt.getFirstChildWithName(qName);
        if (e != null) {
            return e.getText();
        } else {
            handleException("Unable to read configuration value for : " + qName);
        }
        return null;
    }

    protected String getAttribute(OMElement elt, QName qName) {
        OMAttribute a = elt.getAttribute(qName);
        if (a != null) {
            return a.getAttributeValue();
        }
        return null;
    }

    private void saveElementConfig(OMElement element, QName qname, MultipleDBLookupMediator mediator) {
        mediator.addDataSourceProperty(qname, getValue(element, qname));
    }
    @Override
    public QName getTagQName() {
        return DB_MULTIPLE_LOOKUP_QNAME;
    }
}
