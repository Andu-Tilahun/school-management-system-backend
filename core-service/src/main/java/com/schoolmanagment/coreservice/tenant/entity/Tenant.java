package com.schoolmanagment.coreservice.tenant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "tenant_code", nullable = false, unique = true)
    private String tenantCode;
    @Column(name = "tenant_name", nullable = false, unique = true)
    private String tenantName;
    @Column(name = "website")
    private String website;
}