package org.delcom.app.entities;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CashFlowTests {
    @Test
    @DisplayName("Membuat instance dari kelas CashFlow")
    void testMembuatInstanceCashFlow() throws Exception {
        UUID userId = UUID.randomUUID();

        // CashFlow dengan userId (Constructor Baru)
        {
            // Perhatikan urutan parameter: userId dulu!
            CashFlow cashFlow = new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 400000, "Menerima gaji.");
            
            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("Inflow"));
            assert (cashFlow.getSource().equals("Gaji"));
            // Pastikan getter ini ada di CashFlow.java
            assert (cashFlow.getLabel().equals("gaji-bulanan")); 
            assert (cashFlow.getAmount().equals(400000));
            assert (cashFlow.getDescription().equals("Menerima gaji."));
        }

        // CashFlow dengan nilai default
        {
            CashFlow cashFlow = new CashFlow();
            assert (cashFlow.getId() == null);
            assert (cashFlow.getUserId() == null);
            assert (cashFlow.getType() == null);
            assert (cashFlow.getLabel() == null); // Cek label null
        }

        // CashFlow dengan setNilai
        {
            CashFlow cashFlow = new CashFlow();
            UUID generatedId = UUID.randomUUID();
            cashFlow.setId(generatedId);
            cashFlow.setUserId(userId);
            cashFlow.setType("Set Type");
            cashFlow.setSource("Set Source");
            
            // Pastikan setter ini ada
            cashFlow.setLabel("Set Label"); 
            cashFlow.setAmount(500000);
            cashFlow.setDescription("Set Description");
            
            cashFlow.onCreate();
            cashFlow.onUpdate();

            assert (cashFlow.getId().equals(generatedId));
            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("Set Type"));
            
            // Pastikan getter ini ada
            assert (cashFlow.getLabel().equals("Set Label"));
            assert (cashFlow.getAmount().equals(500000));
            assert (cashFlow.getDescription().equals("Set Description"));
            
            assert (cashFlow.getCreatedAt() != null);
            assert (cashFlow.getUpdatedAt() != null);
        }
    }
}