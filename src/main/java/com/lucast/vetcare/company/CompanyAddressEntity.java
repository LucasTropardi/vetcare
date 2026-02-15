package com.lucast.vetcare.company;

import jakarta.persistence.*;

@Entity
@Table(name = "company_address")
public class CompanyAddressEntity {

    @Id
    @Column(name = "company_id")
    private Long companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @Column(name = "zip_code", nullable = false, length = 8)
    private String zipCode;

    @Column(name = "street", nullable = false, length = 160)
    private String street;

    @Column(name = "number", length = 30)
    private String number;

    @Column(name = "complement", length = 120)
    private String complement;

    @Column(name = "neighborhood", length = 120)
    private String neighborhood;

    @Column(name = "city_name", nullable = false, length = 120)
    private String cityName;

    @Column(name = "city_ibge", length = 7)
    private String cityIbge;

    @Column(name = "state_uf", nullable = false, length = 2)
    private String stateUf;

    @Column(name = "country", nullable = false, length = 60)
    private String country;

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public CompanyEntity getCompany() { return company; }
    public void setCompany(CompanyEntity company) { this.company = company; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCityIbge() { return cityIbge; }
    public void setCityIbge(String cityIbge) { this.cityIbge = cityIbge; }

    public String getStateUf() { return stateUf; }
    public void setStateUf(String stateUf) { this.stateUf = stateUf; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
