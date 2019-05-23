package com.neuedu.vo;

public class payVO {
    private Long orderNo;
    private String qrcode;

    public payVO() {

    }
    public payVO(Long orderNo, String qrcode) {
        this.orderNo = orderNo;
        this.qrcode = qrcode;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
