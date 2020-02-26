package org.openmrs.module.kenyaemrCharts.openerp;

import org.json.JSONArray;

import java.util.List;

public interface OpenErpInterface {
    List<UOM> getUOMs() throws OpenErpException;
    List<OdooLocation> getLocations() throws OpenErpException;
    List<Category> getCategories() throws OpenErpException;
    List<Product> getProducts(int limit, int offset) throws OpenErpException;
    Product searchForProductsById(String id) throws OpenErpException;
    List<Product> searchForProductsByName(String searchString, int limit, int offset) throws OpenErpException;
    List<Product> searchForProductsByCategory(String searchString, int limit, int offset) throws OpenErpException;
    JSONArray printProducts() throws OpenErpException;
}
