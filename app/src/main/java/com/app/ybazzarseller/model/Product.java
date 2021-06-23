package com.app.ybazzarseller.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

    String id, seller_id, row_order, name, tax_id, slug, category_id, subcategory_id, indicator, manufacturer, made_in, return_status, cancelable_status, till_status, image, description, status, date_added, delivery_places, pincodes, is_approved, return_days, seller_name, seller_status, price, tax_title, tax_percentage, type,loose_stock,loose_stock_unit_id;
    ArrayList<PriceVariation> variants;
    ArrayList<String> other_images;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
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

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMade_in() {
        return made_in;
    }

    public void setMade_in(String made_in) {
        this.made_in = made_in;
    }

    public String getReturn_status() {
        return return_status;
    }

    public void setReturn_status(String return_status) {
        this.return_status = return_status;
    }

    public String getCancelable_status() {
        return cancelable_status;
    }

    public void setCancelable_status(String cancelable_status) {
        this.cancelable_status = cancelable_status;
    }

    public String getTill_status() {
        return till_status;
    }

    public void setTill_status(String till_status) {
        this.till_status = till_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDelivery_places() {
        return delivery_places;
    }

    public void setDelivery_places(String delivery_places) {
        this.delivery_places = delivery_places;
    }

    public String getPincodes() {
        return pincodes;
    }

    public void setPincodes(String pincodes) {
        this.pincodes = pincodes;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }

    public String getReturn_days() {
        return return_days;
    }

    public void setReturn_days(String return_days) {
        this.return_days = return_days;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_status() {
        return seller_status;
    }

    public void setSeller_status(String seller_status) {
        this.seller_status = seller_status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTax_title() {
        return tax_title;
    }

    public void setTax_title(String tax_title) {
        this.tax_title = tax_title;
    }

    public String getTax_percentage() {
        return tax_percentage;
    }

    public void setTax_percentage(String tax_percentage) {
        this.tax_percentage = tax_percentage;
    }

    public ArrayList<PriceVariation> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<PriceVariation> variants) {
        this.variants = variants;
    }

    public ArrayList<String> getOther_images() {
        return other_images;
    }

    public void setOther_images(ArrayList<String> other_images) {
        this.other_images = other_images;
    }

    public String getLoose_stock() {
        return loose_stock;
    }

    public void setLoose_stock(String loose_stock) {
        this.loose_stock = loose_stock;
    }

    public String getLoose_stock_unit_id() {
        return loose_stock_unit_id;
    }

    public void setLoose_stock_unit_id(String loose_stock_unit_id) {
        this.loose_stock_unit_id = loose_stock_unit_id;
    }
}
