package com.lucast.vetcare.company;

import com.lucast.vetcare.common.enums.Crt;
import com.lucast.vetcare.common.enums.IeIndicator;
import jakarta.persistence.*;

@Entity
@Table(name = "company_fiscal_config")
public class CompanyFiscalConfigEntity {

    @Id
    @Column(name = "company_id")
    private Long companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @Column(name = "ie", length = 20)
    private String ie;

    @Enumerated(EnumType.STRING)
    @Column(name = "ie_indicator", nullable = false, length = 30)
    private IeIndicator ieIndicator;

    @Enumerated(EnumType.STRING)
    @Column(name = "crt", nullable = false, length = 30)
    private Crt crt;

    public Long getCompanyId() { return companyId; }
    public CompanyEntity getCompany() { return company; }

    public String getIe() { return ie; }
    public void setIe(String ie) { this.ie = ie; }

    public IeIndicator getIeIndicator() { return ieIndicator; }
    public void setIeIndicator(IeIndicator ieIndicator) { this.ieIndicator = ieIndicator; }

    public Crt getCrt() { return crt; }
    public void setCrt(Crt crt) { this.crt = crt; }
}
