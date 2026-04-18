package com.fxs.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.fxs.aiagent.constant.FileConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@Component
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String normalizedFileName = normalizePdfFileName(fileName);
        String filePath = fileDir + "/" + normalizedFileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                PdfFont font = createChineseFont();
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    private String normalizePdfFileName(String fileName) {
        String safeName = fileName == null ? "" : fileName.trim();
        if (safeName.isEmpty()) {
            safeName = "generated";
        }
        safeName = safeName.replaceAll("[\\\\/:*?\"<>|]", "_");
        safeName = safeName.replaceAll("\\p{Cntrl}", "_");
        return safeName.toLowerCase().endsWith(".pdf") ? safeName : safeName + ".pdf";
    }

    private PdfFont createChineseFont() throws IOException {
        String[] candidateFonts = {
                "C:/Windows/Fonts/simhei.ttf",
                "C:/Windows/Fonts/simkai.ttf",
                "C:/Windows/Fonts/simfang.ttf",
                "C:/Windows/Fonts/simsunb.ttf",
                "C:/Windows/Fonts/Noto Sans SC (TrueType).otf",
                "C:/Windows/Fonts/NotoSansSC-VF.ttf"
        };

        Exception lastError = null;
        for (String fontPath : candidateFonts) {
            if (Files.exists(Path.of(fontPath))) {
                try {
                    return PdfFontFactory.createFont(
                            fontPath,
                            PdfEncodings.IDENTITY_H,
                            PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                    );
                } catch (Exception e) {
                    lastError = e;
                }
            }
        }

        if (lastError != null) {
            throw new IOException("Chinese font exists but cannot be loaded: " + lastError.getMessage(), lastError);
        }

        throw new IOException("No Chinese font found. Please install Noto Sans SC, Microsoft YaHei, SimSun, or SimHei.");
    }
}
