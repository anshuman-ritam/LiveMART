package com.example.livemart;

public class ModelProduct {
    private String productId, productTitle, productDescription,
            productQuantity, productCategory, productPrice, productStock, productAvailable, timestamp, uid;

    public ModelProduct() {

    }

    public ModelProduct(String productId, String productTitle, String productDescription,
                        String productQuantity, String productCategory, String productPrice, String productStock,
                        String productAvailable, String timestamp, String uid) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productQuantity = productQuantity;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productAvailable = productAvailable;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId)  {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle)  {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription)  {
        this.productDescription = productDescription;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity)  {
        this.productQuantity = productQuantity;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory)  {
        this.productCategory = productCategory;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice)  {
        this.productPrice = productPrice;
    }

    public String getProductStock() {
        return productStock;
    }

    public void setProductStock(String productStock)  {
        this.productStock = productStock;
    }

    public String getProductAvailable() {
        return productAvailable;
    }

    public void setProductAvailable(String productAvailable)  { this.productAvailable = productAvailable; }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp)  {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid)  {
        this.uid = uid;
    }
}