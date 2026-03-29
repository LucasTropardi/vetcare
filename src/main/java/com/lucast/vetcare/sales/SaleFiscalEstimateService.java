package com.lucast.vetcare.sales;

import com.lucast.vetcare.catalog.ProductFiscalSnapshotProjection;
import com.lucast.vetcare.catalog.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleFiscalEstimateService {

    public record ItemFiscalEstimate(
            Long productId,
            String ncm,
            String cest,
            String origin,
            String gtinEan,
            String gtinEanTrib,
            String unitTrib,
            BigDecimal tribFactor,
            String serviceListCode,
            String cfop,
            String icmsCode,
            BigDecimal icmsRate,
            String pisCode,
            BigDecimal pisRate,
            String cofinsCode,
            BigDecimal cofinsRate,
            String ipiCode,
            BigDecimal ipiRate,
            BigDecimal estimatedIcmsTax,
            BigDecimal estimatedPisTax,
            BigDecimal estimatedCofinsTax,
            BigDecimal estimatedIpiTax,
            BigDecimal estimatedFederalTax,
            BigDecimal estimatedStateTax,
            BigDecimal estimatedMunicipalTax,
            BigDecimal estimatedTaxTotal
    ) {}

    public record SaleFiscalEstimate(
            BigDecimal estimatedFederalTax,
            BigDecimal estimatedStateTax,
            BigDecimal estimatedMunicipalTax,
            BigDecimal estimatedTaxTotal,
            Map<Long, ItemFiscalEstimate> itemsByProductId
    ) {}

    private final ProductRepository productRepository;

    public SaleFiscalEstimateService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public SaleFiscalEstimate estimate(SaleEntity sale) {
        if (sale == null || sale.getItems() == null || sale.getItems().isEmpty()) {
            return new SaleFiscalEstimate(zero(), zero(), zero(), zero(), Collections.emptyMap());
        }

        Set<Long> productIds = sale.getItems().stream()
                .map(SaleItemEntity::getProductId)
                .collect(Collectors.toSet());

        Map<Long, ProductFiscalSnapshotProjection> snapshots = loadSnapshots(productIds);
        Map<Long, ItemFiscalEstimate> itemsByProductId = new LinkedHashMap<>();

        BigDecimal federal = zero();
        BigDecimal state = zero();
        BigDecimal municipal = zero();
        BigDecimal total = zero();

        for (var item : sale.getItems()) {
            var snapshot = snapshots.get(item.getProductId());
            BigDecimal base = money(item.getTotal());
            BigDecimal icms = percentage(base, snapshot == null ? null : snapshot.getIcmsRate());
            BigDecimal pis = percentage(base, snapshot == null ? null : snapshot.getPisRate());
            BigDecimal cofins = percentage(base, snapshot == null ? null : snapshot.getCofinsRate());
            BigDecimal ipi = percentage(base, snapshot == null ? null : snapshot.getIpiRate());

            BigDecimal federalItem = money(pis.add(cofins).add(ipi));
            BigDecimal stateItem = money(icms);
            BigDecimal municipalItem = zero();
            BigDecimal itemTotal = money(federalItem.add(stateItem).add(municipalItem));

            federal = money(federal.add(federalItem));
            state = money(state.add(stateItem));
            municipal = money(municipal.add(municipalItem));
            total = money(total.add(itemTotal));

            itemsByProductId.put(item.getId(), new ItemFiscalEstimate(
                    item.getProductId(),
                    snapshot == null ? null : snapshot.getNcm(),
                    snapshot == null ? null : snapshot.getCest(),
                    snapshot == null ? null : snapshot.getOrigin(),
                    snapshot == null ? null : snapshot.getGtinEan(),
                    snapshot == null ? null : snapshot.getGtinEanTrib(),
                    snapshot == null ? null : snapshot.getUnitTrib(),
                    scale(snapshot == null ? null : snapshot.getTribFactor(), 6),
                    snapshot == null ? null : snapshot.getServiceListCode(),
                    snapshot == null ? null : snapshot.getCfop(),
                    snapshot == null ? null : snapshot.getIcmsCode(),
                    scale(snapshot == null ? null : snapshot.getIcmsRate(), 4),
                    snapshot == null ? null : snapshot.getPisCode(),
                    scale(snapshot == null ? null : snapshot.getPisRate(), 4),
                    snapshot == null ? null : snapshot.getCofinsCode(),
                    scale(snapshot == null ? null : snapshot.getCofinsRate(), 4),
                    snapshot == null ? null : snapshot.getIpiCode(),
                    scale(snapshot == null ? null : snapshot.getIpiRate(), 4),
                    stateItem,
                    money(pis),
                    money(cofins),
                    money(ipi),
                    federalItem,
                    stateItem,
                    municipalItem,
                    itemTotal
            ));
        }

        return new SaleFiscalEstimate(federal, state, municipal, total, itemsByProductId);
    }

    private Map<Long, ProductFiscalSnapshotProjection> loadSnapshots(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return productRepository.findFiscalSnapshotsByProductIds(productIds).stream()
                .collect(Collectors.toMap(ProductFiscalSnapshotProjection::getProductId, snapshot -> snapshot, (left, right) -> left));
    }

    private static BigDecimal percentage(BigDecimal base, BigDecimal rate) {
        if (base == null || rate == null) return zero();
        return base.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal money(BigDecimal value) {
        return scale(value, 2);
    }

    private static BigDecimal scale(BigDecimal value, int scale) {
        if (value == null) return null;
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    private static BigDecimal zero() {
        return new BigDecimal("0.00");
    }
}
