package com.lucast.vetcare.reports;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.auth.UserEntity;
import com.lucast.vetcare.auth.UserRepository;
import com.lucast.vetcare.catalog.ProductQueryService;
import com.lucast.vetcare.catalog.dto.ProductListDTO;
import com.lucast.vetcare.common.enums.ProductCategory;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.common.enums.Species;
import com.lucast.vetcare.customers.company.CustomerCompanyService;
import com.lucast.vetcare.customers.company.dto.CustomerCompanyListItemResponse;
import com.lucast.vetcare.customers.pet.PetService;
import com.lucast.vetcare.customers.pet.dto.PetListItemResponse;
import com.lucast.vetcare.customers.tutor.TutorService;
import com.lucast.vetcare.customers.tutor.dto.TutorListItemResponse;
import com.lucast.vetcare.reports.dto.ReportColumnResponse;
import com.lucast.vetcare.reports.dto.ReportDefinitionResponse;
import com.lucast.vetcare.reports.dto.ReportPreviewResponse;
import com.lucast.vetcare.reports.dto.ReportRunRequest;
import com.lucast.vetcare.reports.export.ReportExportService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

@Service
public class ReportService {

    private static final int DEFAULT_SIZE = 20;
    private static final int EXPORT_PAGE_SIZE = 500;

    private final ReportDefinitionRegistry definitionRegistry;
    private final ReportExportService reportExportService;
    private final UserRepository userRepository;
    private final TutorService tutorService;
    private final PetService petService;
    private final ProductQueryService productQueryService;
    private final CustomerCompanyService customerCompanyService;

    public ReportService(
            ReportDefinitionRegistry definitionRegistry,
            ReportExportService reportExportService,
            UserRepository userRepository,
            TutorService tutorService,
            PetService petService,
            ProductQueryService productQueryService,
            CustomerCompanyService customerCompanyService
    ) {
        this.definitionRegistry = definitionRegistry;
        this.reportExportService = reportExportService;
        this.userRepository = userRepository;
        this.tutorService = tutorService;
        this.petService = petService;
        this.productQueryService = productQueryService;
        this.customerCompanyService = customerCompanyService;
    }

    @Transactional(readOnly = true)
    public List<ReportDefinitionResponse> listDefinitions() {
        AuthContext.requireUser();
        return definitionRegistry.list();
    }

    @Transactional(readOnly = true)
    public ReportPreviewResponse preview(String reportKey, ReportRunRequest request) {
        QuerySlice slice = query(reportKey, request, false);
        return new ReportPreviewResponse(
                slice.definition().key(),
                slice.definition().title(),
                slice.columns(),
                slice.rows(),
                slice.totalElements(),
                slice.totalPages(),
                slice.page(),
                slice.size()
        );
    }

    @Transactional(readOnly = true)
    public byte[] export(String reportKey, ReportFormat format, PdfOrientation orientation, ReportRunRequest request) {
        QuerySlice slice = query(reportKey, request, true);
        return reportExportService.export(format, orientation, slice.definition().title(), slice.columns(), slice.rows());
    }

    private QuerySlice query(String reportKey, ReportRunRequest request, boolean exportAll) {
        ReportDefinitionResponse definition = definitionRegistry.getByKey(reportKey);
        List<ReportColumnResponse> selectedColumns = resolveColumns(definition, request);

        return switch (reportKey) {
            case "users" -> queryUsers(definition, selectedColumns, request, exportAll);
            case "tutors" -> queryTutors(definition, selectedColumns, request, exportAll);
            case "pets" -> queryPets(definition, selectedColumns, request, exportAll);
            case "products" -> queryProducts(definition, selectedColumns, request, exportAll);
            case "customer-companies" -> queryCustomerCompanies(definition, selectedColumns, request, exportAll);
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report definition not found");
        };
    }

