package com.lucast.vetcare.company;

import com.lucast.vetcare.company.dto.CompanyProfileResponse;
import com.lucast.vetcare.company.dto.UpdateCompanyProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@Tag(name = "Company", description = "Issuer company profile (clinic/petshop)")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/current")
    @Operation(
            summary = "Get current issuer company profile",
            description = "Returns issuer company profile data used for fiscal emission setup"
    )
    public CompanyProfileResponse getCurrent() {
        return companyService.getCurrentProfile();
    }

    @PutMapping("/current")
    @Operation(
            summary = "Update current issuer company profile",
            description = "Updates issuer company profile. Only ADMIN and VET can edit"
    )
    public CompanyProfileResponse updateCurrent(@RequestBody @Valid UpdateCompanyProfileRequest request) {
        return companyService.updateCurrentProfile(request);
    }
}
