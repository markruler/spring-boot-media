package com.example.demo;

import com.example.demo.application.sync.ChunkFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class Router {

    private final ChunkFileService chunkFileService;

    @GetMapping("/")
    public String chunkUploadPage(Model model) {
        model.addAttribute("filelist", chunkFileService.listFiles());
        return "index";
    }

    @GetMapping("/compress")
    public String compreeVideoPage() {
        return "compress";
    }

}
