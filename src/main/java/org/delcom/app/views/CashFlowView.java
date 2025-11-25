package org.delcom.app.views;

import java.util.List;
import java.util.UUID;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cash-flows")
public class CashFlowView {

    @Autowired
    private CashFlowService cashFlowService;

    @Autowired
    private AuthContext authContext;

    // Menampilkan daftar Cash Flow
    @GetMapping
    public ModelAndView index(@RequestParam(required = false) String search) {
        ModelAndView view = new ModelAndView("pages/cash-flow/index");

        // Cek apakah user sudah login
        if (!authContext.isAuthenticated()) {
            return new ModelAndView("redirect:/auth/login");
        }

        User user = authContext.getAuthUser();
        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(user.getId(), search);

        view.addObject("cashFlows", cashFlows);
        view.addObject("search", search);
        view.addObject("user", user);
        view.addObject("title", "Daftar Cash Flow");
        return view;
    }

    // Menampilkan form tambah data
    @GetMapping("/add")
    public ModelAndView add() {
        if (!authContext.isAuthenticated()) {
            return new ModelAndView("redirect:/auth/login");
        }

        ModelAndView view = new ModelAndView("models/cash-flow/add");
        view.addObject("cashFlow", new CashFlow());
        return view;
    }

    // Memproses penyimpanan data baru
    @PostMapping("/add")
    public String store(@ModelAttribute CashFlow cashFlow, RedirectAttributes redirectAttributes) {
        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User user = authContext.getAuthUser();
        
        // Panggil service (sesuaikan argumen dengan method di CashFlowService Anda)
        cashFlowService.createCashFlow(
            user.getId(), 
            cashFlow.getType(), 
            cashFlow.getSource(), 
            cashFlow.getLabel(), 
            cashFlow.getAmount(), 
            cashFlow.getDescription()
        );

        redirectAttributes.addFlashAttribute("success", "Data cash flow berhasil ditambahkan.");
        return "redirect:/cash-flows";
    }

    // Menampilkan form edit
    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return new ModelAndView("redirect:/auth/login");
        }

        User user = authContext.getAuthUser();
        CashFlow cashFlow = cashFlowService.getCashFlowById(user.getId(), id);

        if (cashFlow == null) {
            return new ModelAndView("redirect:/cash-flows");
        }

        ModelAndView view = new ModelAndView("models/cash-flow/edit");
        view.addObject("cashFlow", cashFlow);
        return view;
    }

    // Memproses update data
    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id, @ModelAttribute CashFlow cashFlow, RedirectAttributes redirectAttributes) {
        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User user = authContext.getAuthUser();
        cashFlowService.updateCashFlow(
            user.getId(), 
            id, 
            cashFlow.getType(), 
            cashFlow.getSource(), 
            cashFlow.getLabel(), 
            cashFlow.getAmount(), 
            cashFlow.getDescription()
        );

        redirectAttributes.addFlashAttribute("success", "Data cash flow berhasil diperbarui.");
        return "redirect:/cash-flows";
    }

    // Menampilkan konfirmasi hapus
    @GetMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return new ModelAndView("redirect:/auth/login");
        }

        User user = authContext.getAuthUser();
        CashFlow cashFlow = cashFlowService.getCashFlowById(user.getId(), id);

        if (cashFlow == null) {
            return new ModelAndView("redirect:/cash-flows");
        }

        ModelAndView view = new ModelAndView("models/cash-flow/delete");
        view.addObject("cashFlow", cashFlow);
        return view;
    }

    // Memproses penghapusan data
    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User user = authContext.getAuthUser();
        cashFlowService.deleteCashFlow(user.getId(), id);

        redirectAttributes.addFlashAttribute("success", "Data cash flow berhasil dihapus.");
        return "redirect:/cash-flows";
    }
}