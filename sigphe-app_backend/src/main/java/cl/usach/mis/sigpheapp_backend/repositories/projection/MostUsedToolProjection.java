package cl.usach.mis.sigpheapp_backend.repositories.projection;

public interface MostUsedToolProjection {
    Long getToolId();
    String getToolName();
    String getToolModel();
    String getToolBrand();
    Long getUsageCount();
}
