package org.delcom.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CashFlowServiceTest {
    @Test
    @DisplayName("Pengujian untuk service CashFlow dengan Autentikasi")
    void testCashFlowService() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();

        CashFlow cashFlow = new CashFlow(userId, "IN", "BANK", "Salary", 1000, "Monthly Salary");
        cashFlow.setId(cashFlowId);

        CashFlowRepository cashFlowRepository = Mockito.mock(CashFlowRepository.class);
        
        // Atur perilaku mock (sekarang menggunakan userId)
        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);
        when(cashFlowRepository.findByKeyword(userId, "BANK")).thenReturn(java.util.List.of(cashFlow));
        when(cashFlowRepository.findAllByUserId(userId)).thenReturn(java.util.List.of(cashFlow));
        when(cashFlowRepository.findByUserIdAndId(userId, cashFlowId)).thenReturn(java.util.Optional.of(cashFlow));
        when(cashFlowRepository.findByUserIdAndId(userId, nonexistentCashFlowId)).thenReturn(java.util.Optional.empty());
        when(cashFlowRepository.findDistinctLabels(userId)).thenReturn(java.util.List.of("Salary"));
        doNothing().when(cashFlowRepository).deleteById(any(UUID.class));

        CashFlowService cashFlowService = new CashFlowService(cashFlowRepository);

        // Menguji create cash flow
        {
            CashFlow createdCashFlow = cashFlowService.createCashFlow(userId, "IN", "BANK", "Salary", 1000, "Monthly Salary");
            assert (createdCashFlow.getUserId().equals(userId));
            assert (createdCashFlow.getLabel().equals("Salary"));
        }

        // Menguji getAllCashFlows
        {
            var cashFlows = cashFlowService.getAllCashFlows(userId, null);
            assert (cashFlows.size() == 1);
        }

        // Menguji getAllCashFlows dengan pencarian
        {
            var cashFlows = cashFlowService.getAllCashFlows(userId, "BANK");
            assert (cashFlows.size() == 1);
            cashFlows = cashFlowService.getAllCashFlows(userId, "    ");
            assert (cashFlows.size() == 1);
        }

        // Menguji getCashFlowById
        {
            CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, cashFlowId);
            assert (fetchedCashFlow != null);
            assert (fetchedCashFlow.getId().equals(cashFlowId));
        }

        // Menguji getCashFlowById dengan ID yang tidak ada
        {
            CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, nonexistentCashFlowId);
            assert (fetchedCashFlow == null);
        }
        
        // Menguji getCashFlowLabels
        {
            var labels = cashFlowService.getCashFlowLabels(userId);
            assert (labels.size() == 1);
        }

        // Menguji updateCashFlow
        {
            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(userId, cashFlowId, "OUT", "ATM", "Withdraw", 500, "Desc");
            assert (updatedCashFlow != null);
            assert (updatedCashFlow.getType().equals("OUT"));
        }

        // Menguji update CashFlow dengan ID yang tidak ada
        {
            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(userId, nonexistentCashFlowId, "OUT", "ATM", "Withdraw", 500, "Desc");
            assert (updatedCashFlow == null);
        }

        // Menguji deleteCashFlow
        {
            boolean deleted = cashFlowService.deleteCashFlow(userId, cashFlowId);
            assert (deleted == true);
        }

        // Menguji deleteCashFlow dengan ID yang tidak ada
        {
            boolean deleted = cashFlowService.deleteCashFlow(userId, nonexistentCashFlowId);
            assert (deleted == false);
        }
    }
}