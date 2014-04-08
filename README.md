dbMultipleLookup
================

WSO2 ESB (http://wso2.com/products/enterprise-service-bus/) mediator that performs look up against defined connection or data source and puts returned result set as a message context property for further use. This mediator was designed based on abstract database mediator, serializer and factory so it shares common configuration and processing approach of well known dblookup mediator. The difference in these two is that dbMultipleLookup returns a result set rather than a single result.

**Usage:**

Drop the built artifact multiple-dblookup-mediator-1.0.jar into:

```
<WSO2_ESB_HOME>/repository/components/dropins
```


**Configuration using JNDI data source:**

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

**Configuration with custom connection:**

```xml
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

**Getting Multiple DB Lookup Result set:**

```xml
<property name="RESULT_SET" expression="get-property('DB_MULTIPLE_LOOKUP_RESULTSET')"/>
```

```xml
some other examples...
```