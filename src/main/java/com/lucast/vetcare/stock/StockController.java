package com.lucast.vetcare.stock;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.stock.dto.CreateStockMovementRequest;
import com.lucast.vetcare.stock.dto.ProductStockBalanceListDTO;
import com.lucast.vetcare.stock.dto.ProductStockBalanceResponse;
import com.lucast.vetcare.stock.dto.StockMovementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@Tag(
        name = "Stock",
        description = "Operations related to stock and inventory management"
)
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create stock movement",
            description = "Create a new stock movement (inbound or outbound)"
    )
    public StockMovementResponse create(
            @RequestBody @Valid CreateStockMovementRequest req
    ) {
        Long userId = AuthContext.requireUserId();
        return stockService.createMovement(req, userId);
    }

    @GetMapping("/balance")
    @Operation(
            summary = "Get product stock balance",
            description = "Retrieve the current stock balance for a specific product"
    )
    public ProductStockBalanceResponse balance(@RequestParam Long productId) {
        return stockService.getBalance(productId);
    }

    @GetMapping("/movements")
    @Operation(
            summary = "List stock movements",
            description = "List stock movements with pagination, sorting and optional product filter"
    )
    public Page<StockMovementResponse> listMovements(
            @RequestParam(required = false) Long productId,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return stockService.listMovements(productId, pageable);
    }

    @GetMapping("/balances")
    @Operation(
            summary = "List product stock balances",
            description = "List product stock balances with pagination and optional filters"
    )
    public Page<ProductStockBalanceListDTO> listBalances(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean belowMinStock,
            @ParameterObject Pageable pageable
    ) {
        return stockService.listBalances(query, belowMinStock, pageable);
    }
}
