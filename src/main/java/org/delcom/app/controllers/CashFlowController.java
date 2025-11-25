package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Ini adalah API Controller, bukan View Controller
@RequestMapping("/api/cash-flows")
public class CashFlowController {

    private final CashFlowService cashFlowService;

    @Autowired
    protected AuthContext authContext;

    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    // Metode yang diharapkan oleh CashFlowControllerTests.java

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createCashFlow(@RequestBody CashFlow reqCashFlow) {
        // Validasi dan logika bisnis (Sama seperti P10)
        if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        }

        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow newCashFlow = cashFlowService.createCashFlow(
                authUser.getId(),
                reqCashFlow.getType(),
                reqCashFlow.getSource(),
                reqCashFlow.getLabel(),
                reqCashFlow.getAmount(),
                reqCashFlow.getDescription()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "success", "Berhasil menambahkan data", Map.of("id", newCashFlow.getId())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<CashFlow>>>> getAllCashFlows(@RequestParam(required = false) String search) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
                "success", "Daftar cash flow berhasil diambil", Map.of("cash_flows", cashFlows)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, CashFlow>>> getCashFlowById(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow cashFlow = cashFlowService.getCashFlowById(authUser.getId(), id);
        if (cashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success", "Data cash flow berhasil diambil", Map.of("cash_flow", cashFlow)));
    }
    
    @GetMapping("/labels")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getCashFlowLabels() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<String> labels = cashFlowService.getCashFlowLabels(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success", "Berhasil mengambil data", Map.of("labels", labels)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CashFlow>> updateCashFlow(@PathVariable UUID id, @RequestBody CashFlow reqCashFlow) {
        if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        }

        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow updatedCashFlow = cashFlowService.updateCashFlow(
                authUser.getId(), id, reqCashFlow.getType(), reqCashFlow.getSource(),
                reqCashFlow.getLabel(), reqCashFlow.getAmount(), reqCashFlow.getDescription());

        if (updatedCashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Data cash flow berhasil diperbarui", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCashFlow(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = cashFlowService.deleteCashFlow(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Data cash flow berhasil dihapus", null));
    }
}