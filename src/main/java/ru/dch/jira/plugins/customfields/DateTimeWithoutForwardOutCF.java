package ru.dch.jira.plugins.customfields;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.datetime.*;
import com.atlassian.jira.imports.project.customfield.*;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.converters.DatePickerConverter;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.*;
import com.atlassian.jira.issue.fields.rest.json.*;
import com.atlassian.jira.issue.history.DateTimeFieldChangeLogHelper;
import com.atlassian.jira.security.JiraAuthenticationContext;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class DateTimeWithoutForwardOutCF extends AbstractSingleFieldType
        implements SortableCustomField, ProjectImportableCustomField
{
    /* member class not found */
    class Visitor {}


    public DateTimeWithoutForwardOutCF(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager, DateTimeFormatterFactory dateTimeFormatterFactory, JiraAuthenticationContext jiraAuthenticationContext, ApplicationProperties applicationProperties, DateTimeFieldChangeLogHelper dateTimeFieldChangeLogHelper)
    {
        super(customFieldValuePersister, genericConfigManager);
        this.applicationProperties = applicationProperties;
        this.dateTimeFieldChangeLogHelper = dateTimeFieldChangeLogHelper;
        projectCustomFieldImporter = new NoTransformationCustomFieldImporter();
        datePickerFormatter = dateTimeFormatterFactory.formatter().forLoggedInUser().withStyle(DateTimeStyle.DATE_TIME_PICKER);
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

//    /**
//     * @deprecated Method DateTimeCFType is deprecated
//     */
//
//    public DateTimeCFType(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager, DateTimeFormatterFactory dateTimeFormatterFactory, JiraAuthenticationContext jiraAuthenticationContext, ApplicationProperties applicationProperties)
//    {
//        this(customFieldValuePersister, genericConfigManager, dateTimeFormatterFactory, jiraAuthenticationContext, applicationProperties, (DateTimeFieldChangeLogHelper)ComponentAccessor.getComponentOfType(com/atlassian/jira/issue/history/DateTimeFieldChangeLogHelper));
//    }

    protected PersistenceFieldType getDatabaseType()
    {
        return PersistenceFieldType.TYPE_DATE;
    }

    protected Object getDbValueFromObject(Date customFieldObject)
    {
        return customFieldObject;
    }

    protected Date getObjectFromDbValue(Object databaseValue)
            throws FieldValidationException
    {
        return (Date)databaseValue;
    }

    public String getStringFromSingularObject(Date customFieldObject)
    {
        return datePickerFormatter.format(customFieldObject);
    }

    public String getChangelogString(CustomField field, Date value)
    {
        if(value == null)
            return "";
        else
            return getStringFromSingularObject(value);
    }

    public String getChangelogValue(CustomField field, Date value)
    {
        if(value == null)
            return "";
        else
            return dateTimeFieldChangeLogHelper.createChangelogValueForDateTimeField(value);
    }

    public Date getSingularObjectFromString(String string) throws FieldValidationException
    {
        if(StringUtils.isEmpty(string))
            return null;
        Date date = datePickerFormatter.parse(string);
        return new Timestamp(date.getTime());
    }

    public int compare(Date v1, Date v2, FieldConfig fieldConfig)
    {
        return v1.compareTo(v2);
    }

    public Date getDefaultValue(FieldConfig fieldConfig)
    {
        Date defaultValue = (Date)genericConfigManager.retrieve("DefaultValue", fieldConfig.getId().toString());
        if(isUseNow(defaultValue))
            defaultValue = new Timestamp((new Date()).getTime());
        return defaultValue;
    }

    public boolean isUseNow(Date date)
    {
        return DatePickerConverter.USE_NOW_DATE.equals(date);
    }

    public boolean isUseNow(FieldConfig fieldConfig)
    {
        Date defaultValue = (Date)genericConfigManager.retrieve("DefaultValue", fieldConfig.getId().toString());
        return isUseNow(defaultValue);
    }

    public String getNow()
    {
        return datePickerFormatter.format(new Date());
    }

    public Map getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        Map map = super.getVelocityParameters(issue, field, fieldLayoutItem);
        map.put("dateTimePicker", Boolean.TRUE);
        map.put("datePickerFormatter", datePickerFormatter);
        map.put("titleFormatter", datePickerFormatter.withStyle(DateTimeStyle.COMPLETE));
        map.put("iso8601Formatter", datePickerFormatter.withStyle(DateTimeStyle.ISO_8601_DATE_TIME));
        return map;
    }

    public ProjectCustomFieldImporter getProjectImporter()
    {
        return projectCustomFieldImporter;
    }

//    public Object accept(AbstractCustomFieldType.VisitorBase visitor)
//    {
//        if(visitor instanceof Visitor)
//            return ((Visitor)visitor).visitDateTime(this);
//        else
//            return super.accept(visitor);
//    }

    public FieldTypeInfo getFieldTypeInfo(FieldTypeInfoContext fieldTypeInfoContext)
    {
        return new FieldTypeInfo(null, null);
    }

    public JsonType getJsonSchema(CustomField customField)
    {
        return JsonTypeBuilder.custom("datetime", getKey(), customField.getIdAsLong());
    }

//    public FieldJsonRepresentation getJsonFromIssue(CustomField field, Issue issue, boolean renderedVersionRequested, FieldLayoutItem fieldLayoutItem)
//    {
//        Date date = (Date)getValueFromIssue(field, issue);
//        if(date == null)
//            return new FieldJsonRepresentation(new JsonData(null));
//        FieldJsonRepresentation pair = new FieldJsonRepresentation(new JsonData(Dates.asTimeString(date)));
//        if(renderedVersionRequested)
//            pair.setRenderedData(new JsonData(((DateTimeFormatterFactory)ComponentAccessor.getComponent(com/atlassian/jira/datetime/DateTimeFormatterFactory)).formatter().forLoggedInUser().format(date)));
//        return pair;
//    }

//    public RestFieldOperationsHandler getRestFieldOperation(CustomField field)
//    {
//        return new DateTimeCustomFieldOperationsHandler(field, datePickerFormatter, getI18nBean());
//    }



    protected  Object getDbValueFromObject(Object x0)
    {
        return getDbValueFromObject((Date)x0);
    }

    public  String getChangelogValue(CustomField x0, Object x1)
    {
        return getChangelogValue(x0, (Date)x1);
    }



    public  String getChangelogString(CustomField x0, Object x1)
    {
        return getChangelogString(x0, (Date)x1);
    }

    public  String getStringFromSingularObject(Object x0)
    {
        return getStringFromSingularObject((Date)x0);
    }

    public  int compare(Object x0, Object x1, FieldConfig x2)
    {
        return compare((Date)x0, (Date)x1, x2);
    }

    private final ProjectCustomFieldImporter projectCustomFieldImporter;
    private final DateTimeFormatter datePickerFormatter;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ApplicationProperties applicationProperties;
    private final DateTimeFieldChangeLogHelper dateTimeFieldChangeLogHelper;
}