package org.openmrs.module.kenyaemrCharts.openerp;


import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.helper.utils.OdooLog;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UserClient {

    static final String METHOD = "call";
    static final String FIELD_ID = "id";
    static final String FIELD_CODE = "code";
    static final String FIELD_NAME = "name";
    static final String COUNTRY_ID = "country_id";
    static final String COMMENT = "comment";
    static final String ODOO_PARTNER_MODEL = "res.partner";



    /*asList((Object[])models.execute("execute_kw", asList(
            db, uid, password,
    "res.partner", "search_read",
                                    asList(asList(
                                            asList("is_company", "=", true),
    asList("customer", "=", true))),
            new HashMap() {{
        put("fields", asList("name", "country_id", "comment"));
        put("limit", 5);
    }}
)));*/

    private static JSONArray fields = new JSONArray();
    static {
        fields.add(0, FIELD_NAME);
        fields.add(1, COUNTRY_ID);
        fields.add(2, COMMENT);
    }

    /*JSONArray filterSaleOk = new JSONArray();
    filterSaleOk.add(0, "sale_ok");
    filterSaleOk.add(1, "ilike");
    filterSaleOk.add(2, "true");

    JSONArray filterPrice = new JSONArray();
    filterPrice.add(0, "list_price");
    filterPrice.add(1, ">=");
    filterPrice.add(2, "0");
    domain.add(filterSaleOk);
    domain.add(filterPrice);*/

    private JSONRPC2Session mJSONRPC2Session;
    private URL mSearchReadUrl;
    private URL mCreateRecordUrl;
    private String mJSONSessionId;
    private JSONObject mUsercontext;

    public UserClient(OpenERPConnector aOpenERPConnector , URL aSearchReadURL, URL aCreateRecordURL){
        mJSONRPC2Session = aOpenERPConnector.getOpenERPJsonSession();
        mSearchReadUrl = aSearchReadURL;
        mCreateRecordUrl = aCreateRecordURL;
        mJSONSessionId = aOpenERPConnector.getOpenERPSessionId();
        mUsercontext = aOpenERPConnector.getOpenERPUserContext();
    }

    public org.json.JSONArray printProducts() throws OpenErpException {

        JSONRPC2Response jsonRPC2Response = null;
        try {
            mJSONRPC2Session.setURL(mSearchReadUrl);

            JSONArray domain = new JSONArray();
            JSONArray customer = new JSONArray();
            customer.add(0, "is_company");//"customer", "=", true
            customer.add(1, "=");//"customer", "=", true
            customer.add(2, true);//"customer", "=", true
            domain.add(customer);

            Map<String, Object> categoryParams = new HashMap<String, Object>();
            categoryParams.put("session_id", mJSONSessionId);
            categoryParams.put("context", mUsercontext);
            categoryParams.put("domain", domain);
            categoryParams.put("model", ODOO_PARTNER_MODEL);
            categoryParams.put("sort", "");
            categoryParams.put("fields", fields);

            jsonRPC2Response = mJSONRPC2Session.send(new JSONRPC2Request(METHOD, categoryParams, OpenERPUtil.generateRequestID()));
            JSONObject result = (JSONObject) jsonRPC2Response.getResult();
            JSONArray records = (JSONArray) result.get("records");
            org.json.JSONArray prods = new org.json.JSONArray();
            for(Object categoryObject : records) {
                JSONObject productJson = (JSONObject) categoryObject;
                org.json.JSONObject o = new org.json.JSONObject();
                String locName = (String) productJson.get(FIELD_NAME);
                //String countryId = (String) productJson.get(COMMENT);
                o.put("Product name", locName);
                //o.put("Product country code", countryId);
                prods.put(o);

                System.out.print("Product Name: " + locName);
                System.out.println(", Comment Code: " + productJson.get(COMMENT));
            }
            return prods;

        } catch (JSONRPC2SessionException e) {
            throw new OpenErpException(e.getMessage(), "");
        }

    }

    public void createRecord(String model, JSONObject values) {
        JSONRPC2Response jsonRPC2Response = null;
        try {
            mJSONRPC2Session.setURL(mCreateRecordUrl);
            Map<String, Object> params = new HashMap<String, Object>();
            org.json.JSONArray prodArr = new org.json.JSONArray();
            prodArr.put(values);


            org.json.JSONObject kwargs = new org.json.JSONObject();
            org.json.JSONObject context = new org.json.JSONObject();
            kwargs.put("context", context);
            params.put("model", model);
            params.put("method", "create");
            params.put("args", prodArr.toList());
            params.put("kwargs", kwargs);

            System.out.println("Params: " + params.toString());

            jsonRPC2Response = mJSONRPC2Session.send(new JSONRPC2Request(METHOD, params, OpenERPUtil.generateRequestID()));
            JSONObject result = (JSONObject) jsonRPC2Response.getResult();
            System.out.println("Created object: " + result.toJSONString());
            //callMethod(model, "create", args, map, null, callback, backResponse);
        } catch (Exception e) {
            OdooLog.e(e, e.getMessage());
        }
    }




}
