package com.lucast.vetcare.stock;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.stock.dto.CreateStockMovementRequest;
import com.lucast.vetcare.stock.dto.ProductStockBalanceListDTO;
import com.lucast.vetcare.stock.dto.ProductStockBalanceResponse;
import com.lucast.vetcare.stock.dto.StockMovementResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse create(@RequestBody @Valid CreateStockMovementRequest req) {
        Long userId = AuthContext.requireUserId();
        return stockService.createMovement(req, userId);
    }

    @GetMapping("/balance")
    public ProductStockBalanceResponse balance(@RequestParam Long productId) {
        return stockService.getBalance(productId);
    }

    @GetMapping("/movements")
    public Page<StockMovementResponse> listMovements(
            @RequestParam(required = false) Long productId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return stockService.listMovements(productId, pageable);
    }

    @GetMapping("/balances")
    public Page<ProductStockBalanceListDTO> listBalances(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean belowMinStock,
            Pageable pageable
    ) {
        return stockService.listBalances(query, belowMinStock, pageable);
    }
}
