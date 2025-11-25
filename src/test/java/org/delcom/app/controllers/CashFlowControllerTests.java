package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class CashFlowControllerTests {
    @Test
    @DisplayName("Pengujian untuk controller CashFlow dengan Autentikasi")
    void testCashFlowController() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();

        CashFlow cashFlow = new CashFlow(userId, "Pemasukan", "Gaji", "Gaji Bulanan", 5000000, "Gaji bulan ini");
        cashFlow.setId(cashFlowId);

        CashFlowService cashFlowService = Mockito.mock(CashFlowService.class);

        // PERBAIKAN DI SINI: Menambahkan parameter ke-4 (any(String.class)) untuk 'label'
        // Urutan: userId, type, source, label, amount, description
        when(cashFlowService.createCashFlow(
                any(UUID.class), 
                any(String.class), 
                any(String.class), 
                any(String.class), // Tambahan untuk 'label'
                any(Integer.class), 
                any(String.class)))
                .thenReturn(cashFlow);

        CashFlowController cashFlowController = new CashFlowController(cashFlowService);
        cashFlowController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Data tidak valid
        List<CashFlow> invalidCashFlows = List.of(
            new CashFlow(userId, null, "Source", "Label", 1000, "Description"), // Type null
            new CashFlow(userId, "", "Source", "Label", 1000, "Description"), // Type Empty
            new CashFlow(userId, "Type", null, "Label", 1000, "Description"), // Source null
            new CashFlow(userId, "Type", "", "Label", 1000, "Description"), // Source Empty
            new CashFlow(userId, "Type", "Source", null, 1000, "Description"), // Label null
            new CashFlow(userId, "Type", "Source", "", 1000, "Description"), // Label Empty
            new CashFlow(userId, "Type", "Source", "Label", null, "Description"), // Amount null
            new CashFlow(userId, "Type", "Source", "Label", 0, "Description"), // Amount zero
            new CashFlow(userId, "Type", "Source", "Label", 1000, null), // Description null
            new CashFlow(userId, "Type", "Source", "Label", 1000, "") // Description Empty
        );

        // --- Menguji createCashFlow ---
        {
            // Gagal (Data tidak valid)
            for (CashFlow cf : invalidCashFlows) {
                var result = cashFlowController.createCashFlow(cf);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }
            
            // Gagal (Tidak terautentikasi)
            cashFlowController.authContext.setAuthUser(null);
            var result = cashFlowController.createCashFlow(cashFlow);
            assert (result.getStatusCode().is4xxClientError());
            assert (result.getBody().getStatus().equals("fail"));

            // Berhasil
            cashFlowController.authContext.setAuthUser(authUser);
            result = cashFlowController.createCashFlow(cashFlow);
            assert (result.getStatusCode().is2xxSuccessful());
            assert (result.getBody().getStatus().equals("success"));
        }
        
        // --- Menguji getAllCashFlows ---
        {
            // Gagal (Tidak terautentikasi)
            cashFlowController.authContext.setAuthUser(null);
            var result = cashFlowController.getAllCashFlows(null);
            assert (result.getStatusCode().is4xxClientError());
            assert (result.getBody().getStatus().equals("fail"));

            // Berhasil
            cashFlowController.authContext.setAuthUser(authUser);
            when(cashFlowService.getAllCashFlows(any(UUID.class), any())).thenReturn(List.of(cashFlow));
            result = cashFlowController.getAllCashFlows(null);
            assert (result.getStatusCode().is2xxSuccessful());
            assert (result.getBody().getData().get("cash_flows").size() == 1);
        }

        // --- Menguji getCashFlowById ---
        {
            // Gagal (Tidak terautentikasi)
            cashFlowController.authContext.setAuthUser(null);
            var result = cashFlowController.getCashFlowById(cashFlowId);
            assert (result.getStatusCode().is4xxClientError());
            
            cashFlowController.authContext.setAuthUser(authUser);
            
            // Berhasil
            when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(cashFlow);
            var resultSuccess = cashFlowController.getCashFlowById(cashFlowId);
            assert (resultSuccess.getStatusCode().is2xxSuccessful());
            assert (resultSuccess.getBody().getData().get("cash_flow").getId().equals(cashFlowId));
            
            // Gagal (Not Found)
            when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(null);
            var resultFail = cashFlowController.getCashFlowById(nonexistentCashFlowId);
            assert (resultFail.getStatusCode().is4xxClientError());
        }

        // --- Menguji getCashFlowLabels ---
        {
            // Gagal (Tidak terautentikasi)
            cashFlowController.authContext.setAuthUser(null);
            var result = cashFlowController.getCashFlowLabels();
            assert (result.getStatusCode().is4xxClientError());

            // Berhasil
            cashFlowController.authContext.setAuthUser(authUser);
            when(cashFlowService.getCashFlowLabels(any(UUID.class))).thenReturn(List.of("Gaji", "Investasi"));
            var resultSuccess = cashFlowController.getCashFlowLabels();
            assert (resultSuccess.getStatusCode().is2xxSuccessful());
            assert (resultSuccess.getBody().getData().get("labels").size() == 2);
        }

        // --- Menguji updateCashFlow ---
        {
            // Gagal (Data tidak valid)
            for (CashFlow cf : invalidCashFlows) {
                var result = cashFlowController.updateCashFlow(cashFlowId, cf);
                assert (result.getStatusCode().is4xxClientError());
            }

            // Gagal (Tidak terautentikasi)
            cashFlowController.authContext.setAuthUser(null);
            var result = cashFlowController.updateCashFlow(cashFlowId, cashFlow);
            assert (result.getStatusCode().is4xxClientError());

            cashFlowController.authContext.setAuthUser(authUser);

            // Gagal (Not Found)
            // PERBAIKAN: pastikan jumlah argumen di sini juga benar (7 argumen: userId, id, type, source, label, amount, desc)
            when(cashFlowService.updateCashFlow(any(), any(), any(), any(), any(), any(), any())).thenReturn(null);
            var resultFail = cashFlowController.updateCashFlow(nonexistentCashFlowId, cashFlow);
            assert (resultFail.getStatusCode().is4xxClientError());

            // Berhasil
            when(cashFlowService.updateCashFlow(any(), any(), any(), any(), any(), any(), any())).thenReturn(cashFlow);
            var resultSuccess = cashFlowController.updateCashFlow(cashFlowId, cashFlow);
            assert (resultSuccess.getStatusCode().is2xxSuccessful());
        }

        // --- Menguji deleteCashFlow ---
        {
            // Gagal (Tidak terautentikasi)
            cashFlowController.authContext.setAuthUser(null);
            var result = cashFlowController.deleteCashFlow(cashFlowId);
            assert (result.getStatusCode().is4xxClientError());

            cashFlowController.authContext.setAuthUser(authUser);

            // Gagal (Not Found)
            when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(false);
            var resultFail = cashFlowController.deleteCashFlow(nonexistentCashFlowId);
            assert (resultFail.getStatusCode().is4xxClientError());

            // Berhasil
            when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(true);
            var resultSuccess = cashFlowController.deleteCashFlow(cashFlowId);
            assert (resultSuccess.getStatusCode().is2xxSuccessful());
        }
    }
}