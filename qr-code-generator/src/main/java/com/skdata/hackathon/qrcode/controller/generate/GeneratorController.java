package com.skdata.hackathon.qrcode.controller.generate;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.skdata.hackathon.qrcode.controller.QrCodeController;
import com.skdata.hackathon.qrcode.dto.QrCodeBase64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping(path = "/generate")
public class GeneratorController implements QrCodeController {

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @PostMapping(path = "/img", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> generateQrCode(@RequestBody String barCodeText) {
        BufferedImage image = createQrCode(barCodeText);
        log.info("QR Code Generated!!!");
        return ResponseEntity.ok(image);
    }

    @PostMapping(value = "/base64", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QrCodeBase64> generateQrCodeByte(@RequestBody String barCodeText) {
        BufferedImage image = createQrCode(barCodeText);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
            log.info("QR Code Generated!!!");
            String strBs64 = Base64.getEncoder().encodeToString(bos.toByteArray());
            return ResponseEntity.ok(new QrCodeBase64(strBs64));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage createQrCode(String barCodeText) {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        String barcodeInUTF8 = new String(barCodeText.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        try {
            BitMatrix bitMatrix = barcodeWriter.encode(barcodeInUTF8, BarcodeFormat.QR_CODE, 200, 200);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

}
