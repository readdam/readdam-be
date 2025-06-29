package com.kosta.readdam.service;

public interface GeoService {
    String reverseGeocode(Double lat, Double lng) throws Exception;
}
