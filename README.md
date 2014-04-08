dbMultipleLookup
================

WSO2 ESB (http://wso2.com/products/enterprise-service-bus/) mediator that performs look up against defined connection or data source and puts returned result set as a message context property for further use. This mediator was designed based on abstract database mediator, serializer and factory so it shares common configuration and processing approach of well known dblookup mediator. the only difference in these two is that dbMultipleLookup returns a result set rather than a single result.

Configuration using JNDI data source:

```xml
<dbMultipleLookup>
    <connection>
        <pool>
            <dsName>jndi/DATASOURCE</dsName>
        </pool>
    </connection>
    <statement>
        <sql>SELECT * FROM `SOME_TABLE`</sql>
        <result name="RES_1" column="COL_1"/>
        <result name="RES_2" column="COL_2"/>
    </statement>
</dbMultipleLookup>
```

Configuration with custom connection:

<dbMultipleLookup>
    <connection>
         <driver>com.example.Driver</driver>
         <url>jdbc://example.com/database</url>
         <user/>
         <password/>
    </connection>
    <statement>
        <sql>SELECT * FROM `SOME_TABLE`</sql>
        <result name="RES_1" column="COL_1"/>
        <result name="RES_2" column="COL_2"/>
    </statement>
</dbMultipleLookup>
```