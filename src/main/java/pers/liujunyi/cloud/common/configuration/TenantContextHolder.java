package pers.liujunyi.cloud.common.configuration;

/***
 *  租户上下文
 * @author ljy
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> TENANTCONTEXT = new ThreadLocal<>();

    public static void setCurrentTenant(String tenant) {
        TENANTCONTEXT.set(tenant);
    }

    public static String getCurrentTenant() {
        return TENANTCONTEXT.get();
    }

    public static void clearCurrentTenant() {
        TENANTCONTEXT.remove();
    }
}