    private QuerySlice queryUsers(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> selectedColumns,
            ReportRunRequest request,
            boolean exportAll
    ) {
        if (AuthContext.requireUser().getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can access users report");
        }

        Map<String, Object> filters = getFilters(request);
        String query = getText(filters, "query");
        Boolean active = getBoolean(filters, "active");
        Role role = getEnum(filters, "role", Role.class);

        Sort sort = parseSort(request == null ? null : request.sort(), "name");
        List<UserEntity> all = userRepository.findAll(sort);

        List<Map<String, Object>> rows = all.stream()
                .filter(u -> {
                    if (query == null || query.isBlank()) return true;
                    String q = query.toLowerCase(Locale.ROOT);
                    return (u.getName() != null && u.getName().toLowerCase(Locale.ROOT).contains(q))
                            || (u.getEmail() != null && u.getEmail().toLowerCase(Locale.ROOT).contains(q));
                })
                .filter(u -> active == null || u.isActive() == active)
                .filter(u -> role == null || u.getRole() == role)
                .map(u -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getName());
                    map.put("email", u.getEmail());
                    map.put("role", u.getRole() == null ? null : u.getRole().name());
                    map.put("active", u.isActive());
                    return map;
                })
                .toList();

        return paginateMappedRows(definition, selectedColumns, rows, request, exportAll);
    }

    private QuerySlice queryTutors(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> selectedColumns,
            ReportRunRequest request,
            boolean exportAll
    ) {
        Map<String, Object> filters = getFilters(request);

        String query = getText(filters, "query");
        Boolean active = getBoolean(filters, "active");
        Boolean hasCompany = getBoolean(filters, "hasCompany");
        Boolean hasPet = getBoolean(filters, "hasPet");
        Boolean hasContact = getBoolean(filters, "hasContact");

        Sort sort = parseSort(request == null ? null : request.sort(), "name");

        if (exportAll) {
            List<TutorListItemResponse> data = fetchAll(pageable ->
                    tutorService.list(query, active, hasCompany, hasPet, hasContact, pageable), sort);

            List<Map<String, Object>> rows = data.stream().map(this::tutorRow).toList();
            List<Map<String, Object>> projected = projectRows(rows, selectedColumns);
            return new QuerySlice(definition, selectedColumns, projected, projected.size(), 1, 0, projected.size());
        }

        Pageable pageable = pageableFromRequest(request, sort);
        Page<TutorListItemResponse> page = tutorService.list(query, active, hasCompany, hasPet, hasContact, pageable);

        List<Map<String, Object>> rows = page.getContent().stream().map(this::tutorRow).toList();
        return new QuerySlice(
                definition,
                selectedColumns,
                projectRows(rows, selectedColumns),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    private QuerySlice queryPets(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> selectedColumns,
            ReportRunRequest request,
            boolean exportAll
    ) {
        Map<String, Object> filters = getFilters(request);

        Long tutorId = getLong(filters, "tutorId");
        String query = getText(filters, "query");
        Boolean active = getBoolean(filters, "active");
        Species species = getEnum(filters, "species", Species.class);
        Boolean othersSpecies = getBoolean(filters, "othersSpecies");

        Sort sort = parseSort(request == null ? null : request.sort(), "name");

        if (exportAll) {
            List<PetListItemResponse> data = fetchAll(pageable ->
                    petService.list(tutorId, query, active, species, othersSpecies, pageable), sort);

            List<Map<String, Object>> rows = data.stream().map(this::petRow).toList();
            List<Map<String, Object>> projected = projectRows(rows, selectedColumns);
            return new QuerySlice(definition, selectedColumns, projected, projected.size(), 1, 0, projected.size());
        }

        Pageable pageable = pageableFromRequest(request, sort);
        Page<PetListItemResponse> page = petService.list(tutorId, query, active, species, othersSpecies, pageable);

        List<Map<String, Object>> rows = page.getContent().stream().map(this::petRow).toList();
        return new QuerySlice(
                definition,
                selectedColumns,
                projectRows(rows, selectedColumns),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    private QuerySlice queryProducts(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> selectedColumns,
            ReportRunRequest request,
            boolean exportAll
    ) {
        Map<String, Object> filters = getFilters(request);

        String name = getText(filters, "name");
        ProductCategory category = getEnum(filters, "category", ProductCategory.class);
        Boolean active = getBoolean(filters, "active");

        Sort sort = parseSort(request == null ? null : request.sort(), "name");

        if (exportAll) {
            List<ProductListDTO> data = fetchAll(pageable ->
                    productQueryService.list(name, category, active, pageable), sort);

            List<Map<String, Object>> rows = data.stream().map(this::productRow).toList();
            List<Map<String, Object>> projected = projectRows(rows, selectedColumns);
            return new QuerySlice(definition, selectedColumns, projected, projected.size(), 1, 0, projected.size());
        }

        Pageable pageable = pageableFromRequest(request, sort);
        Page<ProductListDTO> page = productQueryService.list(name, category, active, pageable);

        List<Map<String, Object>> rows = page.getContent().stream().map(this::productRow).toList();
        return new QuerySlice(
                definition,
                selectedColumns,
                projectRows(rows, selectedColumns),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    private QuerySlice queryCustomerCompanies(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> selectedColumns,
            ReportRunRequest request,
            boolean exportAll
    ) {
        Map<String, Object> filters = getFilters(request);

        String query = getText(filters, "query");
        Long tutorId = getLong(filters, "tutorId");
        Boolean active = getBoolean(filters, "active");
        Boolean hasAddress = getBoolean(filters, "hasAddress");
        Boolean hasFiscal = getBoolean(filters, "hasFiscal");
        Boolean hasContact = getBoolean(filters, "hasContact");

        Sort sort = parseSort(request == null ? null : request.sort(), "legalName");

        if (exportAll) {
            List<CustomerCompanyListItemResponse> data = fetchAll(pageable ->
                    customerCompanyService.list(tutorId, query, active, hasAddress, hasFiscal, hasContact, pageable), sort);

            List<Map<String, Object>> rows = data.stream().map(this::customerCompanyRow).toList();
            List<Map<String, Object>> projected = projectRows(rows, selectedColumns);
            return new QuerySlice(definition, selectedColumns, projected, projected.size(), 1, 0, projected.size());
        }

        Pageable pageable = pageableFromRequest(request, sort);
        Page<CustomerCompanyListItemResponse> page = customerCompanyService.list(
                tutorId,
                query,
                active,
                hasAddress,
                hasFiscal,
                hasContact,
                pageable
        );

        List<Map<String, Object>> rows = page.getContent().stream().map(this::customerCompanyRow).toList();
        return new QuerySlice(
                definition,
                selectedColumns,
                projectRows(rows, selectedColumns),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    private <T> List<T> fetchAll(Function<Pageable, Page<T>> pageFetcher, Sort sort) {
        List<T> out = new ArrayList<>();
        int pageNumber = 0;

        while (true) {
            Page<T> page = pageFetcher.apply(PageRequest.of(pageNumber, EXPORT_PAGE_SIZE, sort));
            out.addAll(page.getContent());

            if (page.isLast()) {
                break;
            }
            pageNumber++;
        }

        return out;
    }

    private QuerySlice paginateMappedRows(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> selectedColumns,
            List<Map<String, Object>> sourceRows,
            ReportRunRequest request,
            boolean exportAll
    ) {
        List<Map<String, Object>> projected = projectRows(sourceRows, selectedColumns);

        if (exportAll) {
            return new QuerySlice(definition, selectedColumns, projected, projected.size(), 1, 0, projected.size());
        }

        int page = request == null || request.page() == null || request.page() < 0 ? 0 : request.page();
        int size = request == null || request.size() == null || request.size() < 1 ? DEFAULT_SIZE : request.size();

        int from = page * size;
        if (from >= projected.size()) {
            return new QuerySlice(definition, selectedColumns, List.of(), projected.size(), 0, page, size);
        }

        int to = Math.min(from + size, projected.size());
        List<Map<String, Object>> pageRows = projected.subList(from, to);

        int totalPages = projected.isEmpty() ? 0 : (int) Math.ceil((double) projected.size() / (double) size);
        return new QuerySlice(definition, selectedColumns, pageRows, projected.size(), totalPages, page, size);
    }

    private List<Map<String, Object>> projectRows(List<Map<String, Object>> sourceRows, List<ReportColumnResponse> columns) {
        List<Map<String, Object>> rows = new ArrayList<>(sourceRows.size());

        for (Map<String, Object> source : sourceRows) {
            Map<String, Object> projected = new LinkedHashMap<>();
            for (ReportColumnResponse column : columns) {
                projected.put(column.key(), source.get(column.key()));
            }
            rows.add(projected);
        }

        return rows;
    }

    private List<ReportColumnResponse> resolveColumns(ReportDefinitionResponse definition, ReportRunRequest request) {
        List<String> requested = request == null ? null : request.columns();
        if (requested == null || requested.isEmpty()) {
            return definition.columns();
        }

        Set<String> allowed = definition.columns().stream().map(ReportColumnResponse::key).collect(java.util.stream.Collectors.toSet());

        List<ReportColumnResponse> selected = definition.columns().stream()
                .filter(col -> requested.contains(col.key()))
                .toList();

        if (selected.isEmpty()) {
            return definition.columns();
        }

        boolean hasInvalid = requested.stream().anyMatch(key -> !allowed.contains(key));
        if (hasInvalid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid report column selection");
        }

        return selected;
    }

    private Pageable pageableFromRequest(ReportRunRequest request, Sort sort) {
        int page = request == null || request.page() == null || request.page() < 0 ? 0 : request.page();
        int size = request == null || request.size() == null || request.size() < 1 ? DEFAULT_SIZE : request.size();
        return PageRequest.of(page, size, sort);
    }

    private Sort parseSort(List<String> sortItems, String defaultProperty) {
        if (sortItems == null || sortItems.isEmpty()) {
            return Sort.by(Sort.Order.asc(defaultProperty));
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String item : sortItems) {
            if (item == null || item.isBlank()) {
                continue;
            }

            String[] tokens = item.split(",");
            String property = tokens[0].trim();
            if (property.isBlank()) {
                continue;
            }

            Sort.Direction direction = Sort.Direction.ASC;
            if (tokens.length > 1) {
                try {
                    direction = Sort.Direction.fromString(tokens[1].trim());
                } catch (IllegalArgumentException ignored) {
                    direction = Sort.Direction.ASC;
                }
            }

            orders.add(new Sort.Order(direction, property));
        }

        if (orders.isEmpty()) {
            return Sort.by(Sort.Order.asc(defaultProperty));
        }

        return Sort.by(orders);
    }

    private Map<String, Object> getFilters(ReportRunRequest request) {
        return request == null || request.filters() == null ? Map.of() : request.filters();
    }

    private String getText(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        if (value == null) return null;
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private Boolean getBoolean(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        if (value == null) return null;
        if (value instanceof Boolean bool) return bool;

        String text = String.valueOf(value).trim().toLowerCase(Locale.ROOT);
        if (text.isEmpty()) return null;
        if (text.equals("true")) return true;
        if (text.equals("false")) return false;
        return null;
    }

    private Long getLong(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        if (value == null) return null;

        if (value instanceof Number number) {
            return number.longValue();
        }

        String text = String.valueOf(value).trim();
        if (text.isEmpty()) return null;
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid numeric filter: " + key);
        }
    }

    private <E extends Enum<E>> E getEnum(Map<String, Object> filters, String key, Class<E> enumType) {
        Object value = filters.get(key);
        if (value == null) return null;

        String text = String.valueOf(value).trim();
        if (text.isEmpty()) return null;

        try {
            return Enum.valueOf(enumType, text.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid enum filter: " + key);
        }
    }

    private Map<String, Object> tutorRow(TutorListItemResponse tutor) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", tutor.id());
        map.put("name", tutor.name());
        map.put("document", tutor.document());
        map.put("phone", tutor.phone());
        map.put("email", tutor.email());
        map.put("active", tutor.active());
        return map;
    }

    private Map<String, Object> petRow(PetListItemResponse pet) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", pet.id());
        map.put("tutorId", pet.tutorId());
        map.put("tutorName", pet.tutorName());
        map.put("name", pet.name());
        map.put("species", pet.species() == null ? null : pet.species().name());
        map.put("active", pet.active());
        return map;
    }

    private Map<String, Object> productRow(ProductListDTO product) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", product.id());
        map.put("sku", product.sku());
        map.put("name", product.name());
        map.put("category", product.category());
        map.put("unit", product.unit());
        map.put("salePrice", normalizeBigDecimal(product.salePrice()));
        map.put("costPrice", normalizeBigDecimal(product.costPrice()));
        map.put("minStock", normalizeBigDecimal(product.minStock()));
        map.put("active", product.active());
        return map;
    }

    private Map<String, Object> customerCompanyRow(CustomerCompanyListItemResponse company) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", company.id());
        map.put("tutorId", company.tutorId());
        map.put("legalName", company.legalName());
        map.put("tradeName", company.tradeName());
        map.put("cnpj", company.cnpj());
        map.put("active", company.active());
        return map;
    }

    private BigDecimal normalizeBigDecimal(BigDecimal value) {
        return value == null ? null : value.stripTrailingZeros();
    }

    private record QuerySlice(
            ReportDefinitionResponse definition,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }
}
