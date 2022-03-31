package com.ives.idata_inventory.entity;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Stock {

    /**导入的Excel格式
    资产编号	    ERP编号	 资产名称   品牌	    规格	存放位置	保管人	资产说明
    IVES-000028	000028	笔记本电脑	Dell	14寸	销售部	邵翠萍	可协带出公司

    盘点结果Excel格式
    资产编号	     ERP编号	资产名称	   品牌	   规格	   存放位置	保管人	资产说明	    盘点结果	盘点时间
    IVES-000028	 000028	   笔记本电脑	Dell	14寸	销售部	邵翠萍	可协带出公司
     **/
    private String stockId; //资产编号
    private String erpId; //ERP编号
    private String stockName; //资产名称
    private String brand; //品牌
    private String specification;//规格
    private String MF;//存放位置
    private String preserver; //保管人
    private String department;//部门
    private String description;//资产说明

    private int inventoryStatus;//盘点结果 0 //未盘  1 //已盘  //2 补打
    private String newMf = ""; //新区域
    private int  status = -1;//状态
    private String inventoryTime; //盘点时间

    public Stock() {
    }

    public Stock(String stockId, String erpId, String stockName, String brand, String specification, String MF, String preserver, String department, String description, int inventoryStatus, String newMf, int status, String inventoryTime) {
        this.stockId = stockId;
        this.erpId = erpId;
        this.stockName = stockName;
        this.brand = brand;
        this.specification = specification;
        this.MF = MF;
        this.preserver = preserver;
        this.department = department;
        this.description = description;
        this.inventoryStatus = inventoryStatus;
        this.newMf = newMf;
        this.status = status;
        this.inventoryTime = inventoryTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStockId() {
        return stockId;
    }

    public String getNewMf() {
        return newMf;
    }

    public void setNewMf(String newMf) {
        this.newMf = newMf;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getErpId() {
        return erpId;
    }

    public void setErpId(String erpId) {
        this.erpId = erpId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getMF() {
        return MF;
    }

    public void setMF(String MF) {
        this.MF = MF;
    }

    public String getPreserver() {
        return preserver;
    }

    public void setPreserver(String preserver) {
        this.preserver = preserver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(int inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getInventoryTime() {
        return inventoryTime;
    }

    public void setInventoryTime(String inventoryTime) {
        this.inventoryTime = inventoryTime;
    }

    @Override
    public boolean equals(@Nullable Object o) {

        if (this==o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Stock stock = (Stock) o;
        return stockId == stock.stockId && Objects.equals(erpId, stock.erpId);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stockId='" + stockId + '\'' +
                ", erpId='" + erpId + '\'' +
                ", stockName='" + stockName + '\'' +
                ", brand='" + brand + '\'' +
                ", specification='" + specification + '\'' +
                ", MF='" + MF + '\'' +
                ", preserver='" + preserver + '\'' +
                ", description='" + description + '\'' +
                ", inventoryStatus=" + inventoryStatus +
                ", inventoryTime='" + inventoryTime + '\'' +
                '}';
    }
}
