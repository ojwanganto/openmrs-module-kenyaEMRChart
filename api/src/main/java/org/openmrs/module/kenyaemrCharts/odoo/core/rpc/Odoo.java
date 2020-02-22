/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 21/4/15 4:01 PM
 *
 */
package org.openmrs.module.kenyaemrCharts.odoo.core.rpc;


import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.handler.OdooVersionException;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.helper.OdooSession;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.helper.OdooVersion;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.listeners.IOdooConnectionListener;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.listeners.IOdooLoginCallback;
import org.openmrs.module.kenyaemrCharts.odoo.core.rpc.wrapper.OdooWrapper;
import org.openmrs.module.kenyaemrCharts.odoo.core.support.OUser;

public class Odoo extends OdooWrapper<Odoo> {
    public static final String TAG = Odoo.class.getSimpleName();
    public static Boolean DEBUG = false;
    public static Integer REQUEST_TIMEOUT_MS = 3000;
    public static Integer DEFAULT_MAX_RETRIES = 1;

    public enum ErrorCode {
        OdooVersionError(700),
        InvalidURL(404),
        OdooServerError(200),
        UnknownError(703),
        AuthenticationFail(401);

        int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int get() {
            return this.code;
        }

    }

    public Odoo(String baseURL) {
        super(baseURL);
    }


    public static OUser quickConnect(String url, String username,
                                     String password, String database) throws OdooVersionException {
        Odoo odoo = new Odoo(url).connect(true);
        return odoo.authenticate(username, password, database);
    }


    public static void quickConnect(String url, String username, String password,
                                    String database, IOdooLoginCallback callback)
            throws OdooVersionException {
        Odoo odoo = createInstance(url);
        odoo.authenticate(username, password, database, callback);
    }

    public static Odoo createInstance(String baseURL) throws OdooVersionException {
        return new Odoo(baseURL).connect(false);
    }


    public static Odoo createQuickInstance(String baseURL)
            throws OdooVersionException {
        return new Odoo(baseURL).connect(true);
    }

    public Odoo setOnConnect(IOdooConnectionListener callback) {
        mIOdooConnectionListener = callback;
        return this;
    }

    public OdooVersion getVersion() {
        return mVersion;
    }

    public OUser getUser() {
        return user;
    }

    public OdooSession getSession() {
        return odooSession;
    }

    @Override
    public String toString() {
        return "Odoo{" +
                serverURL + ": " +
                mVersion +
                '}';
    }
}
