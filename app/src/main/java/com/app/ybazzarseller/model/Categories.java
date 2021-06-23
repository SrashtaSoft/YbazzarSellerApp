package com.app.ybazzarseller.model;

import java.util.ArrayList;

public class Categories {
    String id, row_order, name, subtitle, image, status, product_rating, web_image;
    ArrayList<SubCategories> childs;

    public ArrayList<SubCategories> getSubCategories() {
        return childs;
    }

    public void setSubCategories(ArrayList<SubCategories> subCategories) {
        this.childs = subCategories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRow_order() {
        return row_order;
    }

    public void setRow_order(String row_order) {
        this.row_order = row_order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProduct_rating() {
        return product_rating;
    }

    public void setProduct_rating(String product_rating) {
        this.product_rating = product_rating;
    }

    public String getWeb_image() {
        return web_image;
    }

    public void setWeb_image(String web_image) {
        this.web_image = web_image;
    }
}
