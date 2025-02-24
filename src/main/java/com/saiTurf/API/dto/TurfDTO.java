package com.saiTurf.API.dto;

import java.math.BigDecimal;

public class TurfDTO {
	private Long id;
	private String name;
	private String location;
	private BigDecimal pricePerHour;
	private int capacity;
	private String imageUrl;

	// ✅ Explicit no-argument constructor (important for ModelMapper)
	public TurfDTO() {
	}

	// ✅ Constructor for manual mapping (if needed)
	public TurfDTO(Long id, String name, String location, BigDecimal pricePerHour, int capacity, String imageUrl) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.pricePerHour = pricePerHour;
		this.capacity = capacity;
		this.imageUrl = imageUrl;
	}

	// ✅ Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BigDecimal getPricePerHour() {
		return pricePerHour;
	}

	public void setPricePerHour(BigDecimal pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
