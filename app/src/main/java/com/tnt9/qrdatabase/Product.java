package com.tnt9.qrdatabase;


public class Product {

    private String brandName;
    private String price;
    private String qrCode;
    private String date;
    private long dateOrder;
    private String oldPrice;
    private String priceChange;
    private boolean watchlist;

    public Product(String qrCode, String brandName, String price, String date, long dateOrder, String oldPrice, String priceChange, boolean watchlist) {
        this.qrCode = qrCode;
        this.brandName = brandName;
        this.price = price;
        this.date = date;
        this.dateOrder = dateOrder;
        this.oldPrice = oldPrice;
        this.priceChange = priceChange;
        this.watchlist = watchlist;
    }

    public Product() {
    }

    public boolean isWatchlist() {
        return watchlist;
    }

    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public long getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(long dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
