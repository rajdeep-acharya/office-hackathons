package com.skdata.hackathon.qrcode.controller.validate;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.skdata.hackathon.qrcode.controller.QrCodeController;
import com.skdata.hackathon.qrcode.dto.QrCodeBase64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping(value = "/read")
public class ReaderController implements QrCodeController {

    @PostMapping(value = "/img", consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> readQrCode(@RequestBody BufferedImage requestImg) {
        if (requestImg == null) {
            return ResponseEntity.badRequest().body("QR image is missing");
        }
        return ResponseEntity.ok(convertToString(requestImg));
    }

    @PostMapping(value = "/base64", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> readQrCodeByte(@RequestBody QrCodeBase64 requestEntity) {
        String strBs64 = requestEntity.getQrCode();
        BufferedImage img;
        try {
            img = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(strBs64)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(convertToString(img));
    }

    private String convertToString(BufferedImage requestImg) {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(requestImg)));
        Result result;
        try {
            result = new MultiFormatReader().decode(binaryBitmap);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        log.info("QR Code Read Successfully!!!");
        return result.getText();
    }
}
