package org.wso2.carbon.mediator.dblookup.multiple;

import org.apache.synapse.SynapseConstants;

import javax.xml.namespace.QName;

/**
 * Class that holds constant for the org.wso2.carbon.mediator.dblookup.multiple.MultipleDBLookupMediator,
 * org.wso2.carbon.mediator.dblookup.multiple.MultipleDBLookupMediatorFactory
 * and org.wso2.carbon.mediator.dblookup.multiple.MultipleDBLookupMediatorSerializer
 */
public class MultipleDBLookupMediatorConstants {
    public static final String DB_MULTIPLE_LOOKUP_TAG_NAME = "dbMultipleLookup";
    public static final QName DB_MULTIPLE_LOOKUP_QNAME = new QName(SynapseConstants.SYNAPSE_NAMESPACE, DB_MULTIPLE_LOOKUP_TAG_NAME);
    public static final String DB_MULTIPLE_LOOKUP_NS="http://wso2.org/mediator/multipledblookup";
    public static final String DB_MULTIPLE_LOOKUP_NS_PREFIX="mdbl";
    public static final String DB_MULTIPLE_LOOKUP_PROPERTY_NAME="DB_MULTIPLE_LOOKUP_RESULTSET";
}
