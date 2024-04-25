package com.cqupt.software4_backendv2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${file.path}")
    private String dirPath;

    @Value("software4opt.docx")
    private String optFileName;
    @Value("software4intro.pdf")
    private String introFile;
    @RequestMapping(value = "getOptFile")
    public String getOptFile() throws IOException {
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(optFileName)
                .toUriString();

        return fileDownloadUri;

    }
    @RequestMapping(value = "getIntroFile")
    public String getIntroFile() throws IOException {
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(introFile)
                .toUriString();

        return fileDownloadUri;

    }
}
