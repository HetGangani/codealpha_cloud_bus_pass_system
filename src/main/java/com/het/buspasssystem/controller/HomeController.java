package com.het.buspasssystem.controller;

import com.het.buspasssystem.entity.BusPass;
import com.het.buspasssystem.entity.Route;
import com.het.buspasssystem.repository.BusPassRepository;
import com.het.buspasssystem.repository.RouteRepository;
import com.het.buspasssystem.service.EmailService;
import com.itextpdf.text.pdf.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.het.buspasssystem.entity.User;
import com.het.buspasssystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import com.itextpdf.text.*;
import tools.jackson.core.ObjectReadContext;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
        public String home() {

            return "index";
        }
    @GetMapping("/register")
        public String registerPage() {

            return "register";
        }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "users";
    }

    @GetMapping("deleteUser/{id}")
    public String deleteUser(@PathVariable int id) {
      User user = userRepository.findById(id).orElse(null);

      if(user == null) {
          return "redirect:/users";
      }

      if("ADMIN".equals(user.getRole())) {
          return "redirect:/users";
      }

      userRepository.delete(user);

        return "redirect:/users";
    }

    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable int id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);

        return "edit-user";
    }

    @GetMapping("/apply-pass")
    public String applyPassPage(
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String duplicate,
            HttpSession session,
            Model model
    ) {

            User user = (User) session.getAttribute("loggedInUser");

            if(user == null) {
                return "redirect:/login";
            }

            model.addAttribute("loggedInUser", user);

            if(success != null) {
                model.addAttribute("successMessage", "Buss Pass Application Submitted Successfully!!" );
            }

            if(error != null) {
                model.addAttribute("errorMessage", "Error submitting application!!");
            }

            if(duplicate != null) {
                model.addAttribute("duplicateMessage", "Pass Application already exists!!");
            }

            model.addAttribute("routes", routeRepository.findAll());

            return "apply-pass";
    }

    @GetMapping("/view-passes")
    public String viewPasses(
            @RequestParam(required = false) String keyword,
            HttpSession session,
            Model model
    ) {

        User user = (User) session.getAttribute("loggedInUser");

        if(user == null) {
            return "redirect:/login";
        }



        if(!user.getRole().equals("ADMIN")) {
            return "redirect:/apply-pass";
        }

        List<BusPass> passes;
        if( keyword != null && !keyword.isEmpty()) {
            passes = busPassRepository.findByEnrollmentNoContaining(keyword);
        } else {
            passes = busPassRepository.findAll();
        }

        model.addAttribute("passes", passes);
        model.addAttribute("totalPasses", busPassRepository.count());
        model.addAttribute("pendingCount", busPassRepository.countByStatus("Pending"));
        model.addAttribute("approvedCount", busPassRepository.countByStatus("Approved"));
        model.addAttribute("rejectedCount", busPassRepository.countByStatus("Rejected"));

        return "view-passes";
    }

    @GetMapping("/approve-pass/{id}")
    public String approvePass(@PathVariable int id) {
        BusPass pass = busPassRepository.findById(id).orElse(null);

        User user = userRepository.findByFullName(pass.getStudentName());

        if(pass != null) {

            if(user != null) {
                pass.setStatus("Approved");
                try {
                    File pdfFile = generatePdfFile(pass);

                    emailService.sendMailWithAttachment(
                            user.getEmail(),
                            "Buss Pass Approved!!",
                            "Hello, " + user.getFullName() +
                                    ",\n\nYour Bus Pass has been APPROVED!!. \n\nPlease find your pass attached.", pdfFile);

                    pdfFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                busPassRepository.save(pass);
            }
        }

        return "redirect:/view-passes";
    }

    @GetMapping("/reject-pass/{id}")
    public String rejectPass(@PathVariable int id) {
        BusPass pass = busPassRepository.findById(id).orElse(null);

        User user = userRepository.findByFullName(pass.getStudentName());

        if(pass != null) {
            pass.setStatus("Rejected");
            emailService.sendMail(user.getEmail(),
                    "Bus Pass Rejected!!",
                    "Hello, " + user.getFullName() +
                    ",\n\nYour Bus Pass Application has been REJECTED."
            );

            busPassRepository.save(pass);
        }

        return "redirect:/view-passes";
    }

    @GetMapping("/download-pass/{id}")
    public ResponseEntity<byte []> downloadPass(
            @PathVariable int id
    ) {
        try {
            BusPass pass = busPassRepository.findById(id).orElse(null);

            if(pass == null) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, out);

            document.open();

            Image logo = Image.getInstance("src/main/resources/static/images/logo.png");
            logo.scaleToFit(80,80);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("CHARUSAT BUS PASS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));

            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);

            PdfPCell cell = new PdfPCell(
                    new Phrase("CHARUSAT BUS PASS", titleFont)
            );

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(10);

            header.addCell(cell);

            document.add(header);

            PdfPTable table = new PdfPTable(2);

            table.addCell("Pass ID: ");
            table.addCell(String.valueOf(pass.getId()));

            table.addCell("Student Name: ");
            table.addCell(pass.getStudentName());

            table.addCell("Enrollement No.: ");
            table.addCell(pass.getEnrollmentNo());

            table.addCell("College Name: ");
            table.addCell(pass.getCollegeName());

            table.addCell("Source Stop");
            table.addCell(pass.getSourceStop());

            table.addCell("Destination Stop: ");
            table.addCell(pass.getDestinationStop());

            table.addCell("Route: ");
            table.addCell(pass.getRouteName());

            table.addCell("Fare: ");
            table.addCell("₹ " + pass.getFare());

            table.addCell("Status: ");
            table.addCell(pass.getStatus());

            document.add(table);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            table.addCell("Issued On: ");
            table.addCell(pass.getAppliedDate().format(formatter));

            String qrText = "Pass ID: " + pass.getId()
                    + "\nStudent Name: " + pass.getStudentName()
                    + "\nEnrollement No:" + pass.getEnrollmentNo()
                    + "\nRoute: " + pass.getRouteName()
                    + "\nStatus: " + pass.getStatus();

            BarcodeQRCode qrCode = new BarcodeQRCode(qrText, 150, 150, null);
            Image qrImage = qrCode.getImage();

            Paragraph qrTitle = new Paragraph("Scan QR for Pass Details: ");
            qrTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(qrTitle);
            document.add(qrImage);

            document.add( new Paragraph(" "));
            document.add( new Paragraph("Generated by Cloud Bus Pass System"));



            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BusPass-" + pass.getEnrollmentNo() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/login")
    public String loginuser() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();

        return "redirect:/login";
    }

    @GetMapping("/student-dashboard")
    public String studentDashboard(
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("loggedInUser");

        if(user == null) {
            return "redirect:/login";
        }

        model.addAttribute("loggedInUser", user);

        BusPass pass = busPassRepository.findTopByStudentNameOrderByAppliedDateDesc(user.getFullName()).orElse(null);

        model.addAttribute("pass", pass);

        long totalApplications = busPassRepository.countByStudentName(user.getFullName());
        long approvedCount = busPassRepository.countByStudentNameAndStatus(user.getFullName(), "Approved");
        long pendingCount = busPassRepository.countByStudentNameAndStatus(user.getFullName(), "Pending");
        long rejectedCount = busPassRepository.countByStudentNameAndStatus(user.getFullName(), "Rejected");

        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("rejectedCount", rejectedCount);

        List<BusPass> applications = busPassRepository.findByStudentNameOrderByAppliedDateDesc(user.getFullName());

        model.addAttribute("applications", applications);

        return "student-dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if(user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "change-password";
    }

    @GetMapping("/routes")
    public String viewRoutes(Model model) {
        model.addAttribute("routes", routeRepository.findAll());

        return "routes";
    }

    @GetMapping("/add-route")
    public String addRoutePage() {
        return "add-route";
    }

    @GetMapping("/edit-route/{id}")
    public String editRoutePage(@PathVariable int id, Model model) {
        Route route = routeRepository.findById(id).orElse(null);

        if(route == null) {
            return "redirect:/routes";
        }

        model.addAttribute("route", route);

        return "edit-route";
    }

    @GetMapping("/delete-route/{id}")
    public String deleteRoute(@PathVariable int id) {
        routeRepository.deleteById(id);

        return "redirect:/routes";
    }

    @PostMapping("/update-route")
    public String updateRoute(
            @RequestParam int id,
            @RequestParam String routeName,
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam double fare
    ) {
        Route route = routeRepository.findById(id).orElse(null);

        if(route != null) {
            route.setRouteName(routeName);
            route.setSource(source);
            route.setDestination(destination);
            route.setFare(fare);

            routeRepository.save(route);
        }

        return "redirect:/routes";
    }

    @PostMapping("/add-route")
    public String addRoute(
            @RequestParam String routeName,
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam double fare
    ) {
        Route route = new Route();

        route.setRouteName(routeName);
        route.setSource(source);
        route.setDestination(destination);
        route.setFare(fare);

        routeRepository.save(route);

        return "redirect:/routes";
    }

    @PostMapping("/registerUser")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password

    ) {
        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        user.setRole("STUDENT");

        userRepository.save(user);

        System.out.println("User saved successfully!!");

        return "register";
    }

    @PostMapping("/updateUser")
    public String updateUser(
            @RequestParam int id,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password
    ) {
        User user = userRepository.findById(id).orElse(null);

        if(user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);

            userRepository.save(user);
        }
        return "redirect:/users";
    }

    @PostMapping("/save-pass")
    public String savePass(
            @RequestParam String studentName,
            @RequestParam String enrollmentNo,
            @RequestParam String collegeName,
            @RequestParam int routeId,
            @RequestParam String mobileNumber
    ) {

            Route route = routeRepository.findById(routeId).orElse(null);

            if(route == null) {
                return "redirect:/apply-pass?error";
            }
            BusPass pass = new BusPass();

            pass.setStudentName(studentName);
            pass.setEnrollmentNo(enrollmentNo);
            pass.setCollegeName(collegeName);
            pass.setSourceStop(route.getSource());
            pass.setDestinationStop(route.getDestination());
            pass.setRouteName(route.getRouteName());
            pass.setFare(route.getFare());
            pass.setMobileNumber(mobileNumber);
            pass.setAppliedDate(LocalDateTime.now());

            pass.setStatus("Pending");

            List<BusPass> existingPass = busPassRepository.findByEnrollmentNo(enrollmentNo);
            if(!existingPass.isEmpty()) {
                return "redirect:/apply-pass?duplicate";
            }

            try {
                busPassRepository.save(pass);

                return "redirect:/apply-pass?success";
            } catch (Exception e) {
                return "redirect:/apply-pass?error";
            }
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session
    ) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            return "redirect:/login?error";
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/login?error";
        }

        session.setAttribute("loggedInUser", user);

        if(user.getRole().equals("ADMIN")) {
            return "redirect:/view-passes";
        }

        return "redirect:/student-dashboard";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("loggedInUser");

        if(user == null) {
            return "redirect:/login";
        }

        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "redirect:/change-password?error";
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        session.setAttribute("loggedInUser", user);

        return "redirect:/profile?success";
    }

    private File generatePdfFile(BusPass pass) throws Exception {
        File pdfFile = new File("BusPass-" + pass.getEnrollmentNo() + ".pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();

        Image logo = Image.getInstance("src/main/resources/static/images/logo.png");

        logo.scaleToFit(80,80);
        logo.setAlignment(Element.ALIGN_CENTER);

        document.add(logo);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);

        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(new Phrase("CHARUSAT BUS PASS", titleFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        cell.setPadding(10);
        header.addCell(cell);
        document.add(header);

        PdfPTable table = new PdfPTable(2);

        table.addCell("Pass ID");
        table.addCell(String.valueOf(pass.getId()));

        table.addCell("Student Name");
        table.addCell(pass.getStudentName());

        table.addCell("Enrollment No");
        table.addCell(pass.getEnrollmentNo());

        table.addCell("College Name");
        table.addCell(pass.getCollegeName());

        table.addCell("Source Stop");
        table.addCell(pass.getSourceStop());

        table.addCell("Destination Stop");
        table.addCell(pass.getDestinationStop());

        table.addCell("Route");
        table.addCell(pass.getRouteName());

        table.addCell("Status");
        table.addCell(pass.getStatus());

        document.add(table);
        document.close();
        return pdfFile;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusPassRepository busPassRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RouteRepository routeRepository;

}

