package com.learn.controller;

import com.learn.model.AppLink;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

        @GetMapping("/")
        public String home(Model model) {
                List<AppLink> apps = getApplicationLinks();
                model.addAttribute("apps", apps);
                model.addAttribute("title", "Application Portal");
                return "index";
        }

        @GetMapping("/api/process-headers")
        public Map<String, Object> processHeaders() {
                try {
                        RestTemplate restTemplate = new RestTemplate();
                        String headerProcessorUrl = "http://localhost:8081/customer-management/header-processor";

                        // Call the HeaderProcessorServlet
                        Map<String, Object> response = restTemplate.getForObject(headerProcessorUrl, Map.class);

                        return Map.of(
                                        "status", "success",
                                        "message", "Headers processed successfully",
                                        "data", response != null ? response : Map.of());
                } catch (Exception e) {
                        return Map.of(
                                        "status", "error",
                                        "message", "Failed to process headers: " + e.getMessage(),
                                        "error", e.getClass().getSimpleName());
                }
        }

        private List<AppLink> getApplicationLinks() {
                List<AppLink> apps = new ArrayList<>();

                apps.add(new AppLink(
                                "User Management",
                                "Manage users, roles and permissions",
                                "http://localhost:8081/user-management",
                                "👥"));

                apps.add(new AppLink(
                                "Inventory System",
                                "Track and manage inventory items",
                                "http://localhost:8082/inventory",
                                "📦"));

                apps.add(new AppLink(
                                "Reporting Dashboard",
                                "View analytics and generate reports",
                                "http://localhost:8083/reports",
                                "📊"));

                apps.add(new AppLink(
                                "Settings",
                                "Configure application settings",
                                "http://localhost:8084/settings",
                                "⚙️"));

                return apps;
        }
}