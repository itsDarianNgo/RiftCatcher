package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "shop_items")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;  // Name of the ongoing event, if any
    private LocalDateTime eventStartTime;  // Start time of the event
    private LocalDateTime eventEndTime;  // End time of the event
    private Integer discountRate;  // Discount rate, if any, during the event
}

