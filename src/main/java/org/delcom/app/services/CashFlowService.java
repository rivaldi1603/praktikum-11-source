package org.delcom.app.services;

import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashFlowService {

    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    /**
     * MODIFIKASI: Memerlukan userId saat membuat
     */
    @Transactional
    public CashFlow createCashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);
        return cashFlowRepository.save(cashFlow);
    }

    /**
     * MODIFIKASI: Memerlukan userId untuk mengambil data
     */
    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return cashFlowRepository.findByKeyword(userId, search);
        }
        // MODIFIKASI: Hanya ambil data milik user
        return cashFlowRepository.findAllByUserId(userId);
    }

    /**
     * MODIFIKASI: Memerlukan userId untuk mengambil data
     */
    public CashFlow getCashFlowById(UUID userId, UUID id) {
        // MODIFIKASI: Hanya ambil data milik user
        return cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
    }

    /**
     * MODIFIKASI: Memerlukan userId untuk mengambil data
     */
    public List<String> getCashFlowLabels(UUID userId) {
        return cashFlowRepository.findDistinctLabels(userId);
    }

    /**
     * MODIFIKASI: Memerlukan userId untuk memvalidasi kepemilikan
     */
    @Transactional
    public CashFlow updateCashFlow(UUID userId, UUID id, String type, String source, String label, Integer amount, String description) {
        // MODIFIKASI: Validasi data milik user
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);

        if (cashFlow != null) {
            cashFlow.setType(type);
            cashFlow.setSource(source);
            cashFlow.setLabel(label);
            cashFlow.setAmount(amount);
            cashFlow.setDescription(description);
            return cashFlowRepository.save(cashFlow);
        }
        return null;
    }

    /**
     * MODIFIKASI: Memerlukan userId untuk memvalidasi kepemilikan
     */
    @Transactional
    public boolean deleteCashFlow(UUID userId, UUID id) {
        // MODIFIKASI: Validasi data milik user
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow == null) {
            return false; // Gagal (data tidak ada atau bukan milik user)
        }

        cashFlowRepository.deleteById(id);
        return true;
    }
}