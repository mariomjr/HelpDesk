package com.mariojr.helpdeskws.api.enums;

public enum EnumStatus {

	NEW,
	ASSIGNED,
	RESOLVED,
	APPROVED,
	DISAPPROVED,
	CLOSED;
	
	public static EnumStatus getStatus(String status) {
		switch(status) {
			case "NEW" : return NEW;
			case "ASSIGNED" : return ASSIGNED;
			case "RESOLVED" : return RESOLVED;
			case "APPROVED" : return APPROVED;
			case "DISAPPROVED" : return DISAPPROVED;
			case "CLOSED" : return CLOSED;
			default:return NEW;
		}
	}
}
