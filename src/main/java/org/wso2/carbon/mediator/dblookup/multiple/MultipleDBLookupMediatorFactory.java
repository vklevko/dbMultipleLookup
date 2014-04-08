package org.wso2.carbon.mediator.dblookup.multiple;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractDBMediatorFactory;

import javax.xml.namespace.QName;
import java.util.Properties;

/**
 Factory for org.wso2.carbon.mediator.dblookup.multiple.MultipleDBLookupMediator instances.

 Configuration syntax:

 <dbMultipleLookup>
    <connection>
        <pool>
        (
            <driver/>
            <url/>
            <user/>
            <password/>
        |
            <dsName/>
            <icClass/>
            <url/>
            <user/>
            <password/>
        )
            <property name="name" value="value"/>*
        </pool>
    </connection>
    <statement>
        <sql>select something from table where something_else = ?</sql>
        <parameter [value="" | expression=""] type="int|string"/>*
        <result name="string"/>
    </statement>
 </dbMultipleLookup>

*/
public class MultipleDBLookupMediatorFactory extends AbstractDBMediatorFactory {



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
     */
    @Override
    public QName getTagQName() {
        return MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_QNAME;
    }
}
