package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;
import java.util.Date;

public class AccessPointDto implements Serializable {

	private static final long serialVersionUID = -9059171394832476797L;
	private Long keyId = null;

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	private String countryCode;

	private Long year;

	private Date collectionDate = null;

	private Double latitude = 0.0;

	private Double longitude = 0.0;

	private Double altitude = 0.0;

	private String communityCode = null;

	private String photoURL = null;

	private TechnologyType typeTechnology = null;

	private String TechnologyTypeOther = null;

	private Date constructionDate = null;

	private Long numberOfHouseholdsUsingPoint = null;

	private Double costPer = null;
	private UnitOfMeasureDto costPerUnitOfMeasure = null;
	private CurrencyDto costPerCurrency = null;

	private String farthestHouseholdfromPoint = null;

	private String currentManagementStructurePoint = null;

	private Status pointStatus = null;
	private String otherStatus = null;

	private String pointPhotoCaption = null;

	private String description = null;

	private AccessPointType pointType;

	public enum Status {
		FUNCTIONING_HIGH, FUNCTIONING_OK, FUNCTIONING_WITH_PROBLEMS, NO_IMPROVED_SYSTEM, OTHER
	}

	public AccessPointType getPointType() {
		return pointType;
	}

	public void setPointType(AccessPointType pointType) {
		this.pointType = pointType;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public TechnologyType getTypeTechnology() {
		return typeTechnology;
	}

	public void setTypeTechnology(TechnologyType typeTechnology) {
		this.typeTechnology = typeTechnology;
	}

	public Date getConstructionDate() {
		return constructionDate;
	}

	public void setConstructionDate(Date constructionDate) {
		this.constructionDate = constructionDate;
	}

	public Long getNumberOfHouseholdsUsingPoint() {
		return numberOfHouseholdsUsingPoint;
	}

	public void setNumberOfHouseholdsUsingPoint(
			Long numberOfHouseholdsUsingPoint) {
		this.numberOfHouseholdsUsingPoint = numberOfHouseholdsUsingPoint;
	}

	public Double getCostPer() {
		return costPer;
	}

	public void setCostPer(Double costPer) {
		this.costPer = costPer;
	}

	public String getFarthestHouseholdfromPoint() {
		return farthestHouseholdfromPoint;
	}

	public void setFarthestHouseholdfromPoint(String farthestHouseholdfromPoint) {
		this.farthestHouseholdfromPoint = farthestHouseholdfromPoint;
	}

	public String getCurrentManagementStructurePoint() {
		return currentManagementStructurePoint;
	}

	public void setCurrentManagementStructurePoint(
			String currentManagementStructurePoint) {
		this.currentManagementStructurePoint = currentManagementStructurePoint;
	}

	public Status getPointStatus() {
		return pointStatus;
	}

	public void setPointStatus(Status pointStatus) {
		this.pointStatus = pointStatus;
	}

	public String getPointPhotoCaption() {
		return pointPhotoCaption;
	}

	public void setPointPhotoCaption(String pointPhotoCaption) {
		this.pointPhotoCaption = pointPhotoCaption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public enum AccessPointType {
		WATER_POINT, SANITATION_POINT
	}

	public void setTechnologyTypeOther(String technologyTypeOther) {
		TechnologyTypeOther = technologyTypeOther;
	}

	public String getTechnologyTypeOther() {
		return TechnologyTypeOther;
	}

	public void setCostPerUnitOfMeasure(UnitOfMeasureDto costPerUnitOfMeasure) {
		this.costPerUnitOfMeasure = costPerUnitOfMeasure;
	}

	public UnitOfMeasureDto getCostPerUnitOfMeasure() {
		return costPerUnitOfMeasure;
	}

	public void setCostPerCurrency(CurrencyDto costPerCurrency) {
		this.costPerCurrency = costPerCurrency;
	}

	public CurrencyDto getCostPerCurrency() {
		return costPerCurrency;
	}

	public void setOtherStatus(String otherStatus) {
		this.otherStatus = otherStatus;
	}

	public String getOtherStatus() {
		return otherStatus;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public Status encodeStatus(String unencodedStatus) {
		if (unencodedStatus.trim().equals("high")) {
			return Status.FUNCTIONING_HIGH;
		} else if (unencodedStatus.trim().toLowerCase().equals(
				"functioning but with problems")) {
			return Status.FUNCTIONING_WITH_PROBLEMS;
		} else if (unencodedStatus.trim().toLowerCase().equals("functional")
				|| unencodedStatus.trim().toLowerCase().equals("functioning")
				|| unencodedStatus.trim().equals("ok")) {
			return Status.FUNCTIONING_OK;
		} else if (unencodedStatus.trim().toLowerCase().contains("no improved")) {
			return Status.NO_IMPROVED_SYSTEM;
		} else {
			return Status.OTHER;
		}
	}
}
