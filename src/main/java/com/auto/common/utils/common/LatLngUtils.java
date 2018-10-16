package com.auto.common.utils.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class LatLngUtils {

	private static Logger logger = LoggerFactory.getLogger(LatLngUtils.class);

	private LatLngUtils() {
		//do nothing
	}

	public static LatLng displaceAPoint(String lat, String lng,
	                                    double displacement, double angle) {

		return displaceAPoint(Double.parseDouble(lat), Double.parseDouble(lng),
				displacement, angle);
	}

	public static LatLng displaceAPoint(Double lat, Double lng,
	                                    double displacement, double angle) {
		try {
			LatLng point = new LatLng(lat, lng);

			LatLng pointd = LatLngTool.travel(point, angle, displacement,
					LengthUnit.KILOMETER);
			return pointd;
		} catch (Exception error) {
			logger.error("Error in displaceAPoint : " + error);
			return new LatLng(lat, lng);
		}
	}

}
