package com.ccthub.userservice.dto;

public class DashboardStatsResponse {
    private Long totalUsers;
    private Long activeToday;
    private Long monthlyOrders;
    private Double monthlyRevenue;

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveToday() {
        return activeToday;
    }

    public void setActiveToday(Long activeToday) {
        this.activeToday = activeToday;
    }

    public Long getMonthlyOrders() {
        return monthlyOrders;
    }

    public void setMonthlyOrders(Long monthlyOrders) {
        this.monthlyOrders = monthlyOrders;
    }

    public Double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
}
