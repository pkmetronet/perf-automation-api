package com.performance.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "http_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_group_id", nullable = false)
    private Long threadGroupId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 10)
    private String protocol = "https";

    @Column(nullable = false)
    private String domain;

    private Integer port;

    @Column(nullable = false, length = 15)
    private String method = "GET";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "body_data", columnDefinition = "TEXT")
    private String bodyData;

    @Column(name = "follow_redirects")
    private Boolean followRedirects = true;

    @Column(name = "use_keepalive")
    private Boolean useKeepalive = true;

    @Column(name = "timeout_connect")
    private Integer timeoutConnect;

    @Column(name = "timeout_response")
    private Integer timeoutResponse;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    /**
     * Helper method to dynamically generate the complete URL
     * for the JMeter httpSampler.
     */
    public String getUrl() {
        StringBuilder url = new StringBuilder();
        
        // 1. Protocol
        String safeProtocol = (protocol != null && !protocol.isBlank()) ? protocol.replace("://", "") : "https";
        url.append(safeProtocol).append("://");
        
        // 2. Domain (strip trailing slash if user accidentally added one)
        String safeDomain = (domain != null) ? domain : "";
        if (safeDomain.endsWith("/")) {
            safeDomain = safeDomain.substring(0, safeDomain.length() - 1);
        }
        url.append(safeDomain);
        
        // 3. Port (only append if it exists)
        if (port != null) {
            url.append(":").append(port);
        }
        
        // 4. Path (ensure it safely starts with a slash)
        if (path != null && !path.isBlank()) {
            if (!path.startsWith("/")) {
                url.append("/");
            }
            url.append(path);
        } else {
            url.append("/"); // Default to root if no path is provided
        }
        
        return url.toString();
    }    
}