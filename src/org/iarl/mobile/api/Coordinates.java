package org.iarl.mobile.api;

public class Coordinates {
	private static final double EARTH_RADIUS = 6371.0;
	private static final double MAX_LATITUDE = 85.0511f;
	
	public double latitude;
	public double longitude;
	
	public Coordinates(double lat, double lon) {
		if (Math.abs(lat) < MAX_LATITUDE) {
			latitude = lat;
		} else {
			latitude = MAX_LATITUDE * (latitude < 0 ? -1 : 1);
		}
		longitude = lon;
	}
	
	public double[] getCoordinates() {
		double out[] = new double[2];
		out[0] = latitude;
		out[1] = longitude;
		return out;
	}
	
	public double bearing(Coordinates other) {
		double dLon = Math.toRadians(this.longitude - other.longitude); 
		double y = Math.sin(dLon) * Math.cos(other.latitude);
		double x = Math.cos(this.latitude) * Math.sin(other.latitude) - 
				Math.sin(this.latitude) * Math.cos(other.latitude) * Math.cos(dLon);
		return Math.toDegrees(Math.atan2(y, x));
	}
	
	public double haversine(Coordinates other) {
		double dLat = Math.toRadians(this.latitude - other.latitude);
		double dLon = Math.toRadians(this.longitude - other.longitude);
		double lat1 = Math.toRadians(this.latitude);
		double lat2 = Math.toRadians(other.latitude);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return EARTH_RADIUS * c;
	}
	
	public String latitudeAsDML() {
		final boolean neg;
		if (this.latitude > 0) {
			neg = false;
		} else {
			neg = true;
		}
		
		return degreesAsDML(this.latitude) + (neg ? "S" : "N");
	}
	
	public String longitudeAsDML() {
		final boolean neg;
		if (this.latitude > 0) {
			neg = false;
		} else {
			neg = true;
		}
		
		return degreesAsDML(longitude) + (neg ? "W" : "E");
	}
	
	private static String degreesAsDML(double deg) {
		StringBuffer dms = new StringBuffer();
		int n = (int)deg;
		dms.append(n);
		dms.append("\u00B0 ");
		
		deg = (deg - n) * 60.0f;
		n = (int)deg;
		dms.append(n);
		dms.append("' ");
		
		deg = (deg - n) * 60.0f;
		n = (int)deg;
		dms.append(n);
		dms.append("\" ");
		
		return dms.toString();
	}
}
