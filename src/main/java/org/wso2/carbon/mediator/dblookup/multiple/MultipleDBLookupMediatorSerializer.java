package org.wso2.carbon.mediator.dblookup.multiple;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.AbstractDBMediatorSerializer;
import org.apache.synapse.config.xml.AbstractMediatorSerializer;
import org.apache.synapse.config.xml.SynapseXPathSerializer;
import org.apache.synapse.mediators.db.Statement;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import java.sql.Types;

/**
 /**
 Serializer for org.wso2.carbon.mediator.dblookup.multiple.MultipleDBLookupMediator objects.

 Serialize into following syntax depending on the mediator config:

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
public class MultipleDBLookupMediatorSerializer extends AbstractDBMediatorSerializer {
    @Override
    protected OMElement serializeSpecificMediator(Mediator mediator) {
        if (!(mediator instanceof MultipleDBLookupMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + mediator.getType());
        }
        MultipleDBLookupMediator multipleDBLookupMediator = (MultipleDBLookupMediator) mediator;
        OMElement multipleDBLookupElement = fac.createOMElement(MultipleDBLookupMediatorConstants.DB_MULTIPLE_LOOKUP_TAG_NAME, synNS);
        saveTracingState(multipleDBLookupElement,mediator);
        serializeDBInformation(multipleDBLookupMediator, multipleDBLookupElement);
        return multipleDBLookupElement;

    }

    @Override
    public String getMediatorClassName() {
        return MultipleDBLookupMediator.class.getName();
    }
}
