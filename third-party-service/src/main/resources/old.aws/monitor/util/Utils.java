package hr.wozai.service.servicecommons.utils.monitor.util;

import java.util.Collection;

/**
 * Author: mazhao
 * Created: 14-2-27 PM9:52
 */
public class Utils {
    private Utils() {
    }

    public static boolean isBlank(String str) {
        return null == str || "".equals(str.trim());
    }

    @SuppressWarnings("rawtypes")
	public static boolean isEmpty(Collection coll) {
        return null == coll || coll.isEmpty();
    }
}
