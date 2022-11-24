package com.skdata.hackathon.qrcode.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QrCodeBase64 {

    public QrCodeBase64(String qrCode) {
        this.qrCode = qrCode;
    }

    private String qrCode;

}
