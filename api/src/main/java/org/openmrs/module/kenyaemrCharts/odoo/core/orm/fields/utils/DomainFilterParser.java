package org.openmrs.module.kenyaemrCharts.odoo.core.orm.fields.utils;

/*import android.os.Bundle;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;*/

import org.json.JSONArray;
import org.openmrs.module.kenyaemrCharts.odoo.core.orm.OModel;
import org.openmrs.module.kenyaemrCharts.odoo.core.orm.fields.OColumn;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.helper.ODomain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DomainFilterParser {

    private OModel baseModel;
    private OColumn baseColumn;
    private String domainString;
    private LinkedHashMap<String, FilterDomain> filterColumnValues = new LinkedHashMap<String, FilterDomain>();

    public DomainFilterParser(OModel model, OColumn column, String domainString) {
        baseModel = model;
        baseColumn = column;
        this.domainString = domainString;
        parseDomains();
    }

    public void parseDomains() {
        try {
            JSONArray fieldDomain = new JSONArray(domainString);
            for (int i = 0; i < fieldDomain.length(); i++) {
                if (fieldDomain.get(i) instanceof String) {
                    FilterDomain operator = new FilterDomain();
                    operator.operator_value = fieldDomain.get(i).toString();
                    filterColumnValues.put("operator#" + i, operator);
                } else {
                    JSONArray domain = fieldDomain.getJSONArray(i);
                    String compareField = domain.getString(0);
                    String operator = domain.getString(1);
                    String value = domain.getString(2);
                    FilterDomain filterDomain = new FilterDomain();
                    filterDomain.baseColumn = compareField;
                    filterDomain.operator = operator;
                    String key;
                    if (value.trim().startsWith("@")) {
                        String valueField = value.replace("@", "");
                        key = String.format("%s#%s", compareField, valueField);
                        filterDomain.valueColumn = valueField;
                        filterColumnValues.put(key, filterDomain);
                    } else {
                        key = String.format("%s#%s", compareField, compareField);
                        filterDomain.value = value;
                    }
                    filterColumnValues.put(key, filterDomain);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getFilterColumns() {
        return new ArrayList<String>(filterColumnValues.keySet());
    }

    private void setValue(String key, Object value) {
        if (filterColumnValues.containsKey(key)) {
            FilterDomain filterDomain = filterColumnValues.get(key);
            filterDomain.value = value;
            filterColumnValues.put(key, filterDomain);
        }
    }

    public LinkedHashMap<String, OColumn.ColumnDomain> getDomain(Map<String, Object> formData) {
        if (formData != null) {
            for (String key : formData.keySet()) {
                setValue(key, formData.get(key));
            }
        }
        LinkedHashMap<String, OColumn.ColumnDomain> domains = new LinkedHashMap<String, OColumn.ColumnDomain>();
        int i = 1;
        if (!baseColumn.getDomains().isEmpty()) {
            domains.put("condition_operator_#" + i, new OColumn.ColumnDomain("and"));
        }
        for (FilterDomain domain : filterColumnValues.values()) {
            if (domain.operator_value != null)
                domains.put("condition_operator_#" + i++,
                        new OColumn.ColumnDomain(domain.operator_value));
            else
                domains.put(domain.baseColumn,
                        new OColumn.ColumnDomain(domain.baseColumn, domain.operator, domain.value));
        }
        return domains;
    }

    public FilterDomain getFilter(String key) {
        return filterColumnValues.get(key);
    }

    public ODomain getRPCDomain(Map<String, Object> formData) {
        ODomain domain = new ODomain();

        // All domains in infix format
        LinkedHashMap<String, OColumn.ColumnDomain> domains = new LinkedHashMap<String, OColumn.ColumnDomain>();
        domains.putAll(baseColumn.getDomains());
        domains.putAll(getDomain(formData));

        // processing to convert in prefix

        List<FilterDomain> priority = new ArrayList<FilterDomain>();
        List<List<FilterDomain>> alternative = new ArrayList<List<FilterDomain>>();
        for (String key : domains.keySet()) {
            OColumn.ColumnDomain colDomain = domains.get(key);
            if (colDomain.getConditionalOperator() != null) {
                if (colDomain.getConditionalOperator().equals("or")) {
                    alternative.add(new ArrayList<FilterDomain>(priority));
                    priority = new ArrayList<FilterDomain>();
                }
            } else {
                FilterDomain filterDomain = new FilterDomain();
                filterDomain.baseColumn = colDomain.getColumn();
                filterDomain.operator = colDomain.getOperator();
                filterDomain.value = colDomain.getValue();
                priority.add(filterDomain);
            }
        }
        if (!priority.isEmpty()) {
            alternative.add(new ArrayList<FilterDomain>(priority));
        }
        List<FilterDomain> newDomain = new ArrayList<FilterDomain>();
        FilterDomain or = new FilterDomain();
        or.operator_value = "or";
        for (int i = 0; i < alternative.size() - 1; i++) newDomain.add(or);
        for (List<FilterDomain> items : alternative) {
            FilterDomain and = new FilterDomain();
            and.operator_value = "and";
            for (int i = 0; i < items.size() - 1; i++) newDomain.add(and);
            newDomain.addAll(items);
        }

        // Creating domain
        for (FilterDomain filterDomain : newDomain) {
            if (filterDomain.operator_value != null) {
                /*switch (filterDomain.operator_value) {
                    case "and":
                        domain.add("&");
                        break;
                    case "or":
                        domain.add("|");
                        break;
                }*/
            } else {
                Object value = filterDomain.value;
                OColumn domainColumn = baseModel.getColumn(filterDomain.baseColumn);
                if (domainColumn != null && domainColumn.getRelationType() != null) {
                    switch (domainColumn.getRelationType()) {
                        case ManyToOne:
                            OModel relModel = baseModel.createInstance(domainColumn.getType());
                            if (filterDomain.value instanceof Integer)
                                value = relModel.selectServerId((Integer) filterDomain.value);
                            break;
                    }
                }
                domain.add(filterDomain.baseColumn, filterDomain.operator, value);
            }
        }
        return domain;
    }

    public class FilterDomain {
        public String baseColumn;
        public String operator;
        public Object value;
        public String valueColumn;
        public String operator_value;

        @Override
        public String toString() {
            if (operator_value != null)
                return operator_value;
            return String.format("['%s', '%s', @%s=%s]",
                    baseColumn, operator, valueColumn != null ? valueColumn : "value", value + "");
        }
    }
}
