package org.delcom.app.views;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeView {

    @Autowired
    private AuthContext authContext;

    // Kita TIDAK butuh TodoService di sini lagi karena Home hanya menampilkan Menu.
    // Logika Todo sudah pindah ke TodoView.java (jika ada) atau TodoController.

    @GetMapping("/")
    public ModelAndView home() {
        // 1. Cek Login menggunakan AuthContext (Konsisten dengan CashFlow)
        if (!authContext.isAuthenticated()) {
            return new ModelAndView("redirect:/auth/login");
        }

        // 2. Siapkan View 'pages/home'
        ModelAndView view = new ModelAndView("pages/home");
        
        // 3. Ambil data User untuk sapaan "Selamat Datang, [Nama]!"
        User user = authContext.getAuthUser();
        view.addObject("user", user);
        
        // 4. Judul Halaman
        view.addObject("title", "Home Dashboard");

        // Kita TIDAK memuat list 'todos' atau 'todoForm' di sini.
        // Halaman ini bersih, hanya menu pilihan.
        
        return view;
    }
}