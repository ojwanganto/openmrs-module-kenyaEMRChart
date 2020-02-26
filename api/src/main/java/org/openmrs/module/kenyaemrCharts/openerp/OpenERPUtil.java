package org.openmrs.module.kenyaemrCharts.openerp;

import java.util.Random;

public class OpenERPUtil {

    /**
     * Generate a random request id
     *
     * @return
     */
    public static String generateRequestID() {
        return "rid" + new Random().nextInt(Integer.MAX_VALUE);
    }

}
