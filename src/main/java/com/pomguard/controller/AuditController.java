package com.pomguard.controller;

import com.pomguard.model.AuditResult;
import com.pomguard.model.Dependency;
import com.pomguard.service.AuditService;
import com.pomguard.service.HistoryService;
import com.pomguard.service.PomParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AuditController {

    private final PomParser pomParser;
    private final AuditService auditService;
    private final HistoryService historyService;

    public AuditController(PomParser pomParser, AuditService auditService, HistoryService historyService) {
        this.pomParser = pomParser;
        this.auditService = auditService;
        this.historyService = historyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sessions", historyService.getRecentSessions());
        return "index";
    }

    @PostMapping("/audit")
    public String audit(@RequestParam("file") MultipartFile file, Model model) throws Exception {
        List<Dependency> deps = pomParser.parse(file.getInputStream());
        List<AuditResult> results = auditService.audit(deps);
        
        historyService.addSession(file.getOriginalFilename(), results);

        model.addAttribute("results", results);
        model.addAttribute("total", results.size());
        model.addAttribute("filename", file.getOriginalFilename());
        return "result";
    }

    @PostMapping("/history/delete")
    public String deleteHistory(@RequestParam("id") String id) {
        historyService.deleteSession(id);
        return "redirect:/";
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "ok";
    }
}
